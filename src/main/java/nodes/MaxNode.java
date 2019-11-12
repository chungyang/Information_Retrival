package nodes;


import java.util.List;

public class MaxNode extends QueryNode{

    public MaxNode(List<QueryNode> children){

        this.children = children;
        this.resetChildren();
    }


    @Override
    public double score(int docid) {

        double max = Double.MAX_VALUE;

        for(QueryNode child : children){
            max = Math.max(max, child.score(docid));
        }

        return max;
    }
}
