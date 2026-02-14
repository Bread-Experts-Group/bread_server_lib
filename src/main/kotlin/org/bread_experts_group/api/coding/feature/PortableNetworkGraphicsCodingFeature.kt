package org.bread_experts_group.api.coding.feature

import org.bread_experts_group.api.coding.CodingFeatureImplementation
import org.bread_experts_group.api.coding.CodingFormats
import org.bread_experts_group.api.coding.png.PNGReadingDataIdentifier
import org.bread_experts_group.api.coding.png.PNGReadingFeatureIdentifier
import org.bread_experts_group.api.coding.png.PNGWritingDataIdentifier
import org.bread_experts_group.api.coding.png.PNGWritingFeatureIdentifier
import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.generic.io.reader.SequentialDataSink
import org.bread_experts_group.generic.io.reader.SequentialDataSource

abstract class PortableNetworkGraphicsCodingFeature :
	CodingFeatureImplementation<PortableNetworkGraphicsCodingFeature>() {
	override val expresses: FeatureExpression<PortableNetworkGraphicsCodingFeature> =
		CodingFormats.PORTABLE_NETWORK_GRAPHICS

	abstract fun read(
		from: org.bread_experts_group.generic.io.reader.SequentialDataSource,
		vararg features: PNGReadingFeatureIdentifier
	): List<PNGReadingDataIdentifier>

	abstract fun write(
		into: org.bread_experts_group.generic.io.reader.SequentialDataSink,
		vararg features: PNGWritingFeatureIdentifier
	): List<PNGWritingDataIdentifier>
}