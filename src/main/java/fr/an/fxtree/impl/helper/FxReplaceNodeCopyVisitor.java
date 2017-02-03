package fr.an.fxtree.impl.helper;

import java.util.Map;

import fr.an.fxtree.impl.stdfunc.FxJqFunc;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxTextNode;

/**
 * helper recursive node copier with name-value substitution
 * for <PRE>#{name}</PRE>, and <PRE>#{name:..jqExpr..}</PRE>
 */
public class FxReplaceNodeCopyVisitor extends FxNodeCopyVisitor {

    private final Map<String, FxNode> varNodeReplacements;

    // ------------------------------------------------------------------------

    public FxReplaceNodeCopyVisitor(Map<String, FxNode> varReplacements) {
        this.varNodeReplacements = varReplacements;
    }

    public static void copyWithReplaceTo(FxChildWriter dest, FxNode template, Map<String, FxNode> varReplacements) {
        FxReplaceNodeCopyVisitor copyVisitor = new FxReplaceNodeCopyVisitor(varReplacements);
        template.accept(copyVisitor, dest);
    }

    // ------------------------------------------------------------------------

    @Override
    public FxNode visitTextValue(FxTextNode src, FxChildWriter out) {
        final String text = src.textValue();
        if (text == null) {
            return super.visitTextValue(src, out); // ???
        }
        // detect patterns "#{....}" for variables
        int replStart = text.indexOf("#{");
        if (replStart == -1) {
            // common case, fast return
            return super.visitTextValue(src, out);
        }

        final int textLen = text.length();
        if (replStart == 0 && text.charAt(textLen - 1) == '}') {
        	// detected exact var: "#{varName}"  => simply replace
        	final String varName = text.substring(replStart+2, textLen - 1);
            FxNode foundVarRepl = varNodeReplacements.get(varName);
            if (foundVarRepl != null) {
            	return foundVarRepl.accept(this, out);
            }

            if (-1 == text.indexOf("#{", replStart+2)) {
            	// TODO detected "#{ .... }" => replace by expr
            }
        }

        StringBuilder sb = new StringBuilder();
        int prevPos = 0;
        int currPos = 0;
        for(;;) {
            int nextExprIndex = text.indexOf("#{", currPos);
            if (nextExprIndex == -1) {
            	sb.append(text, prevPos, textLen);
            	break;
            }
            sb.append(text, prevPos, nextExprIndex);
            prevPos = nextExprIndex;

            // detect "#{varName" ...  until '.', '[', ':' or '}')
            int nextExprVarIndex = nextCharNotVar(text, nextExprIndex+2);
            final String varName = text.substring(nextExprIndex+2, nextExprVarIndex);
            FxNode foundVarRepl = varNodeReplacements.get(varName);
            if (foundVarRepl == null) {
            	// var not substitued
            	currPos = nextClosingBrace(text, nextExprVarIndex);
            	continue;
            } else {
            	// var substituted
                char nextCh = text.charAt(nextExprVarIndex);
                if (nextCh == '}') {
                	// detected exact var: "#{varName}"  => simply replace
                	sb.append(foundVarRepl.asText());
                	prevPos = nextExprVarIndex+1;
                	currPos = prevPos;
                } else {
                	// detect closing "}" .. accepting nested "{.. {} }"
                	int closeBraceIndex = nextClosingBrace(text, nextExprVarIndex);
                	if (closeBraceIndex == textLen) {
                		// error: missing closing '}'
                		sb.append(text, currPos, textLen);
                		break;
                	}

                	if (nextCh == ':') {
        	        	// detected jq expression  "#{varName:  ..jq-expr..}"
                		String jqExpr = text.substring(nextExprVarIndex+1, closeBraceIndex);
        	        	String eval = FxJqFunc.evalJqExprAsText(jqExpr, foundVarRepl);
        	        	sb.append(eval);
        	        } else {
        	        	// detected simple path expression "#{varName.a.b[0]..path-expr..}"
        	        	String pathExpr = text.substring(nextExprVarIndex, closeBraceIndex);
        	        	throw new UnsupportedOperationException("TODO not implemented here eval pathExpr " + pathExpr);
        	        }
                	prevPos = closeBraceIndex + 1;
                	currPos = prevPos;
                }

            }
        }
        return out.add(sb.toString());
    }


	private static int nextClosingBrace(final String text, int fromIndex) {
		int closeBraceIndex = fromIndex + 1;
		int countStackBrace = 1;
		final int textLen = text.length();
		for(; closeBraceIndex < textLen; closeBraceIndex++) {
			char ch = text.charAt(closeBraceIndex);
			if (ch == '{') {
				countStackBrace++;
			} else if (ch == '}') {
				countStackBrace--;
				if (countStackBrace == 0) {
					break;
				}
			}
		}
		return closeBraceIndex;
	}

	private static int nextCharNotVar(final String text, int fromIndex) {
		int replVarEnd = fromIndex;
		final int textLen = text.length();
        label_replVarEnd: for(; replVarEnd < textLen; replVarEnd++) {
        	char ch = text.charAt(replVarEnd);
        	switch(ch) {
        	case '}': case '.': case '[' : case ':':
        		break label_replVarEnd;
        	default:
        	}
        }
		return replVarEnd;
	}

}