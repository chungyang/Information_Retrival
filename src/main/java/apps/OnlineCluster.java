package apps;

import cluster.*;
import index.Index;
import index.InvertedIndex;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

public class OnlineCluster {

    public static void main(String[] args) {

        Index index = new InvertedIndex();
        index.load(Boolean.parseBoolean(args[0]));
        Linkage linkage = Linkage.valueOf(args[1]);


        BigDecimal lastThreshold = new BigDecimal("1.0");
        BigDecimal incrementValue = new BigDecimal("0.05");
        for (BigDecimal threshold = new BigDecimal("0.05"); threshold.compareTo(lastThreshold) != 0; threshold = threshold.add(incrementValue)) {

            Map<Integer, Cluster> clusters = new HashMap<>();
            int clusterid = 0;
            SimilarityMethod sim = CosineSimilarity.getCosineSimilarity();
            int limit = index.getDocCount();

            for (int docid = 1; docid <= limit; docid++) {
                double score = 0.0;
                int best = -1;

                for (Cluster c : clusters.values()) {
                    int cId = c.getId();
                    double s = c.score(index.getDocumentVector(docid));
                    if(s > 1){
                        System.out.println("wrong");
                    }
                    if (s > score) {
                        score = s;
                        best = cId;
                    }
                }

                if (score > threshold.doubleValue()) {
                    clusters.get(best).add(docid);
                } else {
                    clusterid++;
                    Cluster cluster = new Cluster(clusterid, index, linkage, sim);
                    cluster.add(docid);
                    clusters.put(clusterid, cluster);
                }
            }

            String filename = "cluster-<" + threshold.doubleValue() + ">.out";
            int firstRangeCount = 0;
            int secondRangeCount = 0;
            int thirdRangeCount = 0;
            try(PrintWriter printWriter = new PrintWriter(filename)){

                System.out.println("Number Of Clusters: " + clusters.size());

                for(Map.Entry<Integer, Cluster> entry : clusters.entrySet()){

                    int clusterSize = entry.getValue().getMembers().size();
                    if(clusterSize > 0 && clusterSize <= 10){
                        firstRangeCount++;
                    }
                    else if(clusterSize > 10 && clusterSize <= 100){
                        secondRangeCount++;
                    }
                    else if(clusterSize > 100 && clusterSize <= 748){
                        thirdRangeCount++;
                    }

                    for(int docid : entry.getValue().getMembers()) {
                        printWriter.print("<" + entry.getValue().getId() + "> <" + docid + ">\n");
                    }
                }

                System.out.println("Size(1,10]: " + firstRangeCount);
                System.out.println("Size(10,100]: " + secondRangeCount);
                System.out.println("Size(100,748]: " + secondRangeCount);



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
