package com.github.braully.web.jsf;

import com.github.braully.app.EntityRESTfulWS;
import com.github.braully.util.DynamicVirtualTemplateFile;
import com.github.braully.util.UtilCollection;
import com.github.braully.util.UtilIO;
import com.github.braully.web.AutoGenWebResources;
import com.github.braully.web.DescriptorExposedEntity;
import static com.github.braully.web.SpringWebConfig.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceWrapper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 *
 * @author braully
 */
public class AutoGenResourceJSF extends ResourceWrapper {

    private static Logger log = Logger.getLogger(AutoGenResourceJSF.class);

    /* */
    public static final String DEFAULT_AUTOGEN_PREFIX = "/autogen/";
    public static final String DEFAULT_ENTITY_PROPERTIE_KEY = "entityName";
    public static final String DYNAMIC_BASE_TEMPLATE_RESOURC_NAME = "template-base-app";
    public static final String DYNAMIC_BASE_TEMPLATE_RESOURC_NAME_FILE = DYNAMIC_BASE_TEMPLATE_RESOURC_NAME + ".xhtml";

    /* */
    public static String DIRECTORY_DYNAMIC_TEMPLATE_HTML = "web/templates/";
    public static String FILE_DEFAULT_DYNAMIC_BASE_TEMPLATE = DIRECTORY_DYNAMIC_TEMPLATE_HTML + "template-base-app.html";

    /* */
    public static String DIRECTORY_DYNAMIC_TEMPLATE_JSF = "web/jsf/template/";
    public static String FILE_DYNAMIC_TEMPLATE_ENTITY_CRUD = DIRECTORY_DYNAMIC_TEMPLATE_JSF + "template-base-app-entity-crud.xhtml";

    public static final String DEFAULT_PROP_TEMPLATE_HTML_ATTRIBUTE_APPEND_JSF = "xmlns=\"http://www.w3.org/1999/xhtml\"\n"
            + "      xmlns:h=\"http://xmlns.jcp.org/jsf/html\"\n"
            + "      xmlns:f=\"http://xmlns.jcp.org/jsf/core\"\n"
            + "      xmlns:ps=\"http://xmlns.jcp.org/jsf/passthrough\"\n"
            + "      xmlns:ui=\"http://xmlns.jcp.org/jsf/facelets\"\n"
            //            + "      xmlns:p=\"http://primefaces.org/ui\"\n"
            + "      ng-app=\"baseApp\"";

    public static final String DEFAULT_PROP_TEMPLATE_HEAD_ATTRIBUTE_APPEND_JSF = "jsfc=\"h:head\"";
    public static final String DEFAULT_PROP_APP_NAME_JSF = "<ui:insert name=\"template.app.name\" />";
    public static final String DEFAULT_PROP_APP_TITTLE_JSF = "<ui:insert name=\"template.app.title\" />";
    public static final String DEFAULT_PROP_APP_HEADER_JSF = "<ui:insert name=\"template.app.header\" />";
    public static final String DEFAULT_PROP_TEMPLATE_HTML_APPEND_JSF = "<f:view contentType=\"text/html\" locale=\"en\" />";
    public static final String DEFAULT_PROP_TEMPLATE_HEAD_APPEND_JSF = "<ui:insert name=\"template.head.append\" />";
    public static final String DEFAULT_PROP_TEMPLATE_BODY_APPEND_JSF = "<ui:insert name=\"template.body.append\" /> ";
    public static final String DEFAULT_PROP_APP_MENU_JSF = "<ui:insert name=\"template.app.menu\"><ui:include src=\"/jsf/template/menu-base-app.xhtml\" /></ui:insert>";
    public static final String DEFAULT_PROP_APP_CONTET_JSF = "<ui:insert name=\"template.app.content\" />";
    public static final String DEFAULT_PROP_APP_FORM_JSF = "<ui:insert name=\"template.app.content.form\" />";
    public static final String DEFAULT_PROP_APP_FILTER_JSF = "<ui:insert name=\"template.app.content.filter\" />";
    public static final String DEFAULT_PROP_APP_LIST_JSF = "<ui:insert name=\"template.app.content.list\" />";
    public static final String DEFAULT_PROP_FOOT_APPEND_JSF = "<ui:insert name=\"template.foot.append\" />";

    static boolean IS_CACHABLE = false;

    static Map<String, DynamicVirtualTemplateFile> CACHE = new HashMap<>();

