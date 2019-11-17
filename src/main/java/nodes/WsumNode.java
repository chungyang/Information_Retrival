package nodes;


import java.util.List;

public class WsumNode extends QueryNode{


    protected List<Double> weights;


    public WsumNode(List<Double> weights, List<QueryNode> children){

        this.children = children;
        this.weights = weights;
        this.resetChildren();
    }


    @Override
    public double score(int docid) {

        double score = 0, sumWeights = 0;

        for(int i = 0; i < weights.size(); i++){
            QueryNode child = children.get(i);
            score += weights.get(i) * Math.exp(child.score(docid));
            sumWeights += weights.get(i);
        }

        return Math.log(score / sumWeights);
    }
}
