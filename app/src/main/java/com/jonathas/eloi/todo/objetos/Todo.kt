package com.jonathas.eloi.todo.objetos

import java.util.*

class Todo() {
    var texto : String = ""
    var prioridade: String = ""
    var data: String = ""
    var feito: Boolean = false
    var id: String = ""

    constructor(id: String, texto: String, prioridade: String, data: String,feito: Boolean) : this() {
        this.id = id
        this.texto = texto
        this.prioridade = prioridade
        this.data = data
        this.feito = feito
    }
}