    static Map<String, String> MAP_TRANSLATE_TEMPLATE_PROPERTIES = UtilCollection.mapOf(
            DEFAULT_PROP_TEMPLATE_HTML_ATTRIBUTE_APPEND, DEFAULT_PROP_TEMPLATE_HTML_ATTRIBUTE_APPEND_JSF,
            DEFAULT_PROP_TEMPLATE_HEAD_ATTRIBUTE_APPEND, DEFAULT_PROP_TEMPLATE_HEAD_ATTRIBUTE_APPEND_JSF,
            DEFAULT_PROP_TEMPLATE_BODY_ATTRIBUTE_APPEND, "",
            DEFAULT_PROP_APP_NAME, DEFAULT_PROP_APP_NAME_JSF,
            DEFAULT_PROP_APP_TITTLE, DEFAULT_PROP_APP_TITTLE_JSF,
            DEFAULT_PROP_APP_HEADER, DEFAULT_PROP_APP_HEADER_JSF,
            DEFAULT_PROP_TEMPLATE_HTML_APPEND, DEFAULT_PROP_TEMPLATE_HTML_APPEND_JSF,
            DEFAULT_PROP_TEMPLATE_HEAD_APPEND, DEFAULT_PROP_TEMPLATE_HEAD_APPEND_JSF,
            DEFAULT_PROP_TEMPLATE_BODY_APPEND, DEFAULT_PROP_TEMPLATE_BODY_APPEND_JSF,
            DEFAULT_PROP_APP_MENU, DEFAULT_PROP_APP_MENU_JSF,
            DEFAULT_PROP_APP_CONTET, DEFAULT_PROP_APP_CONTET_JSF,
            DEFAULT_PROP_APP_FORM, DEFAULT_PROP_APP_FORM_JSF,
            DEFAULT_PROP_APP_FILTER, DEFAULT_PROP_APP_FILTER_JSF,
            DEFAULT_PROP_APP_LIST, DEFAULT_PROP_APP_LIST_JSF,
            DEFAULT_PROP_TEMPLATE_FOOT_APPEND, DEFAULT_PROP_FOOT_APPEND_JSF
    );

    static boolean isAutoGenResource(String resourceName) {
        return resourceName.startsWith(DEFAULT_AUTOGEN_PREFIX)
                && resourceName.endsWith("html");
    }

    private DynamicVirtualTemplateFile dynamicTemplate;
    private String resourceName;
    private Resource wrapped;

    AutoGenResourceJSF(String resourceName, ResourceHandler wrapped) {
        this.resourceName = resourceName;
        this.wrapped = this.wrapped;
        this.dynamicTemplate = dynamicTemplate(resourceName);
    }

    private DynamicVirtualTemplateFile dynamicTemplate(String resourceName) {
        DynamicVirtualTemplateFile template = CACHE.get(resourceName);
        if (template == null) {
            try {
                String shortResourceName = resourceName.substring(
                        resourceName.lastIndexOf("/") + 1, resourceName.lastIndexOf("."));
                if (isPageTemplate(resourceName, shortResourceName)) {
                    template = dynamicTemplatePage(resourceName, shortResourceName);
                } else {
                    switch (shortResourceName) {
                        case "form":
                        case "list":
                        case "filter":
                            template = generateTemplateEntityCrudElement(resourceName, shortResourceName);
                            break;
                        default:
                            template = dynamicTemplateEntityCrud(resourceName, shortResourceName);
                            break;
                    }
                }
            } catch (IOException ex) {
                log.error("Falha ao gerar template", ex);
            }
            if (IS_CACHABLE) {
                CACHE.put(resourceName, template);
            }
        }
        return template;
    }

    private boolean isPageTemplate(String resourceName, String shortResourceName) {
        return resourceName.endsWith(DYNAMIC_BASE_TEMPLATE_RESOURC_NAME_FILE)
                || shortResourceName.endsWith(DYNAMIC_BASE_TEMPLATE_RESOURC_NAME_FILE);
    }

    private DynamicVirtualTemplateFile dynamicTemplatePage(String resourceName, String shortResourceName) throws IOException {
        DynamicVirtualTemplateFile template = null;
        if (resourceName.endsWith(DYNAMIC_BASE_TEMPLATE_RESOURC_NAME_FILE)
                || shortResourceName.endsWith(DYNAMIC_BASE_TEMPLATE_RESOURC_NAME_FILE)) {
            template = generateBaseTemplate(resourceName, shortResourceName);
        }
        return template;
    }

    private DynamicVirtualTemplateFile dynamicTemplateEntityCrud(String resourceName,
            String shortResourceName) throws IOException {
        DescriptorExposedEntity descExposedEntity = EntityRESTfulWS.EXPOSED_ENTITY.get(shortResourceName);

        String fileFromPathAsString = DIRECTORY_DYNAMIC_TEMPLATE_JSF
                + shortResourceName + ".xhtml";
        try {
            fileFromPathAsString = UtilIO.loadFileFromPathAsString(fileFromPathAsString);
        } catch (Exception e) {
            log.error("Template named " + fileFromPathAsString + " not found or error, using default");
            log.trace(e);
            fileFromPathAsString = UtilIO.loadFileFromPathAsString(FILE_DYNAMIC_TEMPLATE_ENTITY_CRUD);
        }

        DynamicVirtualTemplateFile template = new DynamicVirtualTemplateFile(resourceName,
                fileFromPathAsString, MAP_TRANSLATE_TEMPLATE_PROPERTIES,
                Map.of(DEFAULT_ENTITY_PROPERTIE_KEY, shortResourceName));
        return template;
    }

