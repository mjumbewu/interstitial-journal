<script setup>
import { ref } from 'vue';
import { signInWithEmail, signUp, signInWithGoogle } from '../auth.js';

const email = ref('');
const password = ref('');
const isSignUp = ref(false);
const error = ref('');
const submitting = ref(false);

async function handleEmailSubmit() {
  error.value = '';
  submitting.value = true;
  try {
    if (isSignUp.value) {
      await signUp(email.value, password.value);
    } else {
      await signInWithEmail(email.value, password.value);
    }
  } catch (err) {
    error.value = friendlyError(err.code);
  } finally {
    submitting.value = false;
  }
}

async function handleGoogle() {
  error.value = '';
  try {
    await signInWithGoogle();
  } catch (err) {
    error.value = friendlyError(err.code);
  }
}

function toggleMode() {
  isSignUp.value = !isSignUp.value;
  error.value = '';
}

function friendlyError(code) {
  const map = {
    'auth/user-not-found': 'No account found with that email.',
    'auth/wrong-password': 'Incorrect password.',
    'auth/invalid-credential': 'Invalid email or password.',
    'auth/email-already-in-use': 'An account with that email already exists.',
    'auth/weak-password': 'Password should be at least 6 characters.',
    'auth/invalid-email': 'Please enter a valid email address.',
  };
  return map[code] || 'Something went wrong. Please try again.';
}
</script>

<template>
  <div class="login-screen">
    <div class="login-card">
      <h1 class="login-title">Interstitial Journal</h1>
      <p class="login-subtitle">A space between moments.</p>

      <form @submit.prevent="handleEmailSubmit" class="login-form">
        <input
          id="login-email"
          v-model="email"
          type="email"
          placeholder="Email"
          required
          autocomplete="email"
        />
        <input
          id="login-password"
          v-model="password"
          type="password"
          placeholder="Password"
          required
          autocomplete="current-password"
        />
        <p v-if="error" class="login-error">{{ error }}</p>
        <button id="login-submit" type="submit" :disabled="submitting" class="btn btn-primary">
          {{ isSignUp ? 'Create Account' : 'Sign In' }}
        </button>
      </form>

      <div class="login-divider"><span>or</span></div>

      <button id="login-google" @click="handleGoogle" class="btn btn-google">
        <svg viewBox="0 0 48 48" width="20" height="20" aria-hidden="true">
          <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"/>
          <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/>
          <path fill="#FBBC05" d="M10.53 28.59A14.5 14.5 0 019.5 24c0-1.59.28-3.14.76-4.59l-7.98-6.19A23.998 23.998 0 000 24c0 3.77.9 7.35 2.56 10.59l7.97-6z"/>
          <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6C6.51 42.62 14.62 48 24 48z"/>
        </svg>
        Continue with Google
      </button>

      <p class="login-toggle">
        {{ isSignUp ? 'Already have an account?' : "Don't have an account?" }}
        <a href="#" @click.prevent="toggleMode" id="login-mode-toggle">
          {{ isSignUp ? 'Sign in' : 'Sign up' }}
        </a>
      </p>
    </div>
  </div>
</template>

<style scoped>
.login-screen {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 380px;
}

.login-title {
  font-size: 28px;
  font-weight: 600;
  letter-spacing: -0.5px;
  margin: 0 0 6px;
  color: var(--text-h);
}

.login-subtitle {
  color: var(--text-muted);
  margin: 0 0 32px;
  font-size: 15px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.login-form input {
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 15px;
  font-family: var(--sans);
  background: var(--bg);
  color: var(--text-h);
  transition: border-color 0.2s;
}

.login-form input:focus {
  outline: none;
  border-color: var(--accent);
}

.login-error {
  color: #d64545;
  font-size: 13px;
  margin: 0;
}

.login-divider {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 20px 0;
  color: var(--text-muted);
  font-size: 13px;
}

.login-divider::before,
.login-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--border);
}

.login-toggle {
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-muted);
}

.login-toggle a {
  color: var(--accent);
  text-decoration: none;
  font-weight: 500;
}

.login-toggle a:hover {
  text-decoration: underline;
}
</style>
