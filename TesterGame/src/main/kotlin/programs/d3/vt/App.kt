package programs.d3.vt

import k3dge.render.renderer3d.shader.StaticShader
import k3dge.core.CoreEngine
import k3dge.core.CoreEngineDelegate
import k3dge.core.camera.Camera
import k3dge.core.camera.component.RotateCameraComponent
import k3dge.core.camera.component.TranslateCameraComponent
import k3dge.core.camera.component.ZoomCameraComponent
import k3dge.core.entity.Entity
import k3dge.core.entity.component3d.TexturedMeshEntityComponent
import k3dge.core.light.Light
import k3dge.core.light.component.ColorLightComponent
import k3dge.core.light.component.DirectionalLightComponent
import k3dge.core.light.component.LightRotateLightComponent
import k3dge.render.renderer3d.entity.StaticObjectModel
import k3dge.render.common.model.Texture
import k3dge.tools.ResourceManager
import k3dge.ui.dto.InputStateData
import org.joml.Vector3f
import org.joml.Vector4f

val engine = CoreEngine()

fun main(args: Array<String>) {

    val gameLogic = GameLogic()
    engine.delegate = gameLogic
    engine.start()

    println("Done!")
}

class GameLogic : CoreEngineDelegate {

    override fun onStart() {

        val shader = StaticShader()
        val terrainMeshData = ResourceManager.loadMeshFromFile("/mesh/terrain.obj")!!
        val terrainMesh = StaticObjectModel(
            terrainMeshData.vertices.toTypedArray(),
            terrainMeshData.textureCoordinates.toTypedArray(),
            terrainMeshData.normals.toTypedArray(),
            terrainMeshData.indices.toTypedArray())

        val cubeMeshData = ResourceManager.loadMeshFromFile("/mesh/home.obj")!!
        val cubeMesh = StaticObjectModel(
            cubeMeshData.vertices.toTypedArray(),
            cubeMeshData.textureCoordinates.toTypedArray(),
            cubeMeshData.normals.toTypedArray(),
            cubeMeshData.indices.toTypedArray())

        val terrainTextureData = ResourceManager.loadTextureFromFile("/texture/lowPolyAtlas.png")!!
        val terrainTexture = Texture(terrainTextureData.width, terrainTextureData.height, terrainTextureData.data)

        val terrainMeshComp = TexturedMeshEntityComponent(terrainMesh, terrainTexture, shader)
        val terrain = Entity(
            Vector3f(0f, 0f, 0f),
            Vector3f(0f, 0f, 0f),
            Vector3f(1f, 1f, 1f))
        terrain.addComponent(terrainMeshComp)
        engine.addEntity(terrain)

        val boxMeshComp = TexturedMeshEntityComponent(cubeMesh, terrainTexture, shader)
        val box = Entity(
            Vector3f(0f, 0f, 0f),
            Vector3f(0f, 0f, 0f),
            Vector3f(0.1f, 0.1f, 0.1f))
        box.addComponent(boxMeshComp)
        engine.addEntity(box)

        val camera = Camera(
            Vector3f(0.0f, 1.0f, 10.0f),
            Vector3f(0.0f, -0.5f, -0.5f),
            Vector3f(0.0f, 1.0f, 0.0f))
        camera.addComponent(TranslateCameraComponent(5.0f))
        camera.addComponent(ZoomCameraComponent(50.0f, 2.0f, 105.0f))
        camera.addComponent(RotateCameraComponent(1.0f,-0.85f, -0.25f))
        engine.addEntity(camera)

        val sun = Light(
            Vector3f(0.5f, -0.5f, 0.0f),
            Vector4f(0.0f, 1.0f, 0.0f, 1.0f)
        )
        sun.addComponent(DirectionalLightComponent())
        sun.addComponent(LightRotateLightComponent(0.25f))
        sun.addComponent(ColorLightComponent())
        engine.addEntity(sun)

    }
    override fun onUpdate(elapsedTime: Double, input: InputStateData) {}
    override fun onFrame() {}
    override fun onCleanUp() {}
}