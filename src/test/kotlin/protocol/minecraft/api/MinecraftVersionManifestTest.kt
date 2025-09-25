package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.io.reader.JSONReader
import org.junit.jupiter.api.Test
import java.lang.classfile.*
import java.lang.classfile.Annotation
import java.lang.classfile.attribute.*
import java.lang.classfile.constantpool.ClassEntry
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.net.URI
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.zip.ZipInputStream
import kotlin.io.path.Path
import kotlin.jvm.optionals.getOrNull

class MinecraftVersionManifestTest {
	data class ObfuscatedField(
		val name: String,
		val type: ClassDesc
	)

	data class ObfuscatedMethod(
		val name: String,
		val descriptors: MutableList<MethodTypeDesc>
	)

	data class ObfuscatedClass(
		val name: ClassDesc,
		val fields: MutableMap<String, ObfuscatedField>,
		val methods: MutableMap<String, ObfuscatedMethod>
	)

	@Test
	fun decode() {
		val manifest = run {
			val input = URI("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json")
				.toURL().openStream()
			val jsonReader = JSONReader(input)
			MinecraftVersionManifest.layout.read(jsonReader)
		}
		val potato = run {
			val potatoInput = manifest.versions.first { it.id == "24w14potato" }.uri
				.toURL().openStream()
			val jsonReader = JSONReader(potatoInput)
			MinecraftClient.layout.read(jsonReader)
		}
		val mappings = potato.downloads.clientMappings!!.uri.toURL().openStream().readAllBytes().decodeToString()
			.lines()
			.filter { it.isNotBlank() && it[0] != '#' }
		val mapsName = mutableMapOf<String, ObfuscatedClass>()
		var lastEntry: ObfuscatedClass? = null
		fun obfConvert(name: String): ClassDesc {
			val dimensionless = name.replace("[]", "")
			val dimensions = (name.length - dimensionless.length) / 2
			val desc = when (dimensionless) {
				"void" -> ConstantDescs.CD_void
				"int" -> ConstantDescs.CD_int
				"byte" -> ConstantDescs.CD_byte
				"short" -> ConstantDescs.CD_short
				"long" -> ConstantDescs.CD_long
				"boolean" -> ConstantDescs.CD_boolean
				"char" -> ConstantDescs.CD_char
				"float" -> ConstantDescs.CD_float
				"double" -> ConstantDescs.CD_double
				else -> ClassDesc.of(dimensionless)
			}
			return if (dimensions > 0) desc.arrayType(dimensions) else desc
		}
		for (mapping in mappings) {
			if (mapping.endsWith(':')) {
				val separator = mapping.indexOf(" -> ")
				val obfuscated = mapping.substring(separator + 4, mapping.length - 1)
				val newClass = ObfuscatedClass(
					ClassDesc.of(mapping.substring(0, separator)),
					mutableMapOf(), mutableMapOf()
				)
				mapsName[obfuscated] = newClass
				lastEntry = newClass
			} else if (lastEntry != null) {
				val separator = mapping.indexOf(" -> ")
				if (separator == -1) continue
				val named = mapping.substring(4, separator)
				val (left, right) = named.split(' ', limit = 2)
				val paramStart = right.indexOf('(')
				if (paramStart != -1) {
					val method = lastEntry.methods.getOrPut(mapping.substring(separator + 4, mapping.length)) {
						ObfuscatedMethod(right.substring(0, paramStart), mutableListOf())
					}
					method.descriptors.add(
						MethodTypeDesc.of(
							obfConvert(left.substringAfterLast(':')), // TODO save line #
							right.substring(paramStart + 1, right.length - 1).split(',').filter {
								it.isNotBlank()
							}.map {
								obfConvert(it)
							}
						)
					)
				} else {
					lastEntry.fields[mapping.substring(separator + 4, mapping.length)] = ObfuscatedField(
						right,
						obfConvert(left)
					)
				}
			}
		}
		val unmapped = Files.createDirectories(Path("./${potato.id}"))
		val zip = ZipInputStream(potato.downloads.client.uri.toURL().openStream())
		val data = ByteBuffer.allocate(8192)
		val markForRemap = mutableSetOf<Path>()
		fun ClassDesc.qualifiedName() = this.packageName().let {
			if (it.isNotBlank()) "$it." else ""
		} + this.displayName()

		fun ClassDesc.internalName() = this.qualifiedName().replace('.', '/')
		do {
			val entry = zip.nextEntry ?: break
			if (entry.isDirectory) continue
			val classIndex = entry.name.indexOf(".class")
			val className = if (classIndex != -1) mapsName[entry.name.substring(0, classIndex)] else null
			val file = if (className != null) unmapped.resolve(
				Path(className.name.internalName() + ".class")
			) else unmapped.resolve(entry.name)
			Files.createDirectories(file.parent)
			if (classIndex != -1) markForRemap.add(file)
			val fileData = Files.newByteChannel(
				file,
				StandardOpenOption.READ,
				StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE
			)
			while (true) {
				val count = zip.read(
					data.array(),
					0, 8192
				)
				if (count == -1) break
				data.position(0)
				data.limit(count)
				fileData.write(data)
			}
			fileData.close()
		} while (true)
		val latch = CountDownLatch(markForRemap.size)
		fun String.internalToDotted() = this.replace('/', '.')
		fun ClassEntry.toDotted() = this.asInternalName().internalToDotted()
		fun ObfuscatedMethod.findDescriptor(matching: MethodTypeDesc): MethodTypeDesc? {
			val obfDescriptor = matching.let {
				listOf(it.returnType()) + it.parameterList()
			}
			return this.descriptors.firstOrNull {
				val localDescriptor = listOf(it.returnType()) + it.parameterList()
				if (localDescriptor.size != obfDescriptor.size) return@firstOrNull false
				var checked = 0
				for (descriptor in localDescriptor) {
					val remoteString = obfDescriptor[checked].descriptorString()
					val descriptorString = descriptor.descriptorString()
					if (descriptorString == remoteString) checked++
					else {
						val (checkRemote, checkDescriptor) = if (
							descriptorString[0] == '[' && remoteString[0] == '['
						) {
							val remoteA = remoteString.lastIndexOf('[')
							if (remoteA != descriptorString.lastIndexOf('[')) break
							val remoteCut = remoteString.substring(remoteA + 1)
							val descriptorCut = descriptorString.substring(remoteA + 1)
							if (remoteCut[0] != 'L' || descriptorCut[0] != 'L') break
							remoteCut to descriptorCut
						} else if (
							descriptorString[0] == 'L' && remoteString[0] == 'L'
						) remoteString to descriptorString
						else break
						val remoteDesc = mapsName[
							checkRemote.substring(1, checkRemote.length - 1).replace('/', '.')
						] ?: break
						if (checkDescriptor == remoteDesc.name.descriptorString()) checked++
						else break
					}
				}
				checked == obfDescriptor.size
			}
		}
		for (remap in markForRemap) Thread.ofVirtual().start {
			val cf = ClassFile.of(
				ClassFile.StackMapsOption.DROP_STACK_MAPS
			)
			val classModel = cf.parse(remap)
			val thisClass = mapsName[classModel.thisClass().toDotted()]
			if (thisClass == null) {
				latch.countDown()
				return@start
			}
			Files.write(
				remap,
				cf.build(thisClass.name) { clazz ->
					clazz.withVersion(classModel.majorVersion(), classModel.minorVersion())
					classModel.superclass().ifPresent {
						clazz.withSuperclass(mapsName[it.toDotted()]?.name ?: it.asSymbol())
					}
					clazz.withInterfaceSymbols(
						classModel.interfaces().map { mapsName[it.toDotted()]?.name ?: it.asSymbol() }
					)
					clazz.withFlags(classModel.flags().flagsMask())

					fun Annotation.convert(): Annotation {
						val annotationClassName = this.className()
						val annotationClass = mapsName[
							annotationClassName.substring(1, annotationClassName.length - 1)
						] ?: return this
						return Annotation.of(
							annotationClass.name,
							this.elements()
						)
					}

					fun List<Annotation>.convert(): List<Annotation> = this.mapNotNull { it.convert() }
					fun List<TypeAnnotation>.convert(): List<TypeAnnotation> = this.mapNotNull {
						TypeAnnotation.of(
							it.targetInfo(),
							it.targetPath(),
							it.annotation().convert()
						)
					}

					for (fieldModel in classModel.fields()) {
						val deObfField = thisClass.fields[fieldModel.fieldName().stringValue()]
						if (deObfField == null) {
							println("!!! Non-deobfuscatable field ${fieldModel.fieldName().stringValue()}")
							continue
						}
						clazz.withField(
							deObfField.name,
							deObfField.type
						) { field ->
							field.withFlags(fieldModel.flags().flagsMask())
							for (attribute in fieldModel.attributes()) when (attribute) {
								is SyntheticAttribute -> field.with(attribute)
								is DeprecatedAttribute -> field.with(attribute)
								is ConstantValueAttribute -> field.with(attribute)

								is RuntimeVisibleAnnotationsAttribute -> field.with(
									RuntimeVisibleAnnotationsAttribute.of(
										attribute.annotations().convert()
									)
								)

								is RuntimeInvisibleAnnotationsAttribute -> field.with(
									RuntimeInvisibleAnnotationsAttribute.of(
										attribute.annotations().convert()
									)
								)

								is RuntimeVisibleTypeAnnotationsAttribute -> clazz.with(
									RuntimeVisibleTypeAnnotationsAttribute.of(
										attribute.annotations().convert()
									)
								)

								is RuntimeInvisibleTypeAnnotationsAttribute -> clazz.with(
									RuntimeInvisibleTypeAnnotationsAttribute.of(
										attribute.annotations().convert()
									)
								)

								else -> {}
							}
						}
					}
					for (methodModel in classModel.methods()) {
						val deObfMethod = thisClass.methods[methodModel.methodName().stringValue()]
						if (deObfMethod == null) {
							println("!!! Non-deobfuscatable method ${methodModel.methodName().stringValue()}")
							continue
						}
						val deObfDescriptor = deObfMethod.findDescriptor(methodModel.methodTypeSymbol())
						if (deObfDescriptor == null) {
							println("!!! Non-deobfuscatable method descriptor ${deObfMethod.name} / ${methodModel.methodTypeSymbol()}")
						}
						clazz.withMethod(
							deObfMethod.name,
							deObfDescriptor,
							methodModel.flags().flagsMask()
						) { method ->
							for (attribute in methodModel.attributes()) when (attribute) {
								is SyntheticAttribute -> method.with(attribute)
								is DeprecatedAttribute -> method.with(attribute)
								is AnnotationDefaultAttribute -> method.with(attribute)
								is MethodParametersAttribute -> method.with(attribute)

								is RuntimeVisibleAnnotationsAttribute -> method.with(
									RuntimeVisibleAnnotationsAttribute.of(
										attribute.annotations().convert()
									)
								)

								is RuntimeInvisibleAnnotationsAttribute -> method.with(
									RuntimeInvisibleAnnotationsAttribute.of(
										attribute.annotations().convert()
									)
								)

								is RuntimeVisibleParameterAnnotationsAttribute -> method.with(
									RuntimeVisibleParameterAnnotationsAttribute.of(
										attribute.parameterAnnotations().map { pA ->
											pA.convert()
										}
									)
								)

								is RuntimeInvisibleParameterAnnotationsAttribute -> method.with(
									RuntimeInvisibleParameterAnnotationsAttribute.of(
										attribute.parameterAnnotations().map { pA ->
											pA.convert()
										}
									)
								)

								is RuntimeVisibleTypeAnnotationsAttribute -> clazz.with(
									RuntimeVisibleTypeAnnotationsAttribute.of(
										attribute.annotations().convert()
									)
								)

								is RuntimeInvisibleTypeAnnotationsAttribute -> clazz.with(
									RuntimeInvisibleTypeAnnotationsAttribute.of(
										attribute.annotations().convert()
									)
								)

								is ExceptionsAttribute -> method.with(
									ExceptionsAttribute.of(
										attribute.exceptions().map {
											val exClass = mapsName[it.toDotted()] ?: return@map it
											clazz.constantPool().classEntry(exClass.name)
										}
									)
								)

								else -> {}
							}
						}
					}
					for (attribute in classModel.attributes()) when (attribute) {
						is SyntheticAttribute, is DeprecatedAttribute, is SourceFileAttribute,
						is SourceDebugExtensionAttribute -> clazz.with(attribute)

						is InnerClassesAttribute -> clazz.with(
							InnerClassesAttribute.of(
								attribute.classes().map { innerClass ->
									val deObfIC = mapsName[innerClass.innerClass().toDotted()] ?: return@map innerClass
									InnerClassInfo.of(
										deObfIC.name,
										Optional.ofNullable(
											innerClass.outerClass().getOrNull()?.let { outerClass ->
												mapsName[
													outerClass.toDotted()
												]?.name ?: return@map innerClass
											}
										),
										Optional.ofNullable(
											innerClass.innerName().getOrNull()?.let {
												deObfIC.name.qualifiedName().substringAfterLast('$')
											}
										),
										innerClass.flagsMask()
									)
								}
							)
						)

						is RecordAttribute -> clazz.with(
							RecordAttribute.of(
								attribute.components().map { recordComponent ->
									val field = thisClass.fields[recordComponent.name().stringValue()]
									if (field == null) return@map recordComponent
									RecordComponentInfo.of(
										field.name,
										field.type,
										recordComponent.attributes().mapNotNull { recordAttribute ->
											when (recordAttribute) {
												is RuntimeVisibleAnnotationsAttribute ->
													RuntimeVisibleAnnotationsAttribute.of(
														recordAttribute.annotations().convert()
													)

												is RuntimeInvisibleAnnotationsAttribute ->
													RuntimeInvisibleAnnotationsAttribute.of(
														recordAttribute.annotations().convert()
													)

												is RuntimeVisibleTypeAnnotationsAttribute ->
													RuntimeVisibleTypeAnnotationsAttribute.of(
														recordAttribute.annotations().convert()
													)

												is RuntimeInvisibleTypeAnnotationsAttribute ->
													RuntimeInvisibleTypeAnnotationsAttribute.of(
														recordAttribute.annotations().convert()
													)

												else -> null
											}
										}
									)
								}
							)
						)

						is EnclosingMethodAttribute -> {
							val methodClazz = attribute.enclosingClass().toDotted().let {
								mapsName[it] ?: continue
							}
							val method = attribute.enclosingMethodName().getOrNull()?.let {
								val method = methodClazz.methods[it.stringValue()] ?: return@let null
								method.name to attribute.enclosingMethodTypeSymbol().getOrNull()?.let { desc ->
									method.findDescriptor(desc)
								}
							}
							clazz.with(
								EnclosingMethodAttribute.of(
									methodClazz.name,
									Optional.ofNullable(method?.first),
									Optional.ofNullable(method?.second)
								)
							)
						}

						is RuntimeVisibleAnnotationsAttribute -> clazz.with(
							RuntimeVisibleAnnotationsAttribute.of(
								attribute.annotations().convert()
							)
						)

						is RuntimeInvisibleAnnotationsAttribute -> clazz.with(
							RuntimeInvisibleAnnotationsAttribute.of(
								attribute.annotations().convert()
							)
						)

						is RuntimeVisibleTypeAnnotationsAttribute -> clazz.with(
							RuntimeVisibleTypeAnnotationsAttribute.of(
								attribute.annotations().convert()
							)
						)

						is RuntimeInvisibleTypeAnnotationsAttribute -> clazz.with(
							RuntimeInvisibleTypeAnnotationsAttribute.of(
								attribute.annotations().convert()
							)
						)

						is PermittedSubclassesAttribute -> clazz.with(
							PermittedSubclassesAttribute.of(
								attribute.permittedSubclasses().mapNotNull {
									val desc = mapsName[it.toDotted()]?.name ?: return@mapNotNull null
									clazz.constantPool().classEntry(desc)
								}
							)
						)

						is NestHostAttribute -> mapsName[attribute.nestHost().toDotted()]?.name?.let {
							clazz.with(
								NestHostAttribute.of(clazz.constantPool().classEntry(it))
							)
						}

						is NestMembersAttribute -> clazz.with(
							NestMembersAttribute.of(
								attribute.nestMembers().mapNotNull {
									val desc = mapsName[it.toDotted()]?.name ?: return@mapNotNull null
									clazz.constantPool().classEntry(desc)
								}
							)
						)

						is SignatureAttribute -> {
							val classSig = attribute.asClassSignature()
							val superclassSig = classSig.superclassSignature()
							val superclassSigDeObf = Signature.ClassTypeSig.of(
								null,
								"",
								null
							)
							clazz.with(
								SignatureAttribute.of(
									ClassSignature.of(
										listOf(),
										superclassSigDeObf,
										null
									)
								)
							)
						}

						else -> {}
					}
				},
				StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING
			)
			latch.countDown()
		}
		latch.await()
	}
}