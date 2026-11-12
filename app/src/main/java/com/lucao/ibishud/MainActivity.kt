package com.lucao.ibishud

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 64, 48, 48)
            setBackgroundColor(0xFF090D16.toInt())
        }

        val title = TextView(this).apply {
            text = "IbisHUD - Monitor & Overlay"
            textSize = 22f
            setTextColor(0xFF38BDF8.toInt())
        }

        val specsInfo = TextView(this).apply {
            text = "\nDispositivo: ${Build.MANUFACTURER.uppercase()} ${Build.MODEL}\nSistema: Android ${Build.VERSION.RELEASE}\nDPI Atual: ${resources.displayMetrics.densityDpi} dpi\n"
            textSize = 14f
            setTextColor(0xFFCBD5E1.toInt())
        }

        val btnStartHud = Button(this).apply {
            text = "ATIVAR MENU FLUTUANTE"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    startActivity(intent)
                } else {
                    startService(Intent(this@MainActivity, FloatingMenuService::class.java))
                }
            }
        }

        val btnDevOptions = Button(this).apply {
            text = "ABRIR OPÇÕES DO DESENVOLVEDOR (DPI)"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
            }
        }

        layout.addView(title)
        layout.addView(specsInfo)
        layout.addView(btnStartHud)
        layout.addView(btnDevOptions)
        setContentView(layout)
    }
}
