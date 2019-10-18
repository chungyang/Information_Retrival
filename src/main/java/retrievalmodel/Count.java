package retrievalmodel;

import dataobject.DocumentInfo;
import dataobject.Posting;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Count implements DocumentScorer {
    @Override
    public float scoreDocument(int documentId, Map<String, List<Posting>> queryPostings, Map<String, Integer> queryFrequencies, DocumentInfo documentInfo, int numebrOfDoc, float averageDocLength) {
        //                for(Map.Entry<String, List<Posting>> entry : queryPostings.entrySet()){
//
//                    List<Posting> postings = entry.getValue();
//                    if(postings.isEmpty()){
//                        continue;
//                    }
//                    Posting lastPosting = postings.get(postings.size() - 1);
//
//                    if(lastPosting.getDocumentId() == i){
//                        int score = documentScores.getOrDefault(i, 0);
//                        score += lastPosting.getPositions().size();
//                        documentScores.put(i, score);
//                        postings.remove(postings.size() - 1);
//                    }
//                }
        return 0;

    }

    @Override
    public Map<Integer, Float> scoreDocuments(Set<Integer> documentIDs, Map<String, List<Posting>> queryPostings, Map<String, Integer> queryFrequencies, Map<String, DocumentInfo> documentInfo, float averageDocLength) {
        return null;
    }
}
