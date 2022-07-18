function addEnterListener() {
    let input = document.getElementById("currentPasswordInput");
    if (input == null) {
        return;
    }
    input.onkeydown = (event) => {
        if (event.key.toLowerCase() == "enter") {
            document.getElementById("currentPasswordButton").click();
        }
    };
}
window.addEventListener("load", addEnterListener);
document.addEventListener("htmx:afterSwap", addEnterListener);
export {};
