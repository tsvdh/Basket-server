function main() {
    for (let i = 0; i < 10; i++) {
        console.log("Hello world! " + i)
    }

    let text = document.getElementById("bla").innerText.toUpperCase()

    const newElement = document.createElement("div")
    newElement.innerText = text

    document.getElementById("body").appendChild(newElement)
}

window.onload = main

console.log("test")