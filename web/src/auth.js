import { ref } from 'vue';
import {
  onAuthStateChanged,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signInWithPopup,
  signOut as firebaseSignOut,
  GoogleAuthProvider,
} from 'firebase/auth';
import { auth } from './firebase_config.js';

/** Reactive ref tracking the currently signed-in Firebase user (or null). */
const currentUser = ref(null);

/** True until the initial auth state has been resolved. */
const authLoading = ref(true);

// Keep currentUser in sync with Firebase Auth state.
onAuthStateChanged(auth, (user) => {
  currentUser.value = user;
  authLoading.value = false;
});

/** Sign in with email and password. */
async function signInWithEmail(email, password) {
  return signInWithEmailAndPassword(auth, email, password);
}

/** Create a new account with email and password. */
async function signUp(email, password) {
  return createUserWithEmailAndPassword(auth, email, password);
}

/** Sign in with Google via popup. */
async function signInWithGoogle() {
  const provider = new GoogleAuthProvider();
  return signInWithPopup(auth, provider);
}

/** Sign out the current user. */
async function signOut() {
  return firebaseSignOut(auth);
}

export {
  currentUser,
  authLoading,
  signInWithEmail,
  signUp,
  signInWithGoogle,
  signOut,
};
