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
        seleteds.push($(this).data("id"));
    });
    if (seleteds.length == 0) {
        //trying in same form
        $(callobj).closest('form').find('table tbody :checkbox:checked').each(function () {
            seleteds.push($(this).data("id"));
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
