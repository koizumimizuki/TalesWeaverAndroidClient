package com.kohachori.talesweaver

import javax.microedition.khronos.opengles.GL10
import android.opengl.GLU
import android.opengl.GLUtils

object DebugOverlay extends Drawable {
  private var logArray: Array[String] = Array.empty[String]
  def log(log: String) =
    logArray = logArray.+:(log)
  override def draw(gl: GL10) {
    GameManager.fragment.log(logArray)
    logArray = Array.empty[String]
  }
}
