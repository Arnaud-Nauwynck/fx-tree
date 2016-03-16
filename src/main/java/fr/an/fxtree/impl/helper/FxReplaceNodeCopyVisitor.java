package fr.an.fxtree.impl.helper;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

import fr.an.fxtree.impl.util.FxUtils;
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
        this.varNodeReplacements = ImmutableMap.copyOf(varReplacements);
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

        TmpVarReplMatch tmpVarReplMatch = new TmpVarReplMatch();
        boolean foundRepl = findNextVarRepl(tmpVarReplMatch, text, replStart);
        if (!foundRepl) {
            // var, but not matching one of varReplacements
            return super.visitTextValue(src, out);
        }

        if (tmpVarReplMatch.replStart == 0 && tmpVarReplMatch.replEnd + 1 == text.length()) {
            // string is fully replaced by var => return FxNode (maybe TextNode)

            // recursive copy replacement using replacement node instead of src node
            FxNode varRepl = tmpVarReplMatch.varNodeRepl;
            if (varRepl == null) {
                return null; // should not occur
            }
            return varRepl.accept(this, out);

        } else {
            // interpolate expression in string ... return string (assume var is text..)
            StringBuilder repl = new StringBuilder();
            repl.append(text, 0, replStart);
            for (; replStart != -1;) {
                FxNode foundVarRepl = tmpVarReplMatch.varNodeRepl;
                if (foundVarRepl != null) {
                    repl.append(foundVarRepl.asText());
                } else {
                    // not a replacement for this var, leave other unknown vars unmodified
                    repl.append(text, 0, replStart);
                }
                replStart = tmpVarReplMatch.replEnd + 1;
                boolean next = findNextVarRepl(tmpVarReplMatch, text, replStart);
                if (! next) {
                    break;
                }
            }
            repl.append(text, replStart, text.length());
            return out.add(repl.toString());
        }

        // return super.visitTextValue(src, out);
    }

    private boolean findNextVarRepl(TmpVarReplMatch res, String text, int fromIndex) {
        if (res.matcher == null) {
            res.matcher = matchVarReplacementPattern.matcher(text);
        }
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
 
    

    private static final class TmpVarReplMatch {
        Matcher matcher;
        @SuppressWarnings("unused")
        int replStart;
        int replEnd;
        @SuppressWarnings("unused")
        String varName;
        FxNode varNodeRepl;
//        @SuppressWarnings("unused")
//        String varTextRepl;
    }

}