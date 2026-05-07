import { ref } from 'vue';
import {
  subscribeToEntries,
  loadOlderEntries as loadOlder,
  addEntry as addEntryToDb,
} from './firestore-journal.js';

const PAGE_SIZE = 100;

/**
 * Composable that provides reactive journal state.
 *
 * Usage:
 *   const { entries, loading, hasMore, subscribe, addEntry, loadOlderEntries } = useJournal();
 */
export function useJournal() {
  const entries = ref([]);
  const loading = ref(false);
  const hasMore = ref(true);
  let unsubscribe = null;
  let currentUserId = null;

  /**
   * Start a real-time subscription for the given user.
   * Returns a function to unsubscribe.
   */
  function subscribe(userId) {
    // Clean up any previous subscription.
    if (unsubscribe) {
      unsubscribe();
    }

    currentUserId = userId;
    loading.value = true;

    unsubscribe = subscribeToEntries(userId, PAGE_SIZE, (newEntries) => {
      entries.value = newEntries;
      loading.value = false;
      // If fewer than PAGE_SIZE entries came back, there are no older ones.
      hasMore.value = newEntries.length >= PAGE_SIZE;
    });

    return () => {
      if (unsubscribe) {
        unsubscribe();
        unsubscribe = null;
      }
    };
  }

  /**
   * Load older entries (for upward infinite scroll).
   */
  async function loadOlderEntries() {
    if (!hasMore.value || !currentUserId || entries.value.length === 0) return;

    const oldestEntry = entries.value[0];
    if (!oldestEntry.createdAt) return;

    loading.value = true;
    const olderEntries = await loadOlder(
      currentUserId,
      oldestEntry.createdAt,
      PAGE_SIZE,
    );

    if (olderEntries.length < PAGE_SIZE) {
      hasMore.value = false;
    }

    // Prepend older entries.
    entries.value = [...olderEntries, ...entries.value];
    loading.value = false;
  }

  /**
   * Add a new journal entry.
   */
  async function addEntry(text) {
    if (!currentUserId) return;
    await addEntryToDb(currentUserId, text);
  }

  return {
    entries,
    loading,
    hasMore,
    subscribe,
    addEntry,
    loadOlderEntries,
  };
}
