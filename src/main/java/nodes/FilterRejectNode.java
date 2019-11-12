package nodes;


import index.Posting;

public class FilterRejectNode extends QueryNode{

    protected ProximityNode filter;
    protected QueryNode queryNode;

    public FilterRejectNode(ProximityNode filter, QueryNode queryNode){

        this.filter = filter;
        this.queryNode = queryNode;
    }


    @Override
    public Posting nextCandidate(){

        if(queryNode.hasMore() &&
                filter.skipTo(queryNode.nextCandidateId()) != queryNode.nextCandidateId()){
            return queryNode.nextCandidate();
        }

        return null;
    }

    @Override
    public double score(int docid) {

        if(filter.skipTo(docid) != docid){
            return this.queryNode.score(docid);
        }

        return 0;
    }
}
