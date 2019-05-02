package com.github.braully.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;


public class UtilSimpleCrypt {

    static final Logger log = Logger.getLogger(UtilSimpleCrypt.class);

    private static char[] MD5_HEX = "0123456789abcdef".toCharArray();

    private static final byte[] encoding = {65, 66, 67, 68, 69, 70, 71, 72,
            73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
            90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
            110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122,
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47, 61};

    public static String hashPassword(String user, String password) {
        return createPasswordHash("MD5", "Base64", "UTF-8", user, password);
    }

    public static String createPasswordHash(String hashAlgorithm,
                                            String hashEncoding, String hashCharset, String username,
                                            String password) {
        String passwordHash = null;
        byte[] passBytes;
        try {
            if (hashCharset == null) {
                passBytes = password.getBytes();
            } else {
                passBytes = password.getBytes(hashCharset);
            }
        } catch (UnsupportedEncodingException uee) {
            log.error("charset " + hashCharset
                    + " not found. Using platform default.", uee);
            passBytes = password.getBytes();
        }

        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
            md.update(passBytes);
            byte[] hash = md.digest();
            if (hashEncoding.equalsIgnoreCase("BASE64")) {
                passwordHash = encodeBase64(hash);
            } else if (hashEncoding.equalsIgnoreCase("HEX")) {
                passwordHash = encodeBase16(hash);
            } else if (hashEncoding.equalsIgnoreCase("RFC2617")) {
                passwordHash = encodeRFC2617(hash);
            } else {
                log.error("Unsupported hash encoding format " + hashEncoding);
            }
        } catch (Exception e) {
            log.error("Password hash calculation failed ", e);
        }
        return passwordHash;
    }

    public static String encodeRFC2617(byte[] data) {
        char[] hash = new char[32];
        for (int i = 0; i < 16; ++i) {
            int j = data[i] >> 4 & 0xF;
            hash[(i * 2)] = MD5_HEX[j];
            j = data[i] & 0xF;
            hash[(i * 2 + 1)] = MD5_HEX[j];
        }
        return new String(hash);
    }

    public static String encodeBase16(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            byte b = bytes[i];

            char c = (char) (b >> 4 & 0xF);
            if (c > '\t') {
                c = (char) (c - '\n' + 97);
            } else {
                c = (char) (c + '0');
            }
            sb.append(c);

            c = (char) (b & 0xF);
            if (c > '\t') {
                c = (char) (c - '\n' + 97);
            } else {
                c = (char) (c + '0');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String encodeBase64(byte[] bytes) {
        String base64 = null;
        try {
            // base64 = encode(bytes);
            base64 = Base64.encodeBase64String(bytes);
        } catch (Exception e) {
        }
        return base64;
    }

    public static String encode(byte[] bytes) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        process(in, out);
        return out.toString("ISO-8859-1");
    }

    private static void process(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1024];
        int got = -1;
        int off = 0;
        int count = 0;
        while ((got = in.read(buffer, off, 1024 - off)) > 0) {
            if (got >= 3) {
                got += off;
                off = 0;
                while (off + 3 <= got) {
                    int c1 = get1(buffer, off);
                    int c2 = get2(buffer, off);
                    int c3 = get3(buffer, off);
                    int c4 = get4(buffer, off);
                    switch (count) {
                        case 73:
                            out.write(encoding[c1]);
                            out.write(encoding[c2]);
                            out.write(encoding[c3]);
                            out.write(10);
                            out.write(encoding[c4]);
                            count = 1;
                            break;
                        case 74:
                            out.write(encoding[c1]);
                            out.write(encoding[c2]);
                            out.write(10);
                            out.write(encoding[c3]);
                            out.write(encoding[c4]);
                            count = 2;
                            break;
                        case 75:
                            out.write(encoding[c1]);
                            out.write(10);
                            out.write(encoding[c2]);
                            out.write(encoding[c3]);
                            out.write(encoding[c4]);
                            count = 3;
                            break;
                        case 76:
                            out.write(10);
                            out.write(encoding[c1]);
                            out.write(encoding[c2]);
                            out.write(encoding[c3]);
                            out.write(encoding[c4]);
                            count = 4;
                            break;
                        default:
                            out.write(encoding[c1]);
                            out.write(encoding[c2]);
                            out.write(encoding[c3]);
                            out.write(encoding[c4]);
                            count += 4;

                    }
                    off += 3;
                }

                for (int i = 0; i < 3; ++i) {
                    buffer[i] = ((i < got - off) ? buffer[(off + i)] : 0);
                }
                off = got - off;
            }
            off += got;
        }

        switch (off) {
            case 1:
                out.write(encoding[get1(buffer, 0)]);
                out.write(encoding[get2(buffer, 0)]);
                out.write(61);
                out.write(61);
                break;
            case 2:
                out.write(encoding[get1(buffer, 0)]);
                out.write(encoding[get2(buffer, 0)]);
                out.write(encoding[get3(buffer, 0)]);
                out.write(61);

        }
    }

    private static int get1(byte[] buf, int off) {
        return ((buf[off] & 0xFC) >> 2);
    }

    private static int get2(byte[] buf, int off) {
        return ((buf[off] & 0x3) << 4 | (buf[(off + 1)] & 0xF0) >>> 4);
    }

    private static int get3(byte[] buf, int off) {
        return ((buf[(off + 1)] & 0xF) << 2 | (buf[(off + 2)] & 0xC0) >>> 6);
    }

    private static int get4(byte[] buf, int off) {
        return (buf[(off + 2)] & 0x3F);
    }
}
