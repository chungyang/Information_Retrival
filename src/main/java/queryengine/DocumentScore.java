package queryengine;

public class DocumentScore{
    protected int id;
    protected float score;

    DocumentScore(int id, float score){
        this.id = id;
        this.score = score;
    }
}