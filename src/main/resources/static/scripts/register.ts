export {}
import {makeDangerAlert, makePrimaryAlert, makeSuccessAlert} from "./alert.js";

/*--- Helper methods ---*/

function insertAlert(alert: HTMLElement) {
    document
        .getElementsByTagName("main")
        .item(0)
        .insertAdjacentElement("afterbegin", alert);
}

/*--- Buttons ---*/

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

// function getFeedbackClassName(input: HTMLInputElement): string {
//     return input.id + "-feedback";
// }
//
// function removeStyleFromInput(input: HTMLInputElement): void {
//     const feedbackClassName = getFeedbackClassName(input);
//
//     /*--- Reset feedback elements and style ---*/
//     input.classList.remove("is-valid", "is-invalid");
//
//     let feedbackElements: HTMLCollectionOf<Element>
//         = document.getElementsByClassName(feedbackClassName);
//
//     while (feedbackElements.length > 0) {
//         feedbackElements[0].remove();
//     }
// }
//
// function getFaultCarrier(input: HTMLInputElement): HTMLElement {
//     if (input.parentElement.classList.contains("input-group")) {
//         return input.parentElement;
//     } else {
//         return input;
//     }
// }
//
// function applyStyleToInput(input: HTMLInputElement, faults: string[]): void {
//     const feedbackClassName = getFeedbackClassName(input);
//
//     /*--- Add feedback elements and style ---*/
//     if (faults.length === 0) {
//         input.classList.add("is-valid");
//         return;
//     } else {
//         input.classList.add("is-invalid");
//     }
//
//     /*--- Insert any faults ---*/
//     let faultCarrier =  getFaultCarrier(input);
//
//     for (let fault of faults) {
//         let error: HTMLDivElement = document.createElement("div");
//
//         error.classList.add("invalid-feedback");
//         error.classList.add(feedbackClassName);
//
//         error.textContent = fault;
//         faultCarrier.insertAdjacentElement("afterend", error);
//     }
// }
//
// const USERNAME_PROBLEMS = {
//     WHITE_SPACE: "Whitespaces are not allowed"
// };
//
// const usernameInput = <HTMLInputElement>document.getElementById("usernameInput");
//
// usernameInput.oninput = async function () {
//     const username: string = usernameInput.value.trim();
//
//     if (username === "") {
//         removeStyleFromInput(usernameInput);
//         return;
//     }
//
//     let faults: string[] = [];
//
//     if (username.match(/\s/) !== null) {
//         faults.push(USERNAME_PROBLEMS.WHITE_SPACE);
//     }
//
//     let request = new Request(ROOT_ADDRESS + "/api/v1/account/username",
//         {
//             method: "GET",
//             headers: {
//                 "username": username
//             }
//         });
//
//     //TODO: add spinner
//
//     let response = await fetch(request);
//     let body: string = await response.text();
//
//     if (body === "false") {
//         faults.push("Username already taken");
//     }
//
//     removeStyleFromInput(usernameInput);
//     applyStyleToInput(usernameInput, faults);
// };
//
// const PASSWORD_LENGTH = 8;
//
// const PASSWORD_PROBLEMS = {
//     TOO_SHORT: "Must be at least X characters".replace("X", PASSWORD_LENGTH.toString()),
//     NO_NUMBER: "Must contain a number",
//     NO_LETTER: "Must contain a letter",
//     WHITE_SPACE: "White spaces are not allowed"
// };
//
// const passwordInput = <HTMLInputElement>document.getElementById("passwordInput");
//
// passwordInput.oninput = function () {
//     removeStyleFromInput(passwordInput);
//
//     const password: string = passwordInput.value.trim();
//
//     if (password === "") {
//         return;
//     }
//
//     /*--- Determine faults in password ---*/
//     let faults: string[] = [];
//
//     if (password.match(/\s/) !== null) {
//         faults.push(PASSWORD_PROBLEMS.WHITE_SPACE);
//     }
//
//     if (password.length < PASSWORD_LENGTH) {
//         faults.push(PASSWORD_PROBLEMS.TOO_SHORT);
//     }
//
//     if (password.match(/\d/) === null) {
//         faults.push(PASSWORD_PROBLEMS.NO_NUMBER);
//     }
//
//     if (password.match(/[A-Za-z]/) === null) {
//         faults.push(PASSWORD_PROBLEMS.NO_LETTER);
//     }
//
//     applyStyleToInput(passwordInput, faults);
// };
//
// const emailInput = <HTMLInputElement>document.getElementById("emailInput");
//
// emailInput.oninput = async function () {
//
//     removeStyleFromInput(emailInput);
//
//     const emailAddress: string = emailInput.value.trim();
//
//     let request = new Request(
//         ROOT_ADDRESS + "/api/v1/account/available/email",
//         {
//             method: "GET",
//             headers: {
//                 emailAddress: "x"
//             }
//         }
//     );
// };
//
// document.getElementById("emailButton").onclick = function () {
//     /*--- Button only works when input is valid, no validation needed ---*/
//
//     let request = new Request(
//         ROOT_ADDRESS + "/api/v1/account/submit/email",
//         {
//             method: "GET",
//             headers: {
//                 emailAddress: emailInput.value.trim()
//             }
//         }
//     );
//
// };
