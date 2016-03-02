package fr.an.fxtree.format.json.jackson;

import com.fasterxml.jackson.core.JsonParser.NumberType;

import fr.an.fxtree.model.FxNumberType;

public class Fx2JacksonUtils {

    public static NumberType numberType2Jackson(FxNumberType value) {
        if (value == null) return null;
        switch(value) {
        case INT: return NumberType.INT; 
        case LONG: return NumberType.LONG; 
        case BIG_INTEGER: return NumberType.BIG_INTEGER; 
        case FLOAT: return NumberType.FLOAT; 
        case DOUBLE: return NumberType.DOUBLE; 
        case BIG_DECIMAL: return NumberType.BIG_DECIMAL;
        default: return null;
        }
    }

}
