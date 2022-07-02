import {AlertQueue, AlertType} from "../util/alerts.js";
import {autoFillInputs} from "../util/utils.js";

export {}

let formSwappedIn = false;

document.addEventListener("htmx:afterSwap", event => {

    if (formSwappedIn || !document.getElementById("userInfoForm")) {
        // swap actions completed or main content not loaded yet
        return;
    } else {
        formSwappedIn = true;
    }

    autoFillInputs();

    document.getElementById("emailInput").oninput = function () {
        (<HTMLInputElement>document.getElementById("emailCodeInput")).value = "";
    };

    const phoneNumberCodeReset = function () {
        (<HTMLInputElement>document.getElementById("phoneNumberCodeInput")).value = "";
    };

    document.getElementById("phoneNumberInput").oninput = phoneNumberCodeReset;
    document.getElementById("countryCodeInput").oninput = phoneNumberCodeReset;

    /*--- Form submit ---*/

    document.getElementById("userInfoForm").onsubmit = event => {
        let invalids = document.getElementsByClassName("is-invalid");

        if (invalids.length == 0) {
            return;
        }

        event.preventDefault();
        event.stopPropagation();

        AlertQueue.addAlert("All fields must be valid!", AlertType.Warning);
    };
});