package com.example.bd_room.Screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bd_room.DAO.UserDao
import com.example.bd_room.Model.User
import com.example.bd_room.Repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UserApp(userRepository: UserRepository) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var users by remember { mutableStateOf(listOf<User>()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        users = withContext(Dispatchers.IO) {
            userRepository.getAllUsers()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        TextField(
            value = nombre,
            onValueChange = {
                nombre = it
                updateUser(userRepository, id, nombre, apellido, edad, scope, context)
            },
            label = { Text(text = "nombre") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = apellido,
            onValueChange = {
                apellido = it
                updateUser(userRepository, id, nombre, apellido, edad, scope, context)
            },
            label = { Text(text = "apellido") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = edad,
            onValueChange = {
                edad = it
                updateUser(userRepository, id, nombre, apellido, edad, scope, context)
            },
            label = { Text(text = "Edad") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (nombre.isNotBlank() && apellido.isNotBlank() && edad.isNotBlank() && (edad.toIntOrNull() ?: 0) > 0) {
                    val user = User(
                        nombre = nombre,
                        apellido = apellido,
                        edad = edad.toIntOrNull() ?: 0
                    )
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            userRepository.insertar(user)
                            users = userRepository.getAllUsers() // Fetch users after insertion
                        }
                        Toast.makeText(context, "Usuario Registrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Por favor, complete todos los campos y asegúrese de que la edad sea mayor que cero", Toast.LENGTH_SHORT).show()
                }
            }) {
            Text(text = "Registrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            users.forEach { user ->
                Text("${user.id} ${user.nombre} ${user.apellido} ${user.edad} ")
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        Button(
            onClick = {
                val userIdToDelete = id.toIntOrNull()
                if (userIdToDelete != null) {
                    val userToDelete = users.find { it.id == userIdToDelete }
                    if (userToDelete != null) {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                userRepository.eliminar(userToDelete)
                                users = userRepository.getAllUsers() // Fetch users after deletion
                            }
                            Toast.makeText(context, "Usuario Eliminado", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "ID inválido", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Eliminar Usuario por ID")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = id,
            onValueChange = { id = it },
            label = { Text(text = "ID") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
}

fun updateUser(
    userRepository: UserRepository,
    id: String,
    nombre: String,
    apellido: String,
    edad: String,
    scope: CoroutineScope,
    context: Context
) {
    if (id.isNotBlank() && nombre.isNotBlank() && apellido.isNotBlank() && edad.isNotBlank() && (edad.toIntOrNull() ?: 0) > 0) {
        val user = User(
            id = id.toIntOrNull() ?: 0,
            nombre = nombre,
            apellido = apellido,
            edad = edad.toIntOrNull() ?: 0
        )
        scope.launch {
            withContext(Dispatchers.IO) {
                userRepository.actualizar(user)
            }
            Toast.makeText(context, "Usuario Actualizado", Toast.LENGTH_SHORT).show()
        }
    }
}