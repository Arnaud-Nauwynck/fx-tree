package fr.an.fxtree.model.path.impl;

import java.util.ArrayList;
import java.util.List;

import fr.an.fxtree.model.path.FxChildPathElement;
import fr.an.fxtree.model.path.FxNodePath;

public class FxNodePathParserUtils {

    /* private to force all static */
    private FxNodePathParserUtils() {}
    
    public static FxNodePath parse(String text) {
        List<FxChildPathElement> elts = new ArrayList<>();
        int pos = 0;
        final int textLen = text.length(); 
        for(; pos < textLen; ) {
            char ch = text.charAt(pos);
            // read next token
            if (ch == '[') {
                // read index int (may be negative)
                ch = text.charAt(++pos);
                int nextCloseBracket = text.indexOf(']', pos);
                if (nextCloseBracket == -1) {
                    throw throwUnexpectedTextAt(text, pos, "[index]");
                }
                String indexStr = text.substring(pos, nextCloseBracket); 
                int arrayIndex = Integer.parseInt(indexStr);
                elts.add(FxChildPathElement.of(arrayIndex));
                pos = nextCloseBracket+1;
            } else if (ch == '.') {
                String fieldName; 
                ch = text.charAt(++pos);
                if (ch == '"') {
                    // read escape string...
                    int nextQuote = text.indexOf('"', pos);
                    if (nextQuote == -1) {
                        throw throwUnexpectedTextAt(text, pos, "escaped .\"fieldname\"");
                    }
                    fieldName = text.substring(pos, nextQuote-1);
                    pos = nextQuote+1;
                } else if (Character.isJavaIdentifierStart(ch)) {
                    // read fieldname
                    int endPos = pos;
                    while(endPos < textLen && Character.isJavaIdentifierPart(text.charAt(endPos))) {
                        endPos++;
                    }
                    fieldName = text.substring(pos, endPos);
                    pos = endPos;
                } else {
                    throw throwUnexpectedTextAt(text, pos, ".fieldname");
                }
                elts.add(FxChildPathElement.of(fieldName));
            } else if (ch == '$') {
                ch = text.charAt(++pos);
                if (ch != '.') {
                    throw throwUnexpectedTextAt(text, pos, "'$.'");
                }
                elts.add(FxChildPathElement.thisRoot());
                pos++;
                continue;
            } else {
                throw throwUnexpectedTextAt(text, pos, "'[index]', '.field' or '$.' ..");
            }
        }        
        return FxNodePath.of(elts);
    }
    
    protected static RuntimeException throwUnexpectedTextAt(String text, int pos, String expected) {
        throw new IllegalArgumentException("unparsable jsonpath '" + text + "'" 
                + ", at position " + pos + " got '" + text.charAt(pos) + "' expecting " + expected);
    }
    
}
