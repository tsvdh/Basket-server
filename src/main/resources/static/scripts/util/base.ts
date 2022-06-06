window.addEventListener("load", function () {
    const inputs = document.getElementsByTagName("input");
    const selects = document.getElementsByTagName("select");

    for (let i = 0; i < inputs.length; i++) {
        let input = inputs[i];

        if ("autoFill" in input.dataset) {
            input.value = input.dataset["autoFill"];
        }
    }

    for (let i = 0; i < selects.length; i++) {
        let select = selects[i];

        if ("autoFill" in select.dataset) {
            select.value = select.dataset["autoFill"];
        }
    }
});

Array.from(document.getElementsByTagName("button")).forEach(button => {
    if (!("toggleText" in button.dataset)) {
        return;
    }

    button.addEventListener("click", (event: Event) => {
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
    });
});
