package com.github.braully.web;

import com.github.braully.app.StatisticalConsolidation;
import com.github.braully.domain.AbstractEntity;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import static j2html.TagCreator.*;
import static com.github.braully.web.DescriptorExposedEntity.*;
import j2html.Config;
import j2html.tags.DomContent;
import j2html.tags.EmptyTag;

/**
 *
 * @author braully
 */
public class AutoGenWebResources {
    
    static {
        Config.closeEmptyTags = true;
        Config.textEscaper = text -> text;
    }
    
    public static final AutoGenEntityHtmlCrudPageJsf JSF_ENTITY_CRUD = new AutoGenEntityHtmlCrudPageJsf();
    
    public static class AutoGenEntityHtmlCrudPage {
        
        public ContainerTag entityHtmlCrud(DescriptorHtmlEntity entityDescriptor, StatisticalConsolidation statistic) {
            var html = html(
                    entityHtmlForm(entityDescriptor, statistic),
                    entityHtmlFilter(entityDescriptor, statistic),
                    entityHtmlList(entityDescriptor, statistic)
            );
            extraEntityHtmlCrud(html, entityDescriptor, statistic);
            return html;
        }
        
        public ContainerTag entityHtmlForm(DescriptorHtmlEntity entityDescriptor, StatisticalConsolidation statistic) {
            /* 
            <form>
                <div class="autogen-form-group">
                    <label class="autogen-form-group-label">Name</label>
                    <input class="autogen-form-group-input" type="text" />
                </div>

                <div class="autogen-form-group">
                    <input class="autogen-form-group-button-save" 
                           type="submit" action="save" />
                </div>
            </form>
             */
            var form = form();
            
            entityDescriptor.elementsForm.forEach(e
                    -> form.with(entityHtmlFormInputGroup(e, statistic))
            );
            
            var saveInput = input().withClass("autogen-form-group-button-save")
                    .withType("submit")
                    .withValue("Save")
                    .withAction("save");
            form.with(div(saveInput).withClass("autogen-form-group"));
            
            extraEntityHtmlForm(form, entityDescriptor, statistic);
            extraEntityHtmlFormSaveInput(saveInput, entityDescriptor, statistic);
            
            return form;
        }
        
        protected ContainerTag entityHtmlFormInputGroup(DescriptorHtmlEntity e,
                StatisticalConsolidation statistic) {
            ContainerTag inputGroup = null;
            Tag label = null;
            ContainerTag input = null;
            switch (e.getTypeLow()) {
                case "int":
                case "integer":
                case "float":
                case "double":
                case "string":
                    label = label(e.label).withClass("autogen-form-group-label");
                    input = new ContainerTag("input").withType("text").withValue(e.property).withClass("autogen-form-group-input");
                    extraEntityHtmlFormInput(input, e, statistic);
                    inputGroup = div(label, input).withClass("autogen-form-group");
                    break;
                case "boolean":
                    label = label(e.label).withClass("autogen-form-group-label-check");
                    input = new ContainerTag("input").withType("checkbox").withValue(e.property).withClass("autogen-form-group-input-check");
                    extraEntityHtmlFormInput(input, e, statistic);
                    //Reverse postion, label an input, see bootstrap 4 forms
                    inputGroup = div(input, label).withClass("autogen-form-group-check");
                    break;
                default:
                    label = label(e.label).withClass("autogen-form-group-label");
                    input = select().withValue(e.property).withClass("autogen-form-group-input");
                    extraEntityHtmlFormInput(input, e, statistic);
                    inputGroup = div(label, input).withClass("autogen-form-group");
                    break;
            }
            
            return inputGroup;
        }
        
        public ContainerTag entityHtmlFilter(DescriptorHtmlEntity he, StatisticalConsolidation statistic) {
            /* 
            <div class="autogen-filter-group">
               <input class="autogen-filter-group-input" type="text" />
               <input class="autogen-filter-group-button-search" type="submit"
                      value="Pesquisar" action="save" />
            </div>
             */
            
            var input = input().withClass("autogen-filter-group-input");
            var search = input().withClass("autogen-filter-group-button-search")
                    .withType("submit").withValue("Search").withAction("search");
            
            var filter = form(
                    div(input, search).withClass("autogen-filter-group")
            );
            
            extraEntityHtmlFilterInput(input, he, statistic);
            extraEntityHtmlFilterSearchInput(search, he, statistic);
            extraEntityHtmlFilter(filter, he, statistic);
            
            return filter;
        }
        
