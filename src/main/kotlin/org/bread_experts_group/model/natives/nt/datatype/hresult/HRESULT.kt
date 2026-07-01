package org.bread_experts_group.model.natives.nt.datatype.hresult

import org.bread_experts_group.generic.Mappable.Companion.id
import org.bread_experts_group.generic.MappedEnumeration
import org.bread_experts_group.model.natives.Datatype
import org.bread_experts_group.model.natives.Datatype.Companion.invoke
import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.NativeArray
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.nt.datatype.DWORD
import org.bread_experts_group.model.natives.nt.datatype.WCHAR
import org.bread_experts_group.model.natives.nt.library.Kernel32
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment

sealed interface HRESULT {
	companion object {
		private fun handleFirst(hresult: Int): HRESULTCustomerDefined? {
			if (hresult and 0b00100000000000000000000000000000 != 0) return HRESULTCustomerDefined(hresult)
			else if (hresult and 0b00100000000000000000000000000000 != 0) TODO("NTSTATUS")
			return null
		}

		private fun getFacilityAndCode(
			hresult: Int
		): Pair<MappedEnumeration<Int, HRESULTMicrosoftDefined.HRESULTFacility>, Int> {
			val facility = HRESULTMicrosoftDefined.HRESULTFacility.entries.id((hresult ushr 16) and 0b111_11111111)
			val resultCode = hresult and 0xFFFF
			return facility to resultCode
		}

		fun of(hresult: Int): HRESULT {
			handleFirst(hresult)?.let { return it }
			val (facility, resultCode) = getFacilityAndCode(hresult)

			return if (hresult < 0) HRESULTMicrosoftDefined.Failure(null, facility, resultCode)
			else HRESULTMicrosoftDefined.Success(null, facility, resultCode)
		}

		fun of(hresult: Int, linker: Linker, arena: Arena): HRESULT {
			handleFirst(hresult)?.let { return it }
			val (facility, resultCode) = getFacilityAndCode(hresult)

			val cl = linker.canonicalLayouts()
			val k32 = Library.getLibrary(linker, arena, Kernel32::class)
			val dword = Datatype.getDatatype(cl, DWORD::class)

			val reference = Pointer.of<MemorySegment>(linker)
			val chars = k32.FormatMessageW(
				dword(0x00001100), null, dword(hresult), dword(0),
				NativeArray.fromClass(cl, reference.getSegment(), WCHAR::class.java), dword(0),
				null
			).toLong()
			val message = if (chars > 0) NativeArray.fromClass(
				cl,
				reference.deref().reinterpret(
					Datatype.getLayout(cl, WCHAR::class).byteSize() * chars
				),
				WCHAR::class.java
			).map { Char(it.toInt()) }.toCharArray().concatToString() else null
			k32.LocalFree(reference.deref())

			return if (hresult < 0) HRESULTMicrosoftDefined.Failure(message, facility, resultCode)
			else HRESULTMicrosoftDefined.Success(message, facility, resultCode)
		}
	}
}