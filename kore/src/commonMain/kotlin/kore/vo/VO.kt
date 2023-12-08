@file:Suppress("NOTHING_TO_INLINE", "PropertyName", "UNCHECKED_CAST", "PARAMETER_NAME_CHANGED_ON_OVERRIDE",
               "ObjectPropertyName"
)

package kore.vo

import kore.vo.field.*
import kore.vo.task.Task
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
abstract class VO(@PublishedApi internal val useInstanceField:Boolean = false){ /** 인스턴스에서 필드 정보를 기록할지 여부 */
    companion object{
        @PublishedApi internal val _voTasks:HashMap<String, HashMap<String, Task>> = hashMapOf() /** 전역 태스크 저장소 */
        @PublishedApi internal val _voFields:HashMap<String, HashMap<String, Field<*>>> = hashMapOf() /** 전역 필드 저장소 */
        @PublishedApi internal val _voKeys:HashMap<String, ArrayList<String>> = hashMapOf() /** 전역 필드이름 및 순번 저장소 */
        /** 파서 등에서 해당 VO의 필드이름 리스트를 얻음 */
        inline fun keys(type:VO):List<String>? = _voKeys[type.voID]
        inline fun keys(type:KClass<out VO>):List<String>? = _voKeys[type.qualifiedName]
        private val _delegate:ReadWriteProperty<VO, Any> = object:ReadWriteProperty<VO, Any>{
            override fun getValue(vo:VO, property:KProperty<*>):Any = vo[property.name] ?: Task.NoDefault(vo, property.name).terminate()
            override fun setValue(vo:VO, property:KProperty<*>, value: Any){ vo[property.name] = value }
        }
        @PublishedApi internal val _delegateProvider:PropertyDelegateProvider<VO, ReadWriteProperty<VO, Any>>
        = PropertyDelegateProvider{vo, prop ->
            val key:String = prop.name
            vo.values[key] = null
            vo.__keys__?.let{
                it.add(key)
                _voKeys[vo.voID] = it
                if(!vo.useInstanceField){
                    _voFields.getOrPut(vo.voID, ::hashMapOf)[key] = vo.__field__!!
//                    println("firstRun VO ${vo::class.simpleName} task:${vo.__task__}")
                    vo.__task__?.let{_voTasks.getOrPut(vo.voID, ::hashMapOf)[key] = it}
//                    println("firstRun key ${key} :${_voTasks.getOrPut(vo.voID, ::hashMapOf)[key]}")
                }
            }
            if(vo.useInstanceField){
                vo._fields!![key] = vo.__field__!!
                vo.__task__?.let{vo._tasks!![key] = it}
            }
            vo.__field__ = null
            vo.__task__ = null
            _delegate
        }
    }
    /** 실제 값을 보관하는 저장소 */
    @PublishedApi internal var _values:MutableMap<String, Any?>? = null
    @PublishedApi internal inline val values:MutableMap<String, Any?> get() = _values ?: hashMapOf<String, Any?>().also{ _values = it }
    inline val props:Map<String, Any?> get() = values /** 외부에 표출되는 저장소 */
    override fun toString():String = "${super.toString()}-${values.toList().joinToString{(k,v)->"$k:$v"}}"
    /** 속성 getter, setter*/
    inline operator fun set(key:String, value:Any){values[key] = getTask(key)?.setFold(this, key, value) ?: value}
    inline operator fun get(key:String):Any? = getTask(key)?.let{
//        println("vo getTask, $key: ${it.getDefault(this, key)}, value:${values[key]}")
        (values[key] ?: it.getDefault(this, key))?.let{v->it.getFold(this, key, v)}
    } ?: run{
//        println("vo no getTask, $key")
        values[key]
    }
    /** 전역 컨테이너용 키 */
    @PublishedApi internal var _id:String? = null
    inline val voID:String get() = _id ?: this::class.qualifiedName!!.also {_id = it}
    /** 인스턴스 필드 저장소를 쓸 경우 */
    @PublishedApi internal val _fields:HashMap<String, Field<*>>? = if(useInstanceField) hashMapOf() else null
    @PublishedApi internal val _tasks:HashMap<String, Task>? = if(useInstanceField) hashMapOf() else null
    /** 태스크와 필드의 편의 getter */
    @PublishedApi internal inline fun getTask(name:String):Task? = getTasks()?.get(name)
    @PublishedApi internal inline fun getTasks():HashMap<String, Task>? = _tasks ?: _voTasks[voID]
    @PublishedApi internal inline fun getFields():HashMap<String, Field<*>>? = _fields ?: _voFields[voID]
    /** lazy 필드 매칭용 인덱서 */
    @PublishedApi internal var __index__ = -1 /** 델리게이터 정의 순번. -1은 아직 한번도 정의되지 않은 상태*/
    @PublishedApi internal var __field__:Field<*>? = null /**프로바이더에게 넘겨줄 필드정보 */
    @PublishedApi internal var __task__:Task? = null /** 프로바이더에게 넘겨줄 태스크 정보 */
    @PublishedApi internal var __keys__:ArrayList<String>? = null /**프로바이더에게 넘겨줄 델리게이터 선언 순번별 속성명 */
    /** 표준 델리게이터 생성기 */
    inline fun <TASK:Task, VALUE:Any> delegate(field:Field<VALUE>, block:TASK.()->Unit, task:()->TASK):Prop<VALUE>{
        if(useInstanceField || (__index__ == -1 && voID !in _voKeys) || (__index__ > -1 && __keys__ != null)) task().also{__task__ = it}.block()
        return delegate(field)
    }
    inline fun <TASK:Task, VALUE:Any> delegate(field:Field<VALUE>, block:()->TASK):Prop<VALUE>{
        if(useInstanceField || (__index__ == -1 && voID !in _voKeys) || (__index__ > -1 && __keys__ != null)) __task__ = block()
        return delegate(field)
    }
    inline fun <VALUE:Any> delegate(field:Field<VALUE>):Prop<VALUE>{
        if(__index__ == -1 && voID !in _voKeys) __keys__ = arrayListOf()
        __index__++
        __field__ = field
        return _delegateProvider as Prop<VALUE>
    }
}