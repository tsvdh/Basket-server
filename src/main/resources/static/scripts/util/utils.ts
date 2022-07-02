export function toKB(bytes: number): string {
    return (bytes / 1000).toFixed(0);
}

export function toMB(bytes: number): string {
    return (bytes / 1000000).toFixed(1);
}

export function autoFillInputs () {
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