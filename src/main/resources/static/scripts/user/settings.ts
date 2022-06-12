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

const alertQueue = new AlertQueue();

document.getElementById("currentPasswordInput").addEventListener("input", function () {
    alertQueue.addAlert("Hello world!", AlertType.Info);
});

document.getElementById("currentPasswordInput").addEventListener("mouseleave", function () {
    alertQueue.addAlert("Bye world!", AlertType.Warning);
});