//package vo
//
//import ein2b.core.entity.eEntity
//import ein2b.core.log.log
//import kore.vo.VO
//import kore.vo.field.value.string
//import kore.vo.field.vo
//import kore.vo.field.voList
//import kore.vosn.fromVOSN
//import kore.vosn.toVOSN
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.fail
//
//class Test4{
//    class MdlAirport:VO(){
//        var rowid by string
//        var code by string
//        var name by string
//    }
//    class ClientMdlInputData():VO(){
//        var airport by vo(::MdlAirport)
//        var list by voList(::MdlAirport)
//    }
//
//    fun createEnt() = ClientMdlInputData().also {
//        it.airport = MdlAirport().also{
//            it.rowid = "10"
//            it.code = "ICN0"
//            it.name = "Incheon International Airport0"
//        }
//        it.list = mutableListOf(
//            MdlAirport().also {
//                it.rowid = "1"
//                it.code = "ICN"
//                it.name = "Incheon International Airport"
//            },
//            MdlAirport().also {
//                it.rowid = "2"
//                it.code = "KAG"
//                it.name = "Gangneung"
//            },
//            MdlAirport().also {
//                it.rowid = "3"
//                it.code = "CHN"
//                it.name = "Jeonju"
//            },
//            MdlAirport().also {
//                it.rowid = "4"
//                it.code = "RSU"
//                it.name = "Yeosu Airport"
//            },
//            MdlAirport().also {
//                it.rowid = "5"
//                it.code = "CJU"
//                it.name = "Jeju International Airport"
//            },
//            MdlAirport().also {
//                it.rowid = "6"
//                it.code = "KWJ"
//                it.name = "Gwangju Airport"
//            },
//            MdlAirport().also {
//                it.rowid = "7"
//                it.code = "GMP"
//                it.name = "Gimpo International Airport"
//            }
//        )
//    }
//
//    @Test
//    fun create(){
//        val a = createEnt()
//        testLog("0", a.airport.rowid, "10")
//        testLog("1", a.list[0].rowid, "1")
//        val s = a.toVOSN()
//        val a2 = ClientMdlInputData().fromVOSN(s()!!)
//        val s2 = a2()!!.toVOSN()
//        assertEquals(s(),s2(),"원본 인코딩과 디코딩후 인코딩한 결과가 다름")
//    }
//}