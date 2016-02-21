package fr.an.fxtree.json.jackson;

import com.fasterxml.jackson.core.JsonToken;

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

public class FxNodeAsToken extends FxTreeVisitor2<Void, JsonToken>{

    public static final FxNodeAsToken INSTANCE = new FxNodeAsToken();

    public static JsonToken asToken(FxNode node) {
        return node.accept(INSTANCE, null);
    }
    
    @Override
    public JsonToken visitRoot(FxRootDocument node, Void param) {
        if (node.getContent() == null) return JsonToken.VALUE_NULL;
        return node.getContent().accept(this, null);
    }

    @Override
    public JsonToken visitObj(FxObjNode node, Void param) {
        return JsonToken.START_OBJECT;
    }

    @Override
    public JsonToken visitArray(FxArrayNode node, Void param) {
        return JsonToken.START_ARRAY;
    }

    @Override
    public JsonToken visitTextValue(FxTextNode node, Void param) {
        return JsonToken.VALUE_STRING;
    }

    @Override
    public JsonToken visitDoubleValue(FxDoubleNode node, Void param) {
        return JsonToken.VALUE_NUMBER_FLOAT;
    }

    @Override
    public JsonToken visitIntValue(FxIntNode node, Void param) {
        return JsonToken.VALUE_NUMBER_INT;
    }

    @Override
    public JsonToken visitLongValue(FxLongNode node, Void param) {
        return JsonToken.VALUE_NUMBER_INT; // same as int!
    }

    @Override
    public JsonToken visitBoolValue(FxBoolNode node, Void param) {
        return node.getValue()? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
    }

    @Override
    public JsonToken visitBinaryValue(FxBinaryNode node, Void param) {
        return JsonToken.VALUE_EMBEDDED_OBJECT; // idem POJO!
    }

    @Override
    public JsonToken visitPOJOValue(FxPOJONode node, Void param) {
        return JsonToken.VALUE_EMBEDDED_OBJECT;
    }

    @Override
    public JsonToken visitNullValue(FxNullNode node, Void param) {
        return JsonToken.VALUE_NULL;
    }
    
    
}
