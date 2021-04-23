package br.com.zup.autores

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Autor(
    var nome: String,
    val email: String,
    var descricao: String) {

    @Id
    @GeneratedValue
    var id: Long? = null
    val criadoem: LocalDateTime = LocalDateTime.now()
}
