@file:Suppress("JpaQueryApiInspection")

package koreSpring.member

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class C {
    @Autowired
    lateinit var client:DatabaseClient
    @GetMapping("/member")
    suspend fun member(): Flow<MutableMap<String, Any>> = client.sql("select * from member where username = :name")
        .bind("name", "hika").fetch().flow()
}