package fr.an.fxtree.impl.stdfunc.datasource;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import fr.an.fxtree.format.json.FxJsonUtilsTest;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.util.FxNodeAssert;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxRootDocument;

public class FxExternalDataSourceIncrementalUpdaterTest {

    protected FxDefaultExternalDataSource dataSource0 = new FxDefaultExternalDataSource();
    protected FxDefaultExternalDataSource dataSource1 = new FxDefaultExternalDataSource();
    
    FxExternalDataSourceIncrementalUpdater sut = new FxExternalDataSourceIncrementalUpdater("phase0", 
        ImmutableMap.<String,FxExternalDataSource>builder()
            .put("source0", dataSource0).put("source1", dataSource1).build());

    @Before
    public void setup() {
        initDataSourceValues();
    }
    
    @Test
    public void testEvalExtDatasource1_update23() {
        String evalBaseFilename = "eval-extDataSource1";
        String inputFilename = evalBaseFilename  + "-input.json";
        String outputFilename = evalBaseFilename + "-expected.json";
        String outputFilename2 = evalBaseFilename + "-expected2.json";
        String outputFilename3 = evalBaseFilename + "-expected3.json";
        
        FxNode src = FxJsonUtilsTest.getJsonTstFile(inputFilename).getContentObj();
        FxRootDocument doc = new FxMemRootDocument();
        // Perform
        FxNode res = sut.bindForUpdate(doc.contentWriter(), src);
        
        // Post-check
        FxNode expected = FxJsonUtilsTest.getJsonTstFile(outputFilename).getContentObj();
        FxNodeAssert.assertEquals(expected, res);

        // Perform
        // re-update external datasource ... check modified tree (no re-eeval)
        updateDataSourceValues(2);
        // Post-check
        FxNode expected2 = FxJsonUtilsTest.getJsonTstFile(outputFilename2).getContentObj();
        FxNodeAssert.assertEquals(expected2, res);
        
        // Perform
        // re-update external datasource ... check modified tree (no re-eeval)
        updateDataSourceValues(3);
        // Post-check
        FxNode expected3 = FxJsonUtilsTest.getJsonTstFile(outputFilename3).getContentObj();
        FxNodeAssert.assertEquals(expected3, res);
    }
    
    

    private void initDataSourceValues() {
        FxRootDocument doc0 = new FxMemRootDocument();
        FxObjNode source0Root = doc0.setContentObj();
        FxNode nodeA = source0Root.put("key-a", "value-a");
        dataSource0.putDataValue("key-a", nodeA);
        FxNode nodeB = source0Root.put("key-b", "value-b");
        dataSource0.putDataValue("key-b", nodeB);
        
        FxRootDocument doc1 = new FxMemRootDocument();
        FxObjNode source1Root = doc1.setContentObj();
        FxObjNode node1A = source1Root.putObj("key-a");
        node1A.put("value", "source1-a");
        dataSource1.putDataValue("key-a", node1A);
    }
    
    protected void updateDataSourceValues(int updateCount) {
        FxRootDocument doc0 = new FxMemRootDocument();
        FxObjNode source0Root = doc0.setContentObj();
        FxNode nodeA = source0Root.put("key-a", "value-a" + updateCount);
        dataSource0.putDataValue("key-a", nodeA);
        if (updateCount % 2 == 0) {
            dataSource0.putDataValue("key-b", null);
        } else {
            FxNode nodeB = source0Root.put("key-b", "value-b" + updateCount);
            dataSource0.putDataValue("key-b", nodeB);
        }
        
        FxRootDocument doc1 = new FxMemRootDocument();
        FxObjNode source1Root = doc1.setContentObj();
        FxObjNode node1A = source1Root.putObj("key-a");
        node1A.put("value", "source1-a" + updateCount);
        dataSource1.putDataValue("key-a", node1A);
    }

    
}
