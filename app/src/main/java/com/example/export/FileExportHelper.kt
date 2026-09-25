package com.example.export

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.io.File

object FileExportHelper {

    fun shareOrDownloadFile(context: Context, filename: String, content: String, mimeType: String) {
        try {
            val file = File(context.cacheDir, filename)
            file.writeText(content)

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, filename)
                putExtra(Intent.EXTRA_TEXT, content)
                type = mimeType
            }
            val shareIntent = Intent.createChooser(sendIntent, "Export $filename via")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)

            Toast.makeText(context, "Ready to save or share $filename", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }
}
