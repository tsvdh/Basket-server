export {}

window.addEventListener("load", function () {
    let appName = document.getElementById("appNameValue").innerText;

    document.getElementsByName("appName").forEach(function (element) {
        (<HTMLInputElement>element).value = appName;
    });

    document.getElementsByName("type").forEach(function (element) {
        (<HTMLInputElement>element).value = element.getAttribute("placeholder");
        element.removeAttribute("placeholder");
    });
});