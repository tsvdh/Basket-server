export {toMB}

function toMB(bytes: number): string {
    return (bytes / 1000000).toFixed(1);
}