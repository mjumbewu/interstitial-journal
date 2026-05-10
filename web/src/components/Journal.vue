<script setup>
import { computed, onMounted, onUnmounted, ref, nextTick, watch } from 'vue';
import { currentUser, signOut } from '../auth.js';
import { useJournal } from '../journal-store.js';
import JournalEntry from './JournalEntry.vue';
import EntryForm from './EntryForm.vue';

const { entries, loading, hasMore, subscribe, addEntry, loadOlderEntries } = useJournal();

const scrollContainer = ref(null);
const formContainer = ref(null);
const isFormFixed = ref(true);
const formHeight = ref(160);
const showJumpToBottom = ref(false);
const wasAtBottomOnFocus = ref(false);

// If the scroll position is within this many pixels of the bottom, we consider it "at the bottom".
const atBottomThreshold = 50;

let unsubscribe = null;
let initialScrollDone = false;
let ro = null;

// Group entries by date (e.g. "Wed, May 6, 2026").
const groupedEntries = computed(() => {
  const groups = [];
  let currentDateStr = null;
  let currentGroup = null;

  for (const entry of entries.value) {
    const ts = entry.createdAt || new Date();
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

function checkScrollPosition() {
  const el = scrollContainer.value;
  if (!el) return;
  // If we are within 50px of the bottom, we consider it "at the bottom"
  const isAtBottom = el.scrollHeight - el.scrollTop - el.clientHeight < 50;
  showJumpToBottom.value = !isAtBottom;
}

onMounted(() => {
  if (currentUser.value) {
    unsubscribe = subscribe(currentUser.value.uid);
  }

  ro = new ResizeObserver(() => {
    if (formContainer.value) {
      const h = formContainer.value.offsetHeight;
      formHeight.value = h;
      isFormFixed.value = window.innerHeight > 3 * h;
    }
    checkScrollPosition();
  });
  if (formContainer.value) ro.observe(formContainer.value);
  ro.observe(document.body);
});

onUnmounted(() => {
  if (unsubscribe) unsubscribe();
  if (ro) ro.disconnect();
});

// Auto-scroll to bottom when entries first load.
watch(entries, async () => {
  if (!initialScrollDone && entries.value.length > 0) {
    initialScrollDone = true;
    await nextTick();
    scrollToBottom();
  } else {
    await nextTick();
    checkScrollPosition();
  }
});

function scrollToBottom(smooth = false) {
  const el = scrollContainer.value;
  if (el) {
    if (smooth) {
      el.scrollTo({ top: el.scrollHeight, behavior: 'smooth' });
    } else {
      el.scrollTop = el.scrollHeight;
    }
    showJumpToBottom.value = false;
  }
}

function jumpToBottom() {
  scrollToBottom(true);
}

function handleFormFocus() {
  const el = scrollContainer.value;
  if (!el) return;
  const isAtBottom = el.scrollHeight - el.scrollTop - el.clientHeight < atBottomThreshold;

  // If the user was at the bottom when they focused the form (before the keyboard
  // might have shifted the layout), we should scroll to the bottom after they
  // add a new entry.
  wasAtBottomOnFocus.value = isAtBottom;
}

async function handleScroll() {
  checkScrollPosition();
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
  const el = scrollContainer.value;
  const isCurrentlyAtBottom = el ? (el.scrollHeight - el.scrollTop - el.clientHeight < atBottomThreshold) : false;

  // If the user was at the bottom when they focused the form (before the keyboard
  // might have shifted the layout), or if they are currently at the bottom, we should scroll.
  const shouldScroll = wasAtBottomOnFocus.value || isCurrentlyAtBottom;

  await addEntry(text);
  await nextTick();

  if (shouldScroll) {
    scrollToBottom(true);
  }
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

      <div
        class="journal-form-container"
        ref="formContainer"
        :class="{ 'is-fixed': isFormFixed }"
      >
        <EntryForm @submit="handleNewEntry" @focus="handleFormFocus" />
      </div>
    </div>

    <!-- Jump to bottom button -->
    <button
      class="btn btn-primary jump-to-bottom"
      v-show="showJumpToBottom"
      :style="{ bottom: isFormFixed ? `${formHeight + 24}px` : '24px' }"
      @click="jumpToBottom"
    >
      ↓
    </button>
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
  position: relative;
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
  padding: 0;
  display: flex;
  flex-direction: column;
}

.journal-entries {
  flex-shrink: 0;
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
  flex: 1;
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
  background: var(--bg);
  margin-top: auto;
}

.journal-form-container.is-fixed {
  position: sticky;
  bottom: 0;
  z-index: 10;
}

.jump-to-bottom {
  position: absolute;
  right: 24px;
  z-index: 20;
  border-radius: 9999px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.2s ease-in-out;
  padding: 8px 16px;
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
