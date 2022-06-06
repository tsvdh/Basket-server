export function toKB(bytes: number): string {
    return (bytes / 1000).toFixed(0);
}

export function toMB(bytes: number): string {
    return (bytes / 1000000).toFixed(1);
}

export function autoFillInputs() {
    const inputs = document.getElementsByTagName("input");

    for (let i = 0; i < inputs.length; i++) {
        let input = inputs[i];

        if ("autoFill" in input.dataset) {
            input.value = input.dataset["autoFill"];
        }
    }
}