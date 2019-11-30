package cluster;


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


        return similarity / Math.sqrt(d1 * d2);
    }
}
