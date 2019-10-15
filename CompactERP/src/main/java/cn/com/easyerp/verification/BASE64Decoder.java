package cn.com.easyerp.verification;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class BASE64Decoder extends CharacterDecoder {
    private static final char[] pem_array;
    private static final byte[] pem_convert_array;
    byte[] decode_buffer;

    public BASE64Decoder() {
        this.decode_buffer = new byte[4];
    }

    @Override
    protected int bytesPerAtom() {
        return 4;
    }

    @Override
    protected int bytesPerLine() {
        return 72;
    }

    @Override
    protected void decodeAtom(final PushbackInputStream inStream, final OutputStream outStream, int rem)
            throws IOException {
        byte a = -1;
        byte b = -1;
        byte c = -1;
        byte d = -1;
        if (rem < 2) {
            throw new CEFormatException("BASE64Decoder: Not enough bytes for an atom.");
        }
        int i;
        do {
            i = inStream.read();
            if (i == -1) {
                throw new CEStreamExhausted();
            }
        } while (i == 10 || i == 13);
        this.decode_buffer[0] = (byte) i;
        i = this.readFully(inStream, this.decode_buffer, 1, rem - 1);
        if (i == -1) {
            throw new CEStreamExhausted();
        }
        if (rem > 3 && this.decode_buffer[3] == 61) {
            rem = 3;
        }
        if (rem > 2 && this.decode_buffer[2] == 61) {
            rem = 2;
        }
        switch (rem) {
        case 4: {
            d = BASE64Decoder.pem_convert_array[this.decode_buffer[3] & 0xFF];
        }
        case 3: {
            c = BASE64Decoder.pem_convert_array[this.decode_buffer[2] & 0xFF];
        }
        case 2: {
            b = BASE64Decoder.pem_convert_array[this.decode_buffer[1] & 0xFF];
            a = BASE64Decoder.pem_convert_array[this.decode_buffer[0] & 0xFF];
            break;
        }
        }
        switch (rem) {
        case 2: {
            outStream.write((byte) ((a << 2 & 0xFC) | (b >>> 4 & 0x3)));
            break;
        }
        case 3: {
            outStream.write((byte) ((a << 2 & 0xFC) | (b >>> 4 & 0x3)));
            outStream.write((byte) ((b << 4 & 0xF0) | (c >>> 2 & 0xF)));
            break;
        }
        case 4: {
            outStream.write((byte) ((a << 2 & 0xFC) | (b >>> 4 & 0x3)));
            outStream.write((byte) ((b << 4 & 0xF0) | (c >>> 2 & 0xF)));
            outStream.write((byte) ((c << 6 & 0xC0) | (d & 0x3F)));
            break;
        }
        }
    }

    static {
        pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
                'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9', '+', '/' };
        pem_convert_array = new byte[256];
        for (int i = 0; i < 255; ++i) {
            BASE64Decoder.pem_convert_array[i] = -1;
        }
        for (int i = 0; i < BASE64Decoder.pem_array.length; ++i) {
            BASE64Decoder.pem_convert_array[BASE64Decoder.pem_array[i]] = (byte) i;
        }
    }
}
