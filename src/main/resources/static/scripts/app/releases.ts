export {}
import {address} from "../util/constants";

/*--- Button actions ---*/

document.getElementById("iconButton").onclick = function () {

};

document.getElementById("releaseButton").onclick = function () {
    
    let request = new Request(
        address + "api/v1/app/release",
        {
            method: "PATCH",

        }
    );
};