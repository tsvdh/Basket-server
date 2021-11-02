export {}

const PASSWORD_LENGTH = 8;

const PASSWORD_PROBLEMS = {
    TOO_SHORT: "Must be at least X characters".replace("X", PASSWORD_LENGTH.toString()),
    NO_NUMBER: "Must contain a number"
};

const passwordInput = <HTMLInputElement> document.getElementById("passwordInput");

passwordInput.oninput = function() {
    const password: string = passwordInput.value;

    const faults: string[] = [];

    if (password.length < PASSWORD_LENGTH) {
        faults.push(PASSWORD_PROBLEMS.TOO_SHORT);
    }

    if (password.match(/\d/) === null) {
        faults.push(PASSWORD_PROBLEMS.NO_NUMBER);
    }

    console.log(faults);
};