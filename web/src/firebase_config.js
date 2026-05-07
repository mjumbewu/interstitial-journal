import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyDQhKMQrVBnQAtMKCwerp9geF2JHp2PeMA",
  authDomain: "interstitial-journal-1750a.firebaseapp.com",
  projectId: "interstitial-journal-1750a",
  storageBucket: "interstitial-journal-1750a.firebasestorage.app",
  messagingSenderId: "682736882956",
  appId: "1:682736882956:web:1c8bb593896ff8c37e6529",
  measurementId: "G-S0SKK0VSKQ"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const db = getFirestore(app);

export { app, auth, db };