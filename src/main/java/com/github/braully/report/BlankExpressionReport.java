package com.github.braully.report;

import java.util.Map;

public class BlankExpressionReport implements ar.com.fdvs.dj.domain.CustomExpression {

    @Override
    public Object evaluate(Map map, Map map1, Map map2) {
        return " ";
    }

    @Override
    public String getClassName() {
        return String.class.getName();
    }
}
