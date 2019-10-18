package retrievalmodel;

import dataobject.DocumentInfo;
import dataobject.Posting;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chungyang on 10/17/19.
 */
public class Bm25 implements DocumentScorer {


    @Override
    public float scoreDocument(int documentId, Map<String, List<Posting>> queryPostings, DocumentInfo documentInfo) {
        return 0;
    }

    @Override
    public Map<Integer, Float> scoreDocuments(Set<Integer> documentIDs, Map<String, List<Posting>> queryPostings, DocumentInfo documentInfo) {
        return null;
    }
}
