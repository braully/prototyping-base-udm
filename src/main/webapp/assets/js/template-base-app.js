//https://stackoverflow.com/questions/3915917/make-a-link-use-post-instead-of-get
function normal_post(url, data) {
    var vform = $('<form></form>');
    vform.attr('action', url);
    vform.attr('method', 'post');
    for (name in data) {
        var vinput = $("<input>");
        vinput.attr('name', name);
        vinput.attr('value', data[name]);
        vform.append(vinput);
    }
    //https://stackoverflow.com/questions/42053775/getting-error-form-submission-canceled-because-the-form-is-not-connected
    $(document.body).append(vform);
    vform.submit();
}


function table_selected_id(callobj) {
    var seleteds = [];
    $(callobj).closest('table').find('tbody :checkbox:checked').each(function () {
        seleteds.push($(this).data("modelId"));
    });
    if (seleteds.length == 0) {
        //trying in same form
        $(callobj).closest('form').find('table tbody :checkbox:checked').each(function () {
            seleteds.push($(this).data("modelId"));
        });
    }
    return seleteds;
}

//https://codepen.io/diegobdev/pen/vGoqKW
//TODO: Aprimorar o comportamento do selectall, caso um item seja descelecionado, desmaca-lo, e caso seja selecionado novamente marca-lo
function checkAll(callobj) {
    $(callobj).closest('table').find('tbody :checkbox')
            .prop('checked', callobj.checked)
            .closest('tr').toggleClass('selected', callobj.checked);
}

function autocompleteInput(inputAuto, type) {
    $(inputAuto).autocomplete({
        source: "/app/auto-complete/".concat(type),
        delay: 500, minLength: 3,
        select: function (event, ui) {
            $(this).val(ui.item.label).attr("disabled", true);
            $(this).next("input[type=hidden]").val(''.concat(type, ':', ui.item.value));
            return false;
        }
    });
    $(inputAuto).prop("disabled", ($(inputAuto).next("input[type=hidden]").val() != ""));
}


function clearModal() {
    $('#autogen-modal-title').empty();
    $('#autogen-modal-body').empty();
    $('#autogen-modal-footer').empty();
    $('#autogen-modal-body-iframe').empty();
    $('#autogen-modal-body-iframe').attr('src', '').css({width: '', height: ''});
}

try {
    $(function () {
        $('a.autogen-confirm').click(function (event) {
            event.preventDefault();
            clearModal();

            var titulo = $(this).data('modal-titulo');
            if (!titulo) {
                titullo = "Confirmação";
            }

            var mensagem = $(this).data('modal-mensagem');
            if (!mensagem) {
                mensagem = "<p><h6>Tem certeza que deseja prosseguir?</h6></p>";
            }

            $('#autogen-modal-title').append(titulo);
            $('#autogen-modal-body').append(mensagem);
            $('#autogen-modal-footer')
                    .append("<a class='autogen-button' href='" + $(this).attr('href') + "'>Sim</a>")
                    .append("<div class='m-2' />")
                    .append("<button type='button' class='autogen-button-alt' data-dismiss='modal'>Não</button>");
            $('#autogen-modal').modal('show');
        });
    });
} catch (e) {
    console.log(e);
}

//try {
//    $(function () {
//        $('a.autogen-confirm').confirm({
//            buttons: {
//                hey: function () {
//                    location.href = this.$target.attr('href');
//                }
//            }
//        });
//    });
//} catch (e) {
//    console.log(e);
//}

try {
    //https://stackoverflow.com/questions/34763090/bootstrap-4-with-remote-modal
    $(function () {
        //$('a[data-toggle="modal"]').click(function () {
        $('a.autogen-popup').click(function (event) {
            //console.info('clicked');
            event.preventDefault();

            //Clear last
            clearModal();

            var url = $(this).attr('href');
            if (!url) {
                return;
            }
            //
            if (!url.startsWith('#')) {
                if ($(this).hasClass('iframe')) {
                    //https://mdbootstrap.com/plugins/jquery/iframe/
                    //https://developer.mozilla.org/pt-BR/docs/Web/HTML/Element/iframe
                    $('#autogen-modal-body-iframe').css({width: '680', height: '480'}).attr('src', url);
                } else {
                    $('#autogen-modal-body').load(url);
                }
            }
            //else { //TODO: Implementar Modal dinamico com o conteudo passado pelo botão }
            $('#autogen-modal').modal('show');
        });
    });
} catch (e) {
    console.log(e);
}

