package retrievalmodel;


public class DocumentScorerFactory {

    public static DocumentScorer getDocumentScorer(String scorerType){

        DocumentScorer documentScorer = null;

        switch (scorerType){

            case "Bm25":
                documentScorer = new Bm25();
                break;

            case "Dirichlet":
                break;

            case "Jelinik-Mercer":
                break;

            default:
                documentScorer = new Bm25();
                break;
        }

        return documentScorer;
    }
}
