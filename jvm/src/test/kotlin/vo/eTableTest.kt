//package vo
//
//import ein2b.core.entity.Error
//import ein2b.core.entity.eEntity
//import ein2b.core.entity.indexer.Indexer
//import ein2b.core.entity.task.Store
//import kotlin.properties.ReadWriteProperty
//import kotlin.reflect.KProperty
//import kotlin.test.Test
//
//abstract class eTable<T:eEntity>: ReadWriteProperty<eTable<T>, Any>{
//    override fun getValue(thisRef:eTable<T>, property: KProperty<*>):Any{
//        return this
//    }
//
//    override fun setValue(thisRef:eTable<T>, property: KProperty<*>, value:Any){
//    }
//}
//object UserModelTable: eTable<UserModel>() {
//}
//
//class UserModel: eEntity() {
//    var name by string
//    var birthdate by string
//    var email by string
//    var regdate by utc
//}
//
//class TableTest {
//    @Test
//    fun create() {
//    }
//}