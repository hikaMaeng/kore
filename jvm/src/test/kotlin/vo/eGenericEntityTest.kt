//package vo
//
//import ein2b.core.entity.eEntity
//import kotlin.properties.PropertyDelegateProvider
//import kotlin.properties.ReadWriteProperty
//import kotlin.reflect.KProperty
//
//
//interface IEntity
//
//class MEntity: IEntity, ReadWriteProperty<MEntity, Any>{
//    override fun getValue(thisRef: MEntity, property: KProperty<*>): Any {
//        TODO("Not yet implemented")
//    }
//
//    override fun setValue(thisRef: MEntity, property: KProperty<*>, value: Any) {
//        TODO("Not yet implemented")
//    }
//}
//
//class MGenericEntity<E1: IEntity>: IEntity, ReadWriteProperty<MGenericEntity<E1>,Any>{
//    override fun getValue(thisRef: MGenericEntity<E1>, property: KProperty<*>): Any {
//        TODO("Not yet implemented")
//    }
//
//    override fun setValue(thisRef: MGenericEntity<E1>, property: KProperty<*>, value: Any) {
//        TODO("Not yet implemented")
//    }
//}
//
//class MGenericEntity2<E1: IEntity, E2: IEntity>: IEntity, ReadWriteProperty<MGenericEntity2<E1,E2>,Any>{
//    override fun getValue(thisRef: MGenericEntity2<E1,E2>, property: KProperty<*>): Any {
//        TODO("Not yet implemented")
//    }
//
//    override fun setValue(thisRef: MGenericEntity2<E1,E2>, property: KProperty<*>, value: Any) {
//        TODO("Not yet implemented")
//    }
//}