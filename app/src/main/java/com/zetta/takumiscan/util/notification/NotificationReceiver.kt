package com.zetta.takumiscan.util.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NotificationHelper.showNotification(
            context,
            "Catatan Terjadwal",
            "Hari ini kamu ada jadwal nih!"
        )
    }
}