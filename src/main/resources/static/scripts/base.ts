import {AlertQueue, AlertType} from "./util/alerts.js";
import {autoFillInputs, getChildren} from "./util/utils.js";

/*--- Fill fields automatically ---*/

window.addEventListener("load", autoFillInputs);
document.addEventListener("htmx:afterSwap", autoFillInputs);

/*--- Password toggle ---*/

const toggleInput = (event: Event) => {
    const button = (<HTMLButtonElement>event.currentTarget);

    const iconClasses = button.firstElementChild.classList;

    if (iconClasses.contains("bi-eye-fill")) {
        iconClasses.remove("bi-eye-fill");
        iconClasses.add("bi-eye-slash-fill");
    } else {
        iconClasses.add("bi-eye-fill");
        iconClasses.remove("bi-eye-slash-fill");
    }

    Array.from(button.parentElement.children).forEach(element => {
        if (element.tagName.toLowerCase() != "input") {
            return;
        }

        const input = <HTMLInputElement>element;
        switch (input.type) {
            case "text":
                input.type = "password";
                break;
            case "password":
                input.type = "text";
                break;
            default:
                break;
        }
    });
};

const addToggleListeners = function () {
    Array.from(document.getElementsByTagName("button")).forEach(button => {
        if ("toggleText" in button.dataset) {
            button.onclick = toggleInput;
        }
    });
};

// Add toggle functionality, and to fragments swapped in later
addToggleListeners();
document.addEventListener("htmx:afterSwap", addToggleListeners);

/*--- Fragment functionality ---*/

const emailListener = (event: Event) => {
    let emailInput = document.getElementById("emailInput");

    if (emailInput.classList.contains("is-valid")) {
        AlertQueue.addAlert("Email sent!", AlertType.Info);
    } else {
        event.preventDefault();
        event.stopImmediatePropagation();

        AlertQueue.addAlert("Email must be valid!", AlertType.Warning);
    }
};

const phoneNumberListener = (event: Event) => {
    let phoneNumberInput = document.getElementById("phoneNumberInput");

    if (phoneNumberInput.classList.contains("is-valid")) {
        AlertQueue.addAlert("SMS sent!", AlertType.Info);
    } else {
        event.preventDefault();
        event.stopImmediatePropagation();

        AlertQueue.addAlert("Phone number must be valid!", AlertType.Warning);
    }
};

function addListeners() {
    const emailButton = document.getElementById("emailButton");
    if (emailButton) {
        emailButton.addEventListener("htmx:beforeRequest", emailListener);
    }

    const phoneNumberButton = document.getElementById("phoneNumberButton");
    if (phoneNumberButton) {
        phoneNumberButton.addEventListener("htmx:beforeRequest", phoneNumberListener);
    }
}

// Add listeners, and to fragments swapped in later
addListeners();
document.addEventListener("htmx:afterSwap", addListeners);

/*--- Form submit ---*/

document.addEventListener("submit", event => {
    let invalids = getChildren(event.target as Element,
        (element) => element.classList.contains("is-invalid"));

    if (invalids.length == 0) {
        return;
    }

    event.preventDefault();
    event.stopPropagation();

    AlertQueue.addAlert("All fields must be valid!", AlertType.Warning);
});
