package apps;

import index.Index;
import index.InvertedIndex;
import index.PostingList;
import nodes.*;
import retrieval.Dirichlet;
import retrieval.DocumentScore;
import retrieval.RetrievalModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Test {

    private static List<QueryNode> getTermNodes(String query, RetrievalModel model, Index index){

        List<QueryNode> termNodes = new ArrayList<>();

        for(String term : query.split("\\s+")){
            termNodes.add(new TermNode(model, term, index));
        }

        return termNodes;
    }

    private static List<DocumentScore> runQuery(QueryNode queryNode, int k, int numberOfDoc){

        List<DocumentScore> documentScores = new ArrayList<>();
        for(int i = 1; i <= numberOfDoc; i++){
            double score = queryNode.score(i);
            if(score != 0) {
                DocumentScore documentScore = new DocumentScore(i, score);
                documentScores.add(documentScore);
            }
        }
        Collections.sort(documentScores);

        List<DocumentScore> topK = new ArrayList<>();

        for(int i = 0; i < k; i++){
            if(documentScores.get(i).getScore() != 0){
                topK.add(documentScores.get(i));
            }
        }
        return topK;
    }

    public static void main(String[] args) throws IOException {

        Index index = new InvertedIndex();
        boolean compressed = Boolean.parseBoolean(args[0]);
        String querysFile = args[1];
        int k = Integer.valueOf(args[2]);
        index.load(compressed);


        BufferedReader reader = new BufferedReader(new FileReader(querysFile));
        String line;
        List <String> queries = new ArrayList<String>();

        while ((line = reader.readLine()) != null) {
            queries.add(line);
        }

        RetrievalModel dirichelet = new Dirichlet(index, 1500);

        for(String query : queries){

//            List<QueryNode> termNodes =
        }

//        ProximityNode orderedWindow = new OrderedWindow(dirichelet, index, termNodes, 1);
//
//        List<DocumentScore> documentScores = new ArrayList<>();
//        for(int i = 1; i <= index.getDocCount(); i++){
//            double score = orderedWindow.score(i);
//            if(score != 0) {
//                DocumentScore documentScore = new DocumentScore(i, score);
//                documentScores.add(documentScore);
//            }
//        }

//        Collections.sort(documentScores);
//        int i = 0;
    }

}
