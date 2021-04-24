package br.com.zup.autores

import javax.persistence.Embeddable

@Embeddable
class Endereco(
    enderecoResponse: EnderecoResponse,
    val numero: String
) {
    val logradouro = enderecoResponse.logradouro
    val localidade = enderecoResponse.localidade
    val uf = enderecoResponse.uf
}
