package retrievalmodel;


import dataobject.DocumentInfo;
import dataobject.Posting;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DocumentScorer {

    public float scoreDocument(int documentId, Map<String, List<Posting>> queryPostings, DocumentInfo documentInfo);

    public Map<Integer, Float> scoreDocuments(Set<Integer> documentIDs, Map<String, List<Posting>> queryPostings, DocumentInfo documentInfo);

}
