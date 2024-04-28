package com.example.moviles

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class LoginFragment : Fragment() {

    // Variables
    private lateinit var googleSignInClient: GoogleSignInClient
    private var auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        //definir y modificar los elementos de la vista
        val tvUserName = view.findViewById<TextInputLayout>(R.id.txtUsuario)
        val tvPassword = view.findViewById<TextInputLayout>(R.id.txtContraseña)
        val btRegisterHere = view.findViewById<Button>(R.id.registerHere)
        val btForgotPassword = view.findViewById<Button>(R.id.forgotPassword)
        val btSubmit = view.findViewById<Button>(R.id.submit)
        btSubmit.setBackgroundColor(resources.getColor(R.color.green))
        val btLoginFacebook = view.findViewById<Button>(R.id.logFacebook)
        btLoginFacebook.setBackgroundColor(resources.getColor(R.color.blue))
        val btLoginGoogle = view.findViewById<Button>(R.id.logGoogle)
        btLoginGoogle.setBackgroundColor(resources.getColor(R.color.red))

        // Configuración de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }!!

        // Eventos de los botones
        btSubmit.setOnClickListener {
            var usuario = tvUserName.editText?.text.toString()
            var password = tvPassword.editText?.text.toString()
            tvUserName.error = null
            tvPassword.error = null

            // Compruebo que los campos no estén vacios
            if (usuario.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(usuario).matches()) {
                tvUserName.error = "Introduzca un email valido"
                if (password.isEmpty()) {
                    tvPassword.error = "Introduzca una contraseña"
                }
            } else {
                if (password.isEmpty()) {
                    tvPassword.error = "Introduzca una contraseña"
                } else {
                    // Si no están vacios, entonces se procede a loguear y a guardarlos en la base de datos
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(usuario, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            // guardado de datos en el archivo de preferencias
                            val prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).edit()
                            prefs.putString("email", usuario)
                            prefs.putString("provider", ProviderType.BASIC.name)
                            prefs.apply()
                               // Se redirige al MainActivity
                            startActivity(Intent(activity, MainActivity::class.java))
                        } else {
                            // Si falla el login, se muestra un mensaje de error
                            Toast.makeText(context, "Correo electronico o contraseña no validos", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        btRegisterHere.setOnClickListener {
            // Se cambia al fragmento de registro
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, FragmentFormulario()).addToBackStack(null).commit()

        }

        btForgotPassword.setOnClickListener {
            // Se muestra un mensaje de que se ha pulsado en el botón de olvidar contraseña
            Toast.makeText(context, "Ha pulsado en Forgot Password", Toast.LENGTH_LONG).show()
        }

        btLoginFacebook.setOnClickListener {
            // Se muestra un mensaje de que se ha pulsado en el botón de login con Facebook
            Toast.makeText(context, "Ha pulsado en LoginFacebook", Toast.LENGTH_LONG).show()
        }

        btLoginGoogle.setOnClickListener {
            // Se lanza el proceso de login con Google
            signInWithGoogle()
        }

        //devuelve la información de la vista
        return view
    }

    // Se crea el launcher para el login con Google
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun signInWithGoogle() {
        // se ejecuta el launcher con el intent de login de Google
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        // Se manejan los resultados del login con Google
        if (task.isSuccessful) {
            // Si el login es exitoso, se obtiene la cuenta de Google
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            // Si falla el login, se muestra un mensaje de error
            Toast.makeText(context, "No se pudo registrar, por favor intentelo de nuevo más tarde", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        // Se actualiza la interfaz de usuario con la cuenta de Google
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                // Si el login es exitoso, se guarda la información de la cuenta
                if (it.isSuccessful) {
                    // guardado de datos en el archivo de preferencias
                    val prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).edit()
                    prefs.putString("email", account.email.toString())
                    prefs.putString("provider", ProviderType.GOOGLE.name)
                    prefs.apply()
                    // Se redirige a la pantalla principal
                    var intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                } else {
                    // Si falla el login, se muestra un mensaje de error
                    Toast.makeText(
                        context,
                        "Imposible loguearse ahora, intentelo de nuevo más tarde",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
