package org.bread_experts_group.resource

import java.util.*

/**
 * @author <a href="https://discord.com/users/1024083991176949792">玉藻前</a>
 * Translator (MX)
 * @author <a href="https://ricardetex.carrd.co/">XxricardetexXPro</a>
 * Peer review translator (folder_inaccessible, outside_of_store) (CL)
 * @since D0F0N0P0
 */
@Suppress("unused", "ClassName")
class DirectoryListingResource_es : ListResourceBundle() {
	override fun getContents(): Array<out Array<out Any>> = arrayOf(
		arrayOf("files", "Archivo(s)"),
		arrayOf("folders", "Carpeta(s)"),
		arrayOf("loops", "Bucle(s)"),
		arrayOf("name", "Nombre"),
		arrayOf("size", "Tamaño"),
		arrayOf("last_modification", "Ultima Modificación"),
		arrayOf("empty", "Vacío"),
		arrayOf("unreadable", "No legible"),
		arrayOf("tree_errors", "Error(es) de árbol"),
		arrayOf("folder_empty", "Carpeta vacía"),
		arrayOf("folder_inaccessible", "Carpeta no accesible"),
		arrayOf("outside_of_store", "Fuera del directorio de raiz virtual"),
		arrayOf("this_is_symlink", "Esto es un vínculo simbólico"),
	)
}