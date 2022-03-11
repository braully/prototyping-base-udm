package com.github.braully.web.jsf;

import com.github.braully.app.exposed;
import com.github.braully.domain.Role;
import com.github.braully.domain.UserLogin;
import com.github.braully.web.DescriptorExposedEntity;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author braully
 */
public class autogenweb {

    private static final Map<String, DescriptorExposedEntity> AUTOGEN_WEB = new HashMap<>();

    private static void putAutogenWeb(String entityname, DescriptorExposedEntity descriptorExposedEntity) {
        AUTOGEN_WEB.put(entityname.toLowerCase(), descriptorExposedEntity);
    }

    public static boolean isExposed(Class classe) {
        boolean exp = exposed.isExposed(classe);
        if (!exp) {
            String simpleName = classe.getSimpleName();
            exp = AUTOGEN_WEB.containsKey(simpleName.toLowerCase());
        }
        return exp;
    }

    public static DescriptorExposedEntity getExposedEntity(String entityName) {
        DescriptorExposedEntity exposedEntity = exposed.getExposedEntity(entityName);
        if (exposedEntity == null) {
            exposedEntity = AUTOGEN_WEB.get(entityName.toLowerCase());
        }
        return exposedEntity;
    }

    /* Exposed entities configuration */
    static {
        putAutogenWeb("userLogin", new DescriptorExposedEntity(UserLogin.class)
                .hidden("password", "passwordType", "menus")
                .hiddenForm("lastLogin", "sysRole", "roles", "organizationRole"));

        putAutogenWeb("role", new DescriptorExposedEntity(Role.class)
                .hidden("parent", "childs", "sysRole", "menus")
        );

    }
}
