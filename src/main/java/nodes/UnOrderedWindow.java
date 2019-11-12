package nodes;

import index.Index;
import index.Posting;
import index.PostingList;
import retrieval.RetrievalModel;

import java.util.*;


public class UnOrderedWindow extends Window {

    public UnOrderedWindow(RetrievalModel model, Index index, List<QueryNode> children){

        int maxDocLength = 0;

        for(int i = 1; i <= index.getDocCount(); i++){
            if(index.getDocLength(i) > maxDocLength){
                maxDocLength = index.getDocLength(i);
            }
        }

        this.model = model;
        this.index = index;
        this.children = children;
        this.resetChildren();
        this.getOccurences(maxDocLength);
    }

    public UnOrderedWindow(RetrievalModel model, Index index, List<QueryNode> children,
                           int windowSize){
        this.model = model;
        this.index = index;
        this.children = children;
        this.postingList = new PostingList();
        this.resetChildren();
        this.getOccurences(windowSize);
    }

    @Override
    public void getOccurences(int windowSize) {

        for(int docid = 1; docid <= index.getDocCount(); docid++){


            int commonDoc = nextCandidateDoc(docid);

            if(occurrences.containsKey(commonDoc)){
                continue;
            }

            if(commonDoc != 0) {

                int occurence = 0;
                List<Integer> positions = new ArrayList<>();

                while(!isDone()){
                    int startPosition = minPosition();

                    if(occurInWindowSize(windowSize, startPosition)) {
                        positions.add(startPosition);
                        occurence++;
                    }
                }

                totalOccurences += occurence;
                if(occurence > 0) {
                    this.occurrences.put(commonDoc, occurence);
                    Posting windowPosting = new Posting(commonDoc, positions);
                    this.postingList.add(windowPosting);

                }
            }
        }
    }

    private boolean occurInWindowSize(int windowSize, int startPosition){

        Set<Integer> childrenContainsTerm = new HashSet<>();

        for(int i = 0; i < children.size(); i++){

            Posting posting = children.get(i).nextCandidate();

            while(posting.hasMore() && posting.getCurrentPosition() <= startPosition + windowSize - 1){
                childrenContainsTerm.add(i);
                posting.skipToNextPosition(startPosition + windowSize - 1);
            }
        }


        return childrenContainsTerm.size() == children.size();
    }

    private int minPosition(){

        int min = Integer.MAX_VALUE;

        for(int i = 0; i < children.size(); i++){

            int childPostingPosition = children.get(i).nextCandidate().getCurrentPosition();
            min = Math.min(min, childPostingPosition);
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
