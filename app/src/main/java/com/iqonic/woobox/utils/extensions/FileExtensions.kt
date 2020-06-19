package com.iqonic.woobox.utils.extensions

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.channels.FileChannel


/**
 * Read File data as String and Returns the Result
 */
fun File.readToString(): String {
    var text = ""
    open().use { inpS ->
        inpS.bufferedReader().use {
            text = it.readText()
            it.close()
        }
        inpS.close()
    }
    return text
}

/**
 * Open File in InputStream
 */
fun File.open(): InputStream = FileInputStream(this)

/**
 * Move File/Dir to new Destination
 */
fun File.move(dest: File) {
    if (isFile)
        renameTo(dest)
    else
        moveDirectory(dest)
}

/**
 * Copy File/Dir to new Destination
 */
fun File.copy(dest: File) {
    if (isDirectory)
        copyDirectory(dest)
    else
        copyFile(dest)
}

/**
 * Delete the File or if its a Directory the delete all the contents
 */
fun File.deleteAll() {
    if (isFile && exists()) {
        delete()
        return
    }
    if (isDirectory) {
        val files = listFiles()
        if (files == null || files.isEmpty()) {
            delete()
            return
        }
        files.forEach { it.deleteAll() }
        delete()
    }
}

// Private Methods
private fun File.copyFile(dest: File) {
    var fi: FileInputStream? = null
    var fo: FileOutputStream? = null
    var ic: FileChannel? = null
    var oc: FileChannel? = null
    try {
        if (!dest.exists()) {
            dest.createNewFile()
        }
        fi = FileInputStream(this)
        fo = FileOutputStream(dest)
        ic = fi.channel
        oc = fo.channel
        ic.transferTo(0, ic.size(), oc)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        fi?.close()
        fo?.close()
        ic?.close()
        oc?.close()
    }
}

private fun File.copyDirectory(dest: File) {
    if (!dest.exists()) {
        dest.mkdirs()
    }
    val files = listFiles()
    files?.forEach {
        if (it.isFile) {
            it.copyFile(File("${dest.absolutePath}/${it.name}"))
        }
        if (it.isDirectory) {
            val dirSrc = File("$absolutePath/${it.name}")
            val dirDest = File("${dest.absolutePath}/${it.name}")
            dirSrc.copyDirectory(dirDest)
        }
    }
}

private fun File.moveDirectory(dest: File) {
    copyDirectory(dest)
    deleteAll()
}

fun Uri.realPath(context: Context): Uri {
    val result: String
    val cursor = context.contentResolver.query(this, null, null, null, null)

    if (cursor == null) {
        result = this.path!!
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
    }
    return Uri.parse(result)
}

fun String.toUri(): Uri {
    return Uri.parse(this)
}

fun File.toUri(): Uri {
    return Uri.fromFile(this)
}

fun Uri.toFile(): File {
    return File(this.path)
}
