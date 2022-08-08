package render.font

import org.joml.Vector2f
import render.model.BitmapFont

class DefaultFont(): BitmapFont(
    "/font/default.png",
    16,
    16,
    Vector2f(90f)
) {

    init {
        setCharacter('ª',1, 0)
        setRow(2, " !\"#\$%&\'()*+,-./")
        setRow(3, "0123456789:;<=>?")
        setRow(4, "@ABCDEFGHIJKLMNO")
        setRow(5, "PQRSTUVWXYZ[\\]^_")
        setRow(6, "`abcdefghijklmno")
        setRow( 7, "pqrstuvwxyz{|}~")
    }

}