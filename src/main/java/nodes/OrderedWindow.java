package nodes;

import index.Index;
import index.Posting;
import retrieval.RetrievalModel;

import java.util.ArrayList;
import java.util.List;

public class OrderedWindow extends Window {


    public OrderedWindow(RetrievalModel model, Index index, List<QueryNode> children,
                         int windowSize){
        this.model = model;
        this.index = index;
        this.children = children;
        this.resetChildren();
        this.getOccurences(windowSize);
    }

    @Override
    public void getOccurences(int windowSize) {

        for(int docid = 1; docid < index.getDocCount(); docid++) {

            int occurrence = 0;
            int nextCommonDoc = nextCandidateDoc(docid);

            if(occurrences.containsKey(nextCommonDoc)){
                continue;
            }

            if (nextCommonDoc != 0) {

                List<Posting> postings = new ArrayList<>();

                for(QueryNode child : this.children){
                    if(!child.hasMore()) {
                        return;
                    }
                    postings.add(child.nextCandidate());
                }

                Posting firstPosting = postings.get(0);

                for (int i = 0; i < firstPosting.getPositionSize(); i++) {

                    if (occurInWindowSize(postings, windowSize, firstPosting.getCurrentPosition(), 0)) {
                        occurrence++;
                    }

                    firstPosting.skipToNextPosition(firstPosting.getCurrentPosition());
                }

                if(occurrence > 0){
                    occurrences.put(nextCommonDoc, occurrence);
                    totalOccurences += occurrence;
                }
            }

        }

    }

    /**
     * This method recursively check if there is a position in a posting that's within the
     * specified windowSize from the position in the previous posting.
     * @param postings
     * @param windowSize
     * @param listIndex
     */
    private boolean occurInWindowSize(List<Posting> postings, int windowSize, int position,
                                      int listIndex){

        if(postings.size() == 1){
            return true;
        }

        if(listIndex >= postings.size()){
            return true;
        }

        Posting currentPosting = postings.get(listIndex);

        if(listIndex == 0){
            int currentPosition = currentPosting.getCurrentPosition();
            return occurInWindowSize(postings, windowSize, currentPosition, listIndex + 1);
        }

        currentPosting.skipToNextPosition(position);

        if(currentPosting.getCurrentPosition() != position + windowSize){
            return false;
        }

        return occurInWindowSize(postings, windowSize, currentPosting.getCurrentPosition(), listIndex + 1);
    }

}
