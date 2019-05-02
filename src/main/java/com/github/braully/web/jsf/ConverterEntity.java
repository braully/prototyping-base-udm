package com.github.braully.web.jsf;

import com.github.braully.persistence.IEntity;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("view")
@Component("converterEntity")
@Qualifier("converterEntity")
@FacesConverter("converterEntity")
@SuppressWarnings("unchecked")
public class ConverterEntity implements Converter {

    private static final Logger log = Logger.getLogger(ConverterEntity.class);

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        IEntity ret = null;
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
                            IEntity val = (IEntity) item.getItemValue();
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
                List<IEntity> values = (List<IEntity>) itens.getValue();
                if (values != null) {
                    try {
                        for (IEntity val : values) {
                            if (arg2.equals("" + val.getId())) {
                                ret = val;
                                break;
                            }
                        }
                    } catch (ClassCastException e) {
                        log.debug("Falha ao converter entidades", e);
                        try {
                            for (SelectItemGroup is : (List<SelectItemGroup>) itens.getValue()) {
                                for (SelectItem i : is.getSelectItems()) {
                                    IEntity val = (IEntity) i.getValue();
                                    if (arg2.equals("" + val.getId())) {
                                        ret = val;
                                        break;
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            log.error("Falha ao converter selctgroupitems", ex);
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
        try {
            if (arg2 != null) {
                Object id = null;
                if (arg2 instanceof IEntity) {
                    IEntity m = (IEntity) arg2;
                    id = m.getId();
                    if (id != null) {
                        ret = id.toString();
                    }
                } else if (arg2 instanceof String) {
                    ret = (String) arg2;
                } else {
                    id = PropertyUtils.getProperty(arg2, "id");
                }
                if (id != null) {
                    ret = id.toString();
                }
            }
        } catch (java.lang.NoSuchMethodException e) {
        } catch (Exception e) {
            log.debug("Não foi possivel converter: " + arg2, e);
        }
        return ret;
    }
}
