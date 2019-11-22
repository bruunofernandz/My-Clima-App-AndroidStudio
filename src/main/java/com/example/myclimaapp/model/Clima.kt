package com.example.climaapp.model
class Clima {

    var temperatura: Int? = null
    var data: String? = null
    var hora: String? = null
    var codigoCondicao: String? = null
    var descricao: String? = null
    var atualmente: String? = null
    var cid: String? = null
    var cidade: String? = null
    var idImagem: String? = null
    var humidade: Int? = null
    var velocidadeDoVento: String? = null
    var nascerDoSol: String? = null
    var porDoSol: String? = null
    var condicaoDoTempo: String? = null
    var nomeDaCidade: String? = null

    var previsoes: List<Previsao>? = null

    constructor(
        temperatura: Int?,
        data: String,
        hota: String,
        codigoCondicao: String,
        descricao: String,
        atualmente: String,
        cid: String,
        cidade: String,
        idImagem: String,
        humidade: Int?,
        velocidadeDoVento: String,
        nascerDoSol: String,
        porDoSol: String,
        condicaoDoTempo: String,
        nomeDaCidade: String
    ) {
        this.temperatura = temperatura
        this.data = data
        this.hora = hota
        this.codigoCondicao = codigoCondicao
        this.descricao = descricao
        this.atualmente = atualmente
        this.cid = cid
        this.cidade = cidade
        this.idImagem = idImagem
        this.humidade = humidade
        this.velocidadeDoVento = velocidadeDoVento
        this.nascerDoSol = nascerDoSol
        this.porDoSol = porDoSol
        this.condicaoDoTempo = condicaoDoTempo
        this.nomeDaCidade = nomeDaCidade
    }

    constructor() {}
}
