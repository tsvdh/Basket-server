export {makeDangerAlert}

function makeAlert(role: string, text: string): HTMLElement {
    let alert = document.createElement("div");
    alert.classList.add("alert", "alert-" + role, "alert-dismissible");
    alert.setAttribute("role", "alert");
    alert.textContent = text;

    let dismissButton = document.createElement("button");
    dismissButton.classList.add("btn-close");
    dismissButton.type = "button";
    dismissButton.setAttribute("data-bs-dismiss", "alert");

    alert.appendChild(dismissButton);

    return alert;
}

function makeDangerAlert(text: string): HTMLElement {
    return makeAlert("danger", text);
}