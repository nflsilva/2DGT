package k3dge.tools.dto

import java.nio.ByteBuffer

data class TextureData(val width: Int,
                       val height: Int,
                       val components: Int,
                       val data: ByteBuffer)