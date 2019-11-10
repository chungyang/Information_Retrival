package nodes;


import index.Index;
import retrieval.RetrievalModel;

import java.util.ArrayList;
import java.util.List;

public class TermNode extends ProximityNode{

    protected String term;

    public TermNode(String term, Index index){
        this.term = term;
        this.postingList = index.getPostings(term);
        this.children = new ArrayList<>();
    }

    public TermNode(String term, Index index, List<QueryNode> children){
        this.term = term;
        this.postingList = index.getPostings(term);
        this.children = children;
    }

}
