<template>
  <teleport to="body">
    <div class="toast-stack">
      <transition-group name="toast-fade">
        <div
          v-for="t in toasts"
          :key="t.id"
          class="app-toast"
          :class="`app-toast--${t.type}`"
        >
          <i class="bi" :class="iconFor(t.type)"></i>
          <span class="app-toast__msg">{{ t.message }}</span>
          <button class="app-toast__close" @click="remove(t.id)">
            <i class="bi bi-x"></i>
          </button>
        </div>
      </transition-group>
    </div>
  </teleport>
</template>

<script>
import { toast } from "@/utils/toast";

export default {
  name: "ToastContainer",
  data() {
    return { toasts: [] };
  },
  mounted() {
    toast._target.addEventListener("toast", this.onToast);
  },
  beforeUnmount() {
    toast._target.removeEventListener("toast", this.onToast);
  },
  methods: {
    onToast(e) {
      const t = e.detail;
      this.toasts.push(t);
      window.setTimeout(() => this.remove(t.id), t.duration);
    },
    remove(id) {
      this.toasts = this.toasts.filter((t) => t.id !== id);
    },
    iconFor(type) {
      return (
        {
          success: "bi-check-circle-fill",
          error: "bi-x-circle-fill",
          warning: "bi-exclamation-triangle-fill",
          info: "bi-info-circle-fill",
        }[type] || "bi-info-circle-fill"
      );
    },
  },
};
</script>

<style scoped>
.toast-stack {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 2000;
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 340px;
  max-width: calc(100vw - 32px);
}
.app-toast {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fff;
  border-radius: 12px;
  padding: 12px 14px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
  border-left: 4px solid var(--brand-primary, #2563eb);
  font-size: 0.92rem;
  color: #1f2937;
}
.app-toast i.bi:first-child {
  font-size: 1.15rem;
}
.app-toast--success {
  border-left-color: #16a34a;
}
.app-toast--success i.bi:first-child {
  color: #16a34a;
}
.app-toast--error {
  border-left-color: #dc2626;
}
.app-toast--error i.bi:first-child {
  color: #dc2626;
}
.app-toast--warning {
  border-left-color: #d97706;
}
.app-toast--warning i.bi:first-child {
  color: #d97706;
}
.app-toast--info i.bi:first-child {
  color: #2563eb;
}
.app-toast__msg {
  flex: 1;
  line-height: 1.35;
}
.app-toast__close {
  border: none;
  background: transparent;
  color: #9ca3af;
  line-height: 1;
  padding: 0;
}
.app-toast__close:hover {
  color: #374151;
}
.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: all 0.25s ease;
}
.toast-fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}
.toast-fade-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
