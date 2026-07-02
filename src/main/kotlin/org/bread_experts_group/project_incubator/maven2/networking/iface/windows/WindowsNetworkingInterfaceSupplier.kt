package org.bread_experts_group.project_incubator.maven2.networking.iface.windows

import org.bread_experts_group.generic.MappedEnumeration
import org.bread_experts_group.model.natives.*
import org.bread_experts_group.model.natives.Datatype.Companion.invoke
import org.bread_experts_group.model.natives.nt.datatype.*
import org.bread_experts_group.model.natives.nt.datatype.hresult.HRESULT
import org.bread_experts_group.model.natives.nt.library.IpHlpAPI
import org.bread_experts_group.project_incubator.maven2.data.*
import org.bread_experts_group.project_incubator.maven2.data.windows.WindowsConnectionType
import org.bread_experts_group.project_incubator.maven2.data.windows.WindowsInterfaceType
import org.bread_experts_group.project_incubator.maven2.data.windows.WindowsOperationalStatus
import org.bread_experts_group.project_incubator.maven2.data.windows.WindowsTunnelType
import org.bread_experts_group.project_incubator.maven2.networking.NetworkingDevice
import org.bread_experts_group.project_incubator.maven2.networking.NetworkingInterfaceSupplier
import org.bread_experts_group.project_incubator.maven2.networking.NetworkingProvider
import org.bread_experts_group.project_incubator.maven2.networking.iface.NetworkingInterface
import java.lang.classfile.ClassFile
import java.lang.classfile.CodeBuilder
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandles
import java.lang.reflect.AccessFlag
import java.util.*
import kotlin.reflect.KClass

class WindowsNetworkingInterfaceSupplier : NetworkingInterfaceSupplier, NetworkingProvider {
	override fun systemCompatible(): Boolean {
		println("TODO: Windows compatibility?")
		return true
	}

	override fun new(): NetworkingDevice = this