        public ContainerTag entityHtmlList(DescriptorHtmlEntity he, StatisticalConsolidation statistic) {
            /*<table class="autogen-list-table">
                    <thead class="autogen-list-table-head">
                        <tr class="autogen-list-table-head-row">
                            <th class="autogen-list-table-head-col" scope="col">#</th>
                            <th class="autogen-list-table-head-col" scope="col">Name</th>
                            <th class="autogen-list-table-head-col" scope="col"></th>
                        </tr>
                    </thead>
                    <tbody class="autogen-list-table-body">
                        <tr class="autogen-list-table-body-row">
                            <td class="autogen-list-table-body-col">id</td>
                            <td class="autogen-list-table-body-col">nome</td>
                            <td class="autogen-list-table-body-col">
                                <input class="autogen-list-table-body-col-input" 
                                       value="Editar" title="Editar" />
                            </td>
                        </tr>
                    </tbody>
                </table>*/
            var table = table().withClass("autogen-list-table");
            var trh = tr().withClass("autogen-list-table-head-row");
            
            boolean selectable = he.isAttribute("selectable");

            //Select list elements
            if (selectable) {
                trh.with(th().withClass("autogen-list-table-head-col").with(
                        input().withType("checkbox").attr("onclick", "checkAll(this);")
                ));
            }

            //Fields colummns
            he.elementsList.forEach(
                    e -> trh.with(th(e.label)
                            .withClass("autogen-list-table-head-col"))
            );
            //Action colummn
            trh.with(th().withClass("autogen-list-table-head-col"));
            var thead = thead(trh).withClass("autogen-list-table-head");

            //tbody
            //tbody-row
            var trb = tr().withClass("autogen-list-table-body-row");

            //tbody-row-col-check
            trb.with(
                    td().with(
                            extraEntityHtmlListBodyRowColumnCheckbox(
                                    input().withType("checkbox")
                                            .attr("onclick", "$(this).toggleClass('selected');")
                                            .attr("data-id", "id"), he, statistic)
                    )
            );

            //tbody-row-col-filds
            he.elementsList.forEach(
                    e -> trb.with(
                            extraEntityHtmlListBodyRowColumn(
                                    td().withClass("autogen-list-table-body-col")
                                            .attr("model", e.property),
                                    e, statistic
                            )
                    )
            );

            //tbody-row-col-action
            Tag btnEditList = input()
                    .withValue("Edit")
                    .withTitle("Edit")
                    .withClass("autogen-list-group-button");
            
            extraEntityHtmlListBodyRowActionEdit(btnEditList, he, statistic);
            
            Tag btnView = a("View").withHref("#")
                    .withValue("View").withTitle("View")
                    .withClass("autogen-list-group-button-extra-button");
            
            Tag btnRemove = a("Remove").withHref("#")
                    .withValue("Remove").withTitle("Remove")
                    .withClass("autogen-list-group-button-extra-button");
            
            Tag btnListExtraActions = button().withId("autogen-list-table-body-col-input-extra-action")//TODO: Remove id from auto generation
                    .withType("button")
                    .withClass("autogen-list-group-button")
                    .withTitle("More action")
                    .attr("data-toggle", "dropdown")
                    .attr("aria-haspopup", "true")
                    .attr("aria-expanded", "false");
            
            Tag menuExtraActions = div(btnView, btnRemove).withClass("dropdown-menu")
                    .attr("aria-labelledby", "autogen-list-table-body-col-input-extra-action");
            
            extraEntityHtmlListBodyRowActionEdit(btnEditList, he, statistic);
            
            trb.with(td().with(
                    div(
                            btnEditList,
                            btnListExtraActions,
                            menuExtraActions
                    ).withClass("autogen-list-group-button-col")
            ));
            extraEntityHtmlListBodyRow(trb, he, statistic);
            
            var tbody = tbody(trb).withClass("autogen-list-table-body");
            table.with(thead, tbody);
            
            extraEntityHtmlList(table, he, statistic);
            return table;
        }
        
        protected void extraEntityHtmlListBodyRowActionEdit(Tag th,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            
        }
        
        protected ContainerTag extraEntityHtmlListBodyRowColumn(ContainerTag th,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            return th;
        }
        
        protected void extraEntityHtmlListBodyRow(Tag html,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            
        }
        
        protected void extraEntityHtmlList(Tag html,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            
        }
        
        protected void extraEntityHtmlFilter(Tag html,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            
        }
        
        protected void extraEntityHtmlFilterSearchInput(Tag html,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            
        }
        
        protected void extraEntityHtmlFilterInput(Tag html,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            
        }
        
        protected void extraEntityHtmlFormInput(ContainerTag html,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            
        }
        
        protected void extraEntityHtmlForm(Tag html,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            
        }
        
        protected void extraEntityHtmlFormSaveInput(Tag html,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            
        }
        
