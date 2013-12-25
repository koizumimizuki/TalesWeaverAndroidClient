package com.kohachori.talesweaver

import javax.microedition.khronos.opengles.GL10
import android.opengl.GLU

object Camera {
  var surfaceX: Int = _
  var surfaceY: Int = _
  var cameraX: Int = _
  var cameraY: Int = _
  def onSurfaceChanged(x: Int, y: Int) {
    surfaceX = x
    surfaceY = y
  }
  def update() {
    cameraX = GameManager.player.x - surfaceX / 2
    cameraY = GameManager.player.y - surfaceY / 2
  }
  def draw(gl: GL10) {
    gl.glMatrixMode(GL10.GL_PROJECTION)
    gl.glLoadIdentity
    val left = cameraX
    val right = cameraX + surfaceX
    val bottom = cameraY + surfaceY
    val top = cameraY
    GLU.gluOrtho2D(gl, left, right, bottom, top)
    // 無くても動くので、予期しない挙動を防ぐために一応コメントアウト
    //    gl.glMatrixMode(GL10.GL_MODELVIEW)
    //    gl.glLoadIdentity
    //    gl.glDisable(GL10.GL_DEPTH_TEST)
  }
}