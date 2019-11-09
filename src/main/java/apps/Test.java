package apps;

import index.Index;
import index.InvertedIndex;
import index.PostingList;
import inference.OrderedWindow;
import inference.Window;
import retrieval.Dirichlet;
import retrieval.DocumentScore;
import retrieval.RetrievalModel;

import javax.print.Doc;
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

        List<PostingList> lists = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(querysFile));
        String line;
        List <String> queries = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            queries.add(line);
        }

        for(String query : queries.get(0).split("\\s+")){
            lists.add(index.getPostings(query));
        }

        RetrievalModel dirichelet = new Dirichlet(index, 1500);

        Window orderedWindow = new OrderedWindow(dirichelet, index);
        orderedWindow.getOccurences(lists, 1);

        List<DocumentScore> documentScores = new ArrayList<>();
        for(int i = 1; i <= index.getDocCount(); i++){
            double score = orderedWindow.score(i);
            if(score != 0) {
                DocumentScore documentScore = new DocumentScore(i, score);
                documentScores.add(documentScore);
            }
        }

        Collections.sort(documentScores);

    }

}
