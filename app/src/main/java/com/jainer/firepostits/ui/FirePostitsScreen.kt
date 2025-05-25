package com.jainer.firepostits.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.DatabaseReference
import com.jainer.firepostits.data.Postit
import com.jainer.firepostits.ui.util.EditPostitDialog
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@Composable
fun FirePostitsScreen(
    databaseReference: DatabaseReference, // referência do nó "Post-it"
    modifier: Modifier = Modifier,
    viewModel: FirePostitsViewModel = viewModel(),
) {
    // obtém o uiState da tela FirePostits como um estado
    val uiState by viewModel.uiSate.collectAsState()
    // contexto atual do aplicativo (para geração das mensagens Toast)
    val context = LocalContext.current
    // carrega todos os Post-its armazenados no banco de dados (nó "Post-it")
    viewModel.loadPostits(databaseReference = databaseReference, context = context)

    // conteúdo total da tela
    Scaffold {
        // coluna principal onde todos os elementos da tela serão distribuídos
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxSize().padding(it)
        ) {
            // campo de texto onde o conteúdo do post-it a ser adicionado é especificado
            OutlinedTextField(
                value = uiState.content,
                onValueChange = { uiState.onContentChange(it) },
                placeholder = {
                    Text(
                        text = "Conteúdo do Post-it"
                    )
                },
                modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp)
            )
            // botão para adicionar um novo post-it
            OutlinedButton(
                onClick = {
                    /* é importante que cada filho do nó especificado (Post-it) seja identificado no banco
    de dados com um id único, e para isso, o Firebase Realtime Database disponibiliza um
    recurso que mapeia todos as criações de novos filhos e atribui a elas um id do tipo
    String com o objetivo de diferenciá-lo dos demais */
                    val postitId = databaseReference.push().key
                    /* ao clicar no botão, é verificado se uma nova chave (id) foi gerada a partir
    * de uma nova atividade do banco de dados. Caso tenha ocorrido, será criado um
    * novo filho no nó "Post-it" contendo todas as informações descritas na variável
    * temporária postit (que carrega os dados que serão armazenados pelo filho) */
                    if (postitId != null) {
                        // especifica os dados do Post-it a ser adicionado numa nova instância de classe
                        val postit = Postit(
                            id = postitId,
                            content = uiState.content,
                            createdAt = Date.from(Instant.now())
                        )
                        // executa a função para armazenar um novo Post-it no banco de dados
                        viewModel.addPostit(
                            databaseReference = databaseReference,
                            context = context,
                            postit = postit
                        )
                    } else {
                        Toast.makeText(context, "Algo deu errado!", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = RectangleShape,
                modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp)
            ) {
                Text(
                    text = "Adicionar Post-it"
                )
            }
            // espaço entre os elementos anteriores e a lista de post-its adicionados
            Spacer(modifier = modifier.height(15.dp))
            // grid que exibe os post-its adicionados
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.fillMaxWidth().weight(1f).padding(horizontal = 10.dp)
            ) {
                items(uiState.postitsList.size) { index ->
                    val postit = uiState.postitsList[index]
                    Column(
                        modifier = modifier
                            .background(
                                color = Color(241, 222, 98, 255),
                                shape = RoundedCornerShape(10.dp)
                            ).border(
                                width = 2.dp,
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0, 0, 0, 26)
                            ).padding(10.dp)
                    ) {
                        Spacer(modifier = modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = modifier.fillMaxWidth()
                        ) {
                            // botão de edição dos post-its
                            IconButton(
                                onClick = {
                                    /* define o conteúdo a ser exibido de placeholder
                                    como o conteúdo do post-it em foco */
                                    uiState.onContentChange(postit.content)
                                    uiState.onPostitOnFocusChange(postit)
                                    uiState.onEditPostitDialogOpenedChange()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar post-it"
                                )
                            }
                            // botão de deletar os post-its
                            IconButton(
                                onClick = {
                                    viewModel.deletePostit(
                                        databaseReference = databaseReference,
                                        context = context,
                                        postitId = postit.id
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Deletar post-it"
                                )
                            }
                        }
                        Text(
                            text = postit.content,
                            color = Color(107, 95, 0, 255),
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = formatDateTime(postit.createdAt),
                            textAlign = TextAlign.Left,
                            color = Color(107, 95, 0, 255),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(modifier = modifier.height(5.dp))
                    }
                }
            }
        }
        if (uiState.editPostitDialogOpened) {
            EditPostitDialog(
                postit = uiState.postitOnFocus,
                onDismissRequest = { uiState.onEditPostitDialogOpenedChange() },
                databaseReference = databaseReference,
                context = context
            )
        }
    }
}

// formatar as datas para um padrão específico
fun formatDateTime(date: Date): String {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return format.format(date)
}