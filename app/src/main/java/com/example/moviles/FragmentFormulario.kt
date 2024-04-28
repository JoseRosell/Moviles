package com.example.moviles

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.*

class FragmentFormulario : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Variables
        val view = inflater.inflate(R.layout.fragment_formulario, container, false)
        val imagePerfil = view.findViewById<ImageView>(R.id.imagenPerfil)
        imagePerfil.setImageResource(R.drawable.ic_launcher_foreground)
        val btFecha = view.findViewById<Button>(R.id.btSeleccionaFecha)
        val txtFecha = view.findViewById<TextView>(R.id.fechaNacimiento)
        val crear = view?.findViewById<Button>(R.id.crearCuenta)

        imagePerfil.setOnClickListener {
            // Crear un AlertDialog para que el usuario introduzca la URL de la imagen
            val inputView = LayoutInflater.from(context).inflate(R.layout.dialog_input, null)
            val editText = inputView.findViewById<TextInputLayout>(R.id.editText)

            // Lanza el AlertDialog
            AlertDialog.Builder(this.requireContext())
                .setTitle("Introduce la url de la imagen que deseas")
                .setView(inputView)
                // Si el usuario presiona "Aceptar", se carga la imagen en el ImageView
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val userInput = editText?.editText?.text.toString()
                    // Usa glide para cargar la imagen en el ImageView
                    Glide.with(this)
                        .load(userInput)
                        .error(R.drawable.mimirlogo)
                        .into(imagePerfil)
                    // Cierra el AlertDialog
                    dialog.dismiss()
                }

                    // Si el usuario presiona "Cancelar", se cierra el AlertDialog
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


        btFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            // Crea un DatePickerDialog para que el usuario seleccione una fecha
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                txtFecha.text = "${cal.get(Calendar.DAY_OF_MONTH)} ${cal.get(Calendar.MONTH)} ${cal.get(Calendar.YEAR)}"
            }
            // Muestra el DatePickerDialog
            DatePickerDialog(
                this.requireContext(),
                dateSetListener,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.YEAR)
            ).show()
        }

        crear?.setOnClickListener {
            // Expresión regular para validar el correo electrónico
            val pattern = "^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\$".toRegex()
            val cuenta = view?.findViewById<TextInputEditText>(R.id.nickname)
            val contrasena = view?.findViewById<TextInputEditText>(R.id.contrasena)

            // Si coincide con el patrón es bueno, si no, da error
            if (!pattern.matches(cuenta?.text.toString())) {
                cuenta?.error = "El correo electronico proporcionado no es valido o está vacio"
            } else {
                if (contrasena?.text.toString().isEmpty()) {
                    contrasena?.error = "Introduzca una contraseña"
                } else {
                    // Si t odo está bien, se crea la cuenta y se eliminan los errores de los campos
                    contrasena?.error = null
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        cuenta?.text.toString(),
                        contrasena?.text.toString()
                    ).addOnCompleteListener {
                        if (!it.isSuccessful) {
                            // Si falla la autenticación, se muestra un mensaje de error
                            Toast.makeText(
                                context,
                                "Se ha producido un error al autenticar, por favor intentelo de nuevo",
                                Toast.LENGTH_LONG
                            ).show()
                        }else{
                            // En este caso se guarda el email y el provider en el archivo de preferencias
                            val prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).edit()
                            prefs.putString("email", cuenta?.text.toString())
                            prefs.putString("provider", ProviderType.BASIC.name)
                            prefs.apply()
                            // Se redirige al MainActivity
                            startActivity(Intent(activity, MainActivity::class.java))
                        }
                    }
                }
            }
        }
        return view
    }
}
