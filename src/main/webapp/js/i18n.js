//Reference and based: https://www.webcodegeeks.com/html5/html5-internationalization-example/
var xhttp = new XMLHttpRequest();
var langDocument = {};

xhttp.onreadystatechange = function () {
    if (this.readyState === 4 && this.status === 200) {
        langDocument = JSON.parse(this.responseText);
        processLangDocument();
    }
};

function loadLanguage(language) {
    xhttp.open("GET", "/app/i18n/" + language + ".json", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send();
}
function processLangDocument() {
    var tags = document.querySelectorAll('span,img,a,label,li,option,h1,h2,h3,th,input');
    Array.from(tags).forEach(function (value, index) {
        var key = value.dataset.i18n;
        if (langDocument[key]) {
            value.innerText = langDocument[key];
            if (value.nodeName.toLowerCase() == 'input')
                value.value = langDocument[key];
        }
    });
}

try {
    if (navigator.userLanguage)
        loadLanguage(navigator.userLanguage);
    else
        loadLanguage('pt_br');
} catch (error) {
    console.log(error)
}
