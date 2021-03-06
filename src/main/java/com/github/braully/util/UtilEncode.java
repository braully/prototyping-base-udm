/*
 Copyright 2109 Braully Rocha

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 
 */
package com.github.braully.util;

import com.github.braully.util.logutil;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 *
 * @author braully
 */
//TODO: Merge with UtilCipher
public class UtilEncode {

    private static final String SEED = "segredo";

    public static String encodeSimples(String str) {
        String ret = null;
        if (str != null && !str.trim().isEmpty()) {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword(SEED);
            ret = encryptor.encrypt(str);
        }
        return ret;
    }

    public static String decodeSimples(String str) {
        String ret = null;
        if (str != null && !str.trim().isEmpty()) {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword(SEED);
            ret = encryptor.decrypt(str);
        }
        return ret;
    }

    public static String appendDv(String str) {
        StringBuilder sb = new StringBuilder(str);
        try {
            sb.append("-").append(UtilCipher.md5(str).charAt(0));
        } catch (Exception ex) {
            logutil.error("fail on check digit", ex);
        }
        return sb.toString();
    }
}
