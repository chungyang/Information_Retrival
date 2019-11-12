package nodes;

import index.Posting;

public class FilterRequiredNode extends QueryNode{

    protected ProximityNode filter;
    protected QueryNode queryNode;

    public FilterRequiredNode(ProximityNode filter, QueryNode query){

        this.filter = filter;
        this.queryNode = query;
    }


    @Override
    public Posting nextCandidate(){

        if(queryNode.hasMore() &&
                filter.skipTo(queryNode.nextCandidateId()) == queryNode.nextCandidateId()){
            return queryNode.nextCandidate();
        }

        return null;
    }


    @Override
    public double score(int docid) {

        if(filter.skipTo(docid) == docid){
            return this.queryNode.score(docid);
        }

        return 0;
    }
}
