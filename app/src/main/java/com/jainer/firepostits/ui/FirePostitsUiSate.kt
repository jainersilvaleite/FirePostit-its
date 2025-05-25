package com.jainer.firepostits.ui

import android.text.BoringLayout
import com.jainer.firepostits.data.Postit
import java.time.Instant
import java.util.Date

// estados da tela FirePostits
data class FirePostitsUiSate(
    // armazena o texto inserido no campo correspondente ao conteúdo do novo post-it
    val content: String = "",
    // armazena a lista de post-its atual com base no captador de eventos
    val postitsList: List<Postit> = emptyList(),
    // armazena o estado do diálogo de edição de um post-it (aberto ou fechado)
    val editPostitDialogOpened: Boolean = false,
    // armazena o post-it que será editado/deletado no momento (o que está em foco)
    val postitOnFocus: Postit = Postit("", "", Date.from(Instant.now())),
    val onContentChange: (String) -> Unit = {},
    val onPostitsListEmpty: () -> Unit = {},
    val onPostitsListAdd: (Postit) -> Unit = {},
    val onEditPostitDialogOpenedChange: () -> Unit = {},
    val onPostitOnFocusChange: (Postit) -> Unit = {}
)