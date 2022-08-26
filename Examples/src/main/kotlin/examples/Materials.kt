package examples

import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.component.render.ShapeComponent
import core.component.render.TextComponent
import org.joml.Random
import org.joml.Vector2f
import render.dto.Color
import render.dto.Shape
import render.dto.Transform
import render.font.DefaultFont
import ui.dto.InputStateData
import java.lang.Integer.max
import java.lang.Integer.min

fun main(args: Array<String>) {

    val engine = CoreEngine()
    val gameLogic = Materials(engine)
    engine.delegate = gameLogic
    engine.start()

    println("Done!")
}

class Materials(private val engine: CoreEngine) : CoreEngineDelegate {

    companion object {
        private val SCREEN_HEIGHT = 720f
        private val SCREEN_WIDTH = 1280f

        const val GRID_HEIGHT = 720 / 2
        const val GRID_WIDTH = 1280 / 2

        const val UPDATES_PER_TICK = 0
        const val INSERT_SIZE = 3
        const val EXPLOSION_RADIUS = 10
    }

    private class Grid(val width: Int, val height: Int) {
        private val grid: Array<Array<Material?>> = Array(height) { Array(width) { null } }

        fun getParticle(row: Int, column: Int): Material? {
            return grid[row][column]
        }

        fun setParticle(row: Int, column: Int, particle: Material) {
            grid[row][column] = particle
        }

        fun clearParticle(row: Int, column: Int) {
            grid[row][column] = null
        }

        fun getCellSize(): Vector2f {
            return Vector2f(SCREEN_WIDTH / GRID_WIDTH, SCREEN_HEIGHT / GRID_HEIGHT)
        }

    }

    private abstract class Material(initialRow: Int, initialColumn: Int, size: Vector2f, color: Color) :
        BaseEntity(
            Transform(
                Vector2f(initialColumn.toFloat(), initialRow.toFloat()).mul(size),
                0f,
                size
            )
        ) {

        enum class Type(value: Int) {
            SAND(1),
            ROCK(2),
            TELEPORT(3),
            WATER(4),
        }

        abstract val type: Type
        var toRemove: Boolean = false
        var gridColumn: Int = initialColumn
        var gridRow: Int = initialRow
        protected var updatedAtRound = -1

        init {
            addComponent(ShapeComponent(uid, Shape.Type.SQUARE, color, false))
        }

        open fun updatePosition(grid: Grid, round: Int) {
            updatedAtRound = round

            grid.getParticle(gridRow - 1, gridColumn)
                ?.updatePosition(grid, updatedAtRound)
            grid.getParticle(gridRow - 1, max(gridColumn - 1, 0))
                ?.updatePosition(grid, updatedAtRound)
            grid.getParticle(gridRow - 1, min(gridColumn + 1, grid.width - 1))
                ?.updatePosition(grid, updatedAtRound)
        }
    }

    private class Sand(initialRow: Int, initialColumn: Int, private val size: Vector2f) :
        Material(initialRow, initialColumn, size, Color(240f / 255f, 230f / 255f, 140f / 255f, 1f)) {

        override val type: Type
            get() = Type.SAND

        override fun updatePosition(grid: Grid, round: Int) {
            if (gridRow == 0 || round == updatedAtRound) return
            super.updatePosition(grid, round)

            var newGridRow = gridRow - 1
            var newGridColumn = gridColumn

            val bottom = grid.getParticle(gridRow - 1, gridColumn)
            if (bottom != null && bottom.type != Type.TELEPORT) {

                val r = Random().nextInt(2)
                val tryLeftFirst = r == 0

                val leftIsLimit = gridColumn == 0
                val left = grid.getParticle(gridRow - 1, max(gridColumn - 1, 0))
                val rightIsLimit = gridColumn == grid.width
                val right = grid.getParticle(gridRow - 1, min(gridColumn + 1, grid.width - 1))

                if (tryLeftFirst && !leftIsLimit && left == null) {
                    newGridColumn -= 1

                } else if (!rightIsLimit && right == null) {
                    newGridColumn += 1
                } else {
                    newGridRow = gridRow
                }

            }

            if (newGridRow != gridRow || newGridColumn != gridColumn) {
                grid.clearParticle(gridRow, gridColumn)
                grid.setParticle(newGridRow, newGridColumn, this)
                gridRow = newGridRow
                gridColumn = newGridColumn
                transform.position = Vector2f(newGridColumn.toFloat(), newGridRow.toFloat()).mul(size)
            }
        }
    }

    private class Rock(initialRow: Int, initialColumn: Int, private val size: Vector2f) :
        Material(initialRow, initialColumn, size, Color(1f, 1f, 1f, 1f)) {

        override val type: Type
            get() = Type.ROCK

        override fun updatePosition(grid: Grid, round: Int) {
            if (round == updatedAtRound) return
            super.updatePosition(grid, round)
        }
    }

