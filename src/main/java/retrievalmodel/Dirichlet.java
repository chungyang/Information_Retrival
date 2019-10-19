package retrievalmodel;

import dataobject.DocumentInfo;
import dataobject.DocumentStats;
import dataobject.Posting;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class Dirichlet implements DocumentScorer{
    @Override
    public float scoreDocument(int documentId, Map<String, List<Posting>> queryPostings,
                               Map<String, Integer> queryFrequencies, DocumentStats documentStats) {
        return 0;
    }

}
