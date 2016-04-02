package fr.an.fxtree.impl.helper;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.format.FxFileUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

/**
 * helper class to store function results by ids in File, and reload previous result instead of re-evaluating function
 * 
 *  thread-safety: thread-safe, protected by <code>lock</code> + <code>pendings</code>
 */
public class FxMemoizedFileStoreFuncHelper {
    
    private static final Logger LOG = LoggerFactory.getLogger(FxMemoizedFileStoreFuncHelper.class);
    
    private Object lock = new Object();
    
    private File storeFile;
    private FxObjNode fileContent;
    
    private Set<String> pendings = new HashSet<>();
    
    // ------------------------------------------------------------------------

    public FxMemoizedFileStoreFuncHelper(File storeFile) {
        this.storeFile = storeFile;
        if (storeFile.exists()) {
            this.fileContent = (FxObjNode) FxFileUtils.readTree(storeFile);
        } else {
            FxMemRootDocument doc = new FxMemRootDocument();
            this.fileContent = doc.setContentObj();
            try {
                FxFileUtils.writeTree(storeFile, fileContent);
            } catch(Exception ex) {
                throw new RuntimeException("can not write to file '" + storeFile + "'", ex);
            }
        }
    }

    // ------------------------------------------------------------------------

    public void evalSaveOrReloadResult(String resultId, FxNodeFunc func, FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxNode resultNode;
        synchronized (lock) {
            resultNode = fileContent.get(resultId);
            
            if (resultNode == null) {
                boolean addedPending = pendings.add(resultId);
                if (!addedPending) {
                    while(pendings.contains(resultId)) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    resultNode = fileContent.get(resultId);
                }
            }
        }
        if (resultNode != null) {
            // reload => simply copy
            FxNodeCopyVisitor.copyTo(dest, resultNode);
        } else {
            // do eval func + copy result and save to storage for later re-eval
            // ** do eval func **
            func.eval(dest, ctx, src);
            
            resultNode = dest.getResultChild();
            
            synchronized (lock) {
                FxChildWriter fileResultWriter = fileContent.putBuilder(resultId);
                FxNodeCopyVisitor.copyTo(fileResultWriter, resultNode);
                pendings.remove(resultId);
                try {
                    FxFileUtils.writeTree(storeFile, fileContent);
                } catch(Exception ex) {
                    LOG.error("Failed to store file (function result will be re-evaluated next time!)", ex);
                }
            }
        }
    }

}
