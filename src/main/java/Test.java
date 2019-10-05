import DataObject.LookupItem;
import DataObject.Posting;
import JsonUtil.JsonParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws IOException {

        JsonParser jsonParser = new JsonParser();
        Map<String, LookupItem> lookupTable = jsonParser.parseJson2LookupTable("lookup.json");

        Compressor defaultCompressor = new DefaultCompressor();
        Compressor vbyteCompressor = new VbyteCompressor();
        RandomAccessFile r1 = new RandomAccessFile("test.dat", "rw");
        RandomAccessFile r2 = new RandomAccessFile("compress_test.dat", "rw");

        for(String word : lookupTable.keySet()){
            List<Posting> p1 = defaultCompressor.decompress(lookupTable.get(word), r1);
            List<Posting> p2 = vbyteCompressor.decompress(lookupTable.get(word), r2);

            if(!p1.equals(p2)){
                System.out.println(false);
                System.exit(0);
            }
        }
        System.out.println(true);

    }
}
