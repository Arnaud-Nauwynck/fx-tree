package fr.an.fxtree.impl.stdfunc.datasource;

import java.util.Map;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxConsts;

/**
 * recursive bind+replace external data in copy
 * <PRE>
 *    {
 *      "@fx-eval" : "#<<phase>>:ext-data"
 *      sourceId : ..,
 *      dataId : ..
 *    }
 * </PRE>
 * when replaced => object container replaced by external data ... and keep pointer for incremental update value!!
 */
public class FxExternalDataSourceIncrementalUpdater {

    private String phaseFunc;
    protected Map<String,FxExternalDataSource> extDataSources;

    // ------------------------------------------------------------------------

    public FxExternalDataSourceIncrementalUpdater(String phase, Map<String,FxExternalDataSource> extDataSources) {
        this.phaseFunc = "#" + phase + ":ext-data";
        this.extDataSources = extDataSources;
    }

    // ------------------------------------------------------------------------

    public FxNode bindForUpdate(FxChildWriter dest, FxNode src) {
        FxNode res = src.accept(new InnerVisitor(), dest);
        return res;
    }


    private class InnerVisitor extends FxNodeCopyVisitor {
        public InnerVisitor() {
        }

        /**
         */
        @Override
        public FxNode visitObj(FxObjNode src, FxChildWriter destWriter) {
            FxNode fxEvalFieldValue = src.get(FxConsts.FX_EVAL);
            if (fxEvalFieldValue == null) {
                return super.visitObj(src, destWriter);
            }
            String fxEvalExprText = fxEvalFieldValue.textValue();
            if (fxEvalExprText == null  || ! fxEvalExprText.equals(phaseFunc)) {
                return super.visitObj(src, destWriter);
            }

            String sourceId = FxNodeValueUtils.getOrDefault(src, "sourceId", "defaultSource");
            String dataId = FxNodeValueUtils.getStringOrThrow(src, "dataId");
            FxExternalDataSource dataSource = extDataSources.get(sourceId);
            if (dataSource == null) {
                throw new IllegalArgumentException("sourceId '" + sourceId + "' not found");
            }
            dataSource.addDataOutput(dataId, destWriter);

            FxNode res = destWriter.getResultChild();
            return res;
        }

    }
}
