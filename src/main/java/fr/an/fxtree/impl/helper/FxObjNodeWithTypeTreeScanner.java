package fr.an.fxtree.impl.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

/**
 * a default tree Visitor to scan recursively all FxObjNode containing a field "type":"text..."
 *
 */
public class FxObjNodeWithTypeTreeScanner extends FxDefaultTreeVisitor {

    private BiConsumer<String,FxObjNode> consumer;

    // ------------------------------------------------------------------------

    public FxObjNodeWithTypeTreeScanner(BiConsumer<String, FxObjNode> consumer) {
        this.consumer = consumer;
    }

    public static List<FxObjNode> scanFxObjNodesWithType(FxNode tree) {
        return scanFxObjNodesWithType(tree, x -> true);
    }

    public static List<FxObjNode> scanFxObjNodesWithType(FxNode tree, Predicate<String> typePredicate) {
        List<FxObjNode> res = new ArrayList<>();
        FxObjNodeWithTypeTreeScanner visitor = new FxObjNodeWithTypeTreeScanner((t,n) -> {
            if (typePredicate == null || typePredicate.test(t)) {
                res.add(n);
            }
        });
        tree.accept(visitor);
        return res;
    }

    // ------------------------------------------------------------------------

    @Override
    public void visitObj(FxObjNode node) {
        FxNode typeNode = node.get("type");
        if (typeNode != null && typeNode.isTextual()) {
            String type = typeNode.textValue();
            if (type != null && !type.isEmpty()) {
                consumer.accept(type, node);
            }
        }
        super.visitObj(node);
    }
}
