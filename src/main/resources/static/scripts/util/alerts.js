var _a;
const raw = `<div class="toast show align-items-center text-white border-0 fade" role="alert">
        <div class="d-flex">
            <div class="toast-body"></div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    </div>`;
function makeToast(text, type, alertId) {
    const toast = new DOMParser().parseFromString(raw, "text/html");
    let color;
    switch (type) {
        case AlertType.Info:
            color = "bg-success";
            break;
        case AlertType.Warning:
            color = "bg-danger";
            break;
    }
    toast.getElementsByClassName("toast")[0].classList.add(color);
    toast.getElementsByClassName("toast-body")[0].innerText = text;
    if (alertId) {
        toast.getElementsByClassName("btn-close")[0].addEventListener("click", function () {
            AlertQueue.removeAlert(alertId);
        });
    }
    return toast.getElementsByClassName("toast")[0];
}
export var AlertType;
(function (AlertType) {
    AlertType[AlertType["Info"] = 0] = "Info";
    AlertType[AlertType["Warning"] = 1] = "Warning";
})(AlertType || (AlertType = {}));
class Alert {
    constructor(text, type) {
        this.id = Math.random().toString(32).substring(2, 10);
        this.text = text;
        this.type = type;
        this.count = 1;
        this.createdAt = Date.now();
    }
    equals(other) {
        return this.text == other.text
            && this.type == other.type;
    }
    incrementCount() {
        this.count += 1;
        this.createdAt = Date.now();
    }
    isOld() {
        return (Date.now() - this.createdAt) > (5 * 1000);
    }
    getText() {
        return this.count <= 1
            ? this.text
            : `(${this.count}) ${this.text}`;
    }
    toToast() {
        return makeToast(this.getText(), this.type, this.id);
    }
}
export class AlertQueue {
    static addAlert(text, type) {
        this.add(new Alert(text, type));
    }
    static add(alert) {
        let lastAlert = this.queue[this.queue.length - 1];
        if (lastAlert && lastAlert.equals(alert)) {
            lastAlert.incrementCount();
            this.container.lastElementChild.getElementsByClassName("toast-body")[0]
                .innerText = lastAlert.getText();
        }
        else {
            this.queue.push(alert);
            this.container.appendChild(alert.toToast());
        }
        if (this.queue.length >= 5) {
            this.queue.shift();
            this.container.firstElementChild.remove();
        }
    }
    static remove(alert) {
        this.removeAlert(alert.id);
    }
    static removeAlert(id) {
        for (let [i, alert] of this.queue.entries()) {
            if (alert.id == id) {
                this.queue.splice(i, 1);
                this.container.children[i].remove();
                break;
            }
        }
    }
}
_a = AlertQueue;
AlertQueue.queue = new Array();
AlertQueue.container = document.getElementById("toast-container");
(() => {
    setInterval(() => {
        for (let [i, alert] of _a.queue.entries()) {
            if (alert.isOld()) {
                _a.queue.splice(i, 1);
                _a.container.children[i].remove();
            }
        }
    }, 100);
})();
