package com.example.moviles


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.lifecycle.ReportFragment.Companion.reportFragment

enum class ProviderType{
    BASIC,
    GOOGLE
}

class LoginActivity : AppCompatActivity(){



override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    // Obtener el FragmentManager
    val fragmentManager = supportFragmentManager

    // Lee el archivo preferencias
    val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
    val email = prefs.getString("email", null)
    val provider = prefs.getString("provider", null)



    // Si el email y el provider no son nulos, entonces se redirige a la pantalla principal
    if(email != null && provider != null){
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(intent)
        finish()
    }

    // Comenzar la transacción del Fragment si no se ha redirigido a la pantalla principal
    val fragmentTransaction = fragmentManager.beginTransaction()

    // Agregar el Fragment al contenedor
    fragmentTransaction.add(R.id.fragmentContainer, LoginFragment()).addToBackStack(null).commit()

// Manejar el botón de atrás
    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (fragmentManager.backStackEntryCount > 1) {
                fragmentManager.popBackStack()
            } else {
                finish()
            }
        }
    }
    onBackPressedDispatcher.addCallback(this, callback)

}
}