package retrieval;

public class DocumentScore implements Comparable<DocumentScore>{

    protected int id;
    protected double score;

    public DocumentScore(int id, double score){
        this.id = id;
        this.score = score;
    }

    public int getId(){
        return this.id;
    }

    public double getScore(){
        return this.score;
    }


    @Override
    public int compareTo(DocumentScore o) {
        double compareResult = o.getScore() - this.getScore();
        return compareResult >= 0? (int) Math.ceil(compareResult) : (int) Math.floor(compareResult);
    }
}
