package com.github.braully.web.jsf;

import com.github.braully.app.StatisticalConsolidation;
import com.github.braully.util.DynamicVirtualTemplateFile;
import com.github.braully.util.UtilCollection;
import com.github.braully.util.UtilIO;
import com.github.braully.util.UtilReflection;
import com.github.braully.web.AutoGenWebResources;
import com.github.braully.web.DescriptorExposedEntity;
import com.github.braully.web.DescriptorExposedEntity.DescriptorHtmlEntity;
import static com.github.braully.web.SpringWebConfig.*;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author braully
 */
public class AutoGenResourceJSF extends ResourceWrapper {

    private static Logger log = LogManager.getLogger(AutoGenResourceJSF.class);

    /* */
    public static final String DEFAULT_AUTOGEN_PREFIX = "/autogen/";
    public static final String DEFAULT_ENTITY_PROPERTIE_KEY = "entityName";
    public static final String DYNAMIC_BASE_TEMPLATE_RESOURC_NAME = "template-base-app";
    public static final String DYNAMIC_BASE_TEMPLATE_RESOURC_NAME_FILE = DYNAMIC_BASE_TEMPLATE_RESOURC_NAME + ".xhtml";

    /* */
    public static String DIRECTORY_DYNAMIC_TEMPLATE_HTML = "web/templates/"; //"templates/";
    public static String FILE_DEFAULT_DYNAMIC_BASE_TEMPLATE = DIRECTORY_DYNAMIC_TEMPLATE_HTML + "template-base-app.html";

