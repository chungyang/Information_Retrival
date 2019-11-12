package nodes;


import index.Index;
import index.Posting;
import retrieval.RetrievalModel;

import java.util.ArrayList;
import java.util.List;

public class TermNode extends ProximityNode{

    protected String term;

    public TermNode(RetrievalModel model, String term, Index index){
        this.term = term;
        this.postingList = index.getPostings(term);
        this.children = new ArrayList<>();
        this.model = model;
        this.index = index;
        this.getOccurences();
    }

    public TermNode(RetrievalModel model, String term, Index index, List<QueryNode> children){
        this.term = term;
        this.postingList = index.getPostings(term);
        this.children = children;
        this.model = model;
        this.index = index;
        this.getOccurences();
    }

    public void getOccurences(){

        while(this.postingList.hasMore()){

            Posting posting = this.postingList.getCurrentPosting();
            this.occurrences.put(posting.getDocId(), posting.getTermFreq());
            this.totalOccurences += posting.getTermFreq();
            this.postingList.skipTo(posting.getDocId() + 1);
        }
    }
}
