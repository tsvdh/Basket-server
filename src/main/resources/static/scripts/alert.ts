export {makeDangerAlert, makeSuccessAlert, makePrimaryAlert}

function makeAlert(role: string, text: string, iconName: string): HTMLElement {
    let alert = document.createElement("div");
    alert.classList.add("alert", "alert-" + role, "alert-dismissible", "d-flex", "align-items-center");
    alert.setAttribute("role", "alert");
    alert.textContent = text;

    let dismissButton = document.createElement("button");
    dismissButton.classList.add("btn-close");
    dismissButton.type = "button";
    dismissButton.setAttribute("data-bs-dismiss", "alert");
    dismissButton.style.marginTop = "7px";

    let icon = document.createElement("i");
    icon.classList.add(iconName, "me-2");
    icon.style.fontSize = "1.5rem";

    alert.prepend(icon);
    alert.appendChild(dismissButton);

    return alert;
}

function makeDangerAlert(text: string): HTMLElement {
    return makeAlert("danger", text, "bi-exclamation-circle");
}

function makeSuccessAlert(text: string): HTMLElement {
    return makeAlert("success", text, "bi-check-circle");
}

function makePrimaryAlert(text: string): HTMLElement {
    return makeAlert("primary", text, "bi-info-circle");
}