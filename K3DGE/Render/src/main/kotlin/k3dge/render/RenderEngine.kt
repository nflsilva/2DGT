package k3dge.render

import k3dge.render.dto.CameraRenderData
import k3dge.render.dto.EntityRenderData
import k3dge.render.dto.RenderBatchData
import k3dge.render.model.MeshModel
import k3dge.render.model.ShaderModel
import k3dge.render.model.TextureModel
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30.*

class RenderEngine {

    private val renderBatches: MutableMap<String, RenderBatchData> = mutableMapOf()
    private var projectionMatrix: Matrix4f = Matrix4f()
        .setPerspective(
            1.25F,
            SCREEN_WIDTH.toFloat() / SCREEN_HEIGHT.toFloat(),
            0.1F,
            100F)

    private var viewMatrix: Matrix4f = Matrix4f().identity()

    fun renderCamera(cameraData: CameraRenderData){
        viewMatrix = Matrix4f().lookAt(cameraData.position, cameraData.lookAt, cameraData.up)
    }

    fun renderTexturedMesh(mesh: MeshModel,
                           texture: TextureModel,
                           shader: ShaderModel,
                           position: Vector3f,
                           rotation: Vector3f,
                           scale: Vector3f){

        val modelMatrix = Matrix4f()

        modelMatrix.translation(position)
        modelMatrix.rotate(rotation.x, Vector3f(1.0f, 0.0f, 0.0f))
        modelMatrix.rotate(rotation.y, Vector3f(0.0f, 1.0f, 0.0f))
        modelMatrix.rotate(rotation.z, Vector3f(0.0f, 0.0f, 1.0f))
        modelMatrix.scale(scale)

        val id = "${mesh.vao}-${texture.id}"
        if(!renderBatches.containsKey(id)){
            renderBatches[id] = RenderBatchData(mesh.vao, mesh.size, texture.id)
        }
        renderBatches[id]?.entityData?.add(EntityRenderData(shader, modelMatrix))
    }
    fun onStart() {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
    }
    fun onFrame() {
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        drawBatches()
    }

    private fun drawBatches(){
        for(batch in renderBatches.values){
            bindTexturedModelFromBatch(batch)
            for(entity in batch.entityData){
                prepareEntityShader(entity)
                glDrawElements(GL_TRIANGLES, batch.meshSize, GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModelFromBatch()
        }
        renderBatches.clear()
    }
    private fun bindTexturedModelFromBatch(batch: RenderBatchData){
        glBindVertexArray(batch.vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, batch.textureId);
    }
    private fun unbindTexturedModelFromBatch(){
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glDisableVertexAttribArray(2)
        glBindVertexArray(0)
        glBindTexture(GL_TEXTURE_2D, 0)
    }
    private fun prepareEntityShader(entity: EntityRenderData){
        entity.shader.bind()
        entity.shader.setModelMatrix(entity.modelMatrix)
        entity.shader.setProjectionMatrix(projectionMatrix)
        entity.shader.setViewMatrix(viewMatrix)
    }

    companion object {
        const val SCREEN_WIDTH: Int = 1280
        const val SCREEN_HEIGHT: Int = 720
    }
}