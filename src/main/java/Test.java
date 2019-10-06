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

//        JsonParser jsonParser = new JsonParser();
//        Map<String, LookupItem> lookupTable = jsonParser.parseJson2LookupTable("lookup.json");
//
//        Compressor defaultCompressor = new DefaultCompressor();
//        Compressor vbyteCompressor = new VbyteCompressor();
//        RandomAccessFile r1 = new RandomAccessFile("test.dat", "rw");
//        RandomAccessFile r2 = new RandomAccessFile("compress_test.dat", "rw");
//
//        for(String word : lookupTable.keySet()){
//            List<Posting> p1 = defaultCompressor.decompress(lookupTable.get(word), r1);
//            List<Posting> p2 = vbyteCompressor.decompress(lookupTable.get(word), r2);
//
//            if(!p1.equals(p2)){
//                System.out.println(false);
//                System.exit(0);
//            }
//        }
//        System.out.println(true);
        List<List<String>> termSets = QueryEngine.getTermSets("14term.txt");

        long start = System.currentTimeMillis();

        for(int i = 0; i < 100; i++) {
            for (List<String> set : termSets) {
                List<Integer> docs = QueryEngine.documentQuery("binary.dat", false, 5, set);
            }
        }

        long end =  System.currentTimeMillis();
        System.out.println(end - start);


        start = System.currentTimeMillis();

        for(int i = 0; i < 100; i++) {
            for (List<String> set : termSets) {
                List<Integer> docs = QueryEngine.documentQuery("compress_binary.dat", true, 5, set);
//                QueryEngine.printResult(docs);
            }
        }

        end =  System.currentTimeMillis();
        System.out.println(end - start);

    }
}
