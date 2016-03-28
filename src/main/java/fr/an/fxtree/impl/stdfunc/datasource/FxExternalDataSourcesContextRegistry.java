package fr.an.fxtree.impl.stdfunc.datasource;

import java.util.HashMap;
import java.util.Map;

import fr.an.fxtree.model.func.FxEvalContext;

/**
 * registry of FxExternalDataSource(s), attached on <code>FxEvalContext</code> by <code>sourceId</code>
 */
public class FxExternalDataSourcesContextRegistry {

    protected static final String EXT_SOURCES_CONTEXT = "ext-sources-context";
    
    protected Map<String,FxExternalDataSource> extDataSources = new HashMap<>();
    
    // ------------------------------------------------------------------------

    public FxExternalDataSourcesContextRegistry() {
    }

    public static FxExternalDataSourcesContextRegistry getSourcesRegistryCtx(FxEvalContext ctx) {
        return (FxExternalDataSourcesContextRegistry) ctx.lookupVariable(EXT_SOURCES_CONTEXT);
    }
    
    public static void putSourcesRegistryCtx(FxEvalContext ctx, FxExternalDataSourcesContextRegistry value) {
        ctx.putVariable(EXT_SOURCES_CONTEXT, value);
    }

    public static FxExternalDataSourcesContextRegistry getOrCreateSourcesRegistryCtx(FxEvalContext ctx) {
        FxExternalDataSourcesContextRegistry res = getSourcesRegistryCtx(ctx);
        if (res == null) {
            res = new FxExternalDataSourcesContextRegistry();
            putSourcesRegistryCtx(ctx, res);
        }
        return res;
    }

    
    public static void putExtDataSourceById(FxEvalContext ctx, String sourceId, FxExternalDataSource dataSource) {
        FxExternalDataSourcesContextRegistry registry = getOrCreateSourcesRegistryCtx(ctx);
        registry.put(sourceId, dataSource);
    }
    
    public static FxExternalDataSource getExtDataSourceById(FxEvalContext ctx, String sourceId) {
        FxExternalDataSourcesContextRegistry extSourcesCtx = getSourcesRegistryCtx(ctx);
        if (extSourcesCtx == null) {
            throw new IllegalStateException("no " + EXT_SOURCES_CONTEXT);
        }
        FxExternalDataSource res = extSourcesCtx.get(sourceId);
        if (res == null) {
            throw new IllegalArgumentException("ext dataSource '" + sourceId + "' not found");
        }
        return res;
    }

    // ------------------------------------------------------------------------

    public FxExternalDataSource get(String sourceId) {
        return extDataSources.get(sourceId);
    }
    
    public void put(String sourceId, FxExternalDataSource source) {
        extDataSources.put(sourceId, source);
    }

}
