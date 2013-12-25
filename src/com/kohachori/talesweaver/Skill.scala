//package com.kohachori.talesweaver
//
//import javax.microedition.khronos.egl.EGLConfig
//import javax.microedition.khronos.opengles.GL10
//import android.opengl.GLSurfaceView.Renderer
//import android.content.Context
//import scala.math._
//
//case class Skill(x: Int, y: Int, goalX: Int, goalY: Int, speed: Int, motionFrameRemain: Int, normalAfterDelayRemain: Int, skillBeforDelayRemain: Int, skillAfterDelayRemain: Int) extends Drawable {
//  val NormalMotionFrame = 200
//  val SkillMotionFrame = 300
//  val NormalAfterDelay = 300
//  val SkillBeforeDelay = 600
//  val SkillAfterDelay = 1000
//  case class ShouldRunFullSpeed
//  case class ShouldNotRunFullSpeed
//  case class ShouldNotMove
//  case class AttackEnemy
//
//  // 攻撃やディレイやに関するstateをcase classで表現する
//  def canNormalAttack = motionFrameRemain == 0 && normalAfterDelayRemain == 0 && skillAfterDelayRemain == 0
//  def canSkillAttack = motionFrameRemain == 0 && skillAfterDelayRemain == 0
//  def normalAttack =
//    Player(this.goalX, this.goalY, this.goalX, this.goalY, this.speed, NormalMotionFrame, NormalAfterDelay, this.skillBeforDelayRemain, this.skillAfterDelayRemain)
//  def skillAttack(enemy: Enemy) =
//    Player(this.goalX, this.goalY, this.goalX, this.goalY, this.speed, SkillMotionFrame, this.NormalAfterDelay, SkillBeforeDelay, this.skillAfterDelayRemain)
//
//  def changeGoal(goalX: Int, goalY: Int) =
//    if (0 < motionFrameRemain || 0 < skillBeforDelayRemain) this
//    else Player(this.x, this.y, goalX, goalY, this.speed, this.motionFrameRemain, this.normalAfterDelayRemain, this.skillBeforDelayRemain, this.skillAfterDelayRemain)
//
//  def update(): Player = {
//    println(s"motionFrameRemain:$motionFrameRemain skillBeforDelayRemain:$skillBeforDelayRemain skillAfterDelayRemain:$skillAfterDelayRemain")
//    def decrementIfMoreThan(frame: Int) = if (0 < frame) frame - 1 else frame
//    val updatedMotionFrameRemain = decrementIfMoreThan(motionFrameRemain)
//    val updatedNormalAfterDelayRemain = decrementIfMoreThan(normalAfterDelayRemain)
//    val updatedSkillBeforDelayRemain = decrementIfMoreThan(skillBeforDelayRemain)
//    val updatedSkillAfterDelayRemain = decrementIfMoreThan(skillAfterDelayRemain)
//    val state = {
//      val betweenGoalXAndThisxDistance = abs(goalX - x)
//      val betweenGoalYAndThisyDistance = abs(goalY - y)
//      if (betweenGoalXAndThisxDistance == 0 && betweenGoalYAndThisyDistance == 0 || 0 < motionFrameRemain || 0 < skillBeforDelayRemain) ShouldNotMove
//      else if (betweenGoalXAndThisxDistance < speed && betweenGoalYAndThisyDistance < speed) ShouldNotRunFullSpeed
//      else ShouldRunFullSpeed
//    }
//    state match {
//      case ShouldNotRunFullSpeed => Player(this.goalX, this.goalY, this.goalX, this.goalY, this.speed, updatedMotionFrameRemain, updatedNormalAfterDelayRemain, updatedSkillBeforDelayRemain, updatedSkillAfterDelayRemain)
//      case ShouldNotMove => Player(this.x, this.y, this.goalX, this.goalY, this.speed, updatedMotionFrameRemain, updatedNormalAfterDelayRemain, updatedSkillBeforDelayRemain, updatedSkillAfterDelayRemain)
//      case ShouldRunFullSpeed => {
//        val vector = new Vector2D(this.goalX - this.x, this.goalY - this.y).unitVector()
//        val updatedX = this.x + speed * vector.mX
//        val updatedY = this.y + speed * vector.mY
//        Player(updatedX.toInt, updatedY.toInt, goalX, goalY, speed, updatedMotionFrameRemain, updatedNormalAfterDelayRemain, updatedSkillBeforDelayRemain, updatedSkillAfterDelayRemain)
//      }
//    }
//  }
//  override def draw(gl: GL10) {
//    object Config {
//      val ChipWidth = 256 // GLViewに表示するwidth幅
//      val ChipHeight = 256
//      val TextureWidthLength = 256 // １つぶを何pxでマップに表示するか
//      val TextureHeightLength = 256
//    }
//    gl.glEnable(GL10.GL_BLEND)
//    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
//    GraphicUtil.drawTexture(gl, x - 126, y - 126, Config.TextureWidthLength, Config.TextureHeightLength, GameManager.playerTexture, 256, 256, Config.ChipWidth, Config.ChipHeight)
//    gl.glDisable(GL10.GL_BLEND)
//  }
//}
//object Player {
//  val Radius = 100
//}
//
// 