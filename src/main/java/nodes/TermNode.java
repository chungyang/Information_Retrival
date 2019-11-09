package nodes;


import index.Index;
import retrieval.RetrievalModel;

public class TermNode extends ProximityNode{

    protected String term;

    public TermNode(String term, Index index){
        this.term = term;
        this.postingList = index.getPostings(term);
    }

}