    /* */
    public static String DIRECTORY_DYNAMIC_TEMPLATE_JSF = "web/jsf/template/"; //"jsf/template/";
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
    public static final String DEFAULT_PROP_APP_HEADER_JSF = "<ui:insert name=\"template.app.header\"><ui:include src=\"/jsf/template/header-base-slim.xhtml\" /></ui:insert>";
    public static final String DEFAULT_PROP_TEMPLATE_HTML_APPEND_JSF = "<f:view contentType=\"text/html\" locale=\"en\" />";
    public static final String DEFAULT_PROP_TEMPLATE_HEAD_APPEND_JSF = "<ui:insert name=\"template.head.append\" />";
    public static final String DEFAULT_PROP_TEMPLATE_BODY_APPEND_JSF = "<ui:insert name=\"template.body.append\" /> ";
    public static final String DEFAULT_PROP_APP_MENU_JSF = "<ui:insert name=\"template.app.menu\"><ui:include src=\"/jsf/template/menu-base-app.xhtml\" /></ui:insert>";
    public static final String DEFAULT_PROP_APP_CONTET_JSF = "<ui:insert name=\"template.app.content\" />";
    public static final String DEFAULT_PROP_APP_MSG_JSF = "<ui:insert name=\"template.app.content.msg\"> "
            + "<h:messages style=\"list-style: none;\" \n"
            + "   infoClass=\"autogen-message-info\" "
            + "   warnClass=\"autogen-message-warning\" "
            + "   fatalClass=\"autogen-message-error\" "
            + "   errorClass=\"autogen-message-error\" "
            + "   showSummary=\"true\" showDetail=\"true\" />"
            + " </ui:insert>";
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
            DEFAULT_PROP_APP_MSG, DEFAULT_PROP_APP_MSG_JSF,
            DEFAULT_PROP_APP_FORM, DEFAULT_PROP_APP_FORM_JSF,
            DEFAULT_PROP_APP_FILTER, DEFAULT_PROP_APP_FILTER_JSF,
            DEFAULT_PROP_APP_LIST, DEFAULT_PROP_APP_LIST_JSF,
            DEFAULT_PROP_TEMPLATE_FOOT_APPEND, DEFAULT_PROP_FOOT_APPEND_JSF
    );

    static boolean isAutoGenResource(String resourceName) {
        if (resourceName.startsWith(DEFAULT_AUTOGEN_PREFIX)) {
            return resourceName.contains("html");
        }
        return false;
    }

    private DynamicVirtualTemplateFile dynamicTemplate;
    private Map<String, String> extraParameter = new HashMap<>();
    private String resourceName;
    private Resource wrapped;

    AutoGenResourceJSF(String resourceName, ResourceHandler wrapped) {
        this.resourceName = resourceName;
        try {
            URL url = new URL("http://b" + resourceName);
            String query = url.getQuery();
            if (query != null) {
                String[] params = new String[]{query};
                if (query.contains("&")) {
                    params = query.split("&");
                }
                for (String param : params) {
                    final int idx = param.indexOf("=");
                    final String key = idx > 0 ? URLDecoder.decode(param.substring(0, idx), "UTF-8") : param;
                    final String value = idx > 0 && param.length() > idx + 1 ? URLDecoder.decode(param.substring(idx + 1), "UTF-8") : null;
                    this.extraParameter.put(key, value);
                }
            }
        } catch (Exception e) {
        }
        this.dynamicTemplate = dynamicTemplate(resourceName);
    }

    private DynamicVirtualTemplateFile dynamicTemplate(String resourceName) {
        DynamicVirtualTemplateFile template = CACHE.get(resourceName);
        if (template == null) {
            try {
                int lastbar = resourceName.lastIndexOf("/") + 1;
                String shortResourceName = resourceName.substring(
                        lastbar, resourceName.indexOf(".", lastbar));
                if (isPageTemplate(resourceName, shortResourceName)) {
                    template = dynamicTemplatePage(resourceName, shortResourceName);
                } else {
                    switch (shortResourceName) {
                        case "form":
                        case "list":
                        case "filter":
                            template = generateTemplateEntityCrudElement(resourceName, shortResourceName);
                            break;
                        case "crud":
                            template = dynamicTemplateEntityCrud(resourceName, shortResourceName);
                            break;
                        default:
                            template = generateTemplateEntityCrudElement(resourceName, shortResourceName);
                            if (template == null) {
                                template = dynamicTemplateEntityCrud(resourceName, shortResourceName);
                            }
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

        String fileFromPathAsString = DIRECTORY_DYNAMIC_TEMPLATE_JSF + shortResourceName + ".xhtml";

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
        if (end < begin) {
            return null;
            //throw new IllegalStateException("Invalid url: " + resourceName + " and " + shortResourceName);
        }
        String entityNameExposed = resourceName.substring(begin, end);
        DescriptorExposedEntity descExposedEntity = autogenweb.getExposedEntity(entityNameExposed);
        if (descExposedEntity == null) {
            return null;
        }
        if (shortResourceName.equals("form")) {
            //TODO: Inject
            //StatisticalConsolidation st = StatisticalConsolidation.instance();
            template = generateForm(resourceName, resourceName, entityNameExposed, descExposedEntity, StatisticalConsolidation.instance());
        } else if (shortResourceName.equals("filter")) {
            template = generateFilter(resourceName, resourceName, entityNameExposed, descExposedEntity);
        } else if (shortResourceName.equals("list")) {
            template = generateEntityList(resourceName, resourceName, entityNameExposed, descExposedEntity);
        } else if (descExposedEntity.hasField(shortResourceName)) {
            //log.info("Gerar input form subelement");
            template = generateElement(resourceName, shortResourceName, entityNameExposed, descExposedEntity);
        }
        return template;
    }

    private DynamicVirtualTemplateFile generateElement(String resourceName,
            String shortResourceName, String entityNameExposed,
            DescriptorExposedEntity descExposedEntity)
            throws IOException {
        DescriptorHtmlEntity elementForm = descExposedEntity.getDescriptorHtmlEntity(extraParameter).getElementForm(shortResourceName);
        ContainerTag entityHtmlFormInputGroup = AutoGenWebResources.JSF_ENTITY_CRUD.entityHtmlFormInputGroup(elementForm, StatisticalConsolidation.instance());
        String renderFilter = AutoGenWebResources.JSF_ENTITY_CRUD.embraceJsfComposition(
                entityHtmlFormInputGroup
        ).renderFormatted();
        DynamicVirtualTemplateFile template = new DynamicVirtualTemplateFile(resourceName,
                renderFilter, MAP_TRANSLATE_TEMPLATE_PROPERTIES,
                Map.of(DEFAULT_ENTITY_PROPERTIE_KEY, entityNameExposed));
        return template;
    }

    private DynamicVirtualTemplateFile generateForm(String resourceName, String shortResourceName,
            String entityNameExposed, DescriptorExposedEntity descExposedEntity, StatisticalConsolidation st)
            throws IOException {
        DescriptorExposedEntity.DescriptorHtmlEntity descriptorHtmlEntity = descExposedEntity.getDescriptorHtmlEntity(extraParameter);
        ContainerTag htmlForm = AutoGenWebResources.JSF_ENTITY_CRUD.entityHtmlForm(descriptorHtmlEntity, st);
        String renderForm = null;
        var finalForm = AutoGenWebResources.JSF_ENTITY_CRUD.embraceJsfComposition(
                htmlForm
        );

        if (descriptorHtmlEntity.isAttribute("onlychilds")) {
            try {
                List<DomContent> children = (List<DomContent>) UtilReflection.getPrivateField(htmlForm, "children");
                finalForm = AutoGenWebResources.JSF_ENTITY_CRUD.embraceJsfComposition(children.toArray(new DomContent[0]));
            } catch (Exception ex) {
                log.debug("Falha ao obter atributo privado", ex);
            }
        }
        renderForm = finalForm.renderFormatted();
        DynamicVirtualTemplateFile template = new DynamicVirtualTemplateFile(resourceName,
                renderForm, MAP_TRANSLATE_TEMPLATE_PROPERTIES,
                Map.of(DEFAULT_ENTITY_PROPERTIE_KEY, entityNameExposed));
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
        String renderList = AutoGenWebResources.JSF_ENTITY_CRUD.embraceJsfComposition(
                AutoGenWebResources.JSF_ENTITY_CRUD.entityHtmlList(descExposedEntity.getDescriptorHtmlEntity(extraParameter))
        ).renderFormatted();
        DynamicVirtualTemplateFile template = new DynamicVirtualTemplateFile(resourceName,
                renderList, MAP_TRANSLATE_TEMPLATE_PROPERTIES,
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
        if (this.dynamicTemplate == null) {
            throw new IllegalStateException("Fail on autogen: " + this.resourceName);
        }
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
