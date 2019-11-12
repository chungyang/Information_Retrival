package nodes;


import java.util.List;

public class AndNode extends QueryNode{

    public AndNode(List<QueryNode> children){
        
        this.children = children;
        this.resetChildren();
    }

    @Override
    public double score(int docid) {
        double score = 0;

        for(QueryNode child : children){
            score += child.score(docid);
        }

        return score;
    }
}
