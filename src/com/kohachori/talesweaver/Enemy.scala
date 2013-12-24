package com.kohachori.talesweaver

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView.Renderer
import android.content.Context
import scala.math._
import scala.util.Random
//ベクトルの定義

case class Enemy(x: Int, y: Int, goalX: Int, goalY: Int, speed: Int, hp: Int, time: Int) extends Drawable {
  def changeGoal(goalX: Int, goalY: Int): Enemy = Enemy(this.x, this.y, goalX, goalY, this.speed, this.hp, this.time)
  def update(): Enemy = {
    val updatedX = if (x == goalX) x else if (x < goalX) x + speed else x - speed
    val updatedY = if (y == goalY) y else if (y < goalY) y + speed else y - speed
    val random = if (Random.nextBoolean) 1 else -1
    val updatedGoalX = if (time % 60 == 0) goalX + new java.util.Random().nextInt(100) * random else goalX
    val updatedGoalY = if (time % 60 == 0) goalY + new java.util.Random().nextInt(100) * random else goalY
    val updatedTime = time + 1
    Enemy(updatedX, updatedY, updatedGoalX, updatedGoalY, this.speed, this.hp, updatedTime)
  }
  def attacked(damage: Int) = Enemy(this.x, this.y, this.goalX, this.goalY, this.speed, this.hp - damage, this.time)
  override def draw(gl: GL10) {
    object Config {
      val ChipWidth = 128 // GLViewに表示するwidth幅
      val ChipHeight = 128
      val TextureWidthLength = 128 // １つぶを何pxでマップに表示するか
      val TextureHeightLength = 128
    }
    gl.glEnable(GL10.GL_BLEND)
    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    GraphicUtil.drawTexture(gl, x - 64, y - 64, Config.TextureWidthLength, Config.TextureHeightLength, GameManager.zerripiTexture, 256, 256, Config.ChipWidth, Config.ChipHeight)
    gl.glDisable(GL10.GL_BLEND)
  }
}
object Enemy {
  val Radius = 100
}