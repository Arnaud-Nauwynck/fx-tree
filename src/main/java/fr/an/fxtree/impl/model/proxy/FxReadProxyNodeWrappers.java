package fr.an.fxtree.impl.model.proxy;

import fr.an.fxtree.impl.util.FxUtils;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxBoolNode;
import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxDoubleNode;
import fr.an.fxtree.model.FxIntNode;
import fr.an.fxtree.model.FxLinkProxyNode;
import fr.an.fxtree.model.FxLongNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxNullNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.FxRootDocument;
import fr.an.fxtree.model.FxTextNode;
import fr.an.fxtree.model.FxTreeVisitor2;

public class FxReadProxyNodeWrappers {

    /*pp*/ static RuntimeException throwWriteDenied() {
        throw new RuntimeException("Write acces denied on Read-Only proxy node");
    }

    /*pp*/ static RuntimeException throwGetParentDenied() {
        throw new RuntimeException("getParent() acces denied on Read-Only proxy node");
    }

    @SuppressWarnings("unchecked")
    public static <T extends FxNode> T wrapROProxy(FxContainerNode proxyParent, boolean allowGetParent, T delegate) {
        InnerReadWrapProxyVisitor v = InnerReadWrapProxyVisitor.instance(allowGetParent);
        return (T) delegate.accept(v, proxyParent);
    }

    private static class InnerReadWrapProxyVisitor extends FxTreeVisitor2<FxContainerNode,FxNode> {

        static final InnerReadWrapProxyVisitor INSTANCE_ALLOWGETPARENT = new InnerReadWrapProxyVisitor(true);
        static final InnerReadWrapProxyVisitor INSTANCE_DENYGETPARENT = new InnerReadWrapProxyVisitor(false);
        public static InnerReadWrapProxyVisitor instance(boolean allowGetParent) {
            return allowGetParent? INSTANCE_ALLOWGETPARENT : INSTANCE_DENYGETPARENT;
        }

        private final boolean allowGetParent;
        public InnerReadWrapProxyVisitor(boolean allowGetParent) {
            this.allowGetParent = allowGetParent;
        }

        @Override
        public FxNode visitRoot(FxRootDocument node, FxContainerNode param) {
            throw FxUtils.notImplYet();
        }

        @Override
        public FxNode visitObj(FxObjNode node, FxContainerNode proxyParent) {
            return new FxReadObjNodeProxy(proxyParent, allowGetParent, node);
        }

        @Override
        public FxNode visitArray(FxArrayNode node, FxContainerNode proxyParent) {
            return new FxReadArrayNodeProxy(proxyParent, allowGetParent, node);
        }

        @Override
        public FxNode visitTextValue(FxTextNode node, FxContainerNode proxyParent) {
            throw FxUtils.notImplYet();
        }

        @Override
        public FxNode visitDoubleValue(FxDoubleNode node, FxContainerNode proxyParent) {
            throw FxUtils.notImplYet();
        }

        @Override
        public FxNode visitIntValue(FxIntNode node, FxContainerNode proxyParent) {
            throw FxUtils.notImplYet();
        }

        @Override
        public FxNode visitLongValue(FxLongNode node, FxContainerNode proxyParent) {
            throw FxUtils.notImplYet();
        }

        @Override
        public FxNode visitBoolValue(FxBoolNode node, FxContainerNode proxyParent) {
            throw FxUtils.notImplYet();
        }

        @Override
        public FxNode visitBinaryValue(FxBinaryNode node, FxContainerNode proxyParent) {
            throw FxUtils.notImplYet();
        }

        @Override
        public FxNode visitPOJOValue(FxPOJONode node, FxContainerNode proxyParent) {
            throw FxUtils.notImplYet();
        }

        @Override
        public FxNode visitLink(FxLinkProxyNode node, FxContainerNode proxyParent) {
            throw FxUtils.notImplYet();
        }

        @Override
        public FxNode visitNullValue(FxNullNode node, FxContainerNode proxyParent) {
            throw FxUtils.notImplYet();
        }

    }
}
