package org.bread_experts_group.model.natives.nt.datatype.hresult

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.generic.MappedEnumeration
import org.bread_experts_group.model.natives.SystemStatus

// For [MS-ERREF] 23.0
sealed interface HRESULTMicrosoftDefined : HRESULT {
	enum class HRESULTFacility(override val id: Int) : Mappable<HRESULTFacility, Int> {
		FACILITY_NULL(0),
		FACILITY_RPC(1),
		FACILITY_DISPATCH(2),
		FACILITY_STORAGE(3),
		FACILITY_ITF(4),
		FACILITY_WIN32(7),
		FACILITY_WINDOWS(8),
		FACILITY_SECURITY_OR_SSPI(9),
		FACILITY_CONTROL(10),
		FACILITY_CERT(11),
		FACILITY_INTERNET(12),
		FACILITY_MEDIASERVER(13),
		FACILITY_MSMQ(14),
		FACILITY_SETUPAPI(15),
		FACILITY_SCARD(16),
		FACILITY_COMPLUS(17),
		FACILITY_AAF(18),
		FACILITY_URT(19),
		FACILITY_ACS(20),
		FACILITY_DPLAY(21),
		FACILITY_UMI(22),
		FACILITY_SXS(23),
		FACILITY_WINDOWS_CE(24),
		FACILITY_HTTP(25),
		FACILITY_USERMODE_COMMONLOG(26),
		FACILITY_USERMODE_FILTER_MANAGER(31),
		FACILITY_BACKGROUNDCOPY(32),
		FACILITY_CONFIGURATION(33),
		FACILITY_STATE_MANAGEMENT(34),
		FACILITY_METADIRECTORY(35),
		FACILITY_WINDOWSUPDATE(36),
		FACILITY_DIRECTORYSERVICE(37),
		FACILITY_GRAPHICS(38),
		FACILITY_SHELL(39),
		FACILITY_TPM_SERVICES(40),
		FACILITY_TPM_SOFTWARE(41),
		FACILITY_PLA(48),
		FACILITY_FVE(49),
		FACILITY_FWP(50),
		FACILITY_WINRM(51),
		FACILITY_NDIS(52),
		FACILITY_USERMODE_HYPERVISOR(53),
		FACILITY_CMI(54),
		FACILITY_USERMODE_VIRTUALIZATION(55),
		FACILITY_USERMODE_VOLMGR(56),
		FACILITY_BCD(57),
		FACILITY_USERMODE_VHD(58),
		FACILITY_SDIAG(60),
		FACILITY_WEBSERVICES(61),
		FACILITY_WINDOWS_DEFENDER(80),
		FACILITY_OPC(81);

		override val tag: String = name
	}

	val facility: MappedEnumeration<Int, HRESULTFacility>
	val code: Int

	class Failure(
		message: String?,
		override val facility: MappedEnumeration<Int, HRESULTFacility>,
		override val code: Int
	) : HRESULTMicrosoftDefined, SystemStatus.Error(message) {
		override fun getLocalizedMessage(): String = "$facility (${code.toUShort().toHexString()})" +
				if (message != null) ": $message" else ""
	}

	class Success(
		message: String?,
		override val facility: MappedEnumeration<Int, HRESULTFacility>,
		override val code: Int
	) : HRESULTMicrosoftDefined, SystemStatus.OK(message) {
		override fun getLocalizedMessage(): String = "$facility (${code.toUShort().toHexString()})" +
				if (message != null) ": $message" else ""
	}
}