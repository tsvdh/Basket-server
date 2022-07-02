import {AlertQueue, AlertType} from "./util/alerts.js";

export {}

/*--- Form submit ---*/

document.getElementById("registerForm").onsubmit = event => {
    let invalids = document.getElementsByClassName("is-invalid");

    if (invalids.length == 0) {
        return;
    }

    event.preventDefault();
    event.stopPropagation();

    AlertQueue.addAlert("All fields must be valid!", AlertType.Warning);
};

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
