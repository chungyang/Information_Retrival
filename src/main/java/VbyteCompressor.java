import DataObject.LookupItem;
import DataObject.Posting;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class VbyteCompressor implements Compressor {


    @Override
    public byte[] compress(int[] values){

        List<Byte> compressedBytes = new ArrayList<>();

        for(int value : values){

            while(value >= 128){
                compressedBytes.add((byte) (value & 0x7F));
                value >>>= 7;
            }
            compressedBytes.add((byte) (value | 0x80));
        }

        byte[] byteArray = new byte[compressedBytes.size()];

        for(int i = 0; i < compressedBytes.size(); i++){
            byteArray[i] = compressedBytes.get(i);
        }

        return byteArray;
    }


    @Override
    public List<Posting> decompress(LookupItem item,  RandomAccessFile randomAccessFile ) {

        List<Posting> postings = new ArrayList<>();

        try{

            randomAccessFile.seek(item.getCompressOffset());

            int numberOfDocument = readNexInt(randomAccessFile);
            int baseDocId = 0;

            for(int i = 0; i < numberOfDocument; i++){

                int numberOfPosition = readNexInt(randomAccessFile);
                int docId = readNexInt(randomAccessFile) + baseDocId;
                baseDocId = docId;

                int basePosition = 0;
                Posting posting = new Posting(docId);

                for(int j = 0; j < numberOfPosition; j++){
                    int position = readNexInt(randomAccessFile) + basePosition;
                    basePosition = position;
                    posting.addPosition(position);
                }
                postings.add(posting);

            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        return postings;
    }

    public static int readNexInt(RandomAccessFile file){


        try {
            int value = file.readByte();
            int lastByte = value;
            value &= 0x7F;
            int position = 1;

            while((lastByte & 0x80) == 0){

                int next = (int) file.readByte();
                lastByte = next;
                next &= 0x7F;
                next <<= 7 * position;
                position++;
                value |= next;

            }

            return value;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void main(String[] args) throws IOException {

        RandomAccessFile r = new RandomAccessFile("test.dat", "rw");
        Compressor c = new VbyteCompressor();
        int[] v = new int[]{9, 1, 39, 736, 1, 301};
        byte[] b = c.compress(v);
        r.write(b);
        r.close();
        r = new RandomAccessFile("test.dat", "rw");

        while(r.getFilePointer() < b.length){
            System.out.println(r.readByte());
        }

    }
}
