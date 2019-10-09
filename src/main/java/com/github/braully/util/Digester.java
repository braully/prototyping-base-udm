package com.github.braully.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Digester {

    private static final Logger log = LogManager.getLogger(Digester.class);

    /**
     *
     * @param string
     * @return
     */
    public String MD5(String string) {
        String ret = null;
        String text = "";
        try {
            if (string != null) {
                text = string;
            }
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5hash = new byte[32];
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            md5hash = md.digest();
            ret = convertToHex(md5hash);
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
        }
        return ret;
    }

    private String convertToHex(byte[] data) {
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

    public static void main(String[] args) {
        String string = null;
        Digester d = new Digester();
        if (args != null && args.length > 0) {
            string = args[0];
        } else {
            try {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(System.in));
                string = bufferedReader.readLine();
            } catch (IOException e) {
                log.error("erro", e);
            }
        }

        System.out.println(d.MD5(string));
    }
}
