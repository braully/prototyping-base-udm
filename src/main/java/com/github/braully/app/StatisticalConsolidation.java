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
package com.github.braully.app;

import com.github.braully.persistence.GenericDAO;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author braully
 */
//TODO: Implement DW for statistical entities information
//Static or Instance?
@Component
public class StatisticalConsolidation {

    static StatisticalConsolidation instance;

    private static Map<String, Integer> countEntites = new HashMap<>();

    static {
        countEntites.put("City", 5000);
    }

    public static StatisticalConsolidation instance() {
        if (instance == null) {
            instance = new StatisticalConsolidation();
        }
        return instance;
    }

    @Autowired
    GenericDAO genericDAO;

    @PostConstruct
    void loadInitialData() {

    }

    public int countEntity(Class classe) {
        int count = 0;
        try {
            //count = genericDAO.count(classe);
            count = countEntites.get(classe.getSimpleName());
        } catch (Exception e) {
        }
        return count;
    }
}
