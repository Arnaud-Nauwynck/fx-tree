package fr.an.fxtree.impl.helper;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxBoolNode;
import fr.an.fxtree.model.FxDoubleNode;
import fr.an.fxtree.model.FxIntNode;
import fr.an.fxtree.model.FxLongNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxNullNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.FxRootDocument;
import fr.an.fxtree.model.FxTextNode;
import fr.an.fxtree.model.FxTreeVisitor2;

public class FxNodeCopyVisitor extends FxTreeVisitor2<FxNode,FxNode> {

    // ------------------------------------------------------------------------

    public FxNodeCopyVisitor() {
    }

    // ------------------------------------------------------------------------

    @Override
    public FxNode visitRoot(FxRootDocument src, FxNode destNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FxNode visitObj(FxObjNode src, FxNode destNode) {
        FxObjNode dest = (FxObjNode) destNode;
        for(Iterator<Map.Entry<String, FxNode>> iter = src.fields(); iter.hasNext(); ) {
            Entry<String, FxNode> srcFieldEntry = iter.next();
            String name = srcFieldEntry.getKey();
            FxNode srcValue = srcFieldEntry.getValue();
            visitObjField(name, srcValue, dest);
        }
        return dest;
    }

    protected void visitObjField(String name, FxNode srcValue, FxObjNode dest) {
        switch(srcValue.getNodeType()) {
        case OBJECT: visitObjFieldObj(name, srcValue, dest); break;
        case ARRAY: {
            FxArrayNode destArrayValue = dest.putArray(name);
            srcValue.accept(this, destArrayValue);
        } break;
        case STRING: {
            String srcValueText = srcValue.textValue();
            visitObjFieldText(name, srcValueText, dest);
        } break;
        case NUMBER: {
            switch(srcValue.numberType()) {
            case INT: dest.put(name, srcValue.intValue()); break;  
            case LONG: dest.put(name, srcValue.longValue()); break; 
            case BIG_INTEGER: dest.put(name, srcValue.bigIntegerValue()); break; 
            case FLOAT: dest.put(name, srcValue.floatValue()); break; 
            case DOUBLE: dest.put(name, srcValue.doubleValue()); break; 
            case BIG_DECIMAL : dest.put(name, srcValue.decimalValue()); break; 
            default: throw new IllegalStateException();
            }
        } break;
        case BOOLEAN:
            dest.put(name, srcValue.booleanValue());
            break;
        case BINARY: {
            byte[] cloneBinaryValue = srcValue.binaryValue();
            if (cloneBinaryValue != null) {
                cloneBinaryValue = cloneBinaryValue.clone();
            }
            dest.put(name, cloneBinaryValue);
        } break;
        case MISSING:
            break;
        case NULL:
            dest.putNull(name);
            break;
        case POJO: {
            Object pojo = ((FxPOJONode) srcValue).getValue();
            // TODO clone pojo??
            dest.putPOJO(name, pojo);
        } break;
        case ROOT: throw new IllegalStateException();
        default: throw new IllegalStateException();
        }
    }

    protected void visitObjFieldObj(String name, FxNode srcValue, FxObjNode dest) {
        FxObjNode destValue = dest.putObj(name);
        srcValue.accept(this, destValue);
    }

    protected void visitObjFieldText(String name, String textValue, FxObjNode dest) {
        dest.put(name, textValue);
    }
    
    @Override
    public FxNode visitArray(FxArrayNode src, FxNode destNode) {
        FxArrayNode dest = (FxArrayNode) destNode;
        int index = 0;
        for(FxNode srcChild : src.children()) {
            visitArrayElt(index, srcChild, dest);
            index++;
        }
        return dest;
    }
    
    protected void visitArrayElt(int index, FxNode srcValue, FxArrayNode dest) {
        switch(srcValue.getNodeType()) {
        case OBJECT: visitArrayEltObj(index, srcValue, dest); break;
        case ARRAY: {
            FxArrayNode destArrayValue = dest.addArray();
            srcValue.accept(this, destArrayValue);
        } break;
        case STRING: {
            String srcValueText = srcValue.textValue();
            visitArrayEltText(index, srcValueText, dest);
        } break;
        case NUMBER: {
            switch(srcValue.numberType()) {
            case INT: dest.add(srcValue.intValue()); break;  
            case LONG: dest.add(srcValue.longValue()); break; 
            case BIG_INTEGER: dest.add(srcValue.bigIntegerValue()); break; 
            case FLOAT: dest.add(srcValue.floatValue()); break; 
            case DOUBLE: dest.add(srcValue.doubleValue()); break; 
            case BIG_DECIMAL : dest.add(srcValue.decimalValue()); break; 
            default: throw new IllegalStateException();
            }
        } break;
        case BOOLEAN:
            dest.add(srcValue.booleanValue());
            break;
        case BINARY: {
            byte[] cloneBinaryValue = srcValue.binaryValue();
            if (cloneBinaryValue != null) {
                cloneBinaryValue = cloneBinaryValue.clone();
            }
            dest.add(cloneBinaryValue);
        } break;
        case MISSING:
            break;
        case NULL:
            dest.addNull();
            break;
        case POJO: {
            Object pojo = ((FxPOJONode) srcValue).getValue();
            // TODO clone pojo??
            dest.addPOJO(pojo);
        } break;
        case ROOT: throw new IllegalStateException();
        default: throw new IllegalStateException();
        }
    }

    protected void visitArrayEltObj(int index, FxNode srcValue, FxArrayNode dest) {
        FxObjNode destValue = dest.addObj();
        srcValue.accept(this, destValue);
    }

    protected void visitArrayEltText(int index, String textValue, FxArrayNode dest) {
        dest.add(textValue);
    }
    
    @Override
    public FxNode visitTextValue(FxTextNode src, FxNode destNode) {
        // not called
        return destNode;
    }

    @Override
    public FxNode visitDoubleValue(FxDoubleNode src, FxNode destNode) {
        // not called
        return destNode;
    }

    @Override
    public FxNode visitIntValue(FxIntNode src, FxNode destNode) {
        // not called
        return destNode;
    }

    @Override
    public FxNode visitLongValue(FxLongNode src, FxNode destNode) {
        // not called
        return destNode;
    }

    @Override
    public FxNode visitBoolValue(FxBoolNode src, FxNode destNode) {
        // not called
        return destNode;
    }

    @Override
    public FxNode visitBinaryValue(FxBinaryNode src, FxNode destNode) {
        // not called
        return destNode;
    }

    @Override
    public FxNode visitPOJOValue(FxPOJONode src, FxNode destNode) {
        // not called
        return destNode;
    }

    @Override
    public FxNode visitNullValue(FxNullNode src, FxNode destNode) {
        // not called
        return destNode;
    }
    
}
