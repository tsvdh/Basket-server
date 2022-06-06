import {autoFillInputs} from "../util/utils.js";

export {}

// Automatic initialization

window.addEventListener("load", function () {
    autoFillInputs();
});

document.getElementById("emailInput").oninput = function () {
    (<HTMLInputElement>document.getElementById("emailCodeInput")).value = "";
};

const phoneNumberCodeReset = function () {
    (<HTMLInputElement>document.getElementById("phoneNumberCodeInput")).value = "";
};

document.getElementById("phoneNumberInput").oninput = phoneNumberCodeReset;
document.getElementById("countryCodeInput").oninput = phoneNumberCodeReset;