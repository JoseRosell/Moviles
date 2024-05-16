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
import com.google.firebase.auth.FirebaseAuth
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
            val inputView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input, null)
            val editText = inputView.findViewById<TextInputEditText>(R.id.editText)

            // Verificar que editText no es null
            if (editText == null) {
                Toast.makeText(requireContext(), "Error al cargar el diálogo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lanza el AlertDialog
            AlertDialog.Builder(requireContext())
                .setTitle("Introduce la url de la imagen que deseas")
                .setView(inputView)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val userInput = editText.text.toString()

                    if (userInput.isBlank()) {
                        Toast.makeText(requireContext(), "La URL no puede estar vacía", Toast.LENGTH_SHORT).show()
                    } else {
                        // Usa Glide para cargar la imagen en el ImageView
                        Glide.with(this)
                            .load(userInput)
                            .error(R.drawable.mimirlogo)
                            .into(imagePerfil)
                    }
                    // Cierra el AlertDialog
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        btFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                txtFecha.text = "${cal.get(Calendar.DAY_OF_MONTH)} ${cal.get(Calendar.MONTH)} ${cal.get(Calendar.YEAR)}"
            }
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        crear?.setOnClickListener {
            val pattern = "^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\$".toRegex()
            val cuenta = view.findViewById<TextInputEditText>(R.id.nickname)
            val contrasena = view.findViewById<TextInputEditText>(R.id.contrasena)

            if (!pattern.matches(cuenta?.text.toString())) {
                cuenta?.error = "El correo electronico proporcionado no es valido o está vacio"
            } else {
                if (contrasena?.text.toString().isEmpty()) {
                    contrasena?.error = "Introduzca una contraseña"
                } else {
                    contrasena?.error = null
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        cuenta?.text.toString(),
                        contrasena?.text.toString()
                    ).addOnCompleteListener {
                        if (!it.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Se ha producido un error al autenticar, por favor intentelo de nuevo",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).edit()
                            prefs.putString("email", cuenta?.text.toString())
                            prefs.putString("provider", ProviderType.BASIC.name)
                            prefs.apply()
                            startActivity(Intent(activity, MainActivity::class.java))
                        }
                    }
                }
            }
        }
        return view
    }
}
