<script setup>
import { onMounted, onUnmounted, ref, nextTick, watch } from 'vue';
import { currentUser, signOut } from '../auth.js';
import { useJournal } from '../journal-store.js';
import JournalEntry from './JournalEntry.vue';
import EntryForm from './EntryForm.vue';

const { entries, loading, hasMore, subscribe, addEntry, loadOlderEntries } = useJournal();

const scrollContainer = ref(null);
let unsubscribe = null;
let initialScrollDone = false;

onMounted(() => {
  if (currentUser.value) {
    unsubscribe = subscribe(currentUser.value.uid);
  }
});

onUnmounted(() => {
  if (unsubscribe) unsubscribe();
});

// Auto-scroll to bottom when entries first load.
watch(entries, async () => {
  if (!initialScrollDone && entries.value.length > 0) {
    initialScrollDone = true;
    await nextTick();
    scrollToBottom();
  }
});

function scrollToBottom() {
  const el = scrollContainer.value;
  if (el) {
    el.scrollTop = el.scrollHeight;
  }
}

async function handleScroll() {
  const el = scrollContainer.value;
  if (!el || loading.value || !hasMore.value) return;
  // When user scrolls near the top, load older entries.
  if (el.scrollTop < 200) {
    const prevHeight = el.scrollHeight;
    await loadOlderEntries();
    await nextTick();
    // Maintain scroll position so it doesn't jump.
    el.scrollTop = el.scrollHeight - prevHeight;
  }
}

async function handleNewEntry(text) {
  await addEntry(text);
  await nextTick();
  scrollToBottom();
}

async function handleSignOut() {
  await signOut();
}
</script>

<template>
  <div class="journal-layout">
    <header class="journal-header">
      <h1 class="journal-heading">Journal</h1>
      <button id="sign-out" @click="handleSignOut" class="btn btn-ghost btn-sm">
        Sign Out
      </button>
    </header>

    <div
      class="journal-scroll"
      ref="scrollContainer"
      @scroll="handleScroll"
    >
      <div v-if="hasMore && !loading" class="load-more-area">
        <button @click="loadOlderEntries" class="btn btn-ghost btn-sm">Load older entries</button>
      </div>
      <div v-if="loading" class="journal-loading">Loading…</div>

      <div class="journal-entries">
        <JournalEntry
          v-for="entry in entries"
          :key="entry.id"
          :entry="entry"
        />
      </div>

      <div v-if="!loading && entries.length === 0" class="journal-empty">
        <p>No entries yet. Write your first one below.</p>
      </div>
    </div>

    <div class="journal-form-container">
      <EntryForm @submit="handleNewEntry" />
    </div>
  </div>
</template>

<style scoped>
.journal-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 640px;
  margin: 0 auto;
  padding: 0 24px;
}

.journal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
}

.journal-heading {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: -0.3px;
  margin: 0;
  color: var(--text-h);
}

.journal-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.journal-entries {
  min-height: 0;
}

.journal-loading {
  text-align: center;
  padding: 24px;
  color: var(--text-muted);
  font-size: 14px;
}

.journal-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--text-muted);
  font-size: 15px;
}

.load-more-area {
  text-align: center;
  padding: 12px 0;
}

.journal-form-container {
  flex-shrink: 0;
  padding-bottom: 24px;
}
</style>
