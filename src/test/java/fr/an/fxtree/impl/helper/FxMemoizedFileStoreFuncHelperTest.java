package fr.an.fxtree.impl.helper;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.stdfunc.FxRandomIntFunc;
import fr.an.fxtree.impl.stdfunc.FxStdFuncs;
import fr.an.fxtree.impl.util.FxNodeAssert;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFuncRegistry;

public class FxMemoizedFileStoreFuncHelperTest {

    protected FxMemoizedFileStoreFuncHelper sut;
    
    @Before
    public void setup() {
        File dir = new File("target/test");
        if (! dir.exists()) {
            dir.mkdirs();
        }
        File pendingStoreFile = new File(dir, "test-pendingjobs-filestore.yaml");
        if (pendingStoreFile.exists()) {
            pendingStoreFile.delete();
        }
        File memoizedStoreFile = new File(dir, "test-memoized-filestore.yaml");
        if (memoizedStoreFile.exists()) {
            memoizedStoreFile.delete();
        }
        FxKeyNodeFileStore memoizedStore = new FxKeyNodeFileStore(memoizedStoreFile);
        FxKeyNodeFileStore pendingStore = new FxKeyNodeFileStore(pendingStoreFile);
        FxPendingJobsFileStoreHelper pendingJobsHelper = new FxPendingJobsFileStoreHelper(pendingStore);
        sut = new FxMemoizedFileStoreFuncHelper(memoizedStore, pendingJobsHelper);
    }
    
    @Test
    public void testEvalSaveOrReloadResult() {
        // Prepare
        FxNodeFuncRegistry funcRegistry = FxStdFuncs.stdFuncRegistry();
        FxEvalContext ctx = new FxEvalContext(null, funcRegistry);
        FxMemRootDocument doc = new FxMemRootDocument();
        FxChildWriter writer = doc.contentWriter();
        
        FxRandomIntFunc randomFunc = FxRandomIntFunc.INSTANCE;
        FxNode src = null; // useless, null supported in randomFunc
        
        // Perform
        sut.evalSaveOrReloadResult("key1", randomFunc, writer, ctx, src);
        // Post-check
        FxNode res0 = doc.getContent();
        
        // Perform - reeval
        sut.evalSaveOrReloadResult("key1", randomFunc, writer, ctx, src);
        // Post-check
        FxNode res1 = doc.getContent();
        
        FxNodeAssert.assertEquals(res0, res1);
        
        randomFunc.eval(writer, ctx, src);
        FxNode res2 = doc.getContent();
        // can be equals... with epsilon probability..
        int res2Int = res2.asInt();
        int res1Int = res1.asInt();
        if (res1Int == res2Int) {
            randomFunc.eval(writer, ctx, src);
            res2 = doc.getContent();
            res2Int = res2.asInt();
            
            if (res1Int == res2Int) {
                System.out.println("should not occur!");
            }
        }
        
    }
}
