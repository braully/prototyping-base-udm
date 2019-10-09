package com.github.braully.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.braully.domain.Menu;
import java.util.Date;
import java.util.List;

public class UtilConversor {

    public static final float FATOR_CONVERSAO_PX_MM = 2.83f;

    public static String getStringValue(Object o) {
        String ret = null;
        if (o != null) {
            ret = o.toString();
            if (o instanceof Date) {
                ret = UtilDate.formatData((Date) o);
            }
        }
        return ret;
    }


    public float convertePX2MM(int pixels) {
        return pixels / FATOR_CONVERSAO_PX_MM;
    }

    public float converteMM2PX(float mm) {
        return mm * FATOR_CONVERSAO_PX_MM;
    }

    public int roundConverteMM2PX(float mm) {
        return Math.round(this.converteMM2PX(mm));
    }

    public static Integer getIntValue(Object o) {
        if (o != null && o instanceof Number) {
            return ((Number) o).intValue();
        }
        return null;
    }

    public static Long getLongValue(Object o) {
        Long ret = null;
        if (o != null) {
            if (o instanceof Number) {
                ret = ((Number) o).longValue();
            } else {
                String tmpVal = o.toString();
                tmpVal = tmpVal.replaceAll("[a-zA-Z]+", "");
                ret = Long.parseLong(tmpVal);
            }
        }
        return ret;
    }
}
