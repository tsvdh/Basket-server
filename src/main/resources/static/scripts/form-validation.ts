export {}


function getFeedbackClassName(input: HTMLInputElement): string {
    return input.id + "-feedback";
}

function removeStyle(input: HTMLInputElement): void {
    const feedbackClassName = getFeedbackClassName(input);

    /*--- Reset feedback elements and style ---*/
    input.classList.remove("is-valid", "is-invalid");

    let feedbackElements: HTMLCollectionOf<Element>
        = document.getElementsByClassName(feedbackClassName);

    while (feedbackElements.length > 0) {
        feedbackElements[0].remove();
    }
}

function applyStyle(input: HTMLInputElement, faults: string[]): void {
    const feedbackClassName = getFeedbackClassName(input);

    /*--- Add feedback elements and style ---*/
    if (faults.length === 0) {
        input.classList.add("is-valid");
    } else {
        input.classList.add("is-invalid");

        for (let fault of faults) {
            let error: HTMLDivElement = document.createElement("div");

            error.classList.add("invalid-feedback");
            error.classList.add(feedbackClassName);

            error.textContent = fault;
            input.insertAdjacentElement("afterend", error);
        }
    }
}


const USERNAME_PROBLEMS = {
    WHITE_SPACE: "Whitespaces are not allowed"
};

const usernameInput = <HTMLInputElement> document.getElementById("usernameInput");

usernameInput.oninput = async function () {
    removeStyle(usernameInput);

    const username: string = usernameInput.value.trim();

    if (username === "") {
        return;
    }

    let faults: string[] = [];

    if (username.match(/\s/) !== null) {
        faults.push(USERNAME_PROBLEMS.WHITE_SPACE);
    }

    let request = new Request("http://localhost:8080/api/v1/account/username");
    request.headers.append("username", username);

    let response = await fetch(request);
    let body: string = await response.text();

    if (body === "false") {
        faults.push("Username already taken");
    }

    applyStyle(usernameInput, faults);
};


const PASSWORD_LENGTH = 8;

const PASSWORD_PROBLEMS = {
    TOO_SHORT: "Must be at least X characters".replace("X", PASSWORD_LENGTH.toString()),
    NO_NUMBER: "Must contain a number",
    WHITE_SPACE: "White spaces are not allowed"
};

const passwordInput = <HTMLInputElement> document.getElementById("passwordInput");

passwordInput.oninput = function () {
    removeStyle(passwordInput);

    const password: string = passwordInput.value.trim();

    if (password === "") {
        return;
    }

    /*--- Determine faults in password ---*/
    let faults: string[] = [];

    if (password.match(/\s/) !== null) {
        faults.push(PASSWORD_PROBLEMS.WHITE_SPACE);
    }

    if (password.length < PASSWORD_LENGTH) {
        faults.push(PASSWORD_PROBLEMS.TOO_SHORT);
    }

    if (password.match(/\d/) === null) {
        faults.push(PASSWORD_PROBLEMS.NO_NUMBER);
    }

    applyStyle(passwordInput, faults);
};
