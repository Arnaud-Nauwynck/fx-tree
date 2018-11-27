package fr.an.fxtree.model;

import java.io.IOException;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;

public abstract class FxTextNode extends FxValueNode {

    // ------------------------------------------------------------------------

    protected FxTextNode(FxContainerNode parent, FxChildId childId, FxSourceLoc sourceLoc) {
        super(parent, childId, sourceLoc);
    }

    // ------------------------------------------------------------------------

    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.STRING;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitTextValue(this);
    }

    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitTextValue(this, param);
    }

    public abstract String getValue();

    public abstract void setValue(String value);

    @Override
    public String textValue() {
        return getValue();
    }

    /**
     * Method for accessing textual contents assuming they were base64 encoded;
     * if so, they are decoded and resulting binary data is returned.
     */
    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
        String _value = getValue();
        ByteArrayBuilder builder = new ByteArrayBuilder(100);
        final String str = _value;
        int ptr = 0;
        int len = str.length();

        main_loop: while (ptr < len) {
            // first, we'll skip preceding white space, if any
            char ch;
            do {
                ch = str.charAt(ptr++);
                if (ptr >= len) {
                    break main_loop;
                }
            } while (ch <= ' ');
            int bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
                _reportInvalidBase64(b64variant, ch, 0);
            }
            int decodedData = bits;
            // then second base64 char; can't get padding yet, nor ws
            if (ptr >= len) {
                _reportBase64EOF();
            }
            ch = str.charAt(ptr++);
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
                _reportInvalidBase64(b64variant, ch, 1);
            }
            decodedData = (decodedData << 6) | bits;
            // third base64 char; can be padding, but not ws
            if (ptr >= len) {
                // but as per [JACKSON-631] can be end-of-input, iff not using
                // padding
                if (!b64variant.usesPadding()) {
                    // Got 12 bits, only need 8, need to shift
                    decodedData >>= 4;
                    builder.append(decodedData);
                    break;
                }
                _reportBase64EOF();
            }
            ch = str.charAt(ptr++);
            bits = b64variant.decodeBase64Char(ch);

            // First branch: can get padding (-> 1 byte)
            if (bits < 0) {
                if (bits != Base64Variant.BASE64_VALUE_PADDING) {
                    _reportInvalidBase64(b64variant, ch, 2);
                }
                // Ok, must get padding
                if (ptr >= len) {
                    _reportBase64EOF();
                }
                ch = str.charAt(ptr++);
                if (!b64variant.usesPaddingChar(ch)) {
                    _reportInvalidBase64(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                }
                // Got 12 bits, only need 8, need to shift
                decodedData >>= 4;
                builder.append(decodedData);
                continue;
            }
            // Nope, 2 or 3 bytes
            decodedData = (decodedData << 6) | bits;
            // fourth and last base64 char; can be padding, but not ws
            if (ptr >= len) {
                // but as per [JACKSON-631] can be end-of-input, iff not using
                // padding
                if (!b64variant.usesPadding()) {
                    decodedData >>= 2;
                    builder.appendTwoBytes(decodedData);
                    break;
                }
                _reportBase64EOF();
            }
            ch = str.charAt(ptr++);
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
                if (bits != Base64Variant.BASE64_VALUE_PADDING) {
                    _reportInvalidBase64(b64variant, ch, 3);
                }
                decodedData >>= 2;
                builder.appendTwoBytes(decodedData);
            } else {
                // otherwise, our triple is now complete
                decodedData = (decodedData << 6) | bits;
                builder.appendThreeBytes(decodedData);
            }
        }
        return builder.toByteArray();
    }

    @Override
    public byte[] binaryValue() {
        try {
            return getBinaryValue(Base64Variants.getDefaultVariant());
        } catch (IOException ex) {
            throw new RuntimeException("Failed", ex);
        }
    }

    @Override
    public String asText() {
        return getValue();
    }

    @Override
    public String asText(String defaultValue) {
        String value = getValue();
        return (value == null) ? defaultValue : value;
    }

    @Override
    public boolean asBoolean(boolean defaultValue) {
        String value = getValue();
        if (value != null) {
            String v = value.trim();
            if ("true".equals(v)) {
                return true;
            }
            if ("false".equals(v)) {
                return false;
            }
        }
        return defaultValue;
    }

    @Override
    public int asInt(int defaultValue) {
        return NumberInput.parseAsInt(getValue(), defaultValue);
    }

    @Override
    public long asLong(long defaultValue) {
        return NumberInput.parseAsLong(getValue(), defaultValue);
    }

    @Override
    public double asDouble(double defaultValue) {
        return NumberInput.parseAsDouble(getValue(), defaultValue);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof FxTextNode) {
            return ((FxTextNode) o).getValue().equals(getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public String toString() {
        String value = getValue();
        int len = value.length();
        len = len + 2 + (len >> 4);
        StringBuilder sb = new StringBuilder(len);
        appendQuoted(sb, value);
        return sb.toString();
    }

    protected static void appendQuoted(StringBuilder sb, String content) {
        sb.append('"');
        CharTypes.appendQuoted(sb, content);
        sb.append('"');
    }

    protected void _reportInvalidBase64(Base64Variant b64variant, char ch, int bindex) throws JsonParseException {
        _reportInvalidBase64(b64variant, ch, bindex, null);
    }

    /**
     * @param bindex
     *            Relative index within base64 character unit; between 0 and 3
     *            (as unit has exactly 4 characters)
     */
    protected void _reportInvalidBase64(Base64Variant b64variant, char ch, int bindex, String msg) throws JsonParseException {
        String base;
        if (ch <= ' ') {
            base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1)
                + " of 4-char base64 unit: can only used between units";
        } else if (b64variant.usesPaddingChar(ch)) {
            base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1)
                + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
        } else if (!Character.isDefined(ch) || Character.isISOControl(ch)) {
            // Not sure if we can really get here... ? (most illegal xml chars
            // are caught at lower level)
            base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
        } else {
            base = "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
        }
        if (msg != null) {
            base = base + ": " + msg;
        }
        throw new JsonParseException(null, base, JsonLocation.NA);
    }

    protected void _reportBase64EOF() throws JsonParseException {
        throw new JsonParseException(null, "Unexpected end-of-String when base64 content");
    }
    
}
