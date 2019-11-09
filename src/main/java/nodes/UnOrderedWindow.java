package nodes;

import index.Index;
import index.PostingList;
import nodes.Window;
import retrieval.RetrievalModel;

import java.util.List;


public class UnOrderedWindow extends Window {


    public UnOrderedWindow(RetrievalModel model, Index index, List<QueryNode> children,
                         int windowSize){
        this.model = model;
        this.index = index;
        this.children = children;
        this.getOccurences(windowSize);
    }


    @Override
    public double score(int docid) {
        return 0;
    }

    @Override
    public void getOccurences(int windowSize) {

    }
}
