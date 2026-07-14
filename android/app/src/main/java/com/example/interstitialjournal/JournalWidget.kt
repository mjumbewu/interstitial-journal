package com.example.interstitialjournal

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.GlanceTheme
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import java.text.SimpleDateFormat
import java.util.Locale

class JournalWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = JournalWidget()
}

class JournalWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = EntryRepository()
        val entries = repository.getRecentEntries(20)

        provideContent {
            GlanceTheme {
                WidgetUI(entries)
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun WidgetUI(entries: List<JournalEntry>) {
    val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Journal",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GlanceTheme.colors.onBackground
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            Text(
                text = "+ Add",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GlanceTheme.colors.primary
                ),
                modifier = GlanceModifier.clickable(actionStartActivity<QuickEntryActivity>())
            )
        }
        
        Spacer(modifier = GlanceModifier.height(8.dp))

        if (entries.isEmpty()) {
            Text(
                text = "No entries found. Log in via main app.",
                style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant)
            )
        } else {
            LazyColumn {
                items(entries) { entry ->
                    val timeString = dateFormat.format(entry.createdAt.toDate())
                    Column(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = timeString,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        Text(
                            text = entry.text,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
