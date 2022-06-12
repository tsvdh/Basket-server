const raw =
    `<div class="toast show align-items-center text-white border-0" role="alert">
        <div class="d-flex">
            <div class="toast-body"></div>
            <button type="button" class="btn btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    </div>`;

function makeToast(text: string, type: AlertType): HTMLElement {
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

    return toast.documentElement;
}


export enum AlertType {
    Info,
    Warning
}

class Alert {

    private readonly id: string;

    private readonly text: string;
    private readonly type: AlertType;

    private count: number;

    private createdAt: number;

    constructor(text: string, type: AlertType) {
        this.text = text;
        this.type = type;
        this.count = 0;
        this.createdAt = Date.now();
    }

    equals(other: Alert): boolean {
        return this.text === other.text
            && this.type === other.type;
    }

    incrementCount(): void {
        this.count += 1;
        this.createdAt = Date.now();
    }

    isOld(): boolean {
        return (Date.now() - this.createdAt) > (5 * 1000);
    }

    getText(): string {
        return this.count == 0
            ? this.text
            : `(${this.count}) ${this.text}`;
    }

    toToast(): HTMLElement {
        return makeToast(this.getText(), this.type);
    }
}

export class AlertQueue {

    private readonly queue: Array<Alert>;
    private readonly container: HTMLElement;

    constructor() {
        this.queue = new Array<Alert>();
        this.container = document.getElementById("toast-container");

        setInterval(() => this.removeOldAlerts(), 100);
    }

    addAlert(text: string, type: AlertType) {
        this.add(new Alert(text, type));
    }

    add(alert: Alert): void {
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

    private removeOldAlerts(): void {
        for (let [i, alert] of this.queue.entries()) {
            if (alert.isOld()) {
                this.queue.splice(i, 1);
                this.container.children[i].remove();
            }
        }
    }
}
