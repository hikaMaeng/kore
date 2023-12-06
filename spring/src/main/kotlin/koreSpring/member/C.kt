@file:Suppress("JpaQueryApiInspection")

package koreSpring.member

import kore.vo.VO
import kore.vo.field.value.int
import kore.vo.field.value.string
import kore.vql.select
import kore.vql.sql.r2dbcSelect
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody


class Member:VO(){
    companion object:()->Member by ::Member
    val member_rowid by int
    val username by string
}

@Controller
class C {
    @Autowired
    lateinit var client:DatabaseClient

    @ResponseBody
    @GetMapping("/member")
    suspend fun member():Flow<String>{
        return Member.select(Member){member, rs->
            select{
                member{::member_rowid}
                member{::username}
            }
        }.r2dbcSelect(client)
    }
//    suspend fun member(): Flow<MutableMap<String, Any>> = client.sql("select * from member where username = :name")
//        .bind("name", "hika").fetch().flow()
}