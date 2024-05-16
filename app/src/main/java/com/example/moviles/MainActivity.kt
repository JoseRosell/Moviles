package com.example.moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.bnmenu.fragments.UbicationFragment
import com.example.moviles.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    // Variables
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de la barra de herramientas
        setSupportActionBar(binding.toolBar)
        // Configuración del ActionBarDrawerToggle
        var toggle = ActionBarDrawerToggle(this, binding.drawerlayout, binding.toolBar, R.string.nav_open, R.string.nav_close)
        // Agregar el ActionBarDrawerToggle al DrawerLayout
        binding.drawerlayout.addDrawerListener(toggle)
        // Sincroniza
        toggle.syncState()

        // Lee el archivo preferencias
        val prefs2 = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        // Obtiene el email
        val email2 = prefs2.getString("email", null)
        // Obtiene el provider
        val header = binding.navigationDrawer.getHeaderView(0)
        // Modifica el texto del header con el nombre de "usuario" (email)
        header.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.nombreUsuario).text = email2

        // Obtiene el FragmentManager
        fragmentManager = supportFragmentManager
        // Abre el Fragmento HomeFragment
        openFragment(HomeFragment())



        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerlayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerlayout.closeDrawer(GravityCompat.START)
                } else {
                    if (fragmentManager.backStackEntryCount > 1) {
                        fragmentManager.popBackStack()
                    } else {
                        finish()
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)


        // escucha que se activa cuando se selecciona un elemento del menú inferior
        binding.navigationDrawer.setNavigationItemSelectedListener(this)
        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            // Dependiendo del elemento seleccionado, se muestra un fragmento diferente
            when(item.itemId){

                R.id.bottomHome -> {
                    openFragment(HomeFragment())
                    selectDrawerMenuItem(R.id.navHome)
                }
                R.id.bottomTabla -> {
                    openFragment(TablasFragment())
                    selectDrawerMenuItem(R.id.navTabla)
                }
                R.id.bottomRecetas -> {
                    openFragment(RecetasFragment())
                    selectDrawerMenuItem(R.id.navRecetas)
                }
                R.id.bottomUbicacion -> {
                    openFragment(UbicationFragment())
                    selectDrawerMenuItem(R.id.navUbicacion)
                }
            }
            // Devuelve verdadero
            true
        }
    }

    // Método que se activa cuando se selecciona un elemento de la barrade navegación inferior
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Dependiendo del elemento seleccionado, se muestra el fragmento correspondiente
        when(item.itemId){
            R.id.navHome -> {
                openFragment(HomeFragment())
                selectBottomNavigationItem(R.id.bottomHome)
            }
            R.id.navTabla -> {
                openFragment(TablasFragment())
                selectBottomNavigationItem(R.id.bottomTabla)
            }
            R.id.navRecetas -> {
                openFragment(RecetasFragment())
                selectBottomNavigationItem(R.id.bottomRecetas)
            }
            R.id.navUbicacion -> {
                openFragment(UbicationFragment())
                selectBottomNavigationItem(R.id.bottomUbicacion)
            }
            R.id.navCerrarSesion -> {
                // Crear un AlertDialog para cerrar sesión
                // Lanza el AlertDialog
                AlertDialog.Builder(this)
                    .setTitle("¿Deseas realmente cerrar sesión?")
                    // Si el usuario presiona "Aceptar", se carga la imagen en el ImageView
                    .setPositiveButton("Aceptar") { dialog, _ ->
                        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
                        prefs.edit().clear().apply()
                        FirebaseAuth.getInstance().signOut()
                        intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        // Cierra el AlertDialog
                        dialog.dismiss()
                    }
                    // Si el usuario presiona "Cancelar", se cierra el AlertDialog
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        // Si se selecciona un elemento fuera del menú, se cierra el DrawerLayout
        binding.drawerlayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Método para seleccionar un elemento en la barra inferior
    private fun selectBottomNavigationItem(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }

    // Método para seleccionar un elemento en el DrawerLayout
    private fun selectDrawerMenuItem(itemId: Int) {
        binding.navigationDrawer.menu.findItem(itemId)?.isChecked = true

    }

    // Método para abrir un Fragmento de forma más escueta
    private fun openFragment(fragment: Fragment){
       fragmentManager.beginTransaction()
        .add(R.id.fragmentContainer, fragment)
        .addToBackStack(null)
        .commit()
    }
}
