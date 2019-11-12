package nodes;


import java.util.List;

public class OrNode extends QueryNode{

    public OrNode(List<QueryNode> children){

        this.children = children;
        this.resetChildren();
    }

    @Override
    public double score(int docid) {

        double score = 0;

        for(QueryNode child : children){
            score += Math.log(1 - Math.exp(child.score(docid)));
        }
        return Math.log(1 - Math.exp(score));
    }
}
