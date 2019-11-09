package inference;


import index.Index;
import index.PostingList;
import retrieval.RetrievalModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Window {

    protected RetrievalModel model;
    protected Index index;
    protected int totalOccurences;
    protected Map<Integer, Integer> occurrences = new HashMap<>();

    public abstract void getOccurences(List<PostingList> postingLists, int windowSize);

    public abstract double score(int docid);

    /**
     * Skip the posting lists to the specified docId. If specified docId
     * does not exist in all posting lists, it returns false, otherwise it returns
     * true.
     * @param postingLists
     * @param docId
     * @return
     */
    protected boolean nextCandiate(List<PostingList> postingLists, int docId){

        boolean containsCommonDocId = true;

        for(PostingList postingList : postingLists){
            postingList.skipTo(docId);
        }

        int commonDocId = postingLists.get(0).getCurrentPosting().getDocId();

        for(PostingList postingList : postingLists){
            containsCommonDocId &= postingList.getCurrentPosting().getDocId() == commonDocId;
        }

        return containsCommonDocId;
    }

}
