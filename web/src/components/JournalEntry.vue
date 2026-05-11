<script setup>
import { computed } from 'vue';
import { marked } from 'marked';
import DOMPurify from 'dompurify';

const props = defineProps({
  entry: {
    type: Object,
    required: true,
  },
});

defineEmits(['retry', 'dismiss']);

// Configure marked for inline-friendly output.
marked.setOptions({
  breaks: true,
  gfm: true,
});

const renderedHtml = computed(() => {
  return DOMPurify.sanitize(marked.parse(props.entry.text || ''));
});

const entryDate = computed(() => {
  const ts = props.entry.createdAt || new Date();
  return ts.toDate ? ts.toDate() : new Date(ts);
});

const formattedTime = computed(() => {
  if (!entryDate.value) return '';
  return entryDate.value.toLocaleTimeString(undefined, {
    hour: 'numeric',
    minute: '2-digit',
  });
});

const isoDatetime = computed(() => {
  if (!entryDate.value) return '';
  return entryDate.value.toISOString();
});
</script>

<template>
  <article class="journal-entry" :class="{ 'is-pending': entry.hasPendingWrites, 'is-failed': entry.isFailed }">
    <time class="entry-time" :datetime="isoDatetime">
      {{ formattedTime }}
    </time>
    <div class="entry-content">
      <div class="entry-body" v-html="renderedHtml"></div>
      <div v-if="entry.hasPendingWrites" class="entry-status pending-text">saving...</div>
      <div v-if="entry.isFailed" class="entry-status failed-text">failed to save</div>
      <div v-if="entry.isFailed" class="entry-actions">
        <button @click="$emit('retry', entry.id)" class="btn btn-sm">Retry</button>
        <button @click="$emit('dismiss', entry.id)" class="btn btn-sm btn-ghost">Dismiss</button>
      </div>
    </div>
  </article>
</template>

<style scoped>
.journal-entry {
  display: flex;
  align-items: baseline;
  gap: 0.75rem;
  padding: 0.375rem 0;
}

.entry-time {
  flex-shrink: 0;
  width: 3.75rem;
  font-size: 0.8rem;
  color: var(--text-muted);
  text-align: right;
}

.entry-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.entry-body {
  color: var(--text-h);
  line-height: 1.6;
}

.is-pending .entry-body {
  opacity: 0.7;
}

.is-failed .entry-body {
  color: var(--danger, #dc3545);
}

.entry-status {
  font-size: 0.75rem;
}
.pending-text {
  color: var(--text-muted);
  font-style: italic;
}
.failed-text {
  color: var(--danger, #dc3545);
  font-weight: bold;
}

.entry-actions {
  display: flex;
  gap: 8px;
  margin-top: 4px;
}

/* Scope markdown output styles */
.entry-body :deep(p) {
  margin: 0 0 8px;
}
.entry-body :deep(p:last-child) {
  margin-bottom: 0;
}
.entry-body :deep(code) {
  font-family: var(--mono);
  font-size: 0.9em;
  background: var(--code-bg);
  padding: 2px 5px;
  border-radius: 3px;
}
.entry-body :deep(pre) {
  background: var(--code-bg);
  padding: 12px 16px;
  border-radius: 6px;
  overflow-x: auto;
}
.entry-body :deep(blockquote) {
  margin: 8px 0;
  padding-left: 16px;
  border-left: 3px solid var(--border);
  color: var(--text);
}
.entry-body :deep(a) {
  color: var(--accent);
}
</style>
