package fr.an.fxtree.format.util;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.function.Supplier;

import fr.an.fxtree.format.json.FxJsonUtils;
import fr.an.fxtree.format.memmaplist.Fx2MemMapListUtils;
import fr.an.fxtree.model.FxNode;

public final class FxReaderUtils {

    /** private to force all static */
    private FxReaderUtils() {
    }
    
    public static void skipWs(PushbackReader reader) throws IOException {
        for(;;) {
            int readCh = reader.read();
            if (readCh == -1) {
                break;
            }
            char ch = (char) readCh;
            if (! Character.isWhitespace(ch)) {
                reader.unread(readCh);
                break;
            }
        }
    }
    
    public static String readUntil(Reader reader, final String endMarker) throws IOException {
        StringBuilder sb = new StringBuilder();
        final int endMarkerLen = endMarker.length();
        for(;;) {
            int ch = reader.read();
            if (ch == -1) {
                return sb.toString();
            }
            sb.append((char) ch);
            int sbLen = sb.length();
            if (sbLen >= endMarkerLen) {
                boolean endWith = true;
                for(int i = 0, index = sbLen - endMarkerLen; i < endMarkerLen; i++) {
                    if (endMarker.charAt(i) != sb.charAt(index)) {
                        endWith = false;
                        break;
                    }
                }
                if (endWith) {
                    break;
                }
            }
        }
        return sb.toString();
    }

    public static String readUntil(Reader reader, final char endMarker, boolean include) throws IOException {
        StringBuilder sb = new StringBuilder();
        for(;;) {
            int readCh = reader.read();
            if (readCh == -1) {
                return sb.toString();
            }
            char ch = (char) readCh;
            if (endMarker == ch) {
                if (include) {
                    sb.append(ch);
                }
                break;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public static String readUntilWS(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        for(;;) {
            int readCh = reader.read();
            if (readCh == -1) {
                return sb.toString();
            }
            char ch = (char) readCh;
            if (Character.isWhitespace(ch)) {
                break;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public static FxNode readNameExpr(PushbackReader reader) throws IOException {
        return readNameOrPathExpr(reader, false);
    }
    
    public static FxNode readNameOrPathExpr(PushbackReader reader, boolean acceptSlashPath) throws IOException {
        FxNode res;
        int readCh = reader.read();
        if (readCh == -1) {
            return null;
        }
        char ch = (char) readCh;
        reader.unread(readCh);
        if (Character.isJavaIdentifierPart(ch)
                || (ch == '/' && acceptSlashPath)) {
            // identifier or path: example: abc, a12, _123, /a/b/1, a/b/1, ... 
            String tmpres = FxReaderUtils.readUntilWS(reader);
            FxReaderUtils.skipWs(reader);
            res = Fx2MemMapListUtils.valueToTree(tmpres);
        } else if (ch == '\"') {
            // double-quoted string: "abc/1 /..."
            String tmpres = FxReaderUtils.readUntil(reader, '\"', false);
            res = Fx2MemMapListUtils.valueToTree(tmpres);
        } else if (ch == '\'') {
            // single-quoted string: 'abc/1 /...'
            String tmpres = FxReaderUtils.readUntil(reader, '\'', false);
            res = Fx2MemMapListUtils.valueToTree(tmpres);
        } else if (ch == '{') {
            // read json object (..to be interpreted as string expression)
            reader.unread(readCh);
            Supplier<FxNode> jsonPartialParser = FxJsonUtils.createPartialParser(reader);
            res = jsonPartialParser.get();            
        } else {
            reader.unread(readCh);
            throw new IllegalArgumentException("expecting identifier, or single/double-quoted string, or json { } expression");
        }
        return res;
    }
    
}
