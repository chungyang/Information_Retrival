package inference;

import index.Index;
import index.PostingList;
import retrieval.RetrievalModel;

import java.util.List;


public class UnOrderedWindow extends Window {


    public UnOrderedWindow(RetrievalModel model, Index index){
        
    }


    @Override
    public void getOccurences(List<PostingList> postingLists, int windowSize) {

    }

    @Override
    public double score(int docid) {
        return 0;
    }
}
