package cluster;


import index.Index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cluster {

    protected List<Integer> members;
    protected Linkage linkage;
    protected SimilarityMethod sim;
    protected Index index;
    protected int id;



    public Cluster(int id, Index index, Linkage linkage, SimilarityMethod sim){

        this.id = id;
        this.members = new ArrayList<>();
        this.sim = sim;
        this.index = index;
        this.linkage = linkage;
    }


    public void add(int docid){
        this.members.add(docid);
    }


    public double score(DocumentVector vector){


        switch(linkage){

            case SINGLE:
                return scoreSingle(vector);

            case COMPLETE:
                return scoreComplete(vector);

            case AVERAGE:
                return scoreAverage(vector);

            case MEAN:
                return scoreMean(vector);

        }

        return scoreSingle(vector);
    }


    private double scoreSingle(DocumentVector vector){

        double minScore = Double.MAX_VALUE;

        for(int member : members){
            double score = sim.getSimilarity(vector, index.getDocumentVector(member));
            minScore = Math.min(score, minScore);
        }

        return minScore;
    }


    private double scoreComplete(DocumentVector vector){

        double maxScore = 0;

        for(int member : members){
            double score = sim.getSimilarity(vector, index.getDocumentVector(member));
            maxScore = Math.max(score, maxScore);
        }

        return maxScore;
    }


    private double scoreAverage(DocumentVector vector){

        double score = 0;

        for(int member : members){
            score += sim.getSimilarity(vector, index.getDocumentVector(member));
        }

        return score / members.size();
    }


    private double scoreMean(DocumentVector vector){

        Map<String, Double> centroidVector = new HashMap<>();

        for(int member : members){

            DocumentVector memberVector = index.getDocumentVector(member);

            for(String term : memberVector.getKeySet()){
                double value = memberVector.getOrDefault(term);
                double centroidValue = centroidVector.getOrDefault(term, 0.0);
                centroidValue += value / members.size();
                centroidVector.put(term, centroidValue);
            }
        }

        return sim.getSimilarity(vector, new DocumentVector(0, centroidVector));

    }

    public int getId(){
        return this.id;
    }

    public List<Integer> getMembers(){
        return this.members;
    }
}
