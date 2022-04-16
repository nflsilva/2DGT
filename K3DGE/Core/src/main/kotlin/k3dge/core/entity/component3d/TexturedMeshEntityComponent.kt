package k3dge.core.entity.component3d

import k3dge.core.common.Component
import k3dge.core.common.dto.UpdateData
import k3dge.render.common.model.Mesh
import k3dge.render.common.model.Shader
import k3dge.render.common.model.Texture
import k3dge.render.renderer3d.shader.StaticShader

class TexturedMeshEntityComponent(private val mesh: Mesh,
                                  private val texture: Texture,
                                  private val shader: Shader = StaticShader()) : Component() {

    init {
        setUpdateObserver { context -> onUpdate(context) }
        setCleanupObserver { cleanUp() }
    }
    private fun onUpdate(context: UpdateData){
        context.entity?.let { entity ->
            context.graphics.renderTexturedMesh(mesh, texture, shader, entity.transform.data)
        }
    }
    private fun cleanUp(){
        mesh.cleanUp()
        texture.cleanUp()
    }
}