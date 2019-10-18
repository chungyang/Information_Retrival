package utilities;

import dataobject.LookupItem;
import dataobject.Posting;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**

 utilities.DefaultCompressor performs no compression or decompression algorithm.
 It simply reads and writes raw bytes.

 **/
public class DefaultCompressor implements Compressor {

    @Override
    public byte[] compress(int[] integersToWrite) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(4 * integersToWrite.length);

        for(int integer : integersToWrite){
            byteBuffer.putInt(integer);
        }

        return byteBuffer.array();
    }

    @Override
    public List<Posting> decompress(LookupItem item,  RandomAccessFile randomAccessFile) {

        List<Posting> postings = new ArrayList<>();

        try{

            randomAccessFile.seek(item.getOffset());
            int numberOfDocument = randomAccessFile.readInt();

            for (int i = 0; i < numberOfDocument; i++) {

                int numberOfPosition = randomAccessFile.readInt();
                int docId = randomAccessFile.readInt();
                Posting posting = new Posting(docId);

                for (int j = 0; j < numberOfPosition; j++) {
                    posting.addPosition(randomAccessFile.readInt());
                }

                postings.add(posting);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return postings;

    }
}
