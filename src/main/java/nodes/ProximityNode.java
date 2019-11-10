package nodes;


import index.Index;
import retrieval.RetrievalModel;

import java.util.*;

public abstract class ProximityNode extends QueryNode {


    protected List<QueryNode> children;
    protected Map<Integer, Integer> occurrences = new HashMap<>();
    protected RetrievalModel model;
    protected int totalOccurences;
    protected Index index;


    /**
     * Skip the posting lists to the specified docId. If specified docId
     * does not exist in all posting lists, it returns false, otherwise it returns
     * true.
     * @param docId
     * @return
     */
    public boolean nextCandidateDoc(int docId){

        Set<Integer> docIds = new HashSet<>();

        for(QueryNode child : children){
            docIds.add(child.skipTo(docId));
        }

        return docIds.size() == 1;
    }

    @Override
    public double score(int docid) {

        if(!occurrences.containsKey(docid)){
            return 0;
        }
        return model.scoreOccurrence(this.occurrences.get(docid), this.totalOccurences, index.getDocLength(docid));
    }

    public void resetChildren(){

        for(QueryNode child : children){
            child.resetNode();
        }
    }
}
