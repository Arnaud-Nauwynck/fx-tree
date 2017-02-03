package fr.an.fxtree.impl.stdfunc;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import fr.an.fxtree.format.json.jackson.Fx2JacksonUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class FxJqFunc extends FxNodeFunc {

    public static final String NAME = "jq";

    // ------------------------------------------------------------------------

    public static final FxJqFunc INSTANCE = new FxJqFunc();

    private FxJqFunc() {
    }

    // ------------------------------------------------------------------------

    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        String expr = FxCurrEvalCtxUtil.recurseEvalToString(ctx, srcObj.get("expr"));
        FxNode in = FxCurrEvalCtxUtil.recurseEval(ctx, srcObj.get("in"));
        boolean single = FxCurrEvalCtxUtil.recurseEvalToBooleanOrDefault(ctx, srcObj.get("single"), false);

        JsonQuery jsonQuery;
        try {
            jsonQuery = JsonQuery.compile(expr);
        } catch(JsonQueryException ex) {
            throw new RuntimeException("Failed to parse json query '" + expr + "'", ex);
        }


        JsonNode inNode = Fx2JacksonUtils.fxTreeToJsonNode(in);
        List<JsonNode> tmpres;
        try {
            tmpres = jsonQuery.apply(inNode);
        } catch(JsonQueryException ex) {
            throw new RuntimeException("Failed to apply json query '" + expr + "' to input ..", ex);
        }

        if (!single) {
            FxArrayNode resArray = dest.addArray();
            FxChildWriter resArrayWriter = resArray.insertBuilder();
            Fx2JacksonUtils.jsonNodesToFxTrees(resArrayWriter, tmpres);
        } else {
            // expecting single result
            if (tmpres.size() == 1) {
                Fx2JacksonUtils.jsonNodeToFxTree(dest, tmpres.get(0));
            } else if (tmpres.size() > 1) {
                throw new RuntimeException("Expecting single result, got " + tmpres.size());
            } else {
                // empty?
            }

        }
    }


	public static String evalJqExprAsText(String jqExpr, FxNode inputValue) {
		JsonQuery jsonQuery;
        try {
            jsonQuery = JsonQuery.compile(jqExpr);
        } catch(JsonQueryException ex) {
            throw new RuntimeException("Failed to parse jq expr '" + jqExpr + "'", ex);
        }

        JsonNode inNode = Fx2JacksonUtils.fxTreeToJsonNode(inputValue);

        List<JsonNode> tmpres;
        try {
        	// *** eval JQ expr (on json) ***
            tmpres = jsonQuery.apply(inNode);
        } catch(JsonQueryException ex) {
            throw new RuntimeException("Failed to apply JQ query '" + jqExpr + "' to input ..", ex);
        }

        // expecting single result
        if (tmpres.size() == 1) {
        	FxNode output = Fx2JacksonUtils.jsonNodeToFxTree(new FxMemRootDocument().contentWriter(), tmpres.get(0));
            return output.asText();
        } else if (tmpres.size() > 1) {
            throw new RuntimeException("Expecting single result, got " + tmpres.size());
        } else {
        	return "";
        }
	}
}
