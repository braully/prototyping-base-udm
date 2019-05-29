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