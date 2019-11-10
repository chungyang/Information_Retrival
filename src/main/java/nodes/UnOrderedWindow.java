package nodes;

import index.Index;
import index.Posting;
import retrieval.RetrievalModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UnOrderedWindow extends Window {


    public UnOrderedWindow(RetrievalModel model, Index index, List<QueryNode> children,
                           int windowSize){
        this.model = model;
        this.index = index;
        this.children = children;
        this.resetChildren();
        this.getOccurences(windowSize);
    }

    @Override
    public void getOccurences(int windowSize) {

        for(int docid = 1; docid < index.getDocCount(); docid++){


            int commonDoc = nextCandidateDoc(docid);

            if(occurrences.containsKey(commonDoc)){
                continue;
            }

            if(commonDoc != 0) {

                int occurence = 0;

                while(!isDone()){
                    if(occurInWindowSize(windowSize)) {
                        occurence++;
                    }
                }

                totalOccurences += occurence;
                if(occurence > 0) {
                    this.occurrences.put(commonDoc, occurence);
                }
            }
        }

    }

    private boolean occurInWindowSize(int windowSize){

        int minPosition = minPosition();
        Set<Integer> childrenContainsTerm = new HashSet<>();

        for(int i = 0; i < children.size(); i++){

            Posting posting = children.get(i).nextCandidate();

            while(posting.hasMore() && posting.getCurrentPosition() <= minPosition + windowSize - 1){
                childrenContainsTerm.add(i);
                posting.skipToNextPosition(minPosition + windowSize - 1);
            }
        }


        return childrenContainsTerm.size() == children.size();
    }

    private int minPosition(){

        int min = Integer.MAX_VALUE;

        for(int i = 0; i < children.size(); i++){

            int childPostingPosition = children.get(i).nextCandidate().getCurrentPosition();

            if (childPostingPosition < min) {
                min = childPostingPosition;
            }

        }

        return min;
    }

    private boolean isDone(){

        boolean done = false;

        for(QueryNode child : children){
            Posting p = child.nextCandidate();

            if(p == null){
                return true;
            }
            done |= !p.hasMore();
        }

        return done;
    }

}
