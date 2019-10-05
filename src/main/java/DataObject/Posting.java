package DataObject;

import java.util.ArrayList;
import java.util.List;

public class Posting {



    private List<Integer> positions;

    private int documentId;

    public Posting(int sceneNumber){

        this.documentId = sceneNumber;
        this.positions = new ArrayList<>();

    }

    @Override
    public boolean equals(Object o){

        if(o == null){
            return false;
        }

        if(this == o){
            return true;
        }

        Posting posting = (Posting) o;

        return posting.getPositions().equals(this.positions) && posting.getDocumentId() == this.documentId;
    }


    @Override
    public int hashCode(){
        int result = Integer.hashCode(this.documentId);

        for(int position : positions){
            result = 31 * result + Integer.hashCode(position);
        }

        return result;
    }

    public void addPosition(int position){
        this.positions.add(position);
    }

    public List<Integer> getPositions(){
        return this.positions;
    }

    public int getDocumentId(){
        return this.documentId;
    }

}
