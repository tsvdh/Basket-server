import {AlertQueue, AlertType} from "../util/alerts.js";

export {}

document.getElementById("emailInput").oninput = function () {
    (<HTMLInputElement>document.getElementById("emailCodeInput")).value = "";
};

const phoneNumberCodeReset = function () {
    (<HTMLInputElement>document.getElementById("phoneNumberCodeInput")).value = "";
};

document.getElementById("phoneNumberInput").oninput = phoneNumberCodeReset;
document.getElementById("countryCodeInput").oninput = phoneNumberCodeReset;

document.getElementById("currentPasswordInput").addEventListener("input", function () {
    AlertQueue.addAlert("Hello world!", AlertType.Info);
});

document.getElementById("currentPasswordInput").addEventListener("mouseleave", function () {
    AlertQueue.addAlert("Bye world!", AlertType.Warning);
});