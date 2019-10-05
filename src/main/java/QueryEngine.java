import DataObject.LookupItem;
import DataObject.Posting;
import JsonUtil.JsonParser;

import java.io.*;
import java.util.*;

public class QueryEngine {

    private static Map<String, LookupItem> lookupTable;
    private static List<String> terms;

    static{
        JsonParser jsonParser = new JsonParser();
        lookupTable = jsonParser.parseJson2LookupTable("lookup.json");
        terms = new ArrayList<>(lookupTable.keySet());
    }

    public static List<Integer> documentQuery(String fileName, boolean isCompress, int topKdoc, List<String> queryTerms){
        String[] queryTermsArray = new String[queryTerms.size()];
        queryTermsArray = queryTerms.toArray(queryTermsArray);

        return documentQuery(fileName, isCompress, topKdoc, queryTermsArray);
    }

    public static List<Integer> documentQuery(String fileName, boolean isCompress, int topKdoc, String... queryTerms){

        List<Integer> result = new LinkedList<>();

        try(RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw")){

            Map<Integer, Integer> documentScores = new HashMap<>();
            Compressor compressor = isCompress? new VbyteCompressor() : new DefaultCompressor();

            for(String queryTerm : queryTerms){

                List<Posting> postings = compressor.decompress(lookupTable.get(queryTerm), randomAccessFile);

                for(Posting posting : postings){
                    int documentScore = documentScores.getOrDefault(posting.getDocumentId(), 0);
                    documentScore++;
                    documentScores.put(posting.getDocumentId(), documentScore);
                }
            }

            PriorityQueue<DocumentScore> scoreRank = new PriorityQueue<>((a, b) -> b.score - a.score);

            for(Map.Entry<Integer, Integer> entry : documentScores.entrySet()){
                DocumentScore documentScore = new DocumentScore(entry.getKey(), entry.getValue());
                scoreRank.add(documentScore);
            }

            while(topKdoc > 0 && !scoreRank.isEmpty()){
                result.add(scoreRank.poll().id);
                topKdoc--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void randomSelect(int numberOfTerms, int times, String fileName){

        Random random = new Random();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {

            for(int i = 0; i < times; i++){

                for(int j = 0; j < numberOfTerms; j++) {
                    String term = terms.get(random.nextInt(lookupTable.size()));
                    writer.append(term);
                    writer.append(" ");
                }
                writer.append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void getDiceCoefficient(List<List<String>> termSets, boolean isCompress, String fileName) throws IOException {

        Compressor compressor = isCompress? new VbyteCompressor() : new DefaultCompressor();

        int count = 0;
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");

        for(List<String> set : termSets){

            int size = set.size();
            for(int i = 0; i < size; i++) {
                System.out.println(count);
                count++;

                String queryTerm = set.get(i);
                String correspondingTerm = null;
                float highestScore = 0;
                List<Posting> postings1 = compressor.decompress(lookupTable.get(queryTerm), randomAccessFile);
                Set<Integer> documentSet1 = getDocumentSet(postings1);
                float na = postings1.size();

                for (String term : terms) {

                    List<Posting> postings2 = compressor.decompress(lookupTable.get(term), randomAccessFile);

                    float nb = postings2.size();

                    Set<Integer> documentSet2 = getDocumentSet(postings2);

                    documentSet2.retainAll(documentSet1);

                    if (documentSet2.size() == 0 || queryTerm.equals(term)) {
                        continue;
                    }

                    Map<Integer, List<List<Integer>>> commonDocPositions = new HashMap<>();

                    for(Posting posting : postings1){

                        if(documentSet2.contains(posting.getDocumentId())){
                            List<List<Integer>> positions = new ArrayList<>();
                            positions.add(posting.getPositions());
                            commonDocPositions.put(posting.getDocumentId(), positions);
                        }
                    }

                    for(Posting posting : postings2){

                        if(documentSet2.contains(posting.getDocumentId())){
                            List<List<Integer>> positions = commonDocPositions.get(posting.getDocumentId());
                            positions.add(posting.getPositions());
                            commonDocPositions.put(posting.getDocumentId(), positions);
                        }
                    }

                    float nab = 0;

                    for(Map.Entry<Integer, List<List<Integer>>> entry : commonDocPositions.entrySet()){
                        List<List<Integer>> positions = entry.getValue();
                        List<Integer> positions1 = positions.get(0);
                        List<Integer> positions2 = positions.get(1);

                        nab += getNab(positions1, positions2);

                    }

                    if((nab/ (na + nb)) > highestScore){
                        highestScore = (nab/ (na + nb));
                        correspondingTerm = term;
                    }
                }

                set.add(correspondingTerm);
                }

            randomAccessFile.close();

        }

        writeToFile(termSets, "14term_" + fileName);
    }

    private static void writeToFile(List<List<String>> termSets, String fileName){

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {

            for(List<String> set : termSets){
                for(String s : set){
                    writer.append(s).append(" ");
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static float getNab(List<Integer> positions1, List<Integer> positions2){
        float count = 0;

        for(int position1 : positions1){
            for(int position2 : positions2){
                if(position2 - position1 == 1){
                    count++;
                }
            }
        }

        return count;
    }

    private static Set<Integer> getDocumentSet(List<Posting> postings){
        Set<Integer> documentSet = new HashSet<>();

        for(Posting posting : postings){
            documentSet.add(posting.getDocumentId());
        }

        return documentSet;
    }


    public static List<List<String>> getTermSets(String fileName){

        List<List<String>> termSets = new ArrayList<>();

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))){

            String line;
            while((line = bufferedReader.readLine()) != null){
                List<String> termSet = new ArrayList<>();
                for(String s : line.split("\\s+")){
                    termSet.add(s);
                }
                termSets.add(termSet);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return termSets;
    }

    static class DocumentScore{
        protected int id;
        protected int score;

        DocumentScore(int id, int score){
            this.id = id;
            this.score = score;
        }
    }

    public static void printResult(List<Integer> results){

        StringBuilder sb = new StringBuilder();
        sb.append("Top ").append(results.size()).append(" results: ");

        for(int result : results){
            sb.append(result).append(" ");
        }

        System.out.println(sb.toString());
    }
    public static void main(String[] args) throws FileNotFoundException {

        String binaryFilename = Boolean.valueOf(args[0])? "compress_binary.dat" : "compress.dat";
        List<List<String>> termSets = getTermSets(args[2]);
        int topKresult = args.length > 2?Integer.valueOf(args[1]):1;

        for(List<String> set : termSets){
            List<Integer> docs = documentQuery(binaryFilename, Boolean.valueOf(args[0]), topKresult, set);
            printResult(docs);
        }

    }
}
