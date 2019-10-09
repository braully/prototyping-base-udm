
function autocompleteInput(inputAuto, type) {

                $(inputAuto).autocomplete({
                source: "/app/auto-complete/".concat(type).concat("/"),
                delay: 500, minLength: 3,
                select: function (event, ui) {
                $(this).val(ui.item.label).attr("disabled", true);
                $(this).next("input[type=hidden]").val(''.concat(type, ':', ui.item.value));
                return false;
                }
                });
                $(inputAuto).prop("disabled", ($(inputAuto).next("input[type=hidden]").val() != ""));
       

}
