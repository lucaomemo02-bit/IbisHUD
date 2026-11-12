package com.lucao.ibishud

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(0xFF030712.toInt()) // Fundo preto/azulado bem escuro
            setPadding(32, 32, 32, 32)
        }

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // Título estilizado
        val title = TextView(this).apply {
            text = "IBISHUD"
            textSize = 24f
            setTextColor(0xFF38BDF8.toInt()) // Azul ciano
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(this).apply {
            text = "Painel de Controle & Telemetria"
            textSize = 13f
            setTextColor(0xFF94A3B8.toInt())
            setPadding(0, 0, 0, 24)
        }

        // Card de Informações do Aparelho
        val cardSpecs = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF0F172A.toInt())
            setPadding(24, 24, 24, 24)
        }

        val specsTitle = TextView(this).apply {
            text = "⚙️ HARDWARE VERIFICADO"
            textSize = 12f
            setTextColor(0xFF34D399.toInt()) // Verde
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }

        val specsInfo = TextView(this).apply {
            text = "• Aparelho: ${Build.MANUFACTURER.uppercase()} ${Build.MODEL}\n" +
                   "• Sistema: Android ${Build.VERSION.RELEASE} (HyperOS)\n" +
                   "• Resolução de Tela / DPI: ${resources.displayMetrics.densityDpi} dpi\n" +
                   "• Estado do HUD: Pronto para Sobreposição"
            textSize = 13f
            setTextColor(0xFFCBD5E1.toInt())
        }

        cardSpecs.addView(specsTitle)
        cardSpecs.addView(specsInfo)

        // Espaçamento
        val spacer = TextView(this).apply { text = ""; setPadding(0, 20, 0, 0) }

        // Botão Ativar Menu Flutuante
        val btnStartHud = Button(this).apply {
            text = "⚡ ATIVAR MENU FLUTUANTE"
            setTextColor(Color.WHITE)
            setBackgroundColor(0xFF0284C7.toInt())
            setPadding(16, 24, 16, 24)
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    startActivity(intent)
                } else {
                    startService(Intent(this@MainActivity, FloatingMenuService::class.java))
                }
            }
        }

        // Botão Opções do Desenvolvedor (DPI)
        val btnDevOptions = Button(this).apply {
            text = "🔧 ABRIR CONFIG. DPI (DEV)"
            setTextColor(Color.WHITE)
            setBackgroundColor(0xFF334155.toInt())
            setPadding(16, 24, 16, 24)
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
            }
        }

        rootLayout.addView(title)
        rootLayout.addView(subtitle)
        rootLayout.addView(cardSpecs)
        rootLayout.addView(spacer)
        rootLayout.addView(btnStartHud)
        
        val spacer2 = TextView(this).apply { text = ""; setPadding(0, 12, 0, 0) }
        rootLayout.addView(spacer2)
        rootLayout.addView(btnDevOptions)

        scrollView.addView(rootLayout)
        setContentView(scrollView)
    }
}
