package com.jainer.firepostits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jainer.firepostits.ui.FirePostitsScreen
import com.jainer.firepostits.ui.FirePostitsViewModel
import com.jainer.firepostits.ui.theme.FirePostitsTheme

class MainActivity : ComponentActivity() {
    // armazena a referência do Firebase Realtime Database
    lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // obtém a ViewModel referente à tela FirePostits
        val viewModel = ViewModelProvider(this)[FirePostitsViewModel::class.java]
        // define a referência do Firebase Realtime Database a ser utilizada
        /* a referência "Post-it" pode ser análoga ao CREATE DATABASE/SCHEMA do SQL, porém
        com a diferença de que, são chamados de nós (nodes) e se já existir o nó especificado,
        o nó existente será utilizado (como se estivéssemos usando o comando USE do SQL */
        /* cada nó (neste caso, o nó Post-it) possui "filhos", que consequentemente armazenarão
        seus próprios valores (para este aplicativo, cada um terá um id, content e createdAt) */
        databaseReference = FirebaseDatabase.getInstance().getReference("Post-it")
        
        // inicializa o conteúdo da tela FirePostits com a ViewModel e Database fornecidos
        setContent {
            FirePostitsTheme {
                FirePostitsScreen(
                    databaseReference = databaseReference,
                    viewModel = viewModel
                )
            }
        }
    }
}