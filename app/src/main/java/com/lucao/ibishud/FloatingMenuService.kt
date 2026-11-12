package com.lucao.ibishud

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.view.Choreographer

class FloatingMenuService : Service(), Choreographer.FrameCallback {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var txtFps: TextView
    private var frameCount = 0
    private var lastTimeNanos = 0L
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_menu, null)
        txtFps = floatingView.findViewById(R.id.txtFloatingFps)

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // Permitimos foco aqui para que os botões e switches do painel respondam aos cliques
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            0, // Removido FLAG_NOT_FOCUSABLE para permitir interação com o menu
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 40
            y = 180
        }

        val collapsedBubble = floatingView.findViewById<LinearLayout>(R.id.collapsedBubble)
        val expandedPanel = floatingView.findViewById<LinearLayout>(R.id.expandedPanel)
        val btnMinimize = floatingView.findViewById<TextView>(R.id.btnMinimize)
        val swEspLine = floatingView.findViewById<Switch>(R.id.swEspLine)
        val swEspBox = floatingView.findViewById<Switch>(R.id.swEspBox)

        // Ao clicar na bolha fechada, esconde a bolha e MOSTRA O PAINEL DE CONFIGURAÇÕES
        collapsedBubble.setOnClickListener {
            collapsedBubble.visibility = View.GONE
            expandedPanel.visibility = View.VISIBLE
        }

        // Botão para fechar o painel e voltar para a bolha
        btnMinimize.setOnClickListener {
            expandedPanel.visibility = View.GONE
            collapsedBubble.visibility = View.VISIBLE
        }

        swEspLine.setOnCheckedChangeListener { _, isChecked ->
            swEspLine.text = if (isChecked) "ESP Linha [ATIVO - DEMO]" else "ESP Linha (Demo UI)"
        }

        swEspBox.setOnCheckedChangeListener { _, isChecked ->
            swEspBox.text = if (isChecked) "ESP Caixa [ATIVO - DEMO]" else "ESP Caixa (Demo UI)"
        }

        windowManager.addView(floatingView, params)
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (lastTimeNanos == 0L) {
            lastTimeNanos = frameTimeNanos
        } else {
            frameCount++
            if (frameTimeNanos - lastTimeNanos >= 1_000_000_000L) {
                txtFps.text = "IBIS | FPS: $frameCount"
                frameCount = 0
                lastTimeNanos = frameTimeNanos
            }
        }
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) windowManager.removeView(floatingView)
    }
}
