package br.com.zup.autores

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.inject.Inject

@MicronautTest
internal class AutorControllerTest{

    @field:Inject
    lateinit var autorRepository: AutorRepository

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @field:Inject
    lateinit var enderecoClient: EnderecoClient

    lateinit var autor: Autor
    lateinit var enderecoResponse: EnderecoResponse
    @BeforeEach
    internal fun setup() {

        enderecoResponse = EnderecoResponse("Rua milão", "Feira de Santana", "Ba")
        val endereco = Endereco(enderecoResponse, "44056680", "340")
        autor = Autor("autor do livro", "autor@email.com.br", "descrição", endereco)

        autorRepository.save(autor)
    }

    @AfterEach
    internal fun tearDown() {
        autorRepository.deleteAll()
    }

    @Test
    internal fun `Deve retornar os detalhes de um autor`(){

        val response = client.toBlocking().exchange(
            "/autores?email=${autor.email}", DetalhesDoAutorResponse::class.java)

        assertEquals(HttpStatus.OK,response.status)
        assertNotNull(response.body())
        assertEquals(autor.nome, response.body()!!.nome)
        assertEquals(autor.email, response.body()!!.email)
        assertEquals(autor.descricao, response.body().descricao)
    }

    @Test
    internal fun `deve cadastrar um novo autor`(){

        //cenário

        val novoAutorRequest = NovoAutorRequest(
            "Lincoln Gadea",
            "lincoln@email.com.br",
            "O Carro de duas rodas",
            "44056680",
            "122"
        )

        Mockito
            .`when`(enderecoClient.consulta(novoAutorRequest.cep))
            .thenReturn(HttpResponse.ok(enderecoResponse))

        val request = HttpRequest.POST("/autores",novoAutorRequest)

        //Ação

        val response = client.toBlocking().exchange(request, Any::class.java)

        //Verificação

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.matches("/autores/\\d".toRegex()))

    }

    @MockBean(EnderecoClient::class)
    fun enderecoMockado(): EnderecoClient{
        return Mockito.mock(EnderecoClient::class.java)
    }
}