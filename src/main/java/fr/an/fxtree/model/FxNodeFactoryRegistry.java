package fr.an.fxtree.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public abstract class FxNodeFactoryRegistry {

    public abstract <T extends FxNode> T newNode(Class<T> clss);

    // helper methods
    // ------------------------------------------------------------------------
    
    public FxArrayNode newArray() {
        return newNode(FxArrayNode.class);
    }
    
    public FxObjNode newObj() {
        return newNode(FxObjNode.class);
    }
    
    public FxTextNode newText() {
        return newNode(FxTextNode.class);
    }

    public FxTextNode newText(String value) {
        FxTextNode res = newText();
        res.setValue(value);
        return res;
    }

    public FxDoubleNode newDouble() {
        return newNode(FxDoubleNode.class);
    }
    
    public FxDoubleNode newDouble(double value) {
        FxDoubleNode res = newDouble();
        res.setValue(value);
        return res;
    }
    
    public FxIntNode newInt() {
        return newNode(FxIntNode.class);
    }

    public FxIntNode newInt(int value) {
        FxIntNode res = newInt();
        res.setValue(value);
        return res;
    }

    public FxBoolNode newBool() {
        return newNode(FxBoolNode.class); 
    }

    public FxBoolNode newBool(boolean value) {
        FxBoolNode res = newBool();
        res.setValue(value);
        return res; 
    }
    
    public FxPOJONode newPOJO() {
        return newNode(FxPOJONode.class);
    }

    public FxPOJONode newPOJO(Object value) {
        FxPOJONode res = newPOJO();
        res.setValue(value);
        return res;
    }

    public FxNullNode newNull() {
        return newNode(FxNullNode.class);
    }
 
    // ------------------------------------------------------------------------
    
    public static class DefaultFxNodeFactoryRegistry extends FxNodeFactoryRegistry {
        
        private Map<Class<?>,FxNodeFactory<?>> class2factory;
        
        public DefaultFxNodeFactoryRegistry(Map<Class<?>,FxNodeFactory<?>> class2factory) {
            this.class2factory = ImmutableMap.copyOf(class2factory);
        }
        
        public <T extends FxNode> void register(Class<T> clss, FxNodeFactory<T> factory) {
            this.class2factory = ImmutableMap.<Class<?>,FxNodeFactory<?>>builder()
                    .putAll(class2factory).put(clss, factory).build();
        }

        public <T extends FxNode> T newNode(Class<T> clss) {
            @SuppressWarnings("unchecked")
            FxNodeFactory<T> factory = (FxNodeFactory<T>) class2factory.get(clss);
            if (factory == null) {
                throw new IllegalArgumentException("Factory not registered for class: " + clss);
            }
            return factory.newNode();
        }

    }
}
