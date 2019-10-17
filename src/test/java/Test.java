import Utilities.Compressor;
import Utilities.DefaultCompressor;
import Utilities.VbyteCompressor;
import DataObject.LookupItem;
import DataObject.Posting;
import JsonUtil.JsonParser;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Test {

    public static void main(String[] args) throws IOException {

        JsonParser jsonParser = new JsonParser();
        Map<String, LookupItem> lookupTable = jsonParser.parseJson2LookupTable("lookup.json");
//
//        Utilities.Compressor defaultCompressor = new Utilities.DefaultCompressor();
//        Utilities.Compressor vbyteCompressor = new Utilities.VbyteCompressor();
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
//        List<List<String>> termSets = QueryEngine.getTermSets("14term.txt");
//
        long start = System.currentTimeMillis();
//
//        for(int i = 0; i < 100; i++) {
//            for (List<String> set : termSets) {
//                List<Integer> docs = QueryEngine.documentQuery("binary.dat", false, 5, set);
//            }
//        }
//
        long end =  System.currentTimeMillis();
//        System.out.println(end - start);
//
//
//        start = System.currentTimeMillis();
//
//        for(int i = 0; i < 100; i++) {
//            for (List<String> set : termSets) {
//                List<Integer> docs = QueryEngine.documentQuery("compress_binary.dat", true, 5, set);
//                QueryEngine.printResult(docs);
//            }
//        }
//
//        end =  System.currentTimeMillis();
//        System.out.println(end - start);
        List<String> terms = new ArrayList<>(lookupTable.keySet());
        Random random = new Random();
        Compressor vByteCompress = new VbyteCompressor();
        Compressor defaultCompress = new DefaultCompressor();

        start = System.currentTimeMillis();
        for(int i = 0; i < 100; i++){
            for(int j = 0; j < 7; j++){
                String term = terms.get(random.nextInt(terms.size()));
                List<Posting> postings = vByteCompress.decompress(lookupTable.get(term), new RandomAccessFile("compress_binary.dat","r"));
            }
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);

        start = System.currentTimeMillis();
        for(int i = 0; i < 100; i++){
            for(int j = 0; j < 7; j++){
                String term = terms.get(random.nextInt(terms.size()));
                List<Posting> postings = defaultCompress.decompress(lookupTable.get(term), new RandomAccessFile("binary.dat","r"));
            }
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
