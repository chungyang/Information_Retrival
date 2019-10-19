package retrievalmodel;

import dataobject.DocumentInfo;
import dataobject.Posting;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class Dirichlet implements DocumentScorer{
    @Override
    public float scoreDocument(int documentId, Map<String, List<Posting>> queryPostings, Map<String, Integer> queryFrequencies, DocumentInfo documentInfo, int numebrOfDoc, float averageDocLength) {
        return 0;
    }
    

    @Override
    public Map<Integer, Float> scoreDocuments(Set<Integer> documentIDs, Map<String, List<Posting>> queryPostings, Map<String, Integer> queryFrequencies, Map<String, DocumentInfo> documentInfo, float averageDocLength) {
        return null;
    }
}
