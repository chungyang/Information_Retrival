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

    public static void main(String[] args) throws IOException {

        Index index = new InvertedIndex();
        boolean compressed = Boolean.parseBoolean(args[0]);
        String querysFile = args[1];
        index.load(compressed);


        BufferedReader reader = new BufferedReader(new FileReader(querysFile));
        String line;
        List <String> queries = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            queries.add(line);
        }

        List<QueryNode> termNodes = new ArrayList<>();

        for(String query : queries.get(0).split("\\s+")){
            termNodes.add(new TermNode(query, index));
        }

        RetrievalModel dirichelet = new Dirichlet(index, 1500);
        ProximityNode proximityNode = new UnOrderedWindow(dirichelet, index, termNodes, 1);

        List<DocumentScore> documentScores = new ArrayList<>();
        for(int i = 1; i <= index.getDocCount(); i++){
            double score = proximityNode.score(i);
            if(score != 0) {
                DocumentScore documentScore = new DocumentScore(i, score);
                documentScores.add(documentScore);
            }
        }

        Collections.sort(documentScores);
        int i = 0;
    }

}
