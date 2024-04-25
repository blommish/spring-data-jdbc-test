package no.blommish

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.postgresql.util.PGobject
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

val objectMapper =
    ObjectMapper()
        .registerKotlinModule()

@Configuration
@EnableJdbcAuditing
@EnableJdbcRepositories("no.blommish")
@SpringBootConfiguration // Appconfig
class DatabaseConfiguration : AbstractJdbcConfiguration() {
    @Bean
    override fun jdbcCustomConversions(): JdbcCustomConversions {
        return JdbcCustomConversions(
            listOf(
                PGobjectToMyJsonConverter(),
                MyJsonToPGobjectConverter(),
            ),
        )
    }

    @ReadingConverter
    class PGobjectToMyJsonConverter : Converter<PGobject, MyJson?> {
        override fun convert(pGobject: PGobject): MyJson? {
            return pGobject.value?.let { objectMapper.readValue(it) }
        }
    }

    @WritingConverter
    class MyJsonToPGobjectConverter : Converter<MyJson, PGobject> {
        override fun convert(obj: MyJson): PGobject =
            PGobject().apply {
                type = "json"
                value = objectMapper.writeValueAsString(obj)
            }
    }
}
