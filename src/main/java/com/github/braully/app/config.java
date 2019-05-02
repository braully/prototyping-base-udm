package com.github.braully.app;

import com.github.braully.domain.Menu;
import com.github.braully.domain.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class config {

    public static Locale DEFAULT_LOCALE = Locale.getDefault();
    public static String DEFAULT_COUNTRY = DEFAULT_LOCALE.getCountry();
    public static String DEFAULT_LANGUAGE = DEFAULT_LOCALE.getLanguage();

    public static Role ADM = new Role().name("Administrador");

    public static List<Menu> MENUS_APP = new ArrayList<>();

    static {
        MENUS_APP.add(new Menu());
    }

}
