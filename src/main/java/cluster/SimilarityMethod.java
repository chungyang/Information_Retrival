package cluster;


public interface SimilarityMethod {

     double getSimilarity(DocumentVector v1, DocumentVector v2);
}
