package cluster;


import java.util.ArrayList;
import java.util.List;

public class Cluster {

    List<DocumentVector> members;




    public Cluster(){
        members = new ArrayList<>();
    }


    public void add(DocumentVector vector){
        this.members.add(vector);
    }

    public void score(DocumentVector vector){

    }
}
