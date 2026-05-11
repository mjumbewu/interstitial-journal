import {
  collection,
  query,
  orderBy,
  limit,
  addDoc,
  getDocs,
  onSnapshot,
  startAfter,
  endBefore,
  limitToLast,
  serverTimestamp,
  Timestamp,
} from 'firebase/firestore';
import { db } from './firebase_config.js';

/**
 * Returns a reference to the entries sub-collection for a given user.
 */
function entriesRef(userId) {
  return collection(db, 'users', userId, 'entries');
}

/**
 * Subscribe to the most recent `count` entries for a user, in ascending
 * chronological order (oldest → newest).
 *
 * @param {string} userId
 * @param {number} count
 * @param {(entries: Array) => void} callback — called with the current snapshot
 * @returns {() => void} unsubscribe function
 */
export function subscribeToEntries(userId, count, callback) {
  const q = query(
    entriesRef(userId),
    orderBy('createdAt', 'desc'),
    limit(count),
  );

  return onSnapshot(q, { includeMetadataChanges: true }, (snapshot) => {
    const entries = [];
    snapshot.forEach((doc) => {
      entries.push({ id: doc.id, hasPendingWrites: doc.metadata.hasPendingWrites, ...doc.data() });
    });
    // Reverse so the list is chronological (oldest first, newest last).
    entries.reverse();
    callback(entries);
  });
}

/**
 * Load entries older than the provided timestamp.
 *
 * @param {string} userId
 * @param {Timestamp} beforeTimestamp — Firestore Timestamp of the oldest currently-loaded entry
 * @param {number} count
 * @returns {Promise<Array>} entries in ascending chronological order
 */
export async function loadOlderEntries(userId, beforeTimestamp, count) {
  const q = query(
    entriesRef(userId),
    orderBy('createdAt', 'desc'),
    startAfter(beforeTimestamp),
    limit(count),
  );

  const snapshot = await getDocs(q);
  const entries = [];
  snapshot.forEach((doc) => {
    entries.push({ id: doc.id, ...doc.data() });
  });
  // Reverse to chronological order.
  entries.reverse();
  return entries;
}

/**
 * Add a new journal entry for the given user.
 *
 * @param {string} userId
 * @param {string} text — markdown text
 */
export async function addEntry(userId, text) {
  return addDoc(entriesRef(userId), {
    text,
    createdAt: serverTimestamp(),
  });
}
