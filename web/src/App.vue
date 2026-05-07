<script setup>
import { currentUser, authLoading } from './auth.js';
import LoginScreen from './components/LoginScreen.vue';
import Journal from './components/Journal.vue';
</script>

<template>
  <div v-if="authLoading" class="auth-loading">
    <div class="loading-dot"></div>
  </div>
  <LoginScreen v-else-if="!currentUser" />
  <Journal v-else />
</template>

<style scoped>
.auth-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.loading-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--text-muted);
  animation: pulse 1.2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}
</style>
