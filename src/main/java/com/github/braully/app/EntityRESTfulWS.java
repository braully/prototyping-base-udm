package com.github.braully.app;

import com.github.braully.persistence.GenericDAO;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.github.braully.web.DescriptorExposedEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.braully.domain.Account;
import com.github.braully.domain.Address;
import com.github.braully.domain.Budget;
import com.github.braully.domain.City;
import com.github.braully.domain.Email;
import com.github.braully.domain.FinancialAccount;
import com.github.braully.domain.GenericType;
import com.github.braully.domain.GenericValue;
import com.github.braully.domain.Inventory;
import java.util.Map;
import com.github.braully.domain.Partner;
import com.github.braully.domain.Phone;
import com.github.braully.domain.Product;
import com.github.braully.domain.PurchaseOrder;
import com.github.braully.domain.PurchaseOrderItem;
import com.github.braully.domain.Task;
import com.github.braully.domain.UserMessage;
import com.github.braully.domain.util.EntityDummy;
import com.github.braully.persistence.EntitySearch;
import com.github.braully.persistence.IEntity;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.util.Collections;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author braully
 */
@RestController
@RequestMapping(path = "/app")
public class EntityRESTfulWS {

    private static final Logger log = Logger.getLogger(EntityRESTfulWS.class);

    public static final Map<String, DescriptorExposedEntity> EXPOSED_ENTITY;

    public ObjectMapper objectMapper = new ObjectMapper();

    {
//        SerializationFeature.
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
    }

    static {
        Map<String, DescriptorExposedEntity> tmp_EXPOSED_ENTITY = new HashMap<>();
        tmp_EXPOSED_ENTITY.put("account", new DescriptorExposedEntity(Account.class));
        tmp_EXPOSED_ENTITY.put("address", new DescriptorExposedEntity(Address.class));
        tmp_EXPOSED_ENTITY.put("budget", new DescriptorExposedEntity(Budget.class));
        tmp_EXPOSED_ENTITY.put("city", new DescriptorExposedEntity(City.class));
        tmp_EXPOSED_ENTITY.put("email", new DescriptorExposedEntity(Email.class));
        tmp_EXPOSED_ENTITY.put("financialAccount", new DescriptorExposedEntity(FinancialAccount.class));
        tmp_EXPOSED_ENTITY.put("genericType", new DescriptorExposedEntity(GenericType.class));
        tmp_EXPOSED_ENTITY.put("genericValue", new DescriptorExposedEntity(GenericValue.class));
        tmp_EXPOSED_ENTITY.put("inventory", new DescriptorExposedEntity(Inventory.class));

        tmp_EXPOSED_ENTITY.put("partner", new DescriptorExposedEntity(Partner.class)
                .hiddenForm("phoneticName", "attribute"));

        tmp_EXPOSED_ENTITY.put("phone", new DescriptorExposedEntity(Phone.class));
        tmp_EXPOSED_ENTITY.put("product", new DescriptorExposedEntity(Product.class));

        tmp_EXPOSED_ENTITY.put("purchaseOrder", new DescriptorExposedEntity(PurchaseOrder.class));
        tmp_EXPOSED_ENTITY.put("purchaseOrderItem", new DescriptorExposedEntity(PurchaseOrderItem.class));

        tmp_EXPOSED_ENTITY.put("userMessage", new DescriptorExposedEntity(UserMessage.class));
        tmp_EXPOSED_ENTITY.put("entityDummy", new DescriptorExposedEntity(EntityDummy.class));

        tmp_EXPOSED_ENTITY.put("task", new DescriptorExposedEntity(Task.class)
                .hiddenForm("tags", "childrens").hiddenList("tags", "childrens"));

        EXPOSED_ENTITY = Collections.unmodifiableMap(tmp_EXPOSED_ENTITY);
    }

    @Autowired
    private GenericDAO genericDAO;

    @Autowired
    private EntitySearch entitySearch;

    @RequestMapping(value = {"/rest/{classe}/{id}"}, method = RequestMethod.GET)
    public IEntity getEntity(@PathVariable("classe") String classe,
            @PathVariable("id") Long id) {
        log.info("getEntity()");
        IEntity ret = null;
        DescriptorExposedEntity exposedEntity = EXPOSED_ENTITY.get(classe);
        if (exposedEntity != null) {
            ret = (IEntity) genericDAO.load(id, exposedEntity.getClassExposed());
        }
        return ret;
    }

    @RequestMapping(value = {"/rest/{classe}"},
            method = {RequestMethod.POST},
            consumes = "application/json")
    @ResponseBody
    public IEntity createEntity(@PathVariable("classe") String classe,
            @RequestBody String jsonEntity) {
        log.info("createEntity()");
        IEntity ret = null;
        DescriptorExposedEntity exposedEntity = EXPOSED_ENTITY.get(classe);
        if (exposedEntity != null && jsonEntity != null && !jsonEntity.isEmpty()) {
            try {
                Class classeMapeada = EXPOSED_ENTITY.get(classe).getClassExposed();
                ret = (IEntity) objectMapper.readValue(jsonEntity, classeMapeada);
                genericDAO.saveEntity(ret);
            } catch (IOException ex) {
                log.error("Falha ao obter conteudo do servlet", ex);
            }
        }
        return ret;
    }

    @RequestMapping(value = {"/rest/{classe}/{id}"},
            method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public IEntity updateEntity(@PathVariable("classe") String classe,
            @PathVariable("id") Integer id,
            @RequestBody String jsonEntity) {
        log.info("updateEntity()");
        IEntity entidade = null;
        if (classe != null && !classe.trim().isEmpty()) {
            try {
                Class classeMapeada = null;
                if (jsonEntity != null) {
                    classeMapeada = EXPOSED_ENTITY.get(classe).getClassExposed();
                    entidade = (IEntity) objectMapper.readValue(jsonEntity, classeMapeada);
                }
            } catch (IOException ex) {
                log.error("Falha ao obter conteudo do servlet", ex);
            }
            if (entidade instanceof IEntity) {
                genericDAO.saveEntity((IEntity) entidade);
            }
        }
        return entidade;
    }

    @RequestMapping(value = {"/rest/{classe}/{id}"},
            method = RequestMethod.DELETE)
    public void removeEntity(@PathVariable("classe") String classe,
            @PathVariable("id") Integer id) {
        log.info("removeEntity()");
    }

    @RequestMapping(value = {"/rest/{classe}"},
            method = RequestMethod.GET)
    @ResponseBody
    public List listEntity(@PathVariable("classe") String classe,
            @RequestParam(required = false) Map<String, String> params) {
        log.info("listEntity()");
        List ret = null;
        DescriptorExposedEntity exposedEntity = EXPOSED_ENTITY.get(classe);
        if (exposedEntity != null) {
            Class entityClass = exposedEntity.getClassExposed();
//            String searchMethodName = exposedEntity.getSearchNameMethod();
            String searchMethodName = "";
            params = exposedEntity.sanitizeFilterParams(params);
            List searchEntitys = entitySearch.searchEntitys(entityClass, searchMethodName, params);
            ret = searchEntitys;
        }
        return ret;
    }

    @RequestMapping(value = {"/rest/{classe}/search"},
            method = RequestMethod.POST,
            consumes = "application/json")
    @ResponseBody
    public List searchEntity(@PathVariable("classe") String classe,
            @RequestParam Map<String, String> allRequestParams,
            ModelMap model) {
        log.info("searchEntity()");
        List ret = null;
        Class entityClass = EXPOSED_ENTITY.get(classe).getClassExposed();
        ret = genericDAO.loadCollection(entityClass);
        return ret;
    }
}