	fun interfaces(
		linker: Linker = Linker.nativeLinker(),
		arena: Arena = Arena.ofAuto(),
		family: MappedEnumeration<Int, WindowsAddressFamilies> = MappedEnumeration(WindowsAddressFamilies.AF_UNSPEC),
		flags: EnumSet<WindowsInterfaceSupplierFlags> = EnumSet.noneOf(WindowsInterfaceSupplierFlags::class.java)
	): Result<Iterator<NetworkingInterface>> {
		val ipHelp = Library.getLibrary(linker, Arena.global(), IpHlpAPI::class)

		val layouts = linker.canonicalLayouts()
		val ulong = Datatype.getDatatype(layouts, ULONG::class)

		val pSize = Pointer.of<ULONG>(linker)
		val flagsN = flags.fold(0) { a, flag -> a or flag.ordinal }
		ipHelp.GetAdaptersAddresses(
			ulong(family.raw),
			ulong(flagsN),
			MemorySegment.NULL,
			null,
			pSize
		)
		val pAddr = arena.allocate(pSize.deref().toLong() and 0xFFFFFFFF)
		val addr = Structure.getStructure<IP_ADAPTER_ADDRESSES_LH>(linker)(pAddr)
		pSize.getSegment().set(ValueLayout.JAVA_INT, 0, pAddr.byteSize().toInt())
		val status = HRESULT.of(
			ipHelp.GetAdaptersAddresses(
				ulong(family.raw),
				ulong(flagsN),
				MemorySegment.NULL,
				addr,
				pSize
			).toInt(),
			linker,
			arena
		)
		if (status !is SystemStatus.OK) return Result.failure(status as SystemStatus)
		return Result.success(
			object : Iterator<NetworkingInterface> {
				private var current: IP_ADAPTER_ADDRESSES_LH? = addr
				override fun hasNext(): Boolean = current != null
				override fun next(): NetworkingInterface {
					val present = current ?: throw NoSuchElementException()
					current = present.Next.deref().let { n -> if (n.getSegment() == MemorySegment.NULL) null else n }

					val cf = ClassFile.of()
					val initParamAdjust = mutableListOf<Any>()
					val thisDesc = ClassDesc.of(this::class.java.packageName, "WNIS Interface")
					@Suppress("CAST_NEVER_SUCCEEDS")
					return MethodHandles.lookup().defineHiddenClass(
						cf.build(thisDesc) { classBuilder ->
							val initAdjust = mutableListOf<(CodeBuilder) -> Unit>()
							val interfaces = mutableListOf(NetworkingInterface::class.desc)
							if (present.FriendlyName.getSegment() != MemorySegment.NULL) {
								interfaces.add(FriendlyName::class.desc)
								classBuilder.property(FriendlyName::friendlyName) { getter ->
									getter.ldc(
										present.FriendlyName.getSegment()
											.reinterpret(Long.MAX_VALUE)
											.getString(0, Charsets.UTF_16LE) as ConstantDesc
									).areturn()
								}
							}
							if (present.AdapterName.getSegment() != MemorySegment.NULL) {
								interfaces.add(Name::class.desc)
								classBuilder.property(Name::name) { getter ->
									getter.ldc(
										present.AdapterName.getSegment()
											.reinterpret(Long.MAX_VALUE)
											.getString(0, Charsets.US_ASCII) as ConstantDesc
									).areturn()
								}
							}
							if (present.Description.getSegment() != MemorySegment.NULL) {
								interfaces.add(Description::class.desc)
								classBuilder.property(Description::description) { getter ->
									getter.ldc(
										present.Description.getSegment()
											.reinterpret(Long.MAX_VALUE)
											.getString(0, Charsets.UTF_16LE) as ConstantDesc
									).areturn()
								}
							}
							if (present.PhysicalAddressLength.toInt() > 0) {
								interfaces.add(PhysicalAddress::class.desc)
								classBuilder.withField(
									"_PAarray", ConstantDescs.CD_byte.arrayType(),
									AccessFlag.PUBLIC.mask() or AccessFlag.FINAL.mask()
								)
								initParamAdjust.add(
									present.PhysicalAddress.getSegment()
										.reinterpret(present.PhysicalAddressLength.toInt().toLong())
										.toArray(ValueLayout.JAVA_BYTE)
								)
								initAdjust.add { initBuilder ->
									initBuilder
										.aload(0)
										.aload(initParamAdjust.size)
										.checkcast(ConstantDescs.CD_byte.arrayType())
										.putfield(
											thisDesc, "_PAarray",
											ConstantDescs.CD_byte.arrayType()
										)
								}
								classBuilder.property(PhysicalAddress::physicalAddress) { getter ->
									getter
										.aload(0)
										.getfield(
											thisDesc, "_PAarray",
											ConstantDescs.CD_byte.arrayType()
										)
										.areturn()
								}
							}
							interfaces.add(WindowsTunnelType::class.desc)
							classBuilder.property(WindowsTunnelType::tunnelType) { getter ->
								getter
									.ldc(present.TunnelType.name as ConstantDesc)
									.invoke(TUNNEL_TYPE::valueOf)
									.areturn()
							}
							interfaces.add(WindowsConnectionType::class.desc)
							classBuilder.property(WindowsConnectionType::connectionType) { getter ->
								getter
									.ldc(present.ConnectionType.name as ConstantDesc)
									.invoke(NET_IF_CONNECTION_TYPE::valueOf)
									.areturn()
							}
							interfaces.add(WindowsOperationalStatus::class.desc)
							classBuilder.property(WindowsOperationalStatus::operationalStatus) { getter ->
								getter
									.ldc(present.OperStatus.name as ConstantDesc)
									.invoke(IF_OPER_STATUS::valueOf)
									.areturn()
							}
							interfaces.add(LinkSpeed.Tx::class.desc)
							classBuilder.property(LinkSpeed.Tx::linkSpeedTx) { getter ->
								getter
									.ldc(present.TransmitLinkSpeed as ConstantDesc)
									.lreturn()
							}
							interfaces.add(LinkSpeed.Rx::class.desc)
							classBuilder.property(LinkSpeed.Rx::linkSpeedRx) { getter ->
								getter
									.ldc(present.ReceiveLinkSpeed as ConstantDesc)
									.lreturn()
							}
							interfaces.add(MaximumTransmissionUnit::class.desc)
							classBuilder.property(MaximumTransmissionUnit::mtu) { getter ->
								getter
									.ldc((present.Mtu.toInt().toLong() and 0xFFFFFFFF) as ConstantDesc)
									.lreturn()
							}
							interfaces.add(WindowsInterfaceType::class.desc)
							classBuilder.property(WindowsInterfaceType::interfaceType) { getter ->
								getter
									.new_(MappedEnumeration::class.desc)
									.dup()
									.ldc(WindowsIFaceDefinitions::class.desc)
									.ldc(present.IfType.toInt() as ConstantDesc)
									.invokestatic(
										ConstantDescs.CD_Integer, "valueOf",
										MethodTypeDesc.of(ConstantDescs.CD_Integer, ConstantDescs.CD_int)
									)
									.invokespecial(
										MappedEnumeration::class.desc, "<init>",
										MethodTypeDesc.of(
											ConstantDescs.CD_void,
											ConstantDescs.CD_Class, ConstantDescs.CD_Object
										)
									)
									.areturn()
							}
							classBuilder.withInterfaceSymbols(interfaces)
							classBuilder.withMethodBody(
								"<init>", MethodTypeDesc.of(
									ConstantDescs.CD_void,
									*Array(initParamAdjust.size) { ConstantDescs.CD_Object }
								),
								AccessFlag.PUBLIC.mask()
							) { methodBuilder ->
								methodBuilder
									.aload(0)
									.invokespecial(
										ConstantDescs.CD_Object, "<init>",
										MethodTypeDesc.of(ConstantDescs.CD_void)
									)
								initAdjust.forEach { it(methodBuilder) }
								methodBuilder.return_()
							}
						},
						false
					).lookupClass()(*initParamAdjust.toTypedArray()) as NetworkingInterface
				}
			}
		)
	}

	override fun interfaces(): Result<Iterator<NetworkingInterface>> = this.interfaces(Linker.nativeLinker())

	override val produces: KClass<out NetworkingDevice> = this::class
}