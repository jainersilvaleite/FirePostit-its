package com.jainer.firepostits.data

import java.time.Instant
import java.util.Date

// classe de dados que contém os valores armazenados por cada filho do nó "Post-it"
data class Postit(
    val id: String,
    val content: String,
    val createdAt: Date
) {
    // construtor necessário para definir valores padrão aos dados armazenados
    constructor(): this("", "", Date.from(Instant.now()))
}
