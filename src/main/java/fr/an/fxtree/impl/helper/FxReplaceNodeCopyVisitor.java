package fr.an.fxtree.impl.helper;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxTextNode;

/**
 * helper recursive node copier with name-value sustitution for <PRE>#{name}</PRE>
 */
public class FxReplaceNodeCopyVisitor extends FxNodeCopyVisitor {

    private final Map<String, FxNode> varNodeReplacements;

    private final Pattern matchVarReplacementPattern;

    // ------------------------------------------------------------------------
    
    public FxReplaceNodeCopyVisitor(Map<String, FxNode> varReplacements) {
        this.varNodeReplacements = varReplacements;
        this.matchVarReplacementPattern = patternForAnyVarsIn(varReplacements.keySet());
    }

    public static void copyWithReplaceTo(FxChildWriter dest, FxNode template, Map<String, FxNode> varReplacements) {
        FxReplaceNodeCopyVisitor copyVisitor = new FxReplaceNodeCopyVisitor(varReplacements);
        template.accept(copyVisitor, dest);
    }
    
    // ------------------------------------------------------------------------

    protected static Pattern patternForAnyVarsIn(Collection<String> varNames) {
        String patternText;
        if (varNames.size() == 1) {
            String var = varNames.iterator().next();
            patternText = "#\\{(" + var // ??Pattern.quote(var)
                + ")\\}";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("#\\{(");
            boolean first = true;
            for (String var : varNames) {
                if (!first) {
                    sb.append("|");
                } else {
                    first = false;
                }
                // ?? sb.append(Pattern.quote(var));
                sb.append(var);
            }
            sb.append(")\\}");
            patternText = sb.toString();
        }
        return Pattern.compile(patternText);
    }
    
    @Override
    public FxNode visitTextValue(FxTextNode src, FxChildWriter out) {
        String text = src.textValue();
        if (text == null) {
            return super.visitTextValue(src, out); // ???
        }
        // detect patterns "#{{....}}" for variables
        int replStart = text.indexOf("#{");
        if (replStart == -1) {
            // common case, fast return
            return super.visitTextValue(src, out);
        }

        TmpVarReplMatch tmpVarReplMatch = matcher(text);
        boolean foundRepl = findNextVarRepl(tmpVarReplMatch, replStart);
        if (!foundRepl) {
            // var, but not matching one of varReplacements
            return super.visitTextValue(src, out);
        }

        if (tmpVarReplMatch.replStart == 2 && tmpVarReplMatch.replEnd + 1 == text.length()) {
            // string is fully replaced by var => return FxNode (maybe TextNode)

            // recursive copy replacement using replacement node instead of src node
            FxNode varRepl = tmpVarReplMatch.varNodeRepl;
            if (varRepl == null) {
                return null; // should not occur
            }
            return varRepl.accept(this, out);

        } else {
            // interpolate expression in string ... return string (assume var is text..)
            String res = replaceMatchingText(text, tmpVarReplMatch);
            return out.add(res);
        }

        // return super.visitTextValue(src, out);
    }

    /*pp*/ String replaceMatchingText(String text, TmpVarReplMatch tmpVarReplMatch) {
        StringBuilder repl = new StringBuilder();
        assert tmpVarReplMatch.replStart >= 2;
        int pos = 0;
        for (;;) {
            repl.append(text, pos, tmpVarReplMatch.replStart-2);
            FxNode foundVarRepl = tmpVarReplMatch.varNodeRepl;
            if (foundVarRepl != null) {
                repl.append(foundVarRepl.asText());
            } else {
                // not a replacement for this var, leave other unknown vars unmodified
                repl.append(text, pos, tmpVarReplMatch.replStart-2);
            }
            pos = tmpVarReplMatch.replEnd + 1;
            boolean next = findNextVarRepl(tmpVarReplMatch, pos);
            if (! next) {
                break;
            }
        }
        repl.append(text, pos, text.length());
        String res = repl.toString();
        return res;
    }

    /*pp*/ TmpVarReplMatch matcher(String text) {
        return new TmpVarReplMatch(this, text);
    }
    
    /*pp*/ boolean findNextVarRepl(TmpVarReplMatch res, int fromIndex) {
        boolean found = res.matcher.find(fromIndex);
        if (found) {
            String varName = res.matcher.group(1);
            FxNode foundVarRepl = varNodeReplacements.get(varName);
            // assert foundVarRepl != null; // replace by null?

            res.replStart = res.matcher.start(1);
            res.replEnd = res.matcher.end(1);
            res.varName = varName;
            res.varNodeRepl = foundVarRepl;
            return true;
        } else {
            res.replStart = -1;
            res.replEnd = -1;
        }
        return false;
    }

     
//    private boolean findNextVarRepl_noPattern(TmpVarReplMatch res, String text, int fromIndex) {
//        int index = fromIndex;
//        for (;;) {
//            int replStart = text.indexOf("#{", index);
//            if (replStart == -1) {
//                return false;
//            }
//            int replEnd = text.indexOf("}", replStart + 2);
//            if (replEnd == -1) {
//                return false;
//            }
//            String varName = text.substring(replStart + 2, replEnd);
//            if (varNodeReplacements.containsKey(varName)) {
//                FxNode foundVarRepl = varNodeReplacements.get(varName);
//                res.replStart = replStart;
//                res.replEnd = replEnd;
//                res.varName = varName;
//                res.varRepl = foundVarRepl;
//                return true;
//            }
//            index = replEnd + 1;
//            if (index == text.length()) {
//                break;
//            }
//        }
//        return false;
//    }
 
    

    /*pp*/ static final class TmpVarReplMatch {
        /*pp*/ TmpVarReplMatch(FxReplaceNodeCopyVisitor outer, String text) {
            this.matcher = outer.matchVarReplacementPattern.matcher(text);
        }

        Matcher matcher;
        int replStart;
        int replEnd;
        String varName;
        FxNode varNodeRepl;
//        @SuppressWarnings("unused")
//        String varTextRepl;
    }

}