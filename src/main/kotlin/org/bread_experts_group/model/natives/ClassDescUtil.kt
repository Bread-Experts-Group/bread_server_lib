package org.bread_experts_group.model.natives

import java.lang.constant.ClassDesc
import kotlin.reflect.KClass
import kotlin.reflect.KType

val KClass<*>.desc: ClassDesc
	get() = this.java.desc
val Class<*>.desc: ClassDesc
	get() = ClassDesc.ofDescriptor(this.descriptorString())

val KType.clazz: KClass<*>
	get() = this.classifier as KClass<*>