        protected void extraEntityHtmlCrud(ContainerTag html,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            
        }
        
        protected Tag extraEntityHtmlListBodyRowColumnCheckbox(Tag th,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            return th;
        }
    }
    
    public static class AutoGenEntityHtmlCrudPageJsf extends AutoGenEntityHtmlCrudPage {
        
        public ContainerTag embraceJsfComposition(ContainerTag tag) {
            ContainerTag html = new ContainerTag("html");
            html.attr("xmlns=\"http://www.w3.org/1999/xhtml\"")
                    .attr("xmlns:ui=\"http://java.sun.com/jsf/facelets\"")
                    .attr("xmlns:h=\"http://java.sun.com/jsf/html\"")
                    .attr("xmlns:c=\"http://java.sun.com/jsp/jstl/core\"")
                    .attr("xmlns:f=\"http://java.sun.com/jsf/core\"");
            ContainerTag compostion = new ContainerTag("ui:composition");
            compostion.with(tag);
            html.with(compostion);
            return html;
        }
        
        @Override
        protected void extraEntityHtmlCrud(ContainerTag html, DescriptorHtmlEntity entityDescriptor, StatisticalConsolidation statistic) {
            html.attr("xmlns=\"http://www.w3.org/1999/xhtml\"")
                    .attr("xmlns:h=\"http://xmlns.jcp.org/jsf/html\"")
                    .attr("xmlns:f=\"http://xmlns.jcp.org/jsf/core\"")
                    .attr("xmlns:c=\"http://java.sun.com/jsp/jstl/core\"")
                    .attr("xmlns:ui=\"http://xmlns.jcp.org/jsf/facelets\"");
        }
        
        @Override
        protected void extraEntityHtmlForm(Tag form, DescriptorHtmlEntity entityDescriptor, StatisticalConsolidation statistic) {
            form.attr("jsfc", "h:form");
        }
        
        @Override
        protected void extraEntityHtmlFormSaveInput(Tag btn, DescriptorHtmlEntity entityDescriptor, StatisticalConsolidation statistic) {
            btn.attr("jsfc", "h:commandButton");
            btn.attr("action", "#{genericMB.crud('entityName').save()}");
        }
        
        @Override
        protected void extraEntityHtmlFormInput(ContainerTag input, DescriptorHtmlEntity fieldEntity,
                StatisticalConsolidation statistic) {
            input.attr("value", "#{genericMB.crud('entityName').entity." + fieldEntity.property + "}");
            switch (fieldEntity.getTypeLow()) {
                case "int":
                case "integer":
                case "long":
                case "float":
                case "double":
                case "string":
                    input.attr("jsfc", "h:inputText");
                    break;
                case "boolean":
                    input.attr("jsfc", "h:selectBooleanCheckbox");
                    break;
                default:
                    
                    input.attr("jsfc", "h:selectOneMenu");
                    /*
                    <f:selectItem itemLabel="" itemValue="" />
                    <f:selectItems value="#{genericMB.crud('entityDummy').values('entityDummy.statusType')}" 
                                   var="st" itemLabel="#{st.toString()}" itemValue="#{st}" /> 
                     */
                    input.with(
                            new ContainerTag("f:selectItem")
                                    .attr("itemLabel", "").attr("itemValue", ""),
                            new ContainerTag("f:selectItems")
                                    .withValue("#{genericMB.crud('entityName').values('entityName." + fieldEntity.property + "')}")
                                    .attr("var", "st").attr("itemValue", "#{st}")
                                    .attr("itemLabel", "#{st.toString()}")
                    );
                    if (AbstractEntity.class.isAssignableFrom(fieldEntity.classe)) {
                        input.attr("converter", "converterEntity");
                    }
                    break;
            }
        }
        
        @Override
        protected void extraEntityHtmlFilterInput(Tag input, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            input.attr("value", "#{genericMB.crud('entityName').searchString}");
            input.attr("jsfc", "h:inputText");
        }
        
        protected void extraEntityHtmlFilterSearchInput(Tag btn, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            btn.attr("action", "#{genericMB.crud('entityName').find()}");
            btn.attr("jsfc", "h:commandButton");
        }
        
        @Override
        protected void extraEntityHtmlFilter(Tag html, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            
        }
        
        @Override
        protected void extraEntityHtmlListBodyRow(Tag row, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            row.attr("jsfc", "ui:repeat");
            row.attr("value", "#{genericMB.crud('entityName').entities}");
            row.attr("var", "ent");
        }
        
        @Override
        protected ContainerTag extraEntityHtmlListBodyRowColumn(ContainerTag th, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            return th.withText("#{ent." + entityDescriptor.property + "}");
        }
        
