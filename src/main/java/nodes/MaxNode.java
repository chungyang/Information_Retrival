package nodes;


import java.util.List;

public class MaxNode extends QueryNode{

    public MaxNode(List<QueryNode> children){

        this.children = children;
        this.resetChildren();
    }


    @Override
    public double score(int docid) {

        double max = Double.NEGATIVE_INFINITY;

        for(QueryNode child : children){
            double score = child.score(docid);
            max = Math.max(max, child.score(docid));
        }

        return max;
    }
}