    private class Teleporter(initialRow: Int, initialColumn: Int, private val size: Vector2f) :
        Material(initialRow, initialColumn, size, Color(0.5f, 0.5f, 1f, 1f)) {

        companion object {
            const val Y_OFFSET = 200
        }

        override val type: Type
            get() = Type.TELEPORT

        override fun updatePosition(grid: Grid, round: Int) {
            if (round == updatedAtRound) return

            grid.getParticle(gridRow + 1, min(gridColumn, grid.width))?.let {
                teleportMaterial(grid, it)
            }
        }

        private fun teleportMaterial(grid: Grid, mat: Material) {
            if (mat.type == Type.TELEPORT) return

            grid.clearParticle(mat.gridRow, mat.gridColumn)
            grid.setParticle(mat.gridRow + Y_OFFSET, mat.gridColumn, mat)
            mat.gridRow += Y_OFFSET
        }
    }

    class TopGui() : BaseEntity(Transform(Vector2f(0f, SCREEN_HEIGHT - 200f), 0.0f, Vector2f(200f, 200f))) {

        private val df = DefaultFont()
        private val titleTextComponent = TextComponent(uid, Vector2f(20f, 160f), "@Hello text rendering!", df, 16f)

        init {
            addComponent(titleTextComponent)
        }

        fun setText(text: String) {
            titleTextComponent.text = text
        }

    }

    private var updateRound = 0
    private var updatesSinceTick = 0
    private val particles: MutableList<Material> = mutableListOf()
    private val grid: Grid = Grid(GRID_WIDTH, GRID_HEIGHT)
    private lateinit var gui: TopGui

    override fun onStart() {
        gui = TopGui()
        engine.addEntity(gui)
    }

    override fun onUpdate(elapsedTime: Double, input: InputStateData) {

        updatesSinceTick += 1
        if (updatesSinceTick < UPDATES_PER_TICK) return
        updatesSinceTick = 0

        handleInput(input)

        particles.forEach {
            it.updatePosition(grid, updateRound)
        }

        updateRound = (updateRound + 1) % 2
        gui.setText("${particles.size} particles")

    }

    override fun onCleanUp() {}
    override fun onFrame() {}

    private fun addSand(row: Int, column: Int) {

        val rr = max(0, min(row, GRID_HEIGHT - 1))
        val rc = max(0, min(column, GRID_WIDTH - 1))

        if (grid.getParticle(rr, rc) != null) return

        val p = Sand(rr, rc, grid.getCellSize())
        grid.setParticle(rr, rc, p)
        particles.add(p)
        engine.addEntity(p)
    }
    private fun addRock(row: Int, column: Int) {
        if (grid.getParticle(row, column) != null) return

        val p = Rock(row, column, grid.getCellSize())
        grid.setParticle(row, column, p)
        particles.add(p)
        engine.addEntity(p)
    }
    private fun addTeleporter(row: Int, column: Int) {

        if (grid.getParticle(row, column) != null) return

        val p = Teleporter(row, column, grid.getCellSize())
        grid.setParticle(row, column, p)
        particles.add(p)
        engine.addEntity(p)
    }
    private fun removeParticle(row: Int, column: Int) {
        grid.getParticle(row, column)?.let {
            particles.remove(it)
            engine.removeEntity(it)
            grid.clearParticle(row, column)
        }
    }

    private fun handleInput(input: InputStateData) {

        val mx = input.mouseX + input.dragDeltaX
        val my = input.mouseY + input.dragDeltaY
        val gc = (mx * GRID_WIDTH / SCREEN_WIDTH).toInt()
        val gr = (my * GRID_HEIGHT / SCREEN_HEIGHT).toInt()

        if (input.isKeyPressed(InputStateData.KEY_S)) {
            addSand(gr, gc)
            for (r in 1 until INSERT_SIZE) {
                addSand(gr, gc - (r * 2))
                addSand(gr, gc + (r * 2))
            }
        } else if (input.isKeyPressed(InputStateData.KEY_R)) {
            addRock(gr, gc)
            for (r in 1 until INSERT_SIZE) {
                addRock(gr - r, gc)
                addRock(gr + r, gc)
                addRock(gr, gc - r)
                addRock(gr, gc + r)
            }
        } else if (input.isKeyPressed(InputStateData.KEY_W)) {
            removeParticle(gr, gc)
            for (r in 1 until INSERT_SIZE) {
                removeParticle(gr - r, gc)
                removeParticle(gr + r, gc)
                removeParticle(gr, gc - r)
                removeParticle(gr, gc + r)
            }
        } else if (input.isKeyPressed(InputStateData.KEY_T)) {
            addTeleporter(gr, gc)
            for (r in 1 until INSERT_SIZE) {
                addTeleporter(gr - r, gc)
                addTeleporter(gr + r, gc)
                addTeleporter(gr, gc - r)
                addTeleporter(gr, gc + r)
            }
        }
    }
}

