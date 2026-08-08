// Lightweight global toast utility (no extra dependency).
// Usage: import { toast } from "@/utils/toast";  toast.success("..."); toast.error("...");

const target = new EventTarget();
let counter = 0;

function push(message, type = "info", duration = 3200) {
  const id = ++counter;
  target.dispatchEvent(
    new CustomEvent("toast", { detail: { id, message, type, duration } })
  );
  return id;
}

export const toast = {
  success: (message, duration) => push(message, "success", duration),
  error: (message, duration) => push(message, "error", duration),
  info: (message, duration) => push(message, "info", duration),
  warning: (message, duration) => push(message, "warning", duration),
  _target: target,
};
