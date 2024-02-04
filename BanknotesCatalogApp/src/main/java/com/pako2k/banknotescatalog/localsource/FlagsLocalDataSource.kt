package com.pako2k.banknotescatalog.localsource

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.coroutineScope
import java.io.IOException

private const val FLAGS_DIR = ""
private const val FLAGS_EXT = "png"

class FlagsLocalDataSource (
    private val assetManager: AssetManager,
    private val sourceAssetsDir : String = FLAGS_DIR,
    private val flagsExt : String = FLAGS_EXT
){
    suspend fun getFlags() : Map<String,ImageBitmap> = coroutineScope{
        getFlagsSync()
    }

    fun getFlagsSync() : Map<String,ImageBitmap> {
        val tmp = mutableMapOf<String,ImageBitmap>()
        try {
            val fileNames = assetManager.list(sourceAssetsDir) ?: arrayOf<String>()
            for (fileName in fileNames){
                try {
                    val fileIS = assetManager.open(fileName)
                    val bitmap = BitmapFactory.decodeStream(fileIS)
                    if (bitmap != null)
                        tmp[fileName.substringBeforeLast(".$flagsExt")] =  bitmap.asImageBitmap()
                }
                catch (_: IOException){
                }
            }
        }
        catch (_ : IOException){
        }

        return tmp
    }
}
