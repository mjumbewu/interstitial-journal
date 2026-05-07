<script setup>
import { ref } from 'vue';

const emit = defineEmits(['submit', 'focus']);

const text = ref('');
const submitting = ref(false);

async function handleSubmit() {
  const trimmed = text.value.trim();
  if (!trimmed) return;

  submitting.value = true;
  try {
    await emit('submit', trimmed);
    text.value = '';
  } finally {
    submitting.value = false;
  }
}

function handleKeydown(e) {
  if ((e.metaKey || e.ctrlKey) && e.key === 'Enter') {
    handleSubmit();
  }
}
</script>

<template>
  <form @submit.prevent="handleSubmit" class="entry-form">
    <textarea
      id="entry-text"
      v-model="text"
      placeholder="What're you up to? What's next?"
      rows="3"
      @keydown="handleKeydown"
      @focus="emit('focus')"
      :disabled="submitting"
    ></textarea>
    <div class="entry-form-footer">
      <span class="entry-form-hint">Markdown supported · Ctrl+Enter to submit</span>
      <button id="entry-submit" type="submit" :disabled="submitting || !text.trim()" class="btn btn-primary btn-sm">
        Add Entry
      </button>
    </div>
  </form>
</template>

<style scoped>
.entry-form {
  border-top: 1px solid var(--border);
  padding-top: 16px;
}

.entry-form textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-family: var(--sans);
  font-size: 15px;
  line-height: 1.5;
  resize: vertical;
  min-height: 80px;
  background: var(--bg);
  color: var(--text-h);
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.entry-form textarea:focus {
  outline: none;
  border-color: var(--accent);
}

.entry-form-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

.entry-form-hint {
  font-size: 12px;
  color: var(--text-muted);
}
</style>
