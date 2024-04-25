package no.blommish

import org.springframework.data.annotation.Id
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BarRepository : CrudRepository<Bar, Long>

data class Bar(
    @Id
    val id: Long = 0,
    val data: MyJson,
)

data class MyJson(
    val foo: String,
)
