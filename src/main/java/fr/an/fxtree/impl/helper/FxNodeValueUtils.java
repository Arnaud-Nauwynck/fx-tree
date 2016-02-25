package fr.an.fxtree.impl.helper;

import java.util.ArrayList;
import java.util.List;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxNode;

public class FxNodeValueUtils {

    private static final String STRING_ARRAY_FORMAT = "CSV 'str1,str2...' or array ['str1, 'str2'..]";
    private static final String STRING_FLATTEN_ARRAY_FORMAT = "CSV 'str1,str2...' or array ['str1, 'str2'..] or flattenize [ 'str1', [ 'str2', 'str3' ]]";
    
    public static String[] extractStringArray(FxNode value, boolean allowRecurseFlatten) {
        if (value == null) return null;
        String[] res;
        if (value.isTextual()) {
            res = value.textValue().split(",");
        } else if (value.isArray()) {
            FxArrayNode array = (FxArrayNode) value;
            int len = array.size();
            List<String> tmpRes = new ArrayList<String>(len);
            for(int i = 0; i < len; i++) {
                FxNode child = array.get(i);
                if (child.isTextual()) {
                    tmpRes.add(child.textValue());
                } else if (value.isArray()) {
                    if (allowRecurseFlatten) {
                        // recurse
                        String[] tmpresElts = extractStringArray(child, allowRecurseFlatten);
                        for(String e : tmpresElts) {
                            tmpRes.add(e);
                        }
                    } else {
                        throw new IllegalArgumentException("expected " + STRING_ARRAY_FORMAT);
                    }
                } else {
                    throw new IllegalArgumentException("expected " + STRING_FLATTEN_ARRAY_FORMAT);
                }
            }
            res = tmpRes.toArray(new String[tmpRes.size()]);
        } else {
            throw new IllegalArgumentException("expected " + (allowRecurseFlatten? STRING_FLATTEN_ARRAY_FORMAT : STRING_ARRAY_FORMAT));
        }
        return res;
    }
    
}