        @Override
        protected Tag extraEntityHtmlListBodyRowColumnCheckbox(Tag th, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            return th.attr("data-id", "#{ent.id}");
        }
        
        @Override
        protected void extraEntityHtmlListBodyRowActionEdit(Tag btn, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            btn.attr("jsfc", "h:commandButton");
            btn.attr("actionListener", "#{genericMB.crud('entityName').setEntity(ent)}");
        }
        
        protected ContainerTag entityHtmlListPaginate(DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            /*
            <div class="autogen-list-group">
                <input class="autogen-list-group-button-prev" jsfc="h:commandButton" value="«"
                       disabled="#{not genericMB.crud('entityName').hasPreviousPageQueryResult()}"
                       action="#{genericMB.crud('entityName').previousPageQueryResult()}"
                       aria-label="Anterior"/>

                <!--TODO: ui:repeat begin="" end="" has bug, verify in future -->
                <c:forEach begin="0" end="#{genericMB.crud('entityName').totalPages}" var="idx">
                    <input class="#{genericMB.crud('entityName').currentPage eq idx ? 'autogen-list-group-button-page-active':'autogen-list-group-button-page'}" 
                           jsfc="h:commandButton" action="#{genericMB.crud('entityName').paginate(idx)}"
                           value="#{idx+1}" aria-label="Pagina #{idx}" />
                </c:forEach>

                <input class="autogen-list-group-button-next" jsfc="h:commandButton" value="»"
                       disabled="#{not genericMB.crud('entityName').hasNextPageQueryResult()}"
                       action="#{genericMB.crud('entityName').nextPageQueryResult()}"
                       aria-label="Proximo"/>
            </div>
             */
            return div().withClass("autogen-list-group")
                    .with(
                            input().withClass("autogen-list-group-button-prev").withValue("«")
                                    .withAction("#{genericMB.crud('entityName').previousPageQueryResult()}")
                                    .attr("disabled", "#{not genericMB.crud('entityName').hasPreviousPageQueryResult()}")
                                    .attr("jsfc", "h:commandButton").attr("aria-label", "Anterior")
                    )
                    .with(
                            new ContainerTag("c:forEach").attr("var", "idx").attr("begin", 0).attr("end", "#{genericMB.crud('entityName').totalPages}")
                                    .with(input().withClass("#{genericMB.crud('entityName').currentPage eq idx ? "
                                            + "'autogen-list-group-button-page-active':'autogen-list-group-button-page'}")
                                            .attr("jsfc", "h:commandButton").attr("aria-label", "Pagina #{idx}")
                                            .withAction("#{genericMB.crud('entityName').paginate(idx)}").withValue("#{idx+1}")
                                    )
                    )
                    .with(
                            input().withClass("autogen-list-group-button-next").withValue("»")
                                    .withAction("#{genericMB.crud('entityName').nextPageQueryResult()}")
                                    .attr("disabled", "#{not genericMB.crud('entityName').hasNextPageQueryResult()}")
                                    .attr("jsfc", "h:commandButton").attr("aria-label", "Proximo")
                    );
        }
        
        public ContainerTag entityHtmlFilter(DescriptorHtmlEntity he) {
            return this.entityHtmlFilter(he, null);
        }
        
        public ContainerTag entityHtmlList(DescriptorHtmlEntity he) {
            ContainerTag hform = new ContainerTag("h:form").with(this.entityHtmlList(he, null), entityHtmlListPaginate(he, null));
            return hform;
        }
        
        public ContainerTag entityHtmlForm(DescriptorHtmlEntity he) {
            return this.entityHtmlForm(he, null);
        }
    }
    
    public static void main(String... args) {
//        String strEntity = null;
//        String strTemplate = null;
//
//        Option entity = new Option("f", "jsf", true, "Generate Java ServerFaces CRUD");
//        Options options = new Options();
//        options.addOption(entity);
//        Option template = new Option("t", "html", true, "Generate Java CRUD");
//        options.addOption(template);
//
//        CommandLineParser parser = new DefaultParser();
//        HelpFormatter formatter = new HelpFormatter();
//        CommandLine cmd = null;
//
//        try {
//            cmd = parser.parse(options, args);
//        } catch (ParseException e) {
//            System.out.println(e.getMessage());
//            formatter.printHelp(AutoGenResourceJSF.class.getName(), options);
//            return;
//        }

        DescriptorHtmlEntity descritor = new DescriptorHtmlEntity(com.github.braully.domain.util.EntityDummy.class);
        System.err.println(JSF_ENTITY_CRUD.entityHtmlCrud(descritor, null).renderFormatted());
        
    }
}
