package com.kohachori.talesweaver

import javax.microedition.khronos.opengles.GL10
import android.opengl.GLU
import android.opengl.GLUtils

object DebugOverlay extends Drawable {
  override def draw(gl: GL10) {
    gl.glMatrixMode(GL10.GL_PROJECTION)
    gl.glLoadIdentity
    val left = Camera.cameraX
    val right = Camera.cameraX + Camera.surfaceX
    val bottom = Camera.cameraY + Camera.surfaceY
    val top = Camera.cameraY
    GLU.gluOrtho2D(gl, left, right, bottom, top)
    GraphicUtil.drawCircle(gl, 0, 0, 100, 100, 1, 1, 1, 1)
  }
}