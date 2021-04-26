package br.com.zup.autores

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.uri.UriBuilder
import io.micronaut.validation.Validated
import java.util.*
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Controller("/autores")
class AutorController(
    val autorRepository: AutorRepository,
    val enderecoClient: EnderecoClient
) {

    @Post
    @Transactional
    fun cadastra(@Body @Valid request: NovoAutorRequest): HttpResponse<Any>{
        println("Requisição: $request")

        val enderecoResponse: HttpResponse<EnderecoResponse> = enderecoClient.consulta(request.cep)

        val autor = request.paraAutor(enderecoResponse.body()!!)
        val salvo = autorRepository.save(autor)
        println("Autor: ${autor.nome}")

        val uri = UriBuilder.of("/autores/{id}")
            .expand(mutableMapOf(Pair("id", salvo.id)))

        return HttpResponse.created(uri)
    }

    @Get
    @Transactional
    fun lista(@QueryValue(defaultValue = "") email: String): HttpResponse<Any>{

        if(email.isBlank()){
            val autores = autorRepository.findAll()
            val resposta = autores.map { autor -> DetalhesDoAutorResponse(
                autor.nome,
                autor.email,
                autor.descricao
            ) }
            return HttpResponse.ok(resposta)
        }
        val possivelAutor = autorRepository.findByEmail(email)
        if(possivelAutor.isEmpty){
            return HttpResponse.notFound()
        }

        val autor = possivelAutor.get()
        return HttpResponse.ok(DetalhesDoAutorResponse(
            autor.nome,
            autor.email,
            autor.descricao
        ))
    }

    @Put("/{id}")
    @Transactional
    fun atualiza(@PathVariable id: Long, nome: String): HttpResponse<Any>{
        val possivelAutor: Optional<Autor> = autorRepository.findById(id)
        if(possivelAutor.isEmpty){
            return HttpResponse.notFound()
        }
        val autor = possivelAutor.get()
        autor.nome = nome
//        autorRepository.update(autor)

        return HttpResponse.ok(DetalhesDoAutorResponse(
            autor.nome,
            autor.email,
            autor.descricao
        ))
    }

    @Delete("/{id}")
    @Transactional
    fun excluiAutor(@PathVariable id:Long):HttpResponse<Any>{

        val autorParaExcluir = autorRepository.findById(id)

        if(autorParaExcluir.isEmpty){
            return HttpResponse.notFound()
        }

//        val autor = autorParaExcluir.get()
//        autorRepository.delete(autor)

        autorRepository.deleteById(id)
        return HttpResponse.ok()
    }


}