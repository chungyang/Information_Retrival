package cluster;


import java.util.HashMap;
import java.util.Map;

public class CosineSimilarity implements SimilarityMethod{

    private static CosineSimilarity cosineSimilarity;



    public static CosineSimilarity getCosineSimilarity(){

        if(cosineSimilarity == null){
            cosineSimilarity = new CosineSimilarity();
        }

        return cosineSimilarity;
    }


    private CosineSimilarity(){}


    @Override
    public double getSimilarity(DocumentVector v1, DocumentVector v2){

        double similarity = 0.0;
        double d1 = 0.0;

        for(String term : v1.getKeySet()){
            similarity += v1.getOrDefault(term) * v2.getOrDefault(term);
            d1 += Math.pow(v1.getOrDefault(term), 2);
        }

        double d2 = 0.0;
        for(String term : v2.getKeySet()){
            d2 += Math.pow(v2.getOrDefault(term), 2);
        }


        return similarity / Math.sqrt(Math.max(1, d1 * d2));
    }


    public static void main(String[] args){
        Map<String, Double> v1 = new HashMap<>();
        v1.put("a", 2.0);
        v1.put("b", 3.0);
        v1.put("c", 5.0);
        DocumentVector d1 = new DocumentVector(1, v1);

        Map<String, Double> v2 = new HashMap<>();

        DocumentVector d2 = new DocumentVector(2, v2);

        CosineSimilarity c = getCosineSimilarity();
        System.out.print(c.getSimilarity(d1, d2));

    }
}
