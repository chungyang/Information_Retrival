package queryengine;

import dataobject.*;
import retrievalmodel.DocumentScorer;
import retrievalmodel.DocumentScorerFactory;
import utilities.Compressor;
import utilities.DefaultCompressor;
import utilities.Utils;
import utilities.VbyteCompressor;
import jsonutil.JsonParser;

import java.io.*;
import java.util.*;

public class QueryEngine {

    private static Map<String, LookupItem> lookupTable;
    private static DocumentStats documentStats;
    private static List<String> terms;
    private static int numberOfDocument;
//    private static float averageDocLength;

    static{
        JsonParser jsonParser = new JsonParser();
        lookupTable = jsonParser.parseJson2LookupTable("lookup.json");
        documentStats = jsonParser.parseJson2DocumentStats("documentinfo.json");
        terms = new ArrayList<>(lookupTable.keySet());
        Corpus corpus = jsonParser.parseJson2Corpus("shakespeare-scenes.json");
        numberOfDocument = corpus.getCorpus().size();
    }

    public static List<DocumentScore> documentQuery(String fileName, boolean isCompress, int topKdoc,
                                              List<String> queryTerms, String scorerType){

        String[] queryTermsArray = new String[queryTerms.size()];
        queryTermsArray = queryTerms.toArray(queryTermsArray);

        return documentQuery(fileName, isCompress, topKdoc, queryTermsArray, scorerType);
    }

    public static List<DocumentScore> documentQuery(String fileName, boolean isCompress, int topKdoc,
                                                    String queryTerms, String scorerType){


        return documentQuery(fileName, isCompress, topKdoc, queryTerms.split("\\s+"), scorerType);
    }

    public static List<DocumentScore> documentQuery(String fileName, boolean isCompress, int topKdoc,  String[] queryTerms, String scorerType){

        List<DocumentScore> topKDocumentScores = new LinkedList<>();

        try(RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw")){

            Compressor compressor = isCompress? new VbyteCompressor() : new DefaultCompressor();
            Map<String, List<Posting>> queryPostings = new HashMap<>();
            Map<String, Integer> queryFrequencies = new HashMap<>();

            for(String queryTerm : queryTerms){

                List<Posting> postings = compressor.decompress(lookupTable.get(queryTerm), randomAccessFile);

                queryPostings.put(queryTerm, postings);

                int queryFrequency = queryFrequencies.getOrDefault(queryTerm, 0);
                queryFrequency++;
                queryFrequencies.put(queryTerm, queryFrequency);
            }

            Map<Integer, Float> documentScores = new HashMap<>();
            DocumentScorer documentScorer = DocumentScorerFactory.getDocumentScorer(scorerType);

            //The posting implementation is backed by an ArrayList. Removing element from
            //the end of list is less expensive because elements won't need to be shifted
            for(int i = numberOfDocument; i > 0; i--){

                float documentScore = documentScorer.scoreDocument(i, queryPostings,
                        queryFrequencies, documentStats);

                if(documentScore > 0){
                    documentScores.put(i, documentScore);
                }
            }

            PriorityQueue<DocumentScore> scoreRank = new PriorityQueue<>(
                    (a, b) -> {
                        float compareResult = b.score - a.score;
                        return compareResult >= 0? (int) Math.ceil(compareResult) : (int) Math.floor(compareResult);
                    });

            for(Map.Entry<Integer, Float> entry : documentScores.entrySet()){
                DocumentScore documentScore = new DocumentScore(entry.getKey(), entry.getValue());
                scoreRank.add(documentScore);
            }

            while(topKdoc > 0 && !scoreRank.isEmpty()){

                if(scoreRank.peek().getScore() > 0) {
                    topKDocumentScores.add(scoreRank.poll());
                }
                topKdoc--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return topKDocumentScores;
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


    public static void printResult(List<DocumentScore> results){

        StringBuilder sb = new StringBuilder();
        sb.append("Top ").append(results.size()).append(" results: ");

        for(DocumentScore result : results){
            sb.append(result.id).append(" ");
        }

        System.out.println(sb.toString());
    }
    public static void main(String[] args) throws FileNotFoundException {

        String binaryFilename = Boolean.valueOf(args[0])? "compress_binary.dat" : "binary.dat";
        List<List<String>> termSets = getTermSets(args[1]);
        int topKresult = args.length > 2?Integer.valueOf(args[2]):1;

        int id = 1;
        for(List<String> set : termSets){
            String queryid = "Q" + id;
            List<DocumentScore> docs = documentQuery(binaryFilename, Boolean.valueOf(args[0]), topKresult, set, "BM25");
            Utils.writeTREC("bm25.trecrun", true, docs, documentStats.getDocumentInfos(), "chungtingyang-bm25", queryid, "1.2", "500");
            id++;
        }


    }
}
