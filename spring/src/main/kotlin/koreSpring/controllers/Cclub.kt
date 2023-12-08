package koreSpring.controllers

import kore.vosn.toVOSN
import kore.vql.select
import kore.vql.sql.r2dbcSelect
import koreSpring.tables.Club
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class Cclub {
    @Autowired
    lateinit var client: DatabaseClient

    @ResponseBody
    @GetMapping("/club/list")
    suspend fun list(): Flow<String> {
        return Club.select(Club) { club, rs ->
            select {
                club { ::club_rowid }
                club { ::title }
            }
        }.r2dbcSelect(client).map { it.toVOSN()({ "$it" }) }
    }
}