    protected DynamicVirtualTemplateFile generateBaseTemplate(String resourceName1,
            String shortResourceName) throws IOException {
        DynamicVirtualTemplateFile template;
        String fileFromPathAsString = DIRECTORY_DYNAMIC_TEMPLATE_HTML + shortResourceName + ".html";
        try {
            fileFromPathAsString = UtilIO.loadFileFromPathAsString(fileFromPathAsString);
        } catch (Exception e) {
            log.error("Template named " + fileFromPathAsString + " not found or error, using default");
            log.trace(e);
            fileFromPathAsString = UtilIO.loadFileFromPathAsString(FILE_DEFAULT_DYNAMIC_BASE_TEMPLATE);
        }
        template = new DynamicVirtualTemplateFile(resourceName1,
                fileFromPathAsString, MAP_TRANSLATE_TEMPLATE_PROPERTIES, null);
        return template;
    }

    private DynamicVirtualTemplateFile generateTemplateEntityCrudElement(String resourceName,
            String shortResourceName) throws IOException {
        DynamicVirtualTemplateFile template = null;
        int begin = resourceName.lastIndexOf(DEFAULT_AUTOGEN_PREFIX) + DEFAULT_AUTOGEN_PREFIX.length();
        int end = resourceName.indexOf("/", begin);
        String entityNameExposed = resourceName.substring(begin, end);
        DescriptorExposedEntity descExposedEntity = EntityRESTfulWS.EXPOSED_ENTITY.get(entityNameExposed);
        if (shortResourceName.equals("form")) {
            template = generateForm(resourceName, resourceName, entityNameExposed, descExposedEntity);
        } else if (shortResourceName.equals("filter")) {
            template = generateFilter(resourceName, resourceName, entityNameExposed, descExposedEntity);
        } else if (shortResourceName.equals("list")) {
            template = generateEntityList(resourceName, resourceName, entityNameExposed, descExposedEntity);
        }
        return template;
    }

    private DynamicVirtualTemplateFile generateFilter(String resourceName,
            String shortResourceName, String entityNameExposed,
            DescriptorExposedEntity descExposedEntity)
            throws IOException {
        String renderFilter = AutoGenWebResources.JSF_ENTITY_CRUD.embraceJsfComposition(
                AutoGenWebResources.JSF_ENTITY_CRUD.entityHtmlFilter(descExposedEntity.getDescriptorHtmlEntity())
        ).renderFormatted();
        DynamicVirtualTemplateFile template = new DynamicVirtualTemplateFile(resourceName,
                renderFilter, MAP_TRANSLATE_TEMPLATE_PROPERTIES,
                Map.of(DEFAULT_ENTITY_PROPERTIE_KEY, entityNameExposed));
        return template;
    }

    private DynamicVirtualTemplateFile generateEntityList(String resourceName, String shortResourceName,
            String entityNameExposed, DescriptorExposedEntity descExposedEntity)
            throws IOException {
        String renderList
                = AutoGenWebResources.JSF_ENTITY_CRUD.embraceJsfComposition(
                        AutoGenWebResources.JSF_ENTITY_CRUD.entityHtmlList(descExposedEntity.getDescriptorHtmlEntity())
                ).renderFormatted();
        DynamicVirtualTemplateFile template = new DynamicVirtualTemplateFile(resourceName,
                renderList, MAP_TRANSLATE_TEMPLATE_PROPERTIES,
                Map.of(DEFAULT_ENTITY_PROPERTIE_KEY, entityNameExposed));
        return template;
    }

    private DynamicVirtualTemplateFile generateForm(String resourceName, String shortResourceName,
            String entityNameExposed, DescriptorExposedEntity descExposedEntity)
            throws IOException {
        String renderForm = AutoGenWebResources.JSF_ENTITY_CRUD.embraceJsfComposition(
                AutoGenWebResources.JSF_ENTITY_CRUD.entityHtmlForm(descExposedEntity.getDescriptorHtmlEntity())
        ).renderFormatted();
        DynamicVirtualTemplateFile template = new DynamicVirtualTemplateFile(resourceName,
                renderForm, MAP_TRANSLATE_TEMPLATE_PROPERTIES,
                Map.of(DEFAULT_ENTITY_PROPERTIE_KEY, entityNameExposed));
        return template;
    }

    @Override
    public Resource getWrapped() {
        return this.wrapped;
    }

    @Override
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    public String getOutput() {
        return dynamicTemplate.getOutput();
    }

    @Override
    public URL getURL() {
        return dynamicTemplate.getURL();
    }

    public static void main(String... args) {
        String strEntity = null;
        String strTemplate = null;

        Option entity = new Option("e", "entity", true, "Entity CRUD");
        Options options = new Options();
        options.addOption(entity);
        Option template = new Option("t", "template", true, "Template basae");
        options.addOption(template);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp(AutoGenResourceJSF.class.getName(), options);
            return;
        }

        strEntity = cmd.getOptionValue("entity");
        strTemplate = cmd.getOptionValue("template");

        if (strEntity == null) {
            strEntity = "partner";
        }

        AutoGenResourceJSF jsfAutoGenResource = new AutoGenResourceJSF(strTemplate, null);

        String output = jsfAutoGenResource.getOutput();
        System.out.println("Output");
        System.out.println(output);
        System.out.println("Params");
        System.out.println(jsfAutoGenResource.dynamicTemplate.getReplaceMap());
    }

}
