package nodes;

import index.Index;
import retrieval.RetrievalModel;

import java.util.List;

public class BooleanAnd extends UnOrderedWindow{

    public BooleanAnd(RetrievalModel model, Index index, List<QueryNode> children){
        super(model, index, children);
        this.resetChildren();
    }

}
