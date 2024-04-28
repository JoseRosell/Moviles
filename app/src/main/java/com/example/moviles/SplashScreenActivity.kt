package com.example.moviles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashScreenActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val background = object : Thread() {
            override fun run() {
                try {
                    // Simular un proceso de carga de 3 segundos
                    Thread.sleep(100)
                    // Lanzar la actividad principal
                    val intent = Intent(baseContext, LoginActivity::class.java)
                    // Iniciar la actividad
                    startActivity(intent)
                    // Finalizar la actividad actual
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        // Iniciar el hilo
        background.start()
    }
}