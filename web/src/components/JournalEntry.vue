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

// Configure marked for inline-friendly output.
marked.setOptions({
  breaks: true,
  gfm: true,
});

const renderedHtml = computed(() => {
  return DOMPurify.sanitize(marked.parse(props.entry.text || ''));
});

const formattedTime = computed(() => {
  const ts = props.entry.createdAt;
  if (!ts) return '';
  // Firestore Timestamps have a toDate() method.
  const date = ts.toDate ? ts.toDate() : new Date(ts);
  return date.toLocaleString(undefined, {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  });
});
</script>

<template>
  <article class="journal-entry">
    <div class="entry-body" v-html="renderedHtml"></div>
    <time class="entry-time" :datetime="entry.createdAt?.toDate?.()?.toISOString?.()">
      {{ formattedTime }}
    </time>
  </article>
</template>

<style scoped>
.journal-entry {
  padding: 16px 0;
}

.journal-entry + .journal-entry {
  border-top: 1px solid var(--border);
}

.entry-body {
  color: var(--text-h);
  line-height: 1.6;
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

.entry-time {
  display: block;
  margin-top: 8px;
  font-size: 13px;
  color: var(--text-muted);
}
</style>
