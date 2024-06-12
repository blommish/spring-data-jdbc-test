package no.blommish

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.convert.CustomConversions
import org.springframework.data.relational.core.dialect.Dialect
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

val listAppender = ListAppender<ILoggingEvent>()

class LoggerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val logger = LoggerFactory.getLogger(CustomConversions::class.java)

        logger.info("Initiating logger")
        listAppender.start()
        (logger as ch.qos.logback.classic.Logger).addAppender(listAppender)
    }
}

@ExtendWith(SpringExtension::class)
@ContextConfiguration(initializers = [LoggerInitializer::class, DbContainerInitializer::class])
@SpringBootTest(classes = [App::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BarRepositoryTest {
    @Autowired
    lateinit var barRepository: BarRepository
    @Autowired
    lateinit var dialect : Dialect
    @Test
    fun `saving and getting db data`() {
        val bar = barRepository.save(Bar(data = MyJson("asd")))
        val reloaded = barRepository.findByIdOrNull(bar.id)

        assertThat(reloaded).isEqualTo(bar)


        assertThat(listAppender.list).isEmpty()
    }
}
