import DataObject.Posting;

import java.util.ArrayList;
import java.util.List;

public class DeltaEncoder {


    public static List<Posting> deltaEncode(List<Posting> postings){

        List<Posting> encodedPostings = new ArrayList<>();
        int baseDocID = postings.get(0).getDocumentId();

        for(Posting posting : postings){

            int encodedDocId = posting.getDocumentId() == baseDocID?
                    baseDocID : posting.getDocumentId() - baseDocID;

            baseDocID = posting.getDocumentId();

            Posting encodedPosting = new Posting(encodedDocId);

            List<Integer> positions = posting.getPositions();
            int basePosition = positions.get(0);

            for(int position : positions){
                int encodedPosition = basePosition == position? basePosition : position - basePosition;
                encodedPosting.addPosition(encodedPosition);
                basePosition = position;
            }
            encodedPostings.add(encodedPosting);
        }

        return encodedPostings;
    }
}
