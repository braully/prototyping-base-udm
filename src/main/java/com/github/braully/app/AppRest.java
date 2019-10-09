package com.github.braully.app;

import com.github.braully.persistence.GenericDAO;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.github.braully.web.DescriptorExposedEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import com.github.braully.persistence.EntitySearch;
import com.github.braully.persistence.IEntity;
import com.github.braully.persistence.PagedQueryResult;
import com.github.braully.util.UtilComparator;
import com.github.braully.util.UtilValidation;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author braully
 */
@RestController
@RequestMapping(path = "/app")
public class AppRest {

    static final Logger log = LogManager.getLogger(AppRest.class);

    public ObjectMapper objectMapper = new ObjectMapper();

    {
//        SerializationFeature
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
    }

    @Autowired
    private GenericDAO genericDAO;

    @Autowired
    private EntitySearch entitySearch;

    @RequestMapping(value = {"/action"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void action(@RequestBody String strBody) {
    public void action(HttpServletRequest request) {
        log.info("action()");
        Map<String, String[]> parameters = request.getParameterMap();
        for (String key : parameters.keySet()) {
            System.out.println(key);
            String[] vals = parameters.get(key);
            for (String val : vals) {
                System.out.println(" -> " + val);
            }
        }
    }

    @RequestMapping(value = {"/rest/{classe}/{id}"}, method = RequestMethod.GET)
    public IEntity getEntity(@PathVariable("classe") String classe,
            @PathVariable("id") Long id) {
        log.info("getEntity()");
        IEntity ret = null;
        DescriptorExposedEntity exposedEntity = exposed.getExposedEntity(classe);
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
        DescriptorExposedEntity exposedEntity = exposed.getExposedEntity(classe);
        if (exposedEntity != null && jsonEntity != null && !jsonEntity.isEmpty()) {
            try {
                Class classeMapeada = exposed.getExposedEntity(classe).getClassExposed();
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
            @PathVariable("id") Long id,
            @RequestBody String jsonEntity) {
        log.info("updateEntity()");
        IEntity entidade = null;
        if (classe != null && !classe.trim().isEmpty()) {
            try {
                Class classeMapeada = null;
                if (jsonEntity != null) {
                    classeMapeada = exposed.getExposedEntity(classe).getClassExposed();
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

    @RequestMapping(value = {"/rest/{classe}/{id}", "/delete/{classe}/{id}"},
            method = {RequestMethod.DELETE, RequestMethod.POST})
    public void removeEntity(@PathVariable("classe") String classe, @PathVariable("id") Long id) {
        log.info("removeEntity()");
        genericDAO.delete(id, exposed.getExposedEntity(classe).getClassExposed());
    }

    @RequestMapping(value = {"/rest/{classe}"},
            method = RequestMethod.GET)
    @ResponseBody
    public List listEntity(@PathVariable("classe") String classe,
            @RequestParam(required = false) Map<String, String> params) {
        log.info("listEntity()");
        List ret = null;
        DescriptorExposedEntity exposedEntity = exposed.getExposedEntity(classe);
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
        Class entityClass = exposed.getExposedEntity(classe).getClassExposed();
        List ret = genericDAO.loadCollection(entityClass);
        return ret;
    }

    @RequestMapping(value = {"/i18n/{language}.json"},
            method = RequestMethod.GET)
    @ResponseBody
    public Map i18nLanguage(@PathVariable("language") String language,
            @RequestParam Map<String, String> allRequestParams,
            ModelMap model) {
        log.info("i18nLanguage()");
        Map map = i18n.getMessageLanguageMap(language);
        return map;
    }

    @RequestMapping(value = {"/auto-complete/{classe}/{atribute}"},
            method = RequestMethod.GET)
    @ResponseBody
    public List autoCompleteSearchItens(@PathVariable("classe") String classe,
            @PathVariable(value = "atribute", required = false) String atribute,
            @RequestParam(value = "term", required = false,
                    defaultValue = "") String term) {

        List result = new ArrayList();
        try {
            Class entityClass = exposed.getExposedEntity(classe).getClassExposed();
            if (UtilValidation.isStringValid(atribute)) {
                entityClass = entityClass.getDeclaredField(atribute).getType();
            }
            if (entityClass != null) {
                PagedQueryResult pagedQuery = genericDAO.genericFulltTextSearchPagedQuery(entityClass, term, null);
                List<IEntity> qryResult = pagedQuery.getResult();
                Collections.sort(qryResult, UtilComparator.comparator("name", "label", "description"));
                qryResult.forEach(e -> result.add(Map.of("label", e.toString(), "value", e.getId(), "id", e.getId())));
                if (pagedQuery.getTotalPages() > 1) {
                    Map.of("label", " ... ", "value", "", "id", "");
                }
            }
        } catch (Exception e) {
            log.error("Fail on load", e);
        }
        return result;
    }

}
