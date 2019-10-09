package com.github.braully.web;

import com.github.braully.app.StatisticalConsolidation;
import com.github.braully.persistence.IEntity;
import com.github.braully.util.UtilValidation;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import static j2html.TagCreator.*;
import static com.github.braully.web.DescriptorExposedEntity.*;
import com.github.braully.web.jsf.autogenweb;
import j2html.Config;
import j2html.tags.DomContent;
import org.apache.commons.lang3.RandomStringUtils;

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

        public ContainerTag entityHtmlCrud(DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            var html = html(
                    entityHtmlForm(entityDescriptor, statistic),
                    entityHtmlFilter(entityDescriptor, statistic),
                    entityHtmlList(entityDescriptor, statistic)
            );
            extraEntityHtmlCrud(html, entityDescriptor, statistic);
            return html;
        }

        public ContainerTag entityHtmlForm(DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            /* 
            <form>
                <div class="autogen-form-group">
                    <label class="autogen-form-group-label">Name</label>
                    <input class="autogen-form-group-input" type="text" />
                </div>

                <div class="autogen-form-group">
                    <input class="autogen-button" 
                           type="submit" action="save" />
                </div>
            </form>
             */
            var form = form();

            entityDescriptor.elementsForm.forEach(e
                    -> form.with(entityHtmlFormInputGroup(e, statistic))
            );

            ContainerTag formGroupActions = div().withClass("autogen-form-group");

            var defaultAction = input()
                    .withClass("autogen-button")
                    .withType("submit")
                    .withValue("Save")
                    .withAction("save")
                    .attr("data-i18n", "Save");
            Tag defaultActionTag = extraEntityHtmlFormActionDefault(defaultAction, entityDescriptor, statistic);
            formGroupActions.with(defaultActionTag);
            extraEntityHtmlFormActionExtra(formGroupActions, entityDescriptor, statistic);

            if (!entityDescriptor.isAttribute("noaction")) {
                form.with(formGroupActions);
            }

            extraEntityHtmlForm(form, entityDescriptor, statistic);
            return form;
        }

        public ContainerTag entityHtmlFormInputGroup(DescriptorHtmlEntity e,
                StatisticalConsolidation statistic) {
            ContainerTag inputGroup = null;
            ContainerTag label = null;
            ContainerTag input = null;

            String size = e.getAttributeAscending("size");
            String classDivGroupInput = "autogen-form-group";
            if (UtilValidation.isStringValid(size)) {
                classDivGroupInput = "autogen-form-group-" + size;
            }

            switch (e.getTypeLow()) {
                case "int":
                case "integer":
                case "float":
                case "double":
                case "string":
                case "money":
                case "monetary":
                case "bigdecimal":
                case "date":
                    label = label(e.label).withClass("autogen-form-group-label").attr("data-i18n", e.label);
                    extraEntityHtmlFormLabel(label, e, statistic);
                    input = new ContainerTag("input").withType("text").withValue(e.property).withClass("autogen-form-group-input");
                    extraEntityHtmlFormInput(input, e, statistic);
                    inputGroup = div(label, input).withClass(classDivGroupInput);
                    break;
                case "boolean":
                    label = label(e.label).withClass("autogen-form-group-label-check").attr("data-i18n", e.label);
                    extraEntityHtmlFormLabel(label, e, statistic);
                    input = new ContainerTag("input").withType("checkbox").withValue(e.property).withClass("autogen-form-group-input-check");
                    extraEntityHtmlFormInput(input, e, statistic);
                    //Reverse postion, label an input, see bootstrap 4 forms
                    inputGroup = div(input, label).withClass("autogen-form-group-check");
                    break;
                default:
                    label = label(e.label).withClass("autogen-form-group-label").attr("data-i18n", e.label);
                    if (statistic != null && statistic.countEntity(e.getTypeClass()) > 10) {
                        /* <div class="autogen-input-group">
                                <div class="autogen-input-group-prepend-text autogen-input-group-append">
                                    <i class="ico-search"></i>
                                </div>
                                <input class="autogen-form-group-input" type="text" autocomplete="off" value="#{genericMB.crud('address').entity.city}" />
                                <h:inputHidden value="#{genericMB.crud('address').entity.city}" converter="#{converterEntityBD}" />
                                <button class="autogen-button-alt autogen-input-group-append " type="button" 
                                    onclick="$(this).prev('input[type=hidden]').val('').prev().val('').attr('disabled', false);">
                                    <i class="ico-cancelar" />
                                </button>
                                <script>
                                    autocompleteInput($('script').last().closest('div').find('input[type=text]'));
                                </script>
                            </div> 
                         */
                        input = new ContainerTag("input").withClass("autogen-form-group-input").withType("text");
                        ContainerTag inputHidden = new ContainerTag("input").withType("hidden");
                        ContainerTag buton = button(i().withClass("ico-cancelar")).withClass("autogen-button-alt autogen-input-group-append ").withType("button")
                                .attr("onclick", "$(this).prev('input[type=hidden]').val('').prev().val('').attr('disabled', false);");
                        ContainerTag script = script("autocompleteInput($('script').last().closest('div').find('input[type=text]'), "
                                + "'entityName/" + e.property + "');");
                        extraEntityHtmlFormInputSelect(input, inputHidden, buton, script, e, statistic);
                        ContainerTag inputappen = div(div(i().withClass("ico-search")).withClass("autogen-input-group-prepend-text autogen-input-group-append"),
                                input,
                                inputHidden,
                                buton,
                                script
                        ).withClass("autogen-input-group");
                        inputGroup = div(label, inputappen).withClass(classDivGroupInput);
                    } else {
                        extraEntityHtmlFormLabel(label, e, statistic);
                        input = select().withValue(e.property)
                                //.withClass("autogen-form-group-select");//TODO: Resolver esse bug CSS
                                .withClass("custom-select");
                        extraEntityHtmlFormInput(input, e, statistic);
                        inputGroup = div(label, input).withClass(classDivGroupInput);
                    }
                    break;
            }

            return inputGroup;
        }

        public ContainerTag entityHtmlFilter(DescriptorHtmlEntity he, StatisticalConsolidation statistic) {
            /* 
            <div class="aig">
               <input class="autogen-form-group-input autogen-filter-group-input" type="text" />
               <input class="aig aiga autogen-button" type="submit"
                      value="Pesquisar" action="save" />
            </div>
             */

            var input = input().withClass("autogen-form-group-input autogen-filter-group-input");
            var search = input().withClass("aiga autogen-button")
                    .withType("submit").withValue("Search")
                    .withAction("search").attr("data-i18n", "Search");

            var filter = form(
                    div(input, search).withClass("aig")
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
                                <input class="autogen-list-table-body-col-input autogen-button-alt" 
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
                trh.with(th().withClass("autogen-list-table-head-col")
                        .with(input().withType("checkbox").attr("onclick", "checkAll(this);")));
            }

            //Fields colummns
            he.elementsList.forEach(
                    e -> trh.with(th(e.label).attr("data-i18n", e.label)
                            .withClass("autogen-list-table-head-col"))
            );
            //Action colummn
            trh.with(th().withClass("autogen-list-table-head-col"));
            var thead = thead(trh).withClass("autogen-list-table-head");

            //tbody
            //tbody-row
            var trb = tr().withClass("autogen-list-table-body-row");

            //tbody-row-col-check
            if (selectable) {
                trb.with(
                        td().with(
                                extraEntityHtmlListBodyRowColumnCheckbox(
                                        input().withType("checkbox")
                                                .attr("onclick", "$(this).toggleClass('selected');")
                                                .attr("data-id", "id"), he, statistic)
                        )
                );
            }

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
            Tag btnEditList = input().withValue("Edit")
                    .withTitle("Edit")
                    .withClass("autogen-button-alt")
                    .attr("data-i18n", "Edit");
            //trb.with(td(btnEditList).withClass("autogen-list-table-body-col"));
            extraEntityHtmlListBodyRowActionEdit(btnEditList, he, statistic);

            Tag btnView = a(i(" ").withClass("ico-search"), span("View").withData("i18n", "View")).withHref("#")
                    .withValue("View").withTitle("View")
                    .withClass("autogen-button-extra-button");
            extraEntityHtmlListBodyRowActionView(btnView, he, statistic);

            ContainerTag btnRemove = a(i(" ").withClass("ico-remove"), span("Remove").withData("i18n", "Remove")).withHref("#")
                    .withValue("Remove").withTitle("Remove")
                    .withClass("autogen-button-extra-button");
            extraEntityHtmlListBodyRowActionRemove(btnRemove, he, statistic);

            Tag btnListExtraActions = button(i().withClass("ico-opcoes"))
                    .withId("autogen-list-table-body-col-input-extra-action")//TODO: Remove id from auto generation
                    .withType("button")
                    .withClass("autogen-button-alt")
                    .withTitle("More action")
                    .attr("data-toggle", "dropdown")
                    .attr("aria-haspopup", "true")
                    .attr("aria-expanded", "false");

            ContainerTag menuExtraActions = div(btnView, btnRemove).withClass("dropdown-menu")
                    .attr("aria-labelledby", "autogen-list-table-body-col-input-extra-action");
            extraEntityHtmlListBodyRowActionMenuExtra(menuExtraActions, he, statistic);

            ContainerTag col = div().withClass("autogen-button-col");
            extraEntityHtmlListBodyRowAction(col, he, statistic);
            if (he.isAttribute("noedit")) {
                col.with(btnListExtraActions, menuExtraActions);
            } else {
                col.with(btnEditList, btnListExtraActions, menuExtraActions);
            }

            trb.with(td().with(col));
            extraEntityHtmlListBodyRow(trb, he, statistic);

            var tbody = tbody(trb).withClass("autogen-list-table-body");
            table.with(thead, tbody);

            extraEntityHtmlList(table, he, statistic);
            return table;
        }

        protected ContainerTag entityHtmlListBulkActions(DescriptorHtmlEntity he, StatisticalConsolidation statistic) {
            /*
            <div class="autogen-button-col" role="group">
                <button type="button" class="autogen-button" title="Mais"
                    data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" 
                    id="autogen-list-head-extra-action">
                    Selecionados<i class="fa fa-ellipsis-v"></i>
                </button>
            </div> 
             */

            String id = "autogen-list-head-extra-action-" + RandomStringUtils.randomAlphanumeric(5);
            ContainerTag actions = div().withClass("autogen-button-col").withRole("group");
            ContainerTag btnSelecionados = button().withType("button")
                    .withClass("autogen-button-alt").withTitle("Selecionados")
                    .withData("toggle", "dropdown").attr("aria-haspopup", "true")
                    .attr("aria-expanded", "false").withId(id)
                    .withText("Selecionados")
                    .with(i().withClass("ico-opcoes"));
            /*
            <div class="dropdown-menu"
            aria-labelledby="autogen-list-head-extra-action">
            <a class="autogen-button-extra-button" href="#">
            <i class="fa fa-search"></i> Excluir
            </a>
            <a onclick="normal_post('/jsf/index.xhtml', {ids: table_selected_id(this)});"
            class="autogen-button-extra-button" href="#">
            <i class="fa fa-barcode"></i> Ação 1  (post)
            </a>
            </div>
             */
            ContainerTag acaoExtra1 = a().withHref("#")
                    .withClass("autogen-button-extra-button")
                    .with(i().withClass("ico-excluir")).withText("Excluir")
                    .attr("onclick", "normal_post('/app/delete/entityName'), {ids: table_selected_id(this)});");
            ContainerTag menuExtra = div(acaoExtra1).withClass("dropdown-menu").attr("aria-labelledby", id);
            extraEntityHtmlListBulkActionMenuExtra(menuExtra, he, statistic);
            /* 
            <input data-toggle="modal" data-target="#autogen-modal" 
                   value="Ação em lote 3" 
                   title="Ação em lote 3" type="button"
                   class="autogen-button">
            </input>
             */
            actions.with(btnSelecionados, menuExtra);
            extraEntityHtmlListBulkActionExtra(actions, he, statistic);
            return actions;
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

        protected void extraEntityHtmlForm(ContainerTag html,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {

        }

        protected Tag extraEntityHtmlFormActionDefault(Tag tag,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            return tag;
        }

        protected void extraEntityHtmlCrud(ContainerTag html,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {

        }

        protected void extraEntityHtmlFormLabel(ContainerTag label, DescriptorHtmlEntity e, StatisticalConsolidation statistic) {

        }

        protected Tag extraEntityHtmlListBodyRowColumnCheckbox(Tag th,
                DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            return th;
        }

        protected void extraEntityHtmlListBodyRowActionView(Tag btnEditList, DescriptorHtmlEntity he, StatisticalConsolidation statistic) {

        }

        protected void extraEntityHtmlListBodyRowActionRemove(ContainerTag btnRemove, DescriptorHtmlEntity he, StatisticalConsolidation statistic) {
        }

        protected void extraEntityHtmlListBodyRowActionMenuExtra(ContainerTag menuExtraActions, DescriptorHtmlEntity he, StatisticalConsolidation statistic) {

        }

        protected void extraEntityHtmlListBulkActionMenuExtra(ContainerTag menuExtra, DescriptorHtmlEntity he, StatisticalConsolidation statistic) {
        }

        protected void extraEntityHtmlListBulkActionExtra(ContainerTag actions, DescriptorHtmlEntity he, StatisticalConsolidation statistic) {

        }

        protected void extraEntityHtmlListBodyRowAction(ContainerTag col, DescriptorHtmlEntity he, StatisticalConsolidation statistic) {

        }

        protected void extraEntityHtmlFormActionExtra(ContainerTag formGroupActions, DescriptorHtmlEntity entityDescriptor, StatisticalConsolidation statistic) {

        }

        protected void extraEntityHtmlFormInputSelect(ContainerTag input, ContainerTag inputHidden, ContainerTag buton, ContainerTag script,
                DescriptorHtmlEntity e, StatisticalConsolidation statistic) {
        }
    }

    public static class AutoGenEntityHtmlCrudPageJsf extends AutoGenEntityHtmlCrudPage {

        public ContainerTag embraceJsfComposition(DomContent... tags) {
            ContainerTag html = new ContainerTag("html");
            html.attr("xmlns=\"http://www.w3.org/1999/xhtml\"")
                    .attr("xmlns:ui=\"http://java.sun.com/jsf/facelets\"")
                    .attr("xmlns:h=\"http://java.sun.com/jsf/html\"")
                    .attr("xmlns:ps=\"http://xmlns.jcp.org/jsf/passthrough\"")
                    .attr("xmlns:c=\"http://java.sun.com/jsp/jstl/core\"")
                    .attr("xmlns:f=\"http://java.sun.com/jsf/core\"");
            ContainerTag compostion = new ContainerTag("ui:composition");
            compostion.with(tags);
            html.with(compostion);
            return html;
        }

        @Override
        protected void extraEntityHtmlCrud(ContainerTag html, DescriptorHtmlEntity entityDescriptor, StatisticalConsolidation statistic) {
            html.attr("xmlns=\"http://www.w3.org/1999/xhtml\"")
                    .attr("xmlns:h=\"http://xmlns.jcp.org/jsf/html\"")
                    .attr("xmlns:f=\"http://xmlns.jcp.org/jsf/core\"")
                    .attr("xmlns:ps=\"http://xmlns.jcp.org/jsf/passthrough\"")
                    .attr("xmlns:c=\"http://java.sun.com/jsp/jstl/core\"")
                    .attr("xmlns:ui=\"http://xmlns.jcp.org/jsf/facelets\"");
        }

        @Override
        protected void extraEntityHtmlForm(ContainerTag form, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            form.attr("jsfc", "h:form");
            form.with(tag("ui:insert"));
        }

        @Override
        protected Tag extraEntityHtmlFormActionDefault(Tag btn, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            btn.attr("jsfc", "h:commandButton");
            btn.attr("action", "#{" + getBindExpression(entityDescriptor) + ".save()}");
            btn.attr("ps:data-i18n", "Save");

            return tag("ui:insert").withName("defaultAction").with(btn);
        }

        @Override
        protected void extraEntityHtmlFormInputSelect(ContainerTag input, ContainerTag inputHidden,
                ContainerTag buton, ContainerTag script, DescriptorHtmlEntity fieldEntity,
                StatisticalConsolidation statistic) {
            String entityBind = getEntityBind(fieldEntity);
            input.withValue(entityBind);
            inputHidden.withValue(entityBind)
                    .attr("jsfc", "h:inputHidden")
                    .attr("converter", "#{converterEntityBD}");
            //<h:inputHidden value="#{genericMB.crud('address').entity.city}" converter="#{converterEntityBD}" />
        }

        @Override
        protected void extraEntityHtmlFormInput(ContainerTag input, DescriptorHtmlEntity fieldEntity,
                StatisticalConsolidation statistic) {

            String entityBind = getEntityBind(fieldEntity);
            String typeLow = fieldEntity.getTypeLow();

            input.attr("value", entityBind);
            input.attr("ps:data-type", typeLow);

            switch (typeLow) {
                case "string":
                    input.attr("jsfc", "h:inputText");
                    break;
                case "int":
                case "integer":
                case "long":
                case "float":
                case "double":
                case "date":
                case "monetary":
                case "bigdecimal":
                    input.attr("jsfc", "h:inputText");
                    input.attr("converter", "converterGenericJsf");
                    break;
                case "boolean":
                    input.attr("jsfc", "h:selectBooleanCheckbox");
                    break;
                default:
                    input.attr("jsfc", "h:selectOneMenu")
                            //.withClass("autogen-form-group-select");//TODO: Resolver esse bug CSS
                            .withClass("custom-select");
                    /*
                    <f:selectItem itemLabel="" itemValue="" />
                    <f:selectItems value="#{genericMB.crud('entityDummy').values('entityDummy.statusType')}" 
                                   var="st" itemLabel="#{st.toString()}" itemValue="#{st}" /> 
                     */
                    Tag selectItemEmpty = new ContainerTag("f:selectItem")
                            .attr("itemLabel", "").attr("itemValue", "");

                    String bind = getBindExpression(fieldEntity);

                    Tag selectItens = new ContainerTag("f:selectItems")
                            .withValue("#{" + bind + ".values('entityName." + fieldEntity.property + "')}")
                            .attr("var", "st").attr("itemValue", "#{st}")
                            .attr("itemLabel", "#{st.toString()}");

                    if (IEntity.class.isAssignableFrom(fieldEntity.classe)) {
                        if (autogenweb.isExposed(fieldEntity.classe)) {
                            selectItens = new ContainerTag("f:selectItems")
                                    .withValue("#{genericMB.crud('" + fieldEntity.classe.getSimpleName() + "').entities}")
                                    .attr("var", "st").attr("itemValue", "#{st}")
                                    .attr("itemLabel", "#{st.toString()}");
                        }
                        input.attr("converter", "converterEntity");
                    }

                    input.with(
                            selectItemEmpty, selectItens
                    );
                    break;
            }
        }

        protected String getEntityBind(DescriptorHtmlEntity fieldEntity) {
            String entityBind;
            entityBind = fieldEntity.getAttributeAscending("entity");
            if (UtilValidation.isStringValid(entityBind)) {
                entityBind = "#{" + entityBind + "." + fieldEntity.property + "}";
            } else {
                entityBind = "#{" + getBindExpression(fieldEntity) + ".entity." + fieldEntity.property + "}";
            }
            return entityBind;
        }

        @Override
        protected void extraEntityHtmlFilterInput(Tag input, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            input.attr("value", "#{" + getBindExpression(entityDescriptor) + ".searchString}");
            input.attr("jsfc", "h:inputText");
        }

        protected void extraEntityHtmlFilterSearchInput(Tag btn, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            btn.attr("action", "#{" + getBindExpression(entityDescriptor) + ".find()}");
            btn.attr("jsfc", "h:commandButton");
            btn.attr("ps:data-i18n", "Search");
        }

        @Override
        protected void extraEntityHtmlListBodyRowActionRemove(ContainerTag btn,
                DescriptorHtmlEntity he,
                StatisticalConsolidation statistic) {
            btn.attr("action", "#{" + getBindExpression(he) + ".remove(ent)}");
            btn.attr("jsfc", "h:commandLink");
            btn.attr("ps:data-i18n", "Remove");
            btn.with(tag("h:panelGroup").withClass("ico-remove"));
        }

        @Override
        protected void extraEntityHtmlFilter(Tag html, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {

        }

        @Override
        protected void extraEntityHtmlListBodyRow(Tag row, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            row.attr("jsfc", "ui:repeat");
            String entities = entityDescriptor.getAttribute("entities");
            if (UtilValidation.isStringValid(entities)) {
                row.attr("value", "#{" + entities + "}");
            } else {
                String bind = getBindExpression(entityDescriptor);
                row.attr("value", "#{" + bind + ".entities}");
            }
            row.attr("var", "ent");
        }

        protected String getBindExpression(DescriptorHtmlEntity entityDescriptor) {
            String crud = entityDescriptor.getAttributeAscending("crud");
            String bind = "genericMB.crud('entityName')";
            if (UtilValidation.isStringValid(crud)) {
                bind = crud;
            }
            return bind;
        }

        @Override
        protected ContainerTag extraEntityHtmlListBodyRowColumn(ContainerTag th, DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            switch (entityDescriptor.getTypeLow()) {
                //case "int":
                //case "long":
                case "double":
                case "float":
                case "date":
                case "monetary":
                case "money":
                case "bigdecimal":
                    th.with(tag("h:outputText")
                            .attr("converter", "converterGenericJsf")
                            .withValue("#{ent." + entityDescriptor.property + "}")
                            .with(
                                    emptyTag("f:attribute")
                                            .attr("name", "entityProperty")
                                            .attr("value", "entityName." + entityDescriptor.property)
                            )
                    );
                    break;
                default:
                    if (entityDescriptor.getTypeClass().isEnum()) {
                        th.with(span("#{ent." + entityDescriptor.property + ".toString()}")
                                .withClass("enum-#{ent." + entityDescriptor.property + ".name().toLowerCase()}")
                        );
                    } else {
                        th.withText("#{ent." + entityDescriptor.property + "}");
                    }
                    break;
            }
            return th;
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
            btn.attr("actionListener", "#{" + getBindExpression(entityDescriptor) + ".setEntity(ent)}");
            btn.attr("ps:data-i18n", "Edit");
        }

        @Override
        protected void extraEntityHtmlListBodyRowAction(ContainerTag col, DescriptorHtmlEntity he, StatisticalConsolidation statistic) {
            col.with(emptyTag("ui:insert").withName("extraAction"));
        }

        @Override
        protected void extraEntityHtmlListBodyRowActionMenuExtra(ContainerTag menuExtraActions, DescriptorHtmlEntity he, StatisticalConsolidation statistic) {
            menuExtraActions.with(emptyTag("ui:insert").withName("extraActionMenu"));
        }

        @Override
        protected void extraEntityHtmlListBulkActionExtra(ContainerTag actions, DescriptorHtmlEntity he, StatisticalConsolidation statistic) {
            actions.with(emptyTag("ui:insert").withName("extraBulkAction"));
        }

        @Override
        protected void extraEntityHtmlListBulkActionMenuExtra(ContainerTag menuExtra, DescriptorHtmlEntity he, StatisticalConsolidation statistic) {
            menuExtra.with(emptyTag("ui:insert").withName("extraBulkActionMenu"));
        }

        @Override
        protected void extraEntityHtmlFormActionExtra(ContainerTag groupActions, DescriptorHtmlEntity entityDescriptor, StatisticalConsolidation statistic) {
            groupActions.with(emptyTag("ui:insert").withName("extraAction"));
        }

        //TODO: Refatorar para a classe superior
        protected ContainerTag entityHtmlListPaginate(DescriptorHtmlEntity entityDescriptor,
                StatisticalConsolidation statistic) {
            /*
            <div class="autogen-list-group">
            <input class="autogen-button-prev autogen-button-page" jsfc="h:commandButton" value="«"
            disabled="#{not genericMB.crud('entityName').hasPreviousPageQueryResult()}"
            action="#{genericMB.crud('entityName').previousPageQueryResult()}"
            aria-label="Anterior"/>
            <!--TODO: ui:repeat begin="" end="" has bug, verify in future -->
            <c:forEach begin="0" end="#{genericMB.crud('entityName').totalPages}" var="idx">
            <input class="#{genericMB.crud('entityName').currentPage eq idx ? 'autogen-button-page autogen-button-page-active':'autogen-button-page'}"
            jsfc="h:commandButton" action="#{genericMB.crud('entityName').paginate(idx)}"
            value="#{idx+1}" aria-label="Pagina #{idx}" />
            </c:forEach>
            <input class="autogen-button-next autogen-button-page" jsfc="h:commandButton" value="»"
            disabled="#{not genericMB.crud('entityName').hasNextPageQueryResult()}"
            action="#{genericMB.crud('entityName').nextPageQueryResult()}"
            aria-label="Proximo"/>
            </div>
             */
            ContainerTag paginationList = div().withClass("autogen-list-group-pagination-col")
                    .with(
                            tag("h:commandLink").withClass("autogen-button-prev autogen-button-page").withValue("«")
                                    .withAction("#{" + getBindExpression(entityDescriptor) + ".previousPageQueryResult()}")
                                    .attr("disabled", "#{not " + getBindExpression(entityDescriptor) + ".hasPreviousPageQueryResult()}")
                    //.attr("jsfc", "h:commandButton").attr("aria-label", "Anterior")
                    )
                    .with(
                            new ContainerTag("ui:repeat").attr("var", "idx").attr("value", "#{" + getBindExpression(entityDescriptor) + ".windowPages}")
                                    .with(tag("h:commandLink").withClass("#{" + getBindExpression(entityDescriptor) + ".currentPage eq idx ? "
                                            + "'autogen-button-page autogen-button-page-active':'autogen-button-page'}")
                                            //.attr("jsfc", "h:commandButton").attr("aria-label", "Pagina #{idx}")
                                            .withAction("#{" + getBindExpression(entityDescriptor) + ".paginate(idx)}").withValue("#{idx+1}")
                                    )
                    )
                    .with(
                            tag("h:outputText").withClass("autogen-button-page").withText("...")
                                    .attr("rendered", "#{" + getBindExpression(entityDescriptor) + ".hasWindowPageQueryResult()}")
                    ).with(
                            tag("h:commandLink").withClass("#{" + getBindExpression(entityDescriptor) + ".currentPage eq "
                                    + getBindExpression(entityDescriptor) + ".totalPages ? "
                                    + "'autogen-button-page autogen-button-page-active':'autogen-button-page'}")
                                    .attr("rendered", "#{" + getBindExpression(entityDescriptor) + ".hasWindowPageQueryResult()}")
                                    .withAction("#{" + getBindExpression(entityDescriptor) + ".paginate(idx)}")
                                    .withValue("#{" + getBindExpression(entityDescriptor) + ".totalPages}")
                    //.attr("jsfc", "h:commandButton").attr("aria-label", "Pagina #{idx}")
                    )
                    .with(
                            tag("h:commandLink").withClass("autogen-button-next autogen-button-page").withValue("»")
                                    .withAction("#{" + getBindExpression(entityDescriptor) + ".nextPageQueryResult()}")
                                    .attr("disabled", "#{not " + getBindExpression(entityDescriptor) + ".hasNextPageQueryResult()}")
                    //.attr("jsfc", "h:commandButton").attr("aria-label", "Proximo")
                    );

            return paginationList;
        }

        public ContainerTag entityHtmlFilter(DescriptorHtmlEntity he) {
            return this.entityHtmlFilter(he, null);
        }

        public ContainerTag entityHtmlList(DescriptorHtmlEntity he) {
            ContainerTag entityHtmlListGroup = div().withClass("autogen-list-group");
            ContainerTag entityHtmlList = this.entityHtmlList(he, null);
            boolean selectable = he.isAttribute("selectable");
            if (selectable) {
                ContainerTag entityHtmlListBulkActions = entityHtmlListBulkActions(he, null);
                entityHtmlListGroup.with(entityHtmlListBulkActions);
            }
            if (!he.isAttribute("nopaginate")) {
                ContainerTag entityHtmlListPaginate = entityHtmlListPaginate(he, null);
                entityHtmlListGroup.with(entityHtmlListPaginate);
            }
            if (he.isAttribute("responsive")) {
                entityHtmlList = div(entityHtmlList).withClass("autogen-list-responsive");
            }
            ContainerTag hform = new ContainerTag("h:form").with(entityHtmlList, entityHtmlListGroup);
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
