@file:Suppress("JpaQueryApiInspection")

package koreSpring.controllers

import kore.vosn.toVOSN
import kore.vql.select
import kore.vql.sql.r2dbcSelect
import koreSpring.recordset.RSclub
import koreSpring.recordset.RSmemberClub
import koreSpring.tables.Club
import koreSpring.tables.ClubMember
import koreSpring.tables.Member
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody


@Controller
class Cmember {
    @Autowired
    lateinit var client:DatabaseClient

    @ResponseBody
    @GetMapping("/member/list")
    suspend fun list():Flow<String>{
        return Member.select(RSmemberClub){ member, rs->
            select{
                member{::member_rowid} put rs{::member_rowid}
                member{::username} put rs{::username}
                Club.select(RSclub){club, rsClub->
                    val c0 = ClubMember{::club_rowid} join club{::club_rowid}
                    val c1 = Member{::member_rowid} join c0{::member_rowid}
                    select {
                        club{::club_rowid} put rsClub{::club_rowid}
                        club{::title} put rsClub{::title}
                        c0{::member_rowid} put rsClub{::member_rowid}
                    }
                    shape {
                        rsClub{::member_rowid} in rs{::member_rowid}
                    }
                } put rs.shape{::userclub}
            }
        }.r2dbcSelect(client).map{
            it.toVOSN()({"$it"}) + "|--${Thread.currentThread().isVirtual}--|"
        }
        //.insertFirst()
        //.addLast()
        //1|hika|1|축구부|1||2|당구부|1||2|jidolstar|3|농구부|2||4|장기부|2|@||
        //1|hika|1|축구부|1||2|당구부|1|@||2|jidolstar|3|농구부|2||4|장기부|2|@|@|
        // class Result:VO(){ val data by voList}
    }
}
