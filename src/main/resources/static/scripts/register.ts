export {}
import {makeDangerAlert, makePrimaryAlert, makeSuccessAlert} from "./util/alert.js";

/*--- Helper methods ---*/

function insertAlert(alert: HTMLElement) {
    document
        .getElementsByTagName("main")
        .item(0)
        .insertAdjacentElement("afterbegin", alert);
}

/*--- Button actions ---*/

document.getElementById("form").onsubmit = function (ev) {
    let invalids = document.getElementsByClassName("is-invalid");

    if (invalids.length == 0) {
        return;
    }

    ev.preventDefault();
    ev.stopPropagation();

    let alert = makeDangerAlert("All fields must be valid!");
    insertAlert(alert);
};

document.getElementById("emailButton").addEventListener("htmx:beforeRequest", function (ev) {
    let emailInput = document.getElementById("emailInput");

    let alert: HTMLElement;

    if (emailInput.classList.contains("is-valid")) {
        alert = makePrimaryAlert("Email sent!");
    } else {
        ev.preventDefault();
        ev.stopImmediatePropagation();

        alert = makeDangerAlert("Email must be valid!");
    }

    insertAlert(alert);
});

document.getElementById("phoneNumberButton").addEventListener("htmx:beforeRequest", function (ev) {
    let phoneNumberInput = document.getElementById("phoneNumberInput");

    let alert: HTMLElement;

    if (phoneNumberInput.classList.contains("is-valid")) {
        alert = makePrimaryAlert("SMS sent!");
    } else {
        ev.preventDefault();
        ev.stopImmediatePropagation();

        alert = makeDangerAlert("Phone number must be valid!");
    }

    insertAlert(alert);
});

/*--- Nav tabs ---*/

const userTypeInput = <HTMLSelectElement>document.getElementById("userTypeInput");

const optionalInputs = [
    <HTMLInputElement>document.getElementById("firstNameInput"),
    <HTMLInputElement>document.getElementById("lastNameInput"),
    <HTMLInputElement>document.getElementById("countryCodeInput"),
    <HTMLInputElement>document.getElementById("phoneNumberInput"),
    <HTMLInputElement>document.getElementById("phoneNumberCodeInput")
];

document.getElementById("nav-user").onclick = function () {
    userTypeInput.value = "USER";
    optionalInputs.forEach(input => input.required = false);
};

document.getElementById("nav-developer").onclick = function () {
    userTypeInput.value = "DEVELOPER";
    optionalInputs.forEach(input => input.required = true);
};
