package com.github.braully.web.jsf;

import com.github.braully.domain.util.GenericTO;
import com.github.braully.persistence.IEntity;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("view")
@Component("converterGenericTO")
@Qualifier("converterGenericTO")
@FacesConverter("converterGenericTO")
@SuppressWarnings("unchecked")
public class ConverterGenericTO implements Converter {

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        GenericTO ret = null;
        UIComponent fonte = arg1;
        if (fonte != null) {
            List<UIComponent> childs = fonte.getChildren();
            UISelectItems itens = null;
            if (childs != null) {
                for (UIComponent ui : childs) {
                    if (ui instanceof UISelectItems) {
                        itens = (UISelectItems) ui;
                        break;
                    } else if (ui instanceof UISelectItem) {
                        UISelectItem item = (UISelectItem) ui;
                        try {
                            GenericTO val = (GenericTO) item.getItemValue();
                            if (arg2.equals("" + val.getId())) {
                                ret = val;
                                break;
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }

            if (itens != null) {
                List<GenericTO> values = (List<GenericTO>) itens.getValue();
                if (values != null) {
                    for (GenericTO val : values) {
                        if (arg2.equals("" + val.getId())) {
                            ret = val;
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        String ret = "";
        if (arg2 != null) {
            if (arg2 instanceof GenericTO) {
                GenericTO m = (GenericTO) arg2;
                if (m != null) {
                    Long id = m.getId();
                    if (id != null) {
                        ret = id.toString();
                    }
                }
            } else if (arg2 instanceof IEntity) {
                IEntity e = (IEntity) arg2;
                if (e != null && e.getId() != null) {
                    ret = e.getId().toString();
                }
            }
        }
        return ret;
    }
}
