package retrievalmodel;

import dataobject.Corpus;
import dataobject.DocumentInfo;
import dataobject.DocumentStats;
import dataobject.Posting;
import jsonutil.JsonParser;
import utilities.Utils;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class Bm25 implements DocumentScorer {



    @Override
    public float scoreDocument(int documentId, Map<String, List<Posting>> queryPostings,
                               Map<String, Integer> queryFrequencies,
                               DocumentStats documentStats, Map<Parameters, Float> params) {

        float score = 0f;
        float k1 = params.get(Parameters.K1);
        float k2 = params.get(Parameters.K2);
        float b = params.get(Parameters.B);
        float dl = documentStats.getDocumentInfos().get(String.valueOf(documentId)).getDocumentLength();
        float numebrOfDoc = documentStats.getDocumentInfos().size();
        float k = k1 * ((1 - b) + b * (dl / documentStats.getAverageDocumentLength()));

        for(Map.Entry<String, List<Posting>> entry : queryPostings.entrySet()){

            float fi;
            float ni = entry.getValue().size();
            int qfi = queryFrequencies.get(entry.getKey());

            for(Posting posting : entry.getValue()){

                if(posting.getDocumentId() == documentId){
                    fi = posting.getPositions().size();
                    score += queryFrequencies.get(entry.getKey()) * Math.log((1 / ((ni + 0.5) / (numebrOfDoc - ni + 0.5))))
                            * (k1 + 1) * fi / (k + fi) * (k2 + 1) * qfi / (k2 + qfi);
                    break;

                }
            }
        }


        return score;
    }

}
