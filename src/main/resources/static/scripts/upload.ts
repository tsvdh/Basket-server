export {}

const address = "http://localhost:8080";

document.getElementById("fileButton").onclick = async function () {
    let fileInput = <HTMLInputElement>document.getElementById("fileInput");

    let request = new Request(
        address + "/api/v1/apps/upload",
        {
            method: "POST",
            body: fileInput.files.item(0)
        }
    );

    let result = await fetch(request);

    if (result.ok) {
        console.log("Upload complete");
    }
};
