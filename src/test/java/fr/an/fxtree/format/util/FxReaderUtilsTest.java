package fr.an.fxtree.format.util;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

public class FxReaderUtilsTest {

    @Test
    public void testSkipWs() throws IOException {
        PushbackReader reader = new PushbackReader(new StringReader("  param1=foo"));
        FxReaderUtils.skipWs(reader);
        Assert.assertEquals((int) 'p', reader.read());
    }
    
    @Test
    public void testReadUntil() throws IOException {
        StringReader reader = new StringReader("param1=foo param2=bar");
        String res = FxReaderUtils.readUntil(reader, '=', true);
        Assert.assertEquals("param1=", res);
    }
    
    @Test
    public void testReadUntil_exclude() throws IOException {
        StringReader reader = new StringReader("param1=foo param2=bar");
        String res = FxReaderUtils.readUntil(reader, '=', false);
        Assert.assertEquals("param1", res);
    }
}
