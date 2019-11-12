package nodes;


import java.util.List;

public class NotNode extends QueryNode{


    public NotNode(List<QueryNode> children){

        this.children = children;
        this.resetChildren();
    }


    @Override
    public double score(int docid) {
        return Math.log(1 - Math.exp(children.get(0).score(docid)));
    }
}
