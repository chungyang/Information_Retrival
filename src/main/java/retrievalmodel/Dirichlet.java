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
                               Map<String, Integer> queryFrequencies,
                               DocumentStats documentStats, Map<Parameters, Float> params) {

        float score = 0;
        float mu = params.get(Parameters.MU);
        float c = documentStats.getTotalDocumentLength();
        float d = documentStats.getDocumentInfos().get(String.valueOf(documentId)).getDocumentLength();

        for (Map.Entry<String, List<Posting>> entry : queryPostings.entrySet()) {
            float ci = 0;
            float fqi = 0;
            for(Posting posting : entry.getValue()){
                ci += posting.getPositions().size();

                if(posting.getDocumentId() == documentId){
                    fqi = posting.getPositions().size();
                    break;
                }
            }


            float termScore = (float) Math.log((fqi + mu * (ci / c)) / (d + mu));
            float termFrequency = queryFrequencies.get(entry.getKey());
            score += termFrequency * termScore;

        }

        return score;
    }

}
