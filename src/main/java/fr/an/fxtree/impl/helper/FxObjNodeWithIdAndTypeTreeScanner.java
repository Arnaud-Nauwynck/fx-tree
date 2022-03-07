package fr.an.fxtree.impl.helper;

import java.util.ArrayList;
import java.util.List;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

/**
 * a default tree Visitor to scan recursively all FxObjNode containing field "id":"..." and field "type":"text..."
 *
 */
public class FxObjNodeWithIdAndTypeTreeScanner extends FxDefaultTreeVisitor {
    
    @FunctionalInterface
    public static interface IdTypeObjPredicate {
         public boolean test(String id, String type, FxObjNode node);
    }
    
    @FunctionalInterface
    public static interface IdTypeObjConsumer {
         public void accept(String id, String type, FxObjNode node, FxSourceLoc loc);
    }

    private IdTypeObjConsumer consumer;
    
    // ------------------------------------------------------------------------
    
    public FxObjNodeWithIdAndTypeTreeScanner(IdTypeObjConsumer consumer) {
        this.consumer = consumer;
    }

    public static void scanConsumeFxNodesWithIdTypeObj(FxNode tree, IdTypeObjConsumer consumer) {
        tree.accept(new FxObjNodeWithIdAndTypeTreeScanner(consumer));
    }
    
    public static List<FxObjNode> scanFxNodesWithIdTypeObj(FxNode tree) {
        return scanFxNodesWithIdTypeObj(tree, null);
    }
    
    public static List<FxObjNode> scanFxNodesWithIdTypeObj(FxNode tree, IdTypeObjPredicate idTypeObjPredicate) {
        List<FxObjNode> res = new ArrayList<>();
        FxObjNodeWithIdAndTypeTreeScanner visitor = new FxObjNodeWithIdAndTypeTreeScanner((id,t,n, loc) -> {
            if (idTypeObjPredicate == null || idTypeObjPredicate.test(id,t,n)) {
                res.add(n);
            }
        });
        tree.accept(visitor);
        return res;
    }
    
    // ------------------------------------------------------------------------

    @Override
    public void visitObj(FxObjNode node) {
        FxNode idNode = node.get("id");
        FxNode typeNode = node.get("type");
        if (idNode != null && idNode.isTextual() && typeNode != null && typeNode.isTextual()) {
            String id = idNode.textValue();
            String type = typeNode.textValue();
            if (id != null 
            		// && !id.isEmpty() // accept root=empty id 
            		&& type != null && !type.isEmpty()) {
            	FxSourceLoc loc = node.getSourceLoc();
                consumer.accept(id, type, node, loc);
            }
        }
        super.visitObj(node);
    }
}
