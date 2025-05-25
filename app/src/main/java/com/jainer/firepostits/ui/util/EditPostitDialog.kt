package com.jainer.firepostits.ui.util

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.DatabaseReference
import com.jainer.firepostits.data.Postit
import com.jainer.firepostits.ui.FirePostitsViewModel
import java.time.Instant
import java.util.Date

@Composable
fun EditPostitDialog(
    postit: Postit,
    onDismissRequest: () -> Unit,
    databaseReference: DatabaseReference,
    context: Context,
    modifier: Modifier = Modifier,
    viewModel: FirePostitsViewModel = viewModel()
) {
    val uiState by viewModel.uiSate.collectAsState()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .background(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White
                ).border(
                    width = 5.dp,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black
                ).fillMaxHeight(0.25f).fillMaxWidth(0.9f)
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = modifier.fillMaxWidth()
            ) {
                // botão de confirmação de edição do post-its
                IconButton(
                    onClick = {
                        viewModel.editPostit(
                            databaseReference = databaseReference,
                            context = context,
                            postit = Postit(
                                postit.id,
                                uiState.content,
                                Date.from(Instant.now())
                            )
                        )
                        uiState.onContentChange("")
                        uiState.onEditPostitDialogOpenedChange()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirmar edição do post-it"
                    )
                }
                // botão de cancelamento da edição do post-it
                IconButton(
                    onClick = {
                        uiState.onContentChange("")
                        uiState.onEditPostitDialogOpenedChange()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancelar edição do post-it"
                    )
                }
            }
            // campo de texto onde o novo conteúdo do post-it é especificado
            OutlinedTextField(
                value = uiState.content,
                onValueChange = { uiState.onContentChange(it) },
                placeholder = {
                    Text(
                        text = "Novo contéudo do Post-it"
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 10.dp,
                        vertical = 10.dp
                    ).weight(1f)
            )
        }
    }
}