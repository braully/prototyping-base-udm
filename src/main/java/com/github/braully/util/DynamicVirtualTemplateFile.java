package com.github.braully.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author braully
 */
public class DynamicVirtualTemplateFile {

    static final Logger log = LogManager.getLogger(DynamicVirtualTemplateFile.class);

    /* */
    public static final String DEFAULT_PREFIX = "<!--{";
    public static final String DEFAULT_SUFIX = "}-->";

    /* */
    private Map<String, String> replacePlaceholder = new HashMap<>();
    private Map<String, String> replaceText = new HashMap<>();
    private String sufix = "}-->";
    private String prefix = "<!--{";
    private String template;
    private String output;
    private String resourceName;
    private byte[] cachebytes;

    public DynamicVirtualTemplateFile(String resourceName,
            String template, Map<String, String> replaceMap) {
        this.template = template;
        this.resourceName = resourceName;
        this.replacePlaceholder.putAll(replaceMap);
    }

    public DynamicVirtualTemplateFile(String resourceName, String template,
            Map<String, String> replaceMap, Map<String, String> replaceText) {
        this.template = template;
        this.resourceName = resourceName;
        if (replaceMap != null) {
            this.replacePlaceholder.putAll(replaceMap);
        }
        if (replaceText != null) {
            this.replaceText.putAll(replaceText);
        }
    }

    public DynamicVirtualTemplateFile(String resourceName, String template,
            Map<String, String> replacePlaceholder, String prefix, String sufix) {
        this.template = template;
        this.resourceName = resourceName;
        if (replacePlaceholder != null) {
            this.replacePlaceholder.putAll(replacePlaceholder);
        }
        this.prefix = prefix;
        this.sufix = sufix;
    }

    void proccess() {
        StrBuilder sb = new StrBuilder(template);
        for (Map.Entry<String, String> e : replaceText.entrySet()) {
            sb.replaceAll(e.getKey(), e.getValue());
        }
        StrSubstitutor sub = new StrSubstitutor(replacePlaceholder, prefix, sufix);
        sub.replaceIn(sb);
        sub.setVariablePrefix("{");
        sub.setVariableSuffix("}");
        sub.replaceIn(sb);
        this.output = sb.toString();
        /* Debug output */
        log.debug(this.resourceName);
        log.debug(this.output);
    }

    public Map<String, String> getReplaceMap() {
        return replacePlaceholder;
    }

    public String getOutput() {
        if (output == null) {
            proccess();
        }
        return output;
    }

    private byte[] getCachebytes() {
        if (cachebytes == null) {
            try {
                cachebytes = getOutput().getBytes("UTF-8");
            } catch (UnsupportedEncodingException ex) {
                cachebytes = getOutput().getBytes();
            }
        }
        return cachebytes;
    }

    public URL getURL() {
        URL url = null;
        try {
            url = new URL(null, "gen://" + resourceName,
                    new VirtualUrlStreamHandler(getCachebytes()));
        } catch (Exception ex) {
            log.info("error on url", ex);
        }
        return url;
    }

    static class VirtualUrlStreamHandler extends URLStreamHandler {

        private final byte[] payload;

        public VirtualUrlStreamHandler(byte[] payload) {
            this.payload = payload;
        }

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            return new CustomURLConnection(u);
        }

        private class CustomURLConnection extends URLConnection {

            public CustomURLConnection(URL url) {
                super(url);
            }

            @Override
            public void connect() throws IOException {
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(payload);
            }
        }
    }

    @Override
    public String toString() {
        return "DynamicVirtualTemplateFile{" + " resourceName="
                + resourceName + ", template=" + template + '}';
    }

}
