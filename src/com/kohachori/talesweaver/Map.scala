package com.kohachori.talesweaver

import javax.microedition.khronos.opengles.GL10
import android.opengl.GLUtils
import android.opengl.GLU

object Map extends Drawable {
  override def draw(gl: GL10) {
    object Config {
      val ChipWidth = 256 // GLViewに表示するwidth幅
      val ChipHeight = 256
      val TextureWidthLength = 256 // １つぶを何pxでマップに表示するか
      val TextureHeightLength = 256
    }
    for (i <- 0 to 10; j <- 0 to 10) {
      GraphicUtil.drawTexture(gl, 0 + i * 256, 0 + j * 256, Config.TextureWidthLength, Config.TextureHeightLength, GameManager.mapTexture, 256, 256, Config.ChipWidth, Config.ChipHeight)
    }
  }
}