package koreSpring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import org.springframework.boot.runApplication


@SpringBootApplication(exclude = [
    SqlInitializationAutoConfiguration::class,
    SecurityAutoConfiguration::class,
    DataSourceAutoConfiguration::class,
    DataSourceTransactionManagerAutoConfiguration::class,
    HibernateJpaAutoConfiguration::class,
    TransactionAutoConfiguration::class,
    SpringDataWebAutoConfiguration::class,
    MongoAutoConfiguration::class
])
class Application

fun main(args:Array<String>) {
    runApplication<Application>(*args)
}