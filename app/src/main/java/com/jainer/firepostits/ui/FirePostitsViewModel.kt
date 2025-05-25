package com.jainer.firepostits.ui

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.jainer.firepostits.data.Postit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FirePostitsViewModel: ViewModel() {
    // cria uma versão mutável e observável do FirePostitsUiState
    private val _uiState = MutableStateFlow(FirePostitsUiSate())
    // cria uma versão imutável e exposta do FirePostitsUiState
    val uiSate = _uiState.asStateFlow()

    // na inicialização do ViewModel, implementa as funções de:
    // - alteração do estado content
    // - alteração do estado editPostitDialogOpened
    // - alteração do estado postitOnFocus
    // - limpeza da lista anteriormente coletada para propósitos de perfomance
    // - adição de novo postit ao estado postitsList
    init {
        _uiState.update {
            it.copy(
                onContentChange = { content ->
                    _uiState.value = _uiState.value.copy(
                        content = content
                    )
                },
                onEditPostitDialogOpenedChange = {
                    val editPostitDialogOpened = _uiState.value.editPostitDialogOpened
                    _uiState.value = _uiState.value.copy(
                        editPostitDialogOpened = !editPostitDialogOpened
                    )
                },
                onPostitOnFocusChange = { postit ->
                    _uiState.value = _uiState.value.copy(
                        postitOnFocus = postit
                    )
                },
                onPostitsListEmpty = {
                    _uiState.value = _uiState.value.copy(
                        postitsList = emptyList()
                    )
                },
                onPostitsListAdd = { postit ->
                    val postitsList = _uiState.value.postitsList
                    _uiState.value = _uiState.value.copy(
                        // adiciona o elemento na lista e os organiza por data de criação
                        postitsList = (postitsList + postit).sortedByDescending { it.createdAt }
                    )
                }
            )
        }
    }

    // função que carrega todos os Post-its armazenados no banco de dados
    fun loadPostits(databaseReference: DatabaseReference, context: Context) {
        databaseReference.addValueEventListener(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _uiState.value.onPostitsListEmpty()
                    snapshot.children.forEach {
                        val postit = it.getValue(Postit::class.java)
                        postit?.let { _uiState.value.onPostitsListAdd(postit) }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context,
                        "Ocorreu um erro: $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    // função que armazena um novo Post-it no banco de dados
    fun addPostit(databaseReference: DatabaseReference, context: Context, postit: Postit) {
        // um novo filho é criado caso o id especificado não exista ainda
        // do contrário, será utilizado o filho existente
        databaseReference.child(postit.id).setValue(postit)
            // em caso de sucesso na criação do filho
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Post-it adicionado com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            // em caso de falha na criação do filho
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Ops! Algo deu errado ao adicionar o Post-it!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        // limpa o conteúdo inserido na caixa de texto
        _uiState.value.onContentChange("")
    }

    // função que edita as informações de um Post-it existente no banco de dados
    fun editPostit(databaseReference: DatabaseReference, context: Context, postit: Postit) {
        val newPostitDataMap = mapOf(
            "content" to postit.content,
            "createdAt" to postit.createdAt
        )
        databaseReference.child(postit.id).updateChildren(newPostitDataMap)
            // em caso de sucesso na atualização do filho
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Post-it editado com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            // em caso de falha na atualização do filho
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Ops! Algo deu errado ao editar este Post-it!",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    // função que deleta um Post-it existente no banco de dados
    fun deletePostit(databaseReference: DatabaseReference, context: Context, postitId: String) {
        databaseReference.child(postitId).removeValue()
            // em caso de sucesso ao remover o filho
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Post-it deletado com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            // em caso de falha ao remover o filho
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Ops! Algo deu errado ao deletar este Post-it!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}