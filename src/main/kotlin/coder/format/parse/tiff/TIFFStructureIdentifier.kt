package org.bread_experts_group.coder.format.parse.tiff

import org.bread_experts_group.coder.Mappable

// Thank you, https://exiv2.org/tags.html
enum class TIFFStructureIdentifier(
	override val id: Int,
	override val tag: String
) : Mappable<TIFFStructureIdentifier, Int> {
	COMPRESSION(0x0103, "Compression (Exif.Image.Compression)"),
	IMAGE_DESCRIPTION(0x010E, "Image Description (Exif.Image.ImageDescription)"),
	EQUIPMENT_MANUFACTURER(0x010F, "Equipment Manufacturer (Exif.Image.Make)"),
	EQUIPMENT_MODEL(0x0110, "Equipment Model (Exif.Image.Model)"),
	ORIENTATION(0x0112, "Orientation (Exif.Image.Orientation)"),
	WIDTH(0x011A, "Width (Exif.Image.XResolution)"),
	HEIGHT(0x011B, "Height (Exif.Image.YResolution)"),
	UNIT(0x0128, "Resolution Unit (Exif.Image.ResolutionUnit)"),
	SOFTWARE(0x0131, "Software (Exif.Image.Software)"),
	MODIFIED_DATE_TIME(0x0132, "Last Modified (Exif.Image.DateTime)"),
	ARTIST(0x013B, "Artist (Exif.Image.Artist)"),
	JPEG_OFFSET(0x0201, "JPEG Offset (Exif.Image.JPEGInterchangeFormat)"),
	JPEG_SIZE(0x0202, "JPEG Size (Exif.Image.JPEGInterchangeFormatLength)"),
	YCBCR_POSITIONING(0x0213, "YCbCr Positioning (Exif.Image.YCbCrPositioning)"),
	COPYRIGHT(0x8298, "Copyright (Exif.Image.Copyright)"),
	EXPOSURE_TIME(0x829A, "Exposure Time (s) (Exif.Image.ExposureTime)"),
	F_NUMBER(0x829D, "F-number (Exif.Image.FNumber)"),
	EXIF_OFFSET(0x8769, "Exif Offset (Exif.Image.ExifTag)"),
	EXPOSURE_PROGRAM(0x8822, "Exposure Program Class (Exif.Image.ExposureProgram)"),
	ISO_SPEED_RATINGS(0x8827, "ISO-12232 Speed / Latitude (Exif.Photo.ISOSpeedRatings)"),
	SENSITIVITY_TYPE(0x8830, "ISO-12232 Sensitivity (Exif.Photo.SensitivityType)"),
	RECOMMENDED_EXPOSURE_INDEX(0x8832, "ISO-12232 Recommended Exposure Index (Exif.Photo.RecommendedExposureIndex)"),
	EXIF_VERSION(0x9000, "Exif Version (Exif.Photo.ExifVersion)"),
	ORIGINAL_DATE_TIME(0x9003, "Created At (Exif.Photo.DateTimeOriginal)"),
	DIGITIZED_DATE_TIME(0x9004, "Digitized At (Exif.Photo.DateTimeDigitized)"),
	MODIFIED_DATE_TIME_OFFSET(0x9010, "Last Modified Offset (Exif.Photo.OffsetTime)"),
	ORIGINAL_DATE_TIME_OFFSET(0x9011, "Created At Offset (Exif.Photo.OffsetTimeOriginal)"),
	DIGITIZED_DATE_TIME_OFFSET(0x9012, "Digitized At Offset (Exif.Photo.OffsetTimeDigitized)"),
	COMPONENTS_CONFIGURATION(0x9101, "Components Configuration (Exif.Photo.ComponentsConfiguration)"),
	SHUTTER_SPEED(0x9201, "Shutter Speed (Exif.Image.ShutterSpeedValue)"),
	APERTURE(0x9202, "Aperture (Exif.Photo.ApertureValue)"),
	BRIGHTNESS(0x9203, "Brightness (Exif.Photo.BrightnessValue)"),
	EXPOSURE_BIAS(0x9204, "Exposure Bias (Exif.Photo.ExposureBiasValue)"),
	MAX_APERTURE(0x9205, "Max Aperture (Exif.Photo.MaxApertureValue)"),
	METERING_MODE(0x9207, "Metering Mode (Exif.Image.MeteringMode)"),
	LIGHT_SOURCE(0x9208, "Light Source (Exif.Photo.LightSource)"),
	FLASH(0x9209, "Flash Used (Exif.Photo.Flash)"),
	FOCAL_LENGTH(0x920A, "Focal Length (mm) (Exif.Photo.FocalLength)"),
	USER_COMMENT(0x9286, "User Comment (Exif.Photo.UserComment)"),
	FLASH_PIX_VERSION(0xA000, "FlashPix Version (Exif.Photo.FlashpixVersion)"),
	COLOR_SPACE(0xA001, "Color Space (Exif.Photo.ColorSpace)"),
	WIDTH_DIMENSION(0xA002, "X Dimension, Pixel (Exif.Photo.PixelXDimension)"),
	HEIGHT_DIMENSION(0xA003, "Y Dimension, Pixel (Exif.Photo.PixelYDimension)"),
	INTEROPERABILITY(0xA005, "Interoperability (Exif.Photo.InteroperabilityTag)"),
	WIDTH_FOCAL_PLANE_DIMENSION(0xA20E, "X Dimension, Focal Plane (Exif.Photo.FocalPlaneXResolution)"),
	HEIGHT_FOCAL_PLANE_DIMENSION(0xA20F, "Y Dimension, Focal Plane (Exif.Photo.FocalPlaneYResolution)"),
	FOCAL_PLANE_UNIT(0xA210, "Focal Plane Resolution Unit (Exif.Photo.FocalPlaneResolutionUnit)"),
	FILE_SOURCE(0xA300, "File Source (Exif.Photo.FileSource)"),
	SCENE_TYPE(0xA301, "Scene Type (Exif.Photo.SceneType)"),
	CUSTOM_RENDERED(0xA401, "Custom Rendering (Exif.Photo.CustomRendered)"),
	EXPOSURE_MODE(0xA402, "Exposure Mode (Exif.Photo.ExposureMode)"),
	WHITE_BALANCE(0xA403, "White Balance (Exif.Photo.WhiteBalance)"),
	DIGITAL_ZOOM_RATIO(0xA404, "Digital Zoom Ratio (Exif.Photo.DigitalZoomRatio)"),
	FOCAL_LENGTH_35MM(0xA405, "Focal Length (35mm equivalent) (Exif.Photo.FocalLengthIn35mmFilm)"),
	SCENE_CAPTURE_TYPE(0xA406, "Scene Capture Type (Exif.Photo.SceneCaptureType)"),
	CONTRAST(0xA408, "Contrast (Exif.Photo.Contrast)"),
	SATURATION(0xA409, "Saturation (Exif.Photo.Saturation)"),
	SHARPNESS(0xA40A, "Sharpness (Exif.Photo.Sharpness)"),
	LENS_SPECIFICATION(0xA432, "Lens Specification (Exif.Photo.LensSpecification)"),
	LENS_MANUFACTURER(0xA433, "Lens Manufacturer (Exif.Photo.LensMake)"),
	LENS_MODEL(0xA434, "Lens Model (Exif.Photo.LensModel)"),
	OTHER(-1, "Other");

	override fun other(): TIFFStructureIdentifier? = OTHER
	override fun toString(): String = stringForm()
}