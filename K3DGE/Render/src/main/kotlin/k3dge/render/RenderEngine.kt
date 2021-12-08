package k3dge.render

import k3dge.render.dho.EntityRenderData
import k3dge.render.dho.RenderBatchData
import k3dge.render.model.MeshModel
import k3dge.render.model.TextureModel
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30.*

class RenderEngine {

    private val renderBatches: MutableMap<String, RenderBatchData> = mutableMapOf()

    fun drawTexturedMesh(mesh: MeshModel, texture: TextureModel, position: Vector3f, rotation: Vector3f, scale: Vector3f){
        val modelMatrix: Matrix4f = Matrix4f().translation(position)
        modelMatrix.rotate(rotation.x, Vector3f(1.0f, 0.0f, 0.0f))
        modelMatrix.rotate(rotation.y, Vector3f(0.0f, 1.0f, 0.0f))
        modelMatrix.rotate(rotation.z, Vector3f(0.0f, 0.0f, 1.0f))
        modelMatrix.scale(scale)

        val id = "${mesh.vao}-${texture.id}"
        if(!renderBatches.containsKey(id)){
            renderBatches[id] = RenderBatchData(mesh.vao, mesh.size, texture.id)
        }
        renderBatches[id]?.entityData?.add(EntityRenderData(modelMatrix))
    }

    fun drawBatches(){
        for(batch in renderBatches.values){

            bindTexturedModelFromBatch(batch)
            for(modelMatrix in batch.entityData){

            }
            unbindTexturedModelFromBatch()
        }
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
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private fun bindEntity(){

    }



    private fun renderTexturedModel(model: TextureModel){
        /*

        glDrawElements(GL_TRIANGLES, size, GL_UNSIGNED_INT, 0);

        */
    }

}