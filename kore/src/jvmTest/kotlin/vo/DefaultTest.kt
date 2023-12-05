//package vo
//
//import DefaultTest.GenericEntity.Ent1
//import DefaultTest.GenericEntity.Ent2
//import ein2b.core.entity.eEntity
//import ein2b.core.entity.eSlowEntity
//import kotlin.reflect.KClass
//import kotlin.test.*
//
//class DefaultTest {
//    class EntityMapTest<T:eEntity>(cls:KClass<T>,factory:()->T): eSlowEntity() {
//        val emap = entityMap({IntEnt()})
//        val tmap = entityMap(cls,factory)
//    }
//    class IntEnt(i:Int=0): eEntity() {
//        var x by int{default(i)}
//        var y by int{default(2*i)}
//    }
//    @Test
//    fun intDefault() {
//        val x1 = IntEnt(10)
//        val y1 = IntEnt(20)
//
//        val xs1 = x1.stringify()
//        val ys1 = y1.stringify()
//
//        val x2 = eEntity.parse(IntEnt(0),xs1){
//            println("Error: ${it.id} ${it.message}")
//            if(it.result!=null) {
//                println(it.result?.joinToString())
//            }
//        }
//        val y2 = eEntity.parse(IntEnt(0),ys1){
//            println("Error: ${it.id} ${it.message}")
//            if(it.result!=null) {
//                println(it?.result?.joinToString())
//            }
//        }
//
//        val xs2 = x2?.stringify()
//        val ys2 = y2?.stringify()
//
//        assertEquals(xs1,xs2)
//        assertEquals(ys1,ys2)
//    }
//
//    class IntListEnt(vararg i:Int): eEntity() {
//        var x by intList{default{mutableListOf(*i.toTypedArray())}}
//        var y by intList{default{mutableListOf(*i.map{it*2}.toTypedArray())}}
//    }
//    @Test
//    fun intListDefault() {
//        val x1 = IntListEnt(10,20,30,40)
//        val y1 = IntListEnt(20,30,40,50)
//
//        val xs1 = x1.stringify()
//        val ys1 = y1.stringify()
//
//        val x2 = eEntity.parse(IntListEnt(0),xs1){
//            println("Error: ${it.id} ${it.message}")
//            if(it.result!=null) {
//                println(it.result?.joinToString())
//            }
//        }
//        val y2 = eEntity.parse(IntListEnt(),ys1){
//            println("Error: ${it.id} ${it.message}")
//            if(it.result!=null) {
//                println(it?.result?.joinToString())
//            }
//        }
//
//        val xs2 = x2?.stringify()
//        val ys2 = y2?.stringify()
//
//        assertEquals(xs1,xs2)
//        assertEquals(ys1,ys2)
//    }
//
//    class Generic<E:eEntity>(cls:KClass<E>,factory:()->E): eSlowEntity() {
//        var e by entity(cls,factory)
//    }
//
//    class GenericEntity<E:eEntity>(cls:KClass<E>, e:()->E): eSlowEntity() {
//        var x by entity(cls,e){default{e()}}
//        var y by entity(cls,e){default{e()}}
//
//        class Ent1:eEntity() {
//            var x by int{default(0)}
//            var y by string{default("this is string")}
//        }
//        class Ent2:eEntity() {
//            var z by int { default(0) }
//            var u by utc { default(defaultUtc) }
//        }
//    }
//
//    @Test
//    fun GenericEntityDefault() {
//        val x1 = GenericEntity(Ent1::class,::Ent1) // x,y가 모두 {"x":0,"y":"this is string"}
//        val y1 = GenericEntity(Ent2::class,::Ent2) // x,y가 모두 {"z":0,"u":"1900-01-01T00:00:00.000Z"}
//
//        assertTrue(x1._defaultMap?.get(0)?.value is Ent1)
//        assertTrue(x1._defaultMap?.get(1)?.value is Ent1)
//        assertEquals(x1._defaultMap?.size,2)
//        assertTrue(x1.x is Ent1)
//        assertTrue(x1.y is Ent1)
//        assertEquals(x1._values?.size,2)
//
//        assertTrue(y1._defaultMap?.get(0)?.value is Ent2)
//        assertTrue(y1._defaultMap?.get(1)?.value is Ent2)
//        assertEquals(y1._defaultMap?.size,2)
//        assertTrue(y1.x is Ent2)
//        assertTrue(y1.y is Ent2)
//        assertEquals(y1._values?.size,2)
//
//        assertEquals(Ent1().stringify(), x1.x.stringify())
//        assertEquals(Ent1().stringify(), x1.y.stringify())
//        assertEquals(Ent2().stringify(), y1.x.stringify())
//        assertEquals(Ent2().stringify(), y1.y.stringify())
//
//        val xs1 = x1.stringify()
//        val ys1 = y1.stringify()
//
//        assertTrue( xs1.contains(Ent1().stringify()) )
//        assertTrue( ys1.contains(Ent2().stringify()) )
//
//        val x2 = eEntity.parse(GenericEntity(Ent1::class){Ent1().also{it.x = 10;it.y = "" }},xs1){
//            println("Error: ${it.id} ${it.message}")
//            if(it.result!=null) {
//                println(it.result?.joinToString())
//            }
//        }?:fail("x2 parse error")
//        val y2 = eEntity.parse(GenericEntity(Ent2::class){Ent2().also{it.z=123456}},ys1){
//            println("Error: ${it.id} ${it.message}")
//            if(it.result!=null) {
//                println(it?.result?.joinToString())
//            }
//        }?:fail("y2 parse error")
//
//        assertTrue(x2._defaultMap?.get(0)?.value is Ent1)
//        assertTrue(x2._defaultMap?.get(1)?.value is Ent1)
//        assertEquals(x2._defaultMap?.size,2)
//        assertTrue(x2.x is Ent1)
//        assertTrue(x2.y is Ent1)
//        assertEquals(x2._values?.size,2)
//
//        assertTrue(y2._defaultMap?.get(0)?.value is Ent2)
//        assertTrue(y2._defaultMap?.get(1)?.value is Ent2)
//        assertEquals(y2._defaultMap?.size,2)
//        assertTrue(y2.x is Ent2)
//        assertTrue(y2.y is Ent2)
//        assertEquals(y2._values?.size,2)
//
//        assertEquals(Ent1().stringify(), x2.x.stringify())
//        assertEquals(Ent1().stringify(), x2.y.stringify())
//        assertEquals(Ent2().stringify(), y2.x.stringify())
//        assertEquals(Ent2().stringify(), y2.y.stringify())
//
//        val xs2 = x2?.stringify()
//        val ys2 = y2?.stringify()
//
//        assertEquals(xs1,xs2)
//        assertEquals(ys1,ys2)
//    }
//}
