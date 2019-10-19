package retrievalmodel;

import dataobject.DocumentInfo;
import dataobject.DocumentStats;
import dataobject.Posting;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class JelinekMercer implements DocumentScorer{


    @Override
    public float scoreDocument(int documentId, Map<String, List<Posting>> queryPostings,
                               Map<String, Integer> queryFrequencies, DocumentStats documentStats) {

        float score = 0;
        float lamda = 0.2f;
        float cqi;

        for (Map.Entry<String, List<Posting>> entry : queryPostings.entrySet()) {

        }


        return score;
    }

    @Override
    public Map<Integer, Float> scoreDocuments(Set<Integer> documentIDs, Map<String, List<Posting>> queryPostings, Map<String, Integer> queryFrequencies, Map<String, DocumentInfo> documentInfo, float averageDocLength) {
        return null;
    }
}
