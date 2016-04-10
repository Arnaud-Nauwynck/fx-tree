package fr.an.fxtree.format.util;

import java.io.IOException;
import java.io.Reader;

public class FxReaderUtils {

    public static String readUntil(Reader reader, String endMarker) throws IOException {
        StringBuilder sb = new StringBuilder();
        final int endMarkerLen = endMarker.length();
        for(;;) {
            int ch = reader.read();
            if (ch == -1) {
                return sb.toString();
            }
            sb.append((char) ch);
            int sbLen = sb.length();
            if (sbLen >= endMarker.length()) {
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


    
}
