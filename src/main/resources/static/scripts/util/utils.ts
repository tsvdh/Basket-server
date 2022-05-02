export function toKB(bytes: number): string {
    return (bytes / 1000).toFixed(0);
}

export function toMB(bytes: number): string {
    return (bytes / 1000000).toFixed(1);
}