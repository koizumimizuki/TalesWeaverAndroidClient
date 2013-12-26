package com.kohachori.talesweaver

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView.Renderer
import android.content.Context
import scala.math._
import scala.util.Properties
import android.util.Log

case class Player(x: Int, y: Int, goalX: Int, goalY: Int, speed: Int,
  normalMotionFrameRemain: Int, skillMotionFrameRemain: Int,
  normalBeforeDelayRemain: Int, normalAfterDelayRemain: Int,
  skillBeforeDelayRemain: Int, skillAfterDelayRemain: Int, enemyOpt: Option[Enemy]) extends Drawable {

  sealed class Move
  case class ShouldRunFullSpeed extends Move
  case class ShouldNotRunFullSpeed extends Move
  case class ShouldNotMove extends Move
  case class AttackEnemy extends Move

  //TODO 通常攻撃コンボ受付時間の実装漏れ
  def canNormalAttack = skillMotionFrameRemain == 0
  def canSkillAttack = skillMotionFrameRemain == 0 && skillAfterDelayRemain == 0 || 0 < normalAfterDelayRemain
  def normalAttack(enemy: Enemy) =
    Player(x = this.x, y = this.y, goalX = this.goalX, goalY = this.goalY, speed = this.speed,
      normalMotionFrameRemain = this.normalMotionFrameRemain, skillMotionFrameRemain = this.skillMotionFrameRemain,
      normalBeforeDelayRemain = Player.NormalBeforeDelay, normalAfterDelayRemain = this.normalAfterDelayRemain,
      skillBeforeDelayRemain = this.skillBeforeDelayRemain, skillAfterDelayRemain = this.skillAfterDelayRemain, Some(enemy))
  def skillAttack(enemy: Enemy) = {
    GameManager.skills = GameManager.skills.+:(BeforeSkillRen(0))
    Player(x = this.x, y = this.y, goalX = this.goalX, goalY = this.goalY, speed = this.speed,
      normalMotionFrameRemain = this.normalMotionFrameRemain, skillMotionFrameRemain = this.skillMotionFrameRemain,
      normalBeforeDelayRemain = this.normalBeforeDelayRemain, normalAfterDelayRemain = this.normalAfterDelayRemain,
      skillBeforeDelayRemain = Player.SkillBeforeDelay, skillAfterDelayRemain = this.skillAfterDelayRemain, Some(enemy))
  }

  def changeGoal(goalX: Int, goalY: Int) =
    if (0 < normalMotionFrameRemain || 0 < skillMotionFrameRemain || 0 < skillBeforeDelayRemain) this
    else Player(this.x, this.y, goalX, goalY, this.speed,
      this.normalMotionFrameRemain, this.skillMotionFrameRemain,
      this.normalBeforeDelayRemain, this.normalAfterDelayRemain,
      this.skillBeforeDelayRemain, this.skillAfterDelayRemain, this.enemyOpt)

  def update(): Player = {
    DebugOverlay.log("==========Player==========")
    DebugOverlay.log(s"x:$x,y:$y,goalX:$goalX,goalY:$goalY")
    DebugOverlay.log(s"normalMotionFrameRemain:$normalMotionFrameRemain")
    DebugOverlay.log(s"skillMotionFrameRemain:$skillMotionFrameRemain")
    DebugOverlay.log(s"normalBeforeDelayRemain:$normalBeforeDelayRemain")
    DebugOverlay.log(s"normalAfterDelayRemain:$normalAfterDelayRemain")
    DebugOverlay.log(s"skillBeforeDelayRemain:$skillBeforeDelayRemain")
    DebugOverlay.log(s"skillAfterDelayRemain:$skillAfterDelayRemain")
    def bar(denominator: Float, numerator: Float): String = Array.fill((30 * (denominator / numerator)).toInt)("|").mkString
    def reverseBar(denominator: Float, numerator: Float): String = {
      val a: Float = 1.0f - skillBeforeDelayRemain.toFloat / Player.SkillBeforeDelay.toFloat
      val barLength =
        if (a == 1.0f) 1
        else (30 * a).toInt
      Array.fill(barLength)("|").mkString
    }
    val normalMotionBar = bar(normalMotionFrameRemain, Player.NormalMotionFrame)
    val skillMotionFrameRemainBar = bar(skillMotionFrameRemain, Player.SkillMotionFrame)
    val normalBeforeDelayRemainBar = bar(normalBeforeDelayRemain, Player.NormalBeforeDelay)
    val normalAfterDelayRemainBar = bar(normalAfterDelayRemain, Player.NormalAfterDelay)
    val skillBeforeDelayRemainBar = bar(skillBeforeDelayRemain, Player.SkillBeforeDelay)
    val skillAfterDelayRemainBar = bar(skillAfterDelayRemain, Player.SkillAfterDelay)
    DebugOverlay.log(s"normalMotionFrameRemain:$normalMotionBar")
    DebugOverlay.log(s"skillMotionFrameRemain:$skillMotionFrameRemainBar")
    DebugOverlay.log(s"normalBeforeDelayRemain:$normalBeforeDelayRemainBar")
    DebugOverlay.log(s"normalAfterDelayRemain:$normalAfterDelayRemainBar")
    DebugOverlay.log(s"skillBeforeDelayRemain:$skillBeforeDelayRemainBar")
    DebugOverlay.log(s"skillAfterDelayRemain:$skillAfterDelayRemainBar")
    val motionBar: String =
      if (0 < normalMotionFrameRemain) bar(normalMotionFrameRemain, Player.NormalMotionFrame)
      else if (0 < skillMotionFrameRemain) bar(skillMotionFrameRemain, Player.SkillMotionFrame)
      else "|"
    val normalDelaybar: String =
      if (0 < normalBeforeDelayRemain) reverseBar(normalBeforeDelayRemain, Player.NormalBeforeDelay)
      else if (0 < normalAfterDelayRemain) bar(normalAfterDelayRemain, Player.NormalAfterDelay)
      else "|"
    val skillDelaybar: String =
      if (0 < skillMotionFrameRemain) bar(1, 1)
      else if (0 < skillBeforeDelayRemain) reverseBar(skillBeforeDelayRemain, Player.SkillBeforeDelay)
      else if (0 < skillAfterDelayRemain) bar(skillAfterDelayRemain, Player.SkillAfterDelay)
      else "|"
    DebugOverlay.log(motionBar)
    DebugOverlay.log(normalDelaybar)
    DebugOverlay.log(skillDelaybar)
    def decrementIfMoreThan(frame: Int) = if (0 < frame) frame - 1 else frame
    val updatedNormalMotionFrameRemain: Int =
      if (this.normalBeforeDelayRemain == 1) {
        enemyOpt.map { enemy =>
          GameManager.skills = GameManager.skills.+:(Normal())
          GameManager.enemies.filter(_.id == enemy.id).headOption.map(_.attacked(1)).foreach { enemy =>
            GameManager.enemies = GameManager.enemies.filterNot(_.id == enemy.id).+:(enemy)
          }
        }
        Player.NormalMotionFrame
      } else decrementIfMoreThan(this.normalMotionFrameRemain)
    val updatedSkillMotionFrameRemain: Int =
      if (skillBeforeDelayRemain == 1) {
        enemyOpt.map { enemy =>
          GameManager.skills = GameManager.skills.+:(SkillRen())
          GameManager.enemies.filter(_.id == enemy.id).headOption.map(_.attacked(5)).foreach { enemy =>
            GameManager.enemies = GameManager.enemies.filterNot(_.id == enemy.id).+:(enemy)
          }
        }
        Player.SkillMotionFrame
      } else decrementIfMoreThan(skillMotionFrameRemain)
    val updatedNormalBeforeDelayRemain = decrementIfMoreThan(normalBeforeDelayRemain)
    val updatedNormalAfterDelayRemain =
      if (normalBeforeDelayRemain == 1) Player.NormalAfterDelay
      else decrementIfMoreThan(normalAfterDelayRemain)
    val updatedSkillBeforDelayRemain = decrementIfMoreThan(skillBeforeDelayRemain)
    val updatedSkillAfterDelayRemain =
      if (skillMotionFrameRemain == 1) Player.SkillAfterDelay
      else decrementIfMoreThan(skillAfterDelayRemain)
    val state = {
      val betweenGoalXAndThisXDistance = abs(goalX - x)
      val betweenGoalYAndThisYDistance = abs(goalY - y)
      if (betweenGoalXAndThisXDistance == 0 && betweenGoalYAndThisYDistance == 0 || 0 < normalMotionFrameRemain || 0 < skillMotionFrameRemain) ShouldNotMove
      else if (betweenGoalXAndThisXDistance < speed && betweenGoalYAndThisYDistance < speed) ShouldNotRunFullSpeed
      else ShouldRunFullSpeed
    }
    state match {
      case ShouldNotRunFullSpeed => Player(this.goalX, this.goalY, this.goalX, this.goalY, this.speed,
        normalMotionFrameRemain = updatedNormalMotionFrameRemain, skillMotionFrameRemain = updatedSkillMotionFrameRemain,
        normalBeforeDelayRemain = updatedNormalBeforeDelayRemain, normalAfterDelayRemain = updatedNormalAfterDelayRemain,
        skillBeforeDelayRemain = updatedSkillBeforDelayRemain, skillAfterDelayRemain = updatedSkillAfterDelayRemain,
        this.enemyOpt)
      case ShouldNotMove => Player(this.x, this.y, this.goalX, this.goalY, this.speed,
        normalMotionFrameRemain = updatedNormalMotionFrameRemain, skillMotionFrameRemain = updatedSkillMotionFrameRemain,
        normalBeforeDelayRemain = updatedNormalBeforeDelayRemain, normalAfterDelayRemain = updatedNormalAfterDelayRemain,
        skillBeforeDelayRemain = updatedSkillBeforDelayRemain, skillAfterDelayRemain = updatedSkillAfterDelayRemain,
        this.enemyOpt)
      case ShouldRunFullSpeed => {
        val vector = new Vector2D(this.goalX - this.x, this.goalY - this.y).unitVector()
        val updatedX = this.x + speed * vector.mX
        val updatedY = this.y + speed * vector.mY
        Player(updatedX.toInt, updatedY.toInt, this.goalX, this.goalY, this.speed,
          normalMotionFrameRemain = updatedNormalMotionFrameRemain, skillMotionFrameRemain = updatedSkillMotionFrameRemain,
          normalBeforeDelayRemain = updatedNormalBeforeDelayRemain, normalAfterDelayRemain = updatedNormalAfterDelayRemain,
          skillBeforeDelayRemain = updatedSkillBeforDelayRemain, skillAfterDelayRemain = updatedSkillAfterDelayRemain,
          this.enemyOpt)
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
  val NormalMotionFrame = 40
  val SkillMotionFrame = 30
  val NormalBeforeDelay = 1
  val NormalAfterDelay = 60
  val SkillBeforeDelay = 60
  val SkillAfterDelay = 100
}