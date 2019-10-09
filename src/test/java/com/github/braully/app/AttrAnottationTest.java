package com.github.braully.app;

import com.github.braully.constant.Attr;
import com.github.braully.constant.Attrs;
import com.github.braully.domain.AccountTransaction;
import java.lang.annotation.Annotation;
import org.junit.Test;

/**
 *
 * @author braully
 */
public class AttrAnottationTest {

    @Test
    public void testAttrAnottation() throws NoSuchFieldException {
        Annotation[] annotations = ObjAttr.class.getDeclaredField("field").getAnnotations();
        for (Annotation a : annotations) {
            System.out.println(a);
        }

        annotations = AccountTransaction.class.getDeclaredField("creditTotal").getAnnotations();
        for (Annotation a : annotations) {
            System.out.println(a);
        }
    }

    class ObjAttr {

        @Attrs(
                @Attr(name = "teste", value = "teste")
        )
        String field;
    }
}
