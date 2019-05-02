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
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author braully
 */
//TODO: Implement DW for statistical entities information
@Component
public class StatisticalConsolidation {

    static {

    }

    @Autowired
    GenericDAO genericDAO;

    @PostConstruct
    void loadInitialData() {

    }

    public int countEntity(Class classe) {
        int count = 0;
        try {
            count = genericDAO.count(classe);
        } catch (Exception e) {

        }
        return count;
    }
}
