package retrievalmodel;


import dataobject.DocumentInfo;
import dataobject.DocumentStats;
import dataobject.Posting;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DocumentScorer {

    public float scoreDocument(int documentId, Map<String, List<Posting>> queryPostings,
                               Map<String, Integer> queryFrequencies,
                               DocumentStats documentStats);

    public Map<Integer, Float> scoreDocuments(Set<Integer> documentIDs, Map<String, List<Posting>> queryPostings,
                                              Map<String, Integer> queryFrequencies,
                                              Map<String, DocumentInfo> documentInfo, float averageDocLength);

}
