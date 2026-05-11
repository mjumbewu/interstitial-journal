import { initializeApp, cert } from 'firebase-admin/app';
import { getFirestore } from 'firebase-admin/firestore';
import { randomUUID } from 'crypto';
import fs from 'fs';

const serviceAccountPath = process.argv[2];

if (!serviceAccountPath) {
  console.error('Please provide the path to your service account key JSON file.');
  console.error('Usage: node scripts/migrate-all-entries.mjs /path/to/serviceAccountKey.json');
  process.exit(1);
}

const serviceAccount = JSON.parse(fs.readFileSync(serviceAccountPath, 'utf8'));

initializeApp({
  credential: cert(serviceAccount)
});

const db = getFirestore();

async function migrateEntries() {
  console.log('Fetching all entries...');
  const entriesSnapshot = await db.collectionGroup('entries').get();
  
  if (entriesSnapshot.empty) {
    console.log('No entries found.');
    return;
  }

  console.log(`Found ${entriesSnapshot.size} total entries. Checking for migrations...`);

  let batch = db.batch();
  let batchCount = 0;
  let totalMigrated = 0;

  for (const entryDoc of entriesSnapshot.docs) {
    const id = entryDoc.id;
    const data = entryDoc.data();
    const entriesRef = entryDoc.ref.parent;

    // Check if it already matches the new format (starts with YYYY-MM-DD)
    if (id.match(/^\d{4}-\d{2}-\d{2}T/)) {
      continue;
    }

    // Determine the datetime to use
    let date;
    if (data.createdAt) {
      date = data.createdAt.toDate();
    } else {
      date = new Date(); // fallback
    }

    const dateStr = date.toISOString().replace(/[:.]/g, '-');
    const randomStr = randomUUID().split('-')[0];
    const newDocId = `${dateStr}-${randomStr}`;

    const newDocRef = entriesRef.doc(newDocId);
    const oldDocRef = entryDoc.ref;

    batch.set(newDocRef, data);
    batch.delete(oldDocRef);
    batchCount += 2; // 1 set, 1 delete
    totalMigrated++;

    // Commit batch if it approaches the 500 operation limit
    if (batchCount >= 498) {
      await batch.commit();
      batch = db.batch();
      batchCount = 0;
    }
  }

  // Commit any remaining operations in the batch
  if (batchCount > 0) {
    await batch.commit();
  }

  console.log(`\nMigration complete. Total entries migrated: ${totalMigrated}`);
}

migrateEntries().catch(console.error);
