package com.github.braully.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UtilCipher {

    static final org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger(UtilCipher.class);
    static final Logger log = Logger.getLogger(UtilCipher.class.getName());
    /* */
    private static char[] MD5_HEX = "0123456789abcdef".toCharArray();
    private static final byte[] encoding = {65, 66, 67, 68, 69, 70, 71, 72,
        73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
        90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
        110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122,
        48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47, 61};

    public static String DEFAULT_ALGORITHM = "MD5";
    public static String DEFAULT_HASH_ENCODING = "BASE64";
    public static String DEFAULT_HASH_CHARSET = "UTF-8";

    public static String hashMessage(String strMessage, String cipherMethod) {
        return UtilCipher.hashMessage(cipherMethod, DEFAULT_HASH_ENCODING, DEFAULT_HASH_CHARSET, strMessage);
    }

    public static String hashMessage(String strMessage) {
        return UtilCipher.hashMessage(DEFAULT_ALGORITHM, DEFAULT_HASH_ENCODING, DEFAULT_HASH_CHARSET, strMessage);
    }

    public static String hashMessage(String hashAlgorithm,
            String hashEncoding, String hashCharset,
            String strMessaage) {
        return UtilCipher.hashMessage(hashAlgorithm, hashEncoding, hashCharset, strMessaage, null);
    }

    public static String hashMessage(String hashAlgorithm,
            String hashEncoding, String hashCharset,
            String strMessage, String salt) {
        log4j.info(String.format("algo: %s encoding: %s charset: %s msg: %s salt: %s", hashAlgorithm, hashEncoding, hashCharset, strMessage, salt));
        String strMessageHash = null;
        byte[] saltBytes = null;
        byte[] passBytes;
        try {
            if (hashCharset == null) {
                passBytes = strMessage.getBytes();
                if (salt != null) {
                    saltBytes = salt.getBytes();
                }
            } else {
                passBytes = strMessage.getBytes(hashCharset);
                if (salt != null) {
                    saltBytes = salt.getBytes();
                }
            }
        } catch (UnsupportedEncodingException uee) {
            log.log(Level.SEVERE, "charset " + hashCharset
                    + " not found. Using platform default.", uee);
            passBytes = strMessage.getBytes();
            if (salt != null) {
                saltBytes = salt.getBytes();
            }
        }

        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
            if (salt != null && !salt.isEmpty()) {
                md.update(saltBytes);
            }
            byte[] hash = md.digest(passBytes);
            if (hashEncoding.equalsIgnoreCase("BASE64")) {
                strMessageHash = encodeBase64(hash);
            } else if (hashEncoding.equalsIgnoreCase("HEX")) {
                strMessageHash = encodeBase16(hash);
            } else if (hashEncoding.equalsIgnoreCase("RFC2617")) {
                strMessageHash = encodeRFC2617(hash);
            } else {
                log.log(Level.SEVERE, "Unsupported hash encoding format "
                        + hashEncoding);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Password hash calculation failed ", e);
        }
        return strMessageHash;
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
        //"ISO-8859-1"
        return out.toString(DEFAULT_HASH_ENCODING);
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

    public static String MD5(String string) {
        String ret = null;
        String text = "";
        try {
            if (string != null) {
                text = string;
            }
            MessageDigest md;
            md = MessageDigest.getInstance("MD5");
            byte[] md5hash;
//            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            md5hash = md.digest(text.getBytes("iso-8859-1"));
            ret = convertToHex(md5hash);
        } catch (Exception e) {
            log4j.warn("Falha ao cacluar md5 da string: " + string, e);
        }
        return ret;
    }

    public static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /*
    
     */
    public static void main(String... args) {
        String strAlgorithm = null;
        String strMsg = null;
        String strSalt = null;

        Option algorithm = new Option("a", "algorithm", true, "Algorithm for cipher");
        Options options = new Options();
        options.addOption(algorithm);

        Option msg = new Option("m", "message", true, "Message for cipher");
//        msg.setRequired(true);
        options.addOption(msg);

        Option salt = new Option("s", "salt", true, "Salt for cipher message");
        options.addOption(salt);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp(UtilCipher.class.getName(), options);
            return;
        }

        strAlgorithm = cmd.getOptionValue("algorithm");
        strMsg = cmd.getOptionValue("message");
        strSalt = cmd.getOptionValue("salt");

        if (strAlgorithm == null) {
            strAlgorithm = DEFAULT_ALGORITHM;
        }
        String strEncoding = DEFAULT_HASH_ENCODING;
        String strCharset = DEFAULT_HASH_CHARSET;
//        String hashMessage = hashMessage(strAlgorithm, strEncoding, strCharset, strMsg, strSalt);
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        strMsg = "user";
        String hashMessage = bcrypt.encode(strMsg);
        System.out.println(hashMessage);
    }
}
