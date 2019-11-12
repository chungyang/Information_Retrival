package nodes;


import java.util.List;

public class WandNode extends QueryNode{

    protected  List<Double> weights;

    public WandNode(List<Double> weights, List<QueryNode> children){

        if(weights.size() != children.size()){
            throw new IllegalArgumentException("weights and children have different length");
        }

        this.children = children;
        this.weights = weights;
        this.resetChildren();
    }

    @Override
    public double score(int docid) {

        double score = 0;

        for(int i = 0; i < weights.size(); i++){
            QueryNode child = children.get(i);
            score += weights.get(i) * child.score(docid);
        }

        return score;
    }
}
