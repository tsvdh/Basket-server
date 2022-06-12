const raw =
    `<div class="toast show align-items-center text-white border-0 fade" role="alert">
        <div class="d-flex">
            <div class="toast-body"></div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    </div>`;

function makeToast(text: string, type: AlertType, alertId?: string): HTMLElement {
    const toast = new DOMParser().parseFromString(raw, "text/html");

    let color: string;

    switch (type) {
        case AlertType.Info:
            color = "bg-success";
            break;
        case AlertType.Warning:
            color = "bg-danger";
            break;
    }

    toast.getElementsByClassName("toast")[0].classList.add(color);
    (toast.getElementsByClassName("toast-body")[0] as HTMLElement).innerText = text;

    if (alertId) {
        toast.getElementsByClassName("btn-close")[0].addEventListener("click", function () {
            AlertQueue.removeAlert(alertId);
        });
    }

    return toast.getElementsByClassName("toast")[0] as HTMLElement;
}


export enum AlertType {
    Info,
    Warning
}

class Alert {

    readonly id: string;

    private readonly text: string;
    private readonly type: AlertType;

    private count: number;

    private createdAt: number;

    constructor(text: string, type: AlertType) {
        this.id = Math.random().toString(32).substring(2, 10);
        this.text = text;
        this.type = type;
        this.count = 1;
        this.createdAt = Date.now();
    }

    equals(other: Alert): boolean {
        return this.text == other.text
            && this.type == other.type;
    }

    incrementCount(): void {
        this.count += 1;
        this.createdAt = Date.now();
    }

    isOld(): boolean {
        return (Date.now() - this.createdAt) > (5 * 1000);
    }

    getText(): string {
        return this.count <= 1
            ? this.text
            : `(${this.count}) ${this.text}`;
    }

    toToast(): HTMLElement {
        return makeToast(this.getText(), this.type, this.id);
    }
}

export class AlertQueue {

    private static readonly queue = new Array<Alert>();
    private static readonly container = document.getElementById("toast-container");

    static {
        setInterval(() => {
            for (let [i, alert] of this.queue.entries()) {
                if (alert.isOld()) {
                    this.queue.splice(i, 1);
                    this.container.children[i].remove();
                }
            }
        }, 100);
    }

    static addAlert(text: string, type: AlertType): void {
        this.add(new Alert(text, type));
    }

    static add(alert: Alert): void {
        let lastAlert = this.queue[this.queue.length - 1];

        if (lastAlert && lastAlert.equals(alert)) {
            lastAlert.incrementCount();
            (this.container.lastElementChild.getElementsByClassName("toast-body")[0] as HTMLElement)
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

    static remove(alert: Alert): void {
        this.removeAlert(alert.id);
    }

    static removeAlert(id: string): void {
        for (let [i, alert] of this.queue.entries()) {
            if (alert.id == id) {
                this.queue.splice(i, 1);
                this.container.children[i].remove();
                break;
            }
        }
    }
}
