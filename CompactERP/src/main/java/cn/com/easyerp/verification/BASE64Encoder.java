package cn.com.easyerp.verification;

import java.io.IOException;
import java.io.OutputStream;

public class BASE64Encoder extends CharacterEncoder {
    protected int bytesPerAtom() {
        return 3;
    }

    protected int bytesPerLine() {
        return 57;
    }

    private static final char[] pem_array = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', '+', '/' };

    protected void encodeAtom(OutputStream outStream, byte[] data, int offset, int len) throws IOException {
        if (len == 1) {

            byte a = data[offset];
            byte b = 0;
            // byte c = 0;
            outStream.write(pem_array[a >>> 2 & 0x3F]);
            outStream.write(pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
            outStream.write(61);
            outStream.write(61);
        } else if (len == 2) {

            byte a = data[offset];
            byte b = data[offset + 1];
            byte c = 0;
            outStream.write(pem_array[a >>> 2 & 0x3F]);
            outStream.write(pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
            outStream.write(pem_array[(b << 2 & 0x3C) + (c >>> 6 & 0x3)]);
            outStream.write(61);
        } else {

            byte a = data[offset];
            byte b = data[offset + 1];
            byte c = data[offset + 2];
            outStream.write(pem_array[a >>> 2 & 0x3F]);
            outStream.write(pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
            outStream.write(pem_array[(b << 2 & 0x3C) + (c >>> 6 & 0x3)]);
            outStream.write(pem_array[c & 0x3F]);
        }
    }
}
