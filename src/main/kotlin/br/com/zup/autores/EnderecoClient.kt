package br.com.zup.autores

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("http://viacep.com.br/ws/")
interface EnderecoClient {

    @Get("{cep}/xml/",consumes = [MediaType.APPLICATION_XML])
    fun consulta(@QueryValue cep: String): HttpResponse<EnderecoResponse>
}