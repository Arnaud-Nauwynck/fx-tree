package fr.an.fxtree.impl.stdfunc;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.util.FxUtils;
import fr.an.fxtree.model.FxChildAdder;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxTextNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxVarsReplaceFunc extends FxNodeFunc {

    private Map<String,FxNode> varReplacements;

    private Pattern matchVarReplacementPattern;
    
    // ------------------------------------------------------------------------
    
    public FxVarsReplaceFunc(Map<String, FxNode> varReplacements) {
        this.varReplacements = varReplacements;
        
        String patternText;
        if (varReplacements.size() == 1) {
            String var = varReplacements.keySet().iterator().next(); 
            patternText = "#\\{(" 
                    + var // ??Pattern.quote(var) 
                    + ")\\}"; 
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("#\\{(");
            boolean first = true;
            for(String var : varReplacements.keySet()) {
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
        matchVarReplacementPattern = Pattern.compile(patternText);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public FxNode eval(FxChildAdder dest, FxEvalContext ctx, FxNode src) {
        return src.accept(new InnerVisitor(ctx), dest); 
    }
    
    private static class TmpVarReplMatch {
        int replStart;
        int replEnd;
        String varName;
        FxNode varRepl;
        Matcher matcher;
        
        public void reset() {
            replStart = -1;
            varName = null;
            varRepl = null;
            matcher = null;
        }
    }
    
    private class InnerVisitor extends FxNodeCopyVisitor {
        FxEvalContext ctx;
        TmpVarReplMatch tmpVarReplMatch = new TmpVarReplMatch();
        
        public InnerVisitor(FxEvalContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public FxNode visitTextValue(FxTextNode src, FxChildAdder out) {
            String text = src.textValue();
            // detect patterns "#{{....}}"  for variables 
            int replStart = text.indexOf("#{");
            if (replStart == -1) {
                // common case, fast return 
                return super.visitTextValue(src, out);
            }

            tmpVarReplMatch.reset();
            boolean foundRepl = findNextVarRepl(tmpVarReplMatch, text, replStart);
            if (! foundRepl) {
                // var, but not matching one of varReplacements
                return super.visitTextValue(src, out);
            }
            
            
            if (tmpVarReplMatch.replEnd+1 == text.length()) {
                // string is fully replaced by var => return FxNode (maybe TextNode)
                
                // recursive copy replacement using replacement node instead of src node
                FxNode varRepl = tmpVarReplMatch.varRepl;
                if (varRepl == null) {
                    return null; //should not occur
                }
                return varRepl.accept(this, out);
                
            } else {
                // interpolate expression in string ... return string (assume var is text..)

//                StringBuilder repl = new StringBuilder();
//                repl.append(text, 0, replStart);
//                for(; replStart != -1; ) {
//                    if (foundVarRepl != null) {
//                        
//                    } else {
//                        repl.append(text, 0, replStart);
//                    }
//                    replStart = text.indexOf("#{");
//                }
                
                throw FxUtils.notImplYet();
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
                FxNode foundVarRepl = varReplacements.get(varName);
                // assert foundVarRepl != null; // replace by null?
                
                res.replStart = res.matcher.start(1);
                res.replEnd = res.matcher.end(1);
                res.varName = varName;
                res.varRepl = foundVarRepl;
                return true;
            }
            return false;
        }
        
        /*
        private boolean findNextVarRepl_noPattern(TmpVarReplMatch res, String text, int fromIndex) {
            int index = fromIndex;
            for(;;) {
                int replStart = text.indexOf("#{", index);
                if (replStart == -1) {
                    return false;
                }
                int replEnd = text.indexOf("}", replStart+2);
                if (replEnd == -1) {
                    return false;
                }                
                String varName = text.substring(replStart+2, replEnd);
                if (varReplacements.containsKey(varName)) {
                    FxNode foundVarRepl = varReplacements.get(varName);
                    res.replStart = replStart;
                    res.replEnd = replEnd;
                    res.varName = varName;
                    res.varRepl = foundVarRepl;
                    return true;
                }
                index = replEnd+1;
                if (index == text.length()) {
                    break;
                }
            }
            return false;
        }
        */
    }
    
    
}
