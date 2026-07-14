package com.example.interstitialjournal

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

data class JournalEntry(
    val id: String = "",
    val text: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

class EntryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getEntriesRef() = auth.currentUser?.let { user ->
        db.collection("users").document(user.uid).collection("entries")
    }

    suspend fun getRecentEntries(count: Long = 100): List<JournalEntry> {
        val ref = getEntriesRef() ?: return emptyList()
        return try {
            val snapshot = ref.orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(count)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(JournalEntry::class.java)?.copy(id = doc.id)
            }.reversed()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addEntry(text: String): Boolean {
        val ref = getEntriesRef() ?: return false
        val now = Date()
        return try {
            // Match the web app's document ID format exactly:
            // new Date().toISOString().replace(/[:.]/g, '-')
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss-SSS'Z'", Locale.US)
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")
            val dateStr = isoFormat.format(now)
            val randomStr = UUID.randomUUID().toString().split("-")[0]
            val docId = "$dateStr-$randomStr"

            // Only write the fields the web app writes: text and createdAt
            val data = hashMapOf(
                "text" to text,
                "createdAt" to Timestamp(now)
            )
            ref.document(docId).set(data).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
