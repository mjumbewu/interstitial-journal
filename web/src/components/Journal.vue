<script setup>
import { computed, onMounted, onUnmounted, ref, nextTick, watch } from 'vue';
import { currentUser, signOut } from '../auth.js';
import { useJournal } from '../journal-store.js';
import JournalEntry from './JournalEntry.vue';
import EntryForm from './EntryForm.vue';

const { entries, loading, hasMore, subscribe, addEntry, loadOlderEntries } = useJournal();

const scrollContainer = ref(null);
let unsubscribe = null;
let initialScrollDone = false;

// Group entries by date (e.g. "Wed, May 6, 2026").
const groupedEntries = computed(() => {
  const groups = [];
  let currentDateStr = null;
  let currentGroup = null;

  for (const entry of entries.value) {
    const ts = entry.createdAt;
    if (!ts) continue;
    const date = ts.toDate ? ts.toDate() : new Date(ts);
    const dateStr = date.toLocaleDateString(undefined, {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });

    if (dateStr !== currentDateStr) {
      currentDateStr = dateStr;
      currentGroup = { date: dateStr, entries: [] };
      groups.push(currentGroup);
    }
    currentGroup.entries.push(entry);
  }

  return groups;
});

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
        <section v-for="group in groupedEntries" :key="group.date" class="day-group">
          <h2 class="day-heading">{{ group.date }}</h2>
          <JournalEntry
            v-for="entry in group.entries"
            :key="entry.id"
            :entry="entry"
          />
        </section>
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
  padding: 1rem 0;
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
  padding: 0 0 8px 0;
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

.day-group {
  margin-bottom: 0.5rem;
}

.day-heading {
  font-size: 0.95rem;
  font-weight: 600;
  letter-spacing: -0.2px;
  margin: 0 0 0.25rem;
  color: var(--text-h);
  position: sticky;
  top: 0;
  background: var(--bg);
  padding: 0.5rem 0 0.25rem;
  z-index: 1;
}

.day-group:first-child .day-heading {
  margin-top: 8px;
}
</style>
