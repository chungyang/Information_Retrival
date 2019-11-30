package apps;

import cluster.*;
import index.Index;
import index.InvertedIndex;

import java.util.HashMap;
import java.util.Map;

public class OnlineCluster {

    public static void main(String[] args){

        Index index = new InvertedIndex();
        index.load(Boolean.parseBoolean(args[0]));
        Linkage linkage = Linkage.valueOf(args[1]);
        double threshold = Double.valueOf(args[2]);

        Map<Integer, Cluster> clusters = new HashMap<>();
        int clusterid = 0;
        SimilarityMethod sim = CosineSimilarity.getCosineSimilarity();
        int limit = index.getDocCount();

        for(int docid = 1; docid <= limit; docid++){
            double score = 0.0;
            int best = -1;

            for(Cluster c : clusters.values()){
                int cId = c.getId();
                double s = c.score(index.getDocumentVector(docid));

                if(s > score){
                    score = s;
                    best = cId;
                }
            }

            if(score > threshold){
                clusters.get(best).add(docid);
            }
            else{
                clusterid++;
                Cluster cluster = new Cluster(clusterid, index, linkage, sim);
                cluster.add(docid);
                clusters.put(clusterid, cluster);
            }
        }

        int hi = 0;
    }
}
