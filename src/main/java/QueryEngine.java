import dataobject.Corpus;
import dataobject.DocumentInfo;
import utilities.Compressor;
import utilities.DefaultCompressor;
import utilities.VbyteCompressor;
import dataobject.LookupItem;
import dataobject.Posting;
import jsonutil.JsonParser;

import java.io.*;
import java.util.*;

public class QueryEngine {

    private static Map<String, LookupItem> lookupTable;
    private static Map<String, DocumentInfo> documentInfoMap;
    private static List<String> terms;
    private static int numberOfDocument;

    static{
        JsonParser jsonParser = new JsonParser();
        lookupTable = jsonParser.parseJson2LookupTable("lookup.json");
        documentInfoMap = jsonParser.parseJson2DocumentInfo("documentinfo.json");
        terms = new ArrayList<>(lookupTable.keySet());
        Corpus corpus = jsonParser.parseJson2Corpus("shakespeare-scenes.json");
        numberOfDocument = corpus.getCorpus().size();
    }

    public static List<Integer> documentQuery(String fileName, boolean isCompress, int topKdoc, List<String> queryTerms){
        String[] queryTermsArray = new String[queryTerms.size()];
        queryTermsArray = queryTerms.toArray(queryTermsArray);

        return documentQuery(fileName, isCompress, topKdoc, queryTermsArray);
    }

    public static List<Integer> documentQuery(String fileName, boolean isCompress, int topKdoc, String... queryTerms){

        List<Integer> result = new LinkedList<>();


        try(RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw")){

            Compressor compressor = isCompress? new VbyteCompressor() : new DefaultCompressor();
            Map<String, List<Posting>> queryPostings = new HashMap<>();

            for(String queryTerm : queryTerms){

                List<Posting> postings = compressor.decompress(lookupTable.get(queryTerm), randomAccessFile);

                queryPostings.put(queryTerm, postings);

            }

            Map<Integer, Integer> documentScores = new HashMap<>();

            //The posting implementation is backed by an ArrayList. Removing element from
            //the end of list is less expensive because elements won't need to be shifted
            for(int i = numberOfDocument; i > 0; i--){

                for(Map.Entry<String, List<Posting>> entry : queryPostings.entrySet()){

                    List<Posting> postings = entry.getValue();
                    if(postings.isEmpty()){
                        continue;
                    }
                    Posting lastPosting = postings.get(postings.size() - 1);

                    if(lastPosting.getDocumentId() == i){
                        int score = documentScores.getOrDefault(i, 0);
                        score += lastPosting.getPositions().size();
                        documentScores.put(i, score);
                        postings.remove(postings.size() - 1);
                    }
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

        String binaryFilename = Boolean.valueOf(args[0])? "compress_binary.dat" : "binary.dat";
        List<List<String>> termSets = getTermSets(args[1]);
        int topKresult = args.length > 2?Integer.valueOf(args[2]):1;

        for(List<String> set : termSets){
            List<Integer> docs = documentQuery(binaryFilename, Boolean.valueOf(args[0]), topKresult, set);
            printResult(docs);
        }


    }
}
