import { setRequiredInputs } from "./util/utils.js";
/*--- Nav tabs ---*/
window.addEventListener("load", event => {
    setRequiredInputs(false);
});
const userTypeInput = document.getElementById("userTypeInput");
document.getElementById("nav-user").onclick = function () {
    userTypeInput.value = "USER";
    setRequiredInputs(false);
};
document.getElementById("nav-developer").onclick = function () {
    userTypeInput.value = "DEVELOPER";
    setRequiredInputs(true);
};
