package nodes;

import java.util.List;


public class SumNode extends QueryNode{

    public SumNode(List<QueryNode> children){

        this.children = children;
        this.resetChildren();
    }


    @Override
    public double score(int docid) {

        double score = 0;

        for(QueryNode child : children){
            score += Math.exp(child.score(docid));
        }

        return Math.log(score / children.size());
    }
}