function ajuda() {
    try {
        var url = window.location.pathname;
        if (!url) {
            url = document.URL;
        }
        url = '/system/help?page=' + url + ' div.base-app-main-content-body';
        clearModal();
        
        //console.log('help url ' + url);
        $('#autogen-modal-title').append("Ajuda");
        $('#autogen-modal-body').load(url);
        
        $('#autogen-modal').modal('show');
    } catch (e) {
        console.log(e);
    }
}

//https://jqueryui.com/datepicker/
try {
    $.datepicker.setDefaults($.datepicker.regional[ 'pt' ]);

    $(function () {
        $('input[data-type="date"]')
                .datepicker({dateFormat: 'dd/mm/yy', locale: 'pt-BR',
                    monthNames: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
                    dayNamesShort: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'],
                    dayNamesMin: ['D', 'S', 'T', 'Q', 'Q', 'S', 'S']
                });
    });
} catch (e) {
    console.log(e);
}

try {
    $(function () {
        $('input[data-type="phone-number"]').mask("(99) 9999-99999")
                .focusout(function (event) {
                    var target, phone, element;
                    target = (event.currentTarget) ? event.currentTarget : event.srcElement;
                    phone = target.value.replace(/\D/g, '');
                    element = $(target);
                    element.unmask();
                    if (phone.length > 10) {
                        element.mask("(99) 9999-9999");
                    } else {
                        element.mask("(99) 9999-99999");
                    }
                });
    });
} catch (e) {
    console.log(e);
}

//https://underscorejs.org/#template
try {
    _.templateSettings = {
        interpolate: /\{\{(.+?)\}\}/g
    };
} catch (e) {
    console.log(e);
}


//https://stackoverflow.com/questions/1349404/generate-random-string-characters-in-javascript
function randomString(len, charSet) {
    charSet = charSet || 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var randomString = '';
    for (var i = 0; i < len; i++) {
        var randomPoz = Math.floor(Math.random() * charSet.length);
        randomString += charSet.substring(randomPoz, randomPoz + 1);
    }
    return randomString;
}


/*var phones = new Backbone.Collection({}, {model: Phone});
 phones.on("add", function (obj) {
 obj.on("change", function () { phones.trigger("update", phones); });
 }).on("update", function (e) {
 $("#Phone-script").prev("input[type=hidden]").val(JSON.stringify(e));
 });*/

//https://backbonejs.org/#Collection-modelId
var modelIdCount = 0;

function getmodels(Model) {
    //https://stackoverflow.com/questions/37607551/backbone-class-properties-this-property-wont-work-in-its-methods
    var modelListName = Model.typeName + 's';
    var modelList = window[modelListName];
    console.log(modelListName);
    console.log(Model);
    if (!modelList) {
        modelList = new Backbone.Collection({}, {model: Model});
        window[modelListName] = modelList;
        modelList.on("add", function (obj) {
            obj.on("change", function () {
                modelList.trigger("update", modelList);
            });
        }).on("update", function (e) {
            $("#" + Model.typeName + "-script").prev("input[type=hidden]").val(JSON.stringify(e));
        });
    }
    return modelList;
}


//function updateModel(input, models, attr) {
function updateModel(input, Model, attr) {
    var models = getmodels(Model);
    var modelid = $(input).closest("div.autogen-form-row").data("modelId");
    var model = models.get(modelid);
    model.set(attr, input.value);
}

//function removeModel(input, models) {
function removeModel(input, Model) {
    var models = getmodels(Model);
    var row = $(input).closest('div.autogen-form-row');
    models.remove(row.data("modelId"));
    row.remove();
}

//function newModel(models, Model) {
function newModel(Model) {
    var models = getmodels(Model);
    var model = new Model({_id: modelIdCount++});
    models.add(model);
    return model;
}

//function newModelTemplate(templateHtmlModel, models, Model) {
function newModelTemplate(templateHtmlModel, Model) {
    var model = newModel(Model);
    var template = templateHtmlModel({model: model.toJSON()});
    return $(template).data("id", model.get('id'));
}

function newRowModelTemplate(callobj, templateHtmlModel, Model) {
    var templ = newModelTemplate(templateHtmlModel, Model);
    $(callobj).closest('div.autogen-form-row').after(templ);
}
