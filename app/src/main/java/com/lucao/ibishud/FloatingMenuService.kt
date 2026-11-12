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

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
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
        val swHeadshot = floatingView.findViewById<Switch>(R.id.swHeadshot)
        val swInjector = floatingView.findViewById<Switch>(R.id.swInjector)

        collapsedBubble.setOnClickListener {
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            windowManager.updateViewLayout(floatingView, params)
            collapsedBubble.visibility = View.GONE
            expandedPanel.visibility = View.VISIBLE
        }

        btnMinimize.setOnClickListener {
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            windowManager.updateViewLayout(floatingView, params)
            expandedPanel.visibility = View.GONE
            collapsedBubble.visibility = View.VISIBLE
        }

        swEspLine.setOnCheckedChangeListener { _, isChecked ->
            swEspLine.text = if (isChecked) "ESP Linha [ATIVO]" else "ESP Linha (Demo)"
        }

        swEspBox.setOnCheckedChangeListener { _, isChecked ->
            swEspBox.text = if (isChecked) "ESP Caixa [ATIVO]" else "ESP Caixa (Demo)"
        }

        swHeadshot.setOnCheckedChangeListener { _, isChecked ->
            swHeadshot.text = if (isChecked) "Puxar Capa [LIGADO]" else "Puxar Capa (Aim Assist)"
        }

        swInjector.setOnCheckedChangeListener { _, isChecked ->
            swInjector.text = if (isChecked) "Injetor FF [INJETADO]" else "Injetor FF (Módulo)"
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
                txtFps.text = "IBIS | $frameCount FPS"
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
