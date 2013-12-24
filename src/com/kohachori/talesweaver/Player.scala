package com.kohachori.talesweaver

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView.Renderer
import android.content.Context
import scala.math._

case class Player(x: Int, y: Int, goalX: Int, goalY: Int, speed: Int) extends Drawable {

  case class ShouldRunFullSpeed
  case class ShouldNotRunFullSpeed
  case class ShouldNotMove

  def changeGoal(goalX: Int, goalY: Int) = Player(this.x, this.y, goalX, goalY, this.speed)
  def update(): Player = {
    val state = {
      val betweenGoalXAndThisxDistance = abs(goalX - x)
      val betweenGoalYAndThisyDistance = abs(goalY - y)
      if (betweenGoalXAndThisxDistance == 0 && betweenGoalYAndThisyDistance == 0) ShouldNotMove
      else if (betweenGoalXAndThisxDistance < speed && betweenGoalYAndThisyDistance < speed) ShouldNotRunFullSpeed
      else ShouldRunFullSpeed
    }
    state match {
      case ShouldNotRunFullSpeed => Player(this.goalX, this.goalY, this.goalX, this.goalY, this.speed)
      case ShouldNotMove => this
      case ShouldRunFullSpeed => {
        val y0 = this.y
        val y1 = this.goalY
        val x0 = this.x
        val x1 = this.goalX
        val vector = new Vector2D(x1 - x0, y1 - y0).unitVector()
        val updatedX = this.x + speed * vector.mX
        val updatedY = this.y + speed * vector.mY
        Player(updatedX.toInt, updatedY.toInt, goalX, goalY, speed)
      }
    }
  }
  override def draw(gl: GL10) {
    object Config {
      val ChipWidth = 256 // GLViewに表示するwidth幅
      val ChipHeight = 256
      val TextureWidthLength = 256 // １つぶを何pxでマップに表示するか
      val TextureHeightLength = 256
    }
    gl.glEnable(GL10.GL_BLEND)
    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    GraphicUtil.drawTexture(gl, x - 126, y - 126, Config.TextureWidthLength, Config.TextureHeightLength, GameManager.playerTexture, 256, 256, Config.ChipWidth, Config.ChipHeight)
    gl.glDisable(GL10.GL_BLEND)
  }
}
object Player {
  val Radius = 100
}

 