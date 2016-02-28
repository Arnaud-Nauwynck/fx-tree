package fr.an.fxtree.impl.stdfunc;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class FxStdFileFuncsTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper(); 

    @Test
    public void testEvalImportJSonFile() {
        tstHelper.doTestFile("evalImportJsonFile");
    }

    @Test
    public void testEvalExportJSonFile() throws IOException {
        File outFile = new File("target/test/data/result-testExportJsonFile.json");
        if (outFile.exists()) {
            outFile.delete();
        }
        tstHelper.doTestFile("evalExportJsonFile");
        Assert.assertTrue(outFile.exists());
        String content = FileUtils.readFileToString(outFile);
        Assert.assertEquals("{\"some-content\":1}", content);
    }

}
