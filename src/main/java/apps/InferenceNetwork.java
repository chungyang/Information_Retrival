package apps;

import index.Index;
import index.InvertedIndex;
import nodes.*;
import retrieval.Dirichlet;
import retrieval.DocumentScore;
import retrieval.RetrievalModel;
import utilities.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InferenceNetwork {

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
            DocumentScore documentScore = new DocumentScore(i, score);
            documentScores.add(documentScore);

        }
        Collections.sort(documentScores);

        List<DocumentScore> topK = new ArrayList<>();

        for(int j = 0; j < Math.min(k, documentScores.size()); j++){
            if(documentScores.get(j).getScore() != Double.NEGATIVE_INFINITY){
                topK.add(documentScores.get(j));
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

        Utils.deleteFile("od1.trecrun");
        Utils.deleteFile("uw.trecrun");
        Utils.deleteFile("sum.trecrun");
        Utils.deleteFile("and.trecrun");
        Utils.deleteFile("or.trecrun");
        Utils.deleteFile("max.trecrun");


        RetrievalModel dirichelet = new Dirichlet(index, 1500);
        int id = 1;

        for(String query : queries){

            String queryid = "Q" + id;
            List<QueryNode> termNodes = getTermNodes(query, dirichelet, index);

            ProximityNode orderedWindow = new OrderedWindow(dirichelet, index, termNodes, 1);
            List<DocumentScore> topK = runQuery(orderedWindow, k, index.getDocCount());
            Utils.writeTREC("od1.trecrun", true, topK, index.getSceneMap(), "chungtingyang-jm-dir", queryid, "1500");

            ProximityNode unOrderedWindow = new UnOrderedWindow(dirichelet, index, termNodes, query.split("\\s+").length * 3);
            topK = runQuery(unOrderedWindow, k, index.getDocCount());
            Utils.writeTREC("uw.trecrun", true, topK, index.getSceneMap(), "chungtingyang-jm-dir", queryid, "1500");

            QueryNode sumNode = new SumNode(termNodes);
            topK = runQuery(sumNode, k, index.getDocCount());
            Utils.writeTREC("sum.trecrun", true, topK, index.getSceneMap(), "chungtingyang-jm-dir", queryid, "1500");

            QueryNode andNode = new AndNode(termNodes);
            topK = runQuery(andNode, k, index.getDocCount());
            Utils.writeTREC("and.trecrun", true, topK, index.getSceneMap(), "chungtingyang-jm-dir", queryid, "1500");

            QueryNode orNode = new OrNode(termNodes);
            topK = runQuery(orNode, k, index.getDocCount());
            Utils.writeTREC("or.trecrun", true, topK, index.getSceneMap(), "chungtingyang-jm-dir", queryid, "1500");

            QueryNode maxNode = new MaxNode(termNodes);
            topK = runQuery(maxNode, k, index.getDocCount());
            Utils.writeTREC("max.trecrun", true, topK, index.getSceneMap(), "chungtingyang-jm-dir", queryid, "1500");

            id++;
        }
    }

}
