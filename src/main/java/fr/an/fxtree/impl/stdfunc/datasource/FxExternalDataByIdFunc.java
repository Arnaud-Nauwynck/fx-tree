package fr.an.fxtree.impl.stdfunc.datasource;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxExternalDataByIdFunc extends FxNodeFunc {

    public static final String NAME = "ext-data";
    
    // ------------------------------------------------------------------------

    public static final FxExternalDataByIdFunc INSTANCE = new FxExternalDataByIdFunc();
    
    public FxExternalDataByIdFunc() {
    }

    // ------------------------------------------------------------------------

    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode objSrc = (FxObjNode) src;
        String sourceId = FxNodeValueUtils.getOrDefault(objSrc, "sourceId", "defaultSource");
        String dataId = FxNodeValueUtils.getStringOrThrow(objSrc, "dataId");
        FxExternalDataSource dataSource = FxExternalDataSourcesContextRegistry.getExtDataSourceById(ctx, sourceId);
        dataSource.addDataOutput(dataId, dest);
    }

}
