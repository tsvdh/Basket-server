export function toKB(bytes) {
    return (bytes / 1000).toFixed(0);
}
export function toMB(bytes) {
    return (bytes / 1000000).toFixed(1);
}
export function autoFillInputs() {
    const inputs = document.getElementsByTagName("input");
    const selects = document.getElementsByTagName("select");
    for (let i = 0; i < inputs.length; i++) {
        let input = inputs[i];
        if ("autoFill" in input.dataset) {
            input.value = input.dataset["autoFill"];
            delete input.dataset["autoFill"];
        }
    }
    for (let i = 0; i < selects.length; i++) {
        let select = selects[i];
        if ("autoFill" in select.dataset) {
            select.value = select.dataset["autoFill"];
            delete select.dataset["autoFill"];
        }
    }
}
export function getChildren(element, filter) {
    let list = [];
    if (filter(element)) {
        list.push(element);
    }
    for (let child of element.children) {
        list.push(...getChildren(child, filter));
    }
    return list;
}
export function setRequiredInputs(required) {
    const optionalInputs = [
        document.getElementById("firstNameInput"),
        document.getElementById("lastNameInput"),
        document.getElementById("countryCodeInput"),
        document.getElementById("phoneNumberInput"),
        document.getElementById("phoneNumberCodeInput")
    ];
    for (let input of optionalInputs) {
        input.required = required;
    }
}
