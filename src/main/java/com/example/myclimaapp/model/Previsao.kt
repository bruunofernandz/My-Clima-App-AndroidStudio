class Previsao : Serializable {
    var data: String? = null
    var diaDaSemana: String? = null
    var maxima: String? = null
    var minima: String? = null
    var descricao: String? = null
    var condicao: String? = null

    constructor(
        data: String, diaDaSemana: String, maxima: String, minima: String, descricao: String, condicao: String) {
        this.data = data
        this.diaDaSemana = diaDaSemana
        this.maxima = maxima
        this.minima = minima
        this.descricao = descricao
        this.condicao = condicao
    }

    constructor() {}

    companion object {
        private const val serialVersionUID = 1L
    }


}