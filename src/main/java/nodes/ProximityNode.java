package nodes;


import index.Index;
import retrieval.RetrievalModel;

import java.util.*;

public abstract class ProximityNode extends QueryNode {


    protected Map<Integer, Integer> occurrences = new HashMap<>();
    protected RetrievalModel model;
    protected int totalOccurences;
    protected Index index;


    public int nextCandidateDoc(int docId){

        List<Integer> docs = new ArrayList<>();

        for(QueryNode child : children){
            int doc = child.skipTo(docId);

            if(doc == 0){
                return 0;
            }

            docs.add(doc);
        }
        int firstDoc = docs.get(0);

        for(int doc : docs){
            if(firstDoc != doc){
                return 0;
            }
        }

        return firstDoc;
    }

    @Override
    public double score(int docid) {

        if(!occurrences.containsKey(docid)){
            return 0;
        }
        return model.scoreOccurrence(this.occurrences.get(docid), this.totalOccurences, index.getDocLength(docid));
    }

}
