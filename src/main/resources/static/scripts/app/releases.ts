import {toMB} from "../util/utils.js";

export {}

// Automatic initialization

window.addEventListener("load", function () {
    let appName = document.getElementById("appNameValue").innerText;

    document.getElementsByName("appName").forEach(element => {
        (<HTMLInputElement>element).value = appName;
    });

    document.getElementsByName("type").forEach(element => {
        (<HTMLInputElement>element).value = element.getAttribute("placeholder");
        element.removeAttribute("placeholder");
    });

    document.getElementsByName("toFire").forEach(element => {
        (<HTMLButtonElement>element).click();
    });
});

// Progress listeners

const types = ["icon", "stable", "experimental"];

document.addEventListener("htmx:afterSwap", function () {
    types.forEach((type: string) => {
        let formId = type + "UploadForm";
        let progressId = type + "UploadProgress";

        let form = document.getElementById(formId);
        if (form == null) {
            return;
        }
        types.splice(types.indexOf(type), 1);

        let progress = document.getElementById(progressId);

        form.addEventListener("htmx:xhr:loadstart", function (event: CustomEvent) {
            form.classList.add("d-none");

            let bar = progress.children.namedItem("bar");
            (<HTMLElement>bar.firstElementChild).style.width = "0";

            progress.children.namedItem("text").textContent = "0 / 0 MB";

            progress.classList.remove("d-none");
        });

        form.addEventListener("htmx:xhr:loadend", function () {
            progress.classList.add("d-none");

            form.classList.remove("d-none");
        });

        form.addEventListener("htmx:xhr:progress", (event: CustomEvent) => {
            // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
            let progressPercent = `${(event.detail.loaded / event.detail.total) * 100}%`;

            let bar = progress.children.namedItem("bar");
            (<HTMLElement>bar.firstElementChild).style.width = progressPercent;

            // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-argument
            progress.children.namedItem("text").textContent = `${toMB(event.detail.loaded)} / ${toMB(event.detail.total)} MB`;
        });
    });
});
