package nodes;


import index.Posting;
import index.PostingList;

import java.util.List;

public abstract class QueryNode {

    protected List<QueryNode> children;
    protected PostingList postingList;
    protected int docIndex = 0;


    public abstract double score(int docid);

    public int nextCandidateId(){

        if(hasMore()){
            return postingList.get(docIndex).getDocId();
        }
        return 0;
    }

    public Posting nextCandidate(){

        if(hasMore()){
            return postingList.get(docIndex);
        }
        return null;
    }

    public int skipTo(int docid){

        while(hasMore() && docid > postingList.get(docIndex).getDocId()){
            docIndex++;
        }

        if(hasMore()){
            return postingList.get(docIndex).getDocId();
        }

        return 0;
    }

    public boolean hasMore(){
        if(docIndex >= postingList.documentCount()){
            return false;
        }
        return true;
    }

    public void resetNode(){
        postingList.startIteration();
        docIndex = 0;
    }

    public void resetChildren(){

        children.forEach(QueryNode::resetNode);
    }
}
