package tools.dto

import java.nio.ByteBuffer

data class ImageData(val width: Int,
                     val height: Int,
                     val components: Int,
                     val data: ByteBuffer)