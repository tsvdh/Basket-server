import {toKB, toMB} from "../util/utils.js";

export {}

// Automatic initialization

window.addEventListener("load", function () {
    let appName = document.getElementById("appNameValue").innerText;

    document.getElementsByName("appName").forEach(element => {
        (<HTMLInputElement>element).value = appName;
    });

    document.getElementsByName("toFire").forEach(element => {
        (<HTMLButtonElement>element).click();
    });
});

// Progress listeners

const uploadTypes: string[] = ["icon", "stable", "experimental"];

const loadingMap = new Map<string, boolean>();
uploadTypes.forEach(type => {
    loadingMap.set(type, false);
});

document.addEventListener("htmx:afterSwap", function () {
    uploadTypes.forEach((type: string) => {
        let form = document.getElementById(type + "UploadForm");
        let progress = document.getElementById(type + "UploadProgress");
        let cancelButton = document.getElementById(type + "CancelButton");

        if (form == null) {
            return;
        }
        uploadTypes.splice(uploadTypes.indexOf(type), 1);

        form.addEventListener("htmx:xhr:loadstart", function () {
            if (loadingMap.get(type)) {
                return;
            } else {
                loadingMap.set(type, true);
            }

            form.classList.add("d-none");

            let bar = progress.children.namedItem("bar");
            (<HTMLElement>bar.firstElementChild).style.width = "0";

            progress.classList.remove("d-none");
        });

        form.addEventListener("htmx:xhr:loadend", function () {
            if (!loadingMap.get(type)) {
                return;
            } else {
                loadingMap.set(type, false);
            }

            setTimeout(function () {
                progress.classList.add("d-none");
                form.classList.remove("d-none");

                (<HTMLButtonElement>document.getElementById("storageStatusRefreshButton")).click();
            }, 1000);
        });

        form.addEventListener("htmx:xhr:progress", (event: CustomEvent) => {
            // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment, @typescript-eslint/no-unsafe-member-access
            const loaded: number = event.detail.loaded;
            // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment, @typescript-eslint/no-unsafe-member-access
            const total: number = event.detail.total;

            let progressPercent = `${(loaded / total) * 100}%`;

            let bar = progress.children.namedItem("bar");
            (<HTMLElement>bar.firstElementChild).style.width = progressPercent;

            let progressText: string;

            if (total < 1000000) {
                progressText = `${toKB(loaded)} / ${toKB(total)} KB`;
            }  else {
                progressText = `${toMB(loaded)} / ${toMB(total)} MB`;
            }

            progress.children.namedItem("text").textContent = progressText;
        });

        cancelButton.onclick = function () {
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-ignore
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call, @typescript-eslint/no-unsafe-member-access
            htmx.trigger(form, "htmx:abort", null);
        };
    });

    let releaseButton = document.getElementById("releaseButton");

    if (releaseButton != null) {
        releaseButton.onclick = function () {
            // Schedule click so htmx sync order is correct
            setTimeout(function () {
                (<HTMLButtonElement>document.getElementById("storageStatusRefreshButton")).click();
            });
        };
    }
});
