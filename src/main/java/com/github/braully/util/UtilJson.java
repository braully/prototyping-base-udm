package com.github.braully.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.braully.domain.Menu;
import java.util.List;

/**
 *
 * @author strike
 */
public class UtilJson {
    
    public static String toJson(List<Menu> userMenus) {
        try {
            return new ObjectMapper().writeValueAsString(userMenus);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String toJsonIgnoreFields(Object value, String... ignoredFilds) {
        ObjectMapper mapper = new ObjectMapper();
        try {
//            FilterProvider filter = new SimpleFilterProvider()
//                    .addFilter("ignoreFileds", SimpleBeanPropertyFilter.serializeAllExcept(ignoredFilds));
            if (ignoredFilds != null) {
                for (String prop : ignoredFilds) {
                    mapper.writer().withoutAttribute(prop);
                }
            }
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
