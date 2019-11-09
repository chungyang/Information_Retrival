package inference;

import index.Index;
import index.Posting;
import index.PostingList;
import retrieval.RetrievalModel;

import java.util.ArrayList;
import java.util.List;

public class OrderedWindow extends Window{


    public OrderedWindow(RetrievalModel model, Index index){
        this.model = model;
        this.index = index;
    }

    @Override
    public void getOccurences(List<PostingList> postingLists, int windowSize) {

        for(int docid = 1; docid < index.getDocCount(); docid++) {

            int occurences = 0;

            if (nextCandiate(postingLists, docid)) {

                List<Posting> postings = new ArrayList<>();

                for (int i = 0; i < postingLists.size(); i++) {

                    Posting posting = postingLists.get(i).getCurrentPosting();
                    posting.resetPositionIndex();
                    postings.add(posting);
                }

                Posting firstPosting = postings.get(0);

                for (int i = 0; i < firstPosting.getPositionSize(); i++) {

                    if (occurInWindowSize(postings, windowSize, firstPosting.getCurrentPosition(), 0)) {
                        occurences++;
                    }

                    firstPosting.skipToNextPosition(firstPosting.getCurrentPosition());
                }
            }

            if(occurences > 0){
                occurrences.put(docid, occurences);
                totalOccurences += occurences;
            }
        }

    }


    @Override
    public double score(int docid) {

        if(!occurrences.containsKey(docid)){
            return 0;
        }

        return model.scoreOccurrence(occurrences.get(docid), totalOccurences, index.getDocLength(docid));
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
