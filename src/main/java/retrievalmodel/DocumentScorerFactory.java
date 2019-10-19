package retrievalmodel;


public class DocumentScorerFactory {

    public static DocumentScorer getDocumentScorer(ScoreType scorerType){

        DocumentScorer documentScorer = null;

        switch (scorerType){

            case BM25:
                documentScorer = new Bm25();
                break;

            case DIRICHLET:
                documentScorer = new Dirichlet();

            case JELINEKMERCER:
                documentScorer = new JelinekMercer();
                break;

            default:
                documentScorer = new Bm25();
                break;
        }

        return documentScorer;
    }
}
