package fr.an.fxtree.impl.stdfunc.datasource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.an.fxtree.format.json.FxJsonUtilsTest;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.stdfunc.FxEvalFuncTstHelper;
import fr.an.fxtree.impl.util.FxNodeAssert;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxRootDocument;
import fr.an.fxtree.model.func.FxEvalContext;

public class FxExternalDataByIdFuncTest {

    protected FxDefaultExternalDataSource dataSource0 = new FxDefaultExternalDataSource();
    protected FxDefaultExternalDataSource dataSource1 = new FxDefaultExternalDataSource();
    
    protected FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper() {
        @Override
        public void prepareEvalContext(FxEvalContext ctx, boolean phase1) {
            FxExternalDataSourcesContextRegistry.putExtDataSourceById(ctx, "source0", dataSource0);
            FxExternalDataSourcesContextRegistry.putExtDataSourceById(ctx, "source1", dataSource1);
        }
    };

    @Before
    public void setup() {
        initDataSourceValues();
    }
    
    @Test
    public void testEvalExtDatasource() {
        tstHelper.doTestFile("eval-extDataSource");
    }
    
    @Test
    public void testEvalExtDatasource_update23_WARN_not_updated() {
        String evalBaseFilename = "eval-extDataSource";
        String inputFilename = evalBaseFilename  + "-input.json";
        String outputFilename = evalBaseFilename + "-expected.json";
        
        // Perform
        FxNode resNode = tstHelper.doEvalTstFile_phase01(inputFilename, false);
        // Post-check
        FxNode expected = FxJsonUtilsTest.getJsonTstFile(outputFilename).getContentObj();
        FxNodeAssert.assertEquals(expected, resNode);

        String res1Text = resNode.toString();
        
        // Perform
        // re-update external datasource ... check modified tree (no re-eeval)
        updateDataSource(2);

        // Post-check
        // FxNode expected2 = FxJsonUtilsTest.getJsonTstFile(outputFilename2).getContentObj();
        // incremental update DOES NOT WORK "Recursive Func" evaluation ... because of temporary objects node copy!! 
        // (DataOutput references are lost in recursive evaluation)

        String res2Text = resNode.toString();
        Assert.assertEquals(res1Text, res2Text);
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
    
    protected void updateDataSource(int updateCount) {
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
