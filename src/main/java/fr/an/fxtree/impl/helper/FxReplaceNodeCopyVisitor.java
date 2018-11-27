package fr.an.fxtree.impl.helper;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.impl.stdfunc.FxJqFunc;
import fr.an.fxtree.impl.stdfunc.FxNodePathFunc;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxTextNode;

/**
 * helper recursive node copier with name-value substitution 
 * for <PRE>@{name}</PRE>, and <PRE>@{name:..jqExpr..}</PRE> 
 */
public class FxReplaceNodeCopyVisitor extends FxNodeCopyVisitor {

    private static final Logger LOG = LoggerFactory.getLogger(FxReplaceNodeCopyVisitor.class);
    
    private final Map<String, FxNode> varNodeReplacements;

    // ------------------------------------------------------------------------
    
    public FxReplaceNodeCopyVisitor(Map<String, FxNode> varReplacements) {
        this.varNodeReplacements = varReplacements;
    }

    public static void copyWithReplaceTo(FxChildWriter dest, FxNode template, Map<String, FxNode> varReplacements) {
        FxReplaceNodeCopyVisitor copyVisitor = new FxReplaceNodeCopyVisitor(varReplacements);
        template.accept(copyVisitor, dest);
    }

    public static FxNode copyWithReplace(FxNode template, Map<String, FxNode> varReplacements) {
        FxMemRootDocument doc = new FxMemRootDocument(template.getSourceLoc());
        copyWithReplaceTo(doc.contentWriter(), template, varReplacements);
        return doc.getContent();
    }

    // ------------------------------------------------------------------------

    protected static FxSourceLoc locFromReplace(FxSourceLoc loc) {
        return FxSourceLoc.newFrom("replace vars", loc);
    }
    
    @Override
    public FxNode visitTextValue(FxTextNode src, FxChildWriter out) {
        final String text = src.textValue();
        if (text == null) {
            return super.visitTextValue(src, out); // ???
        }
        // detect patterns "@{....}" for variables
        int replStart = text.indexOf("@{");
        if (replStart == -1) {
            // common case, fast return
            return super.visitTextValue(src, out);
        }

        try {
            final int textLen = text.length();
            if (replStart == 0 && text.charAt(textLen - 1) == '}' && text.indexOf("}") == (textLen - 1)) {
                // detected exact var: "@{expr...}"  => eval and replace, possibly as Text,Array,Obj...
                final String expr = text.substring(replStart+2, textLen - 1);
                FxNode eval = evalExprRepl(expr);
                if (eval == null) {
                    return super.visitTextValue(src, out);
                }
                return FxNodeCopyVisitor.copyTo(out, eval);
            }
            
            // text contains fragments "... @{expr1} ..."
            StringBuilder sb = new StringBuilder();
            int currPos = 0;
            for(;;) {
                int nextExprIndex = text.indexOf("@{", currPos);
                if (nextExprIndex == -1) {
                    sb.append(text, currPos, textLen);
                    currPos = textLen;
                    break;
                }
                if (currPos < nextExprIndex) {
                    sb.append(text, currPos, nextExprIndex);
                    currPos = nextExprIndex;
                }
                
                int nextClosingBrace = nextClosingBrace(text, nextExprIndex+2);
                if (nextClosingBrace == textLen) {
                    // error: missing closing '}'
                    sb.append(text, currPos, textLen);
                    currPos = textLen;
                    break;
                }
                currPos = nextClosingBrace+1;
                String expr = text.substring(nextExprIndex+2, nextClosingBrace);
                FxNode eval = evalExprRepl(expr);
                if (eval == null) {
                    // do not replace
                    sb.append("@{" + expr + "}"); 
                } else {
                    sb.append(eval.asText());
                }
                
            }
            FxSourceLoc loc = locFromReplace(src.getSourceLoc()); 
            return out.add(sb.toString(), loc);
        } catch(Exception ex) {
            LOG.error("Failed to eval '" + text + "' .. ignore, no rethrow!", ex);
            FxSourceLoc loc = locFromReplace(src.getSourceLoc()); 
            return out.add("****** ERROR Failed to eval '" + text + "' : " + ex.getMessage(), loc);
        }
    }

    private FxNode evalExprRepl(String expr) {
        // detect "@{varName" ...  until ':', '|', or '}')
        FxNode res = varNodeReplacements.get(expr);
        if (res != null) {
            return res;
        }
        // var not substituted (var not found) or expr .. 
        int nextChIndex = nextCharNotVar(expr, 1);
        final int exprLen = expr.length();
        if (nextChIndex == exprLen) {
            // exact var "@{varExpr}" but var not found
            return null;
        }
        char nextCh = expr.charAt(nextChIndex);
        if (nextCh == ':' || nextCh == '|') {
            // detected simple path expression  "@{varName:  ..subfield..}" or "@{varName|  ..jq-expr..}"
            String varName = expr.substring(0, nextChIndex);
            String subExpr = expr.substring(nextChIndex+1, exprLen);
            FxNode varValue = varNodeReplacements.get(varName);
            if (varValue == null) {
                return null;
            }
            FxNode eval;
            if (nextCh == ':') {
                eval = FxNodePathFunc.evalSubPathExpr(varValue, subExpr);
            } else {
                eval = FxJqFunc.evalJqExpr(varValue, subExpr, true);
            }
            return eval;
        } else if (expr.charAt(0) == '{' && nextCh == '}') {
            return res; // do not replace. may occur in "@{{..}}" using mustach substitutions
        } else {
            // detected simple path expression "@{varName.a.b[0]..path-expr..}"
            // throw new UnsupportedOperationException("Unrecognized var expression .. expected @{var:..subfield expr..} or @{var|..jq expr..}, got " + expr);
            return res;
        }
    }        


    private static int nextClosingBrace(final String text, int fromIndex) {
        int closeBraceIndex = fromIndex; // + 1;
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
            case '}': case ':': case '|': 
                break label_replVarEnd;
            default:
            }            
        }
        return replVarEnd;
    }

}