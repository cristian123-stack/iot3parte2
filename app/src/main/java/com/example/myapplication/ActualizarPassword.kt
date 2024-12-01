package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ActualizarPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actualizar_password)

        val inputUsuarioPassword = findViewById<EditText>(R.id.inputUsuarioPassword)
        val inputPasswordAnterior = findViewById<EditText>(R.id.inputPasswordAnterior)
        val inputNuevaPassword = findViewById<EditText>(R.id.inputNuevaPassword)
        val btnActualizarPassword = findViewById<Button>(R.id.btnActualizarPassword)
        val btnVolverLoginPassword = findViewById<Button>(R.id.btnVolverLoginPassword)

        val auth = FirebaseAuth.getInstance()

        btnActualizarPassword.setOnClickListener {
            val usuario = inputUsuarioPassword.text.toString().trim()
            val passwordAnterior = inputPasswordAnterior.text.toString().trim()
            val nuevaPassword = inputNuevaPassword.text.toString().trim()

            if (usuario.isEmpty() || passwordAnterior.isEmpty() || nuevaPassword.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null && user.email == usuario) {
                // Reautenticar al usuario
                val credential = EmailAuthProvider.getCredential(usuario, passwordAnterior)
                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            // Actualizar la contraseña
                            user.updatePassword(nuevaPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show()
                                        // Redirigir al login
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Error al actualizar contraseña: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Contraseña anterior incorrecta", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al reautenticar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Correo electrónico no coincide con el usuario autenticado", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolverLoginPassword.setOnClickListener {
            val intent = Intent(this, OpcionesCuenta::class.java)
            startActivity(intent)
            finish()
        }
    }
}

