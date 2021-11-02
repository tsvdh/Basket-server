export {}

const PASSWORD_LENGTH = 8;

const PASSWORD_PROBLEMS = {
    TOO_SHORT: "Must be at least X characters".replace("X", PASSWORD_LENGTH.toString()),
    NO_NUMBER: "Must contain a number"
};

const passwordInput = <HTMLInputElement> document.getElementById("passwordInput");

passwordInput.oninput = function () {
    const password: string = passwordInput.value;

    /*--- Determine faults in password ---*/
    let faults: string[] = [];

    if (password.length < PASSWORD_LENGTH) {
        faults.push(PASSWORD_PROBLEMS.TOO_SHORT);
    }

    if (password.match(/\d/) === null) {
        faults.push(PASSWORD_PROBLEMS.NO_NUMBER);
    }

    /*--- Reset feedback elements and style ---*/
    passwordInput.classList.remove("is-valid", "is-invalid");

    let feedbackElements: HTMLCollectionOf<Element>
        = document.getElementsByClassName("password-feedback");

    while (feedbackElements.length > 0) {
        feedbackElements[0].remove();
    }

    /*--- Add feedback elements and style ---*/
    if (faults.length === 0) {
        passwordInput.classList.add("is-valid");
    } else {
        passwordInput.classList.add("is-invalid");

        for (let fault of faults) {
            let error: HTMLDivElement = document.createElement("div");

            error.classList.add("invalid-feedback");
            error.classList.add("password-feedback");
            error.textContent = fault;
            passwordInput.insertAdjacentElement("afterend", error);
        }
    }
};


const usernameInput = <HTMLInputElement> document.getElementById("usernameInput");

usernameInput.oninput = function () {
    const username: string = usernameInput.value;

    
};
