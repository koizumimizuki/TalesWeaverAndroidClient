package com.kohachori.talesweaver

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView.Renderer
import android.content.Context
import android.opengl.GLU

class MainRenderer(context: Context) extends Renderer {

  var mWidth: Int = _
  var mHeight: Int = _

  override def onSurfaceCreated(gl: GL10, config: EGLConfig) {
    GameManager.mapTexture = GraphicUtil.loadTexture(gl, context.getResources(), "glass_gland.png")
    GameManager.playerTexture = GraphicUtil.loadTexture(gl, context.getResources(), "upper_left_makishi.png")
    GameManager.zerripiTexture = GraphicUtil.loadTexture(gl, context.getResources(), "zerripi.png")
  }

  override def onSurfaceChanged(gl: GL10, width: Int, height: Int) {
    mWidth = width
    mHeight = height
    gl.glViewport(0, 0, width, height)
    Camera.onSurfaceChanged(width, height)
  }

  override def onDrawFrame(gl: GL10) {
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
    Camera.update()
    Camera.draw(gl)
    GameManager.update()
    GameManager.draw(gl)
    FPSCounter.update()
    FPSCounter.draw()
  }

}