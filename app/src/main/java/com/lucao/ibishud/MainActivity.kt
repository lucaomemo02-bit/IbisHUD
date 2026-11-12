package com.lucao.ibishud

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verifica a permissão de sobreposição de tela
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        } else {
            // Inicia o serviço flutuante imediatamente
            startService(Intent(this, FloatingMenuService::class.java))
        }

        // Fecha a MainActivity na mesma hora para sumir com esse menu feio da frente
        finish()
    }
}
