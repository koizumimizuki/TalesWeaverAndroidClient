package com.kohachori.talesweaver

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView.Renderer
import android.content.Context
import scala.math._
import scala.util.Properties
import android.util.Log

case class Player(x: Int, y: Int, goalX: Int, goalY: Int, speed: Int, normalMotionFrameRemain: Int, skillMotionFrameRemain: Int, normalBeforeDelayRemain: Int, normalAfterDelayRemain: Int, skillBeforDelayRemain: Int, skillAfterDelayRemain: Int, enemyOpt: Option[Enemy]) extends Drawable {

  sealed class Move
  case class ShouldRunFullSpeed extends Move
  case class ShouldNotRunFullSpeed extends Move
  case class ShouldNotMove extends Move
  case class AttackEnemy extends Move

  //TODO 通常攻撃コンボ受付時間の実装漏れ
  val NormalMotionFrame = 5
  val SkillMotionFrame = 30
  val NormalBeforeDelay = 1
  val NormalAfterDelay = 60
  val SkillBeforeDelay = 60
  val SkillAfterDelay = 100
  def canNormalAttack = skillMotionFrameRemain == 0 && normalAfterDelayRemain == 0
  def canSkillAttack = skillMotionFrameRemain == 0 && skillAfterDelayRemain == 0
  def normalAttack(enemy: Enemy) =
    Player(this.x, this.y, this.goalX, this.goalY, this.speed, this.normalMotionFrameRemain, this.skillMotionFrameRemain, NormalBeforeDelay, this.normalAfterDelayRemain, this.skillBeforDelayRemain, this.skillAfterDelayRemain, Some(enemy))
  def skillAttack(enemy: Enemy) = {
    Log.d("", "skillAttack")
    GameManager.skills = GameManager.skills.+:(BeforeSkillRen(0))
    Player(this.x, this.y, this.goalX, this.goalY, this.speed, this.normalMotionFrameRemain, this.skillMotionFrameRemain, this.normalBeforeDelayRemain, this.NormalAfterDelay, SkillBeforeDelay, this.skillAfterDelayRemain, Some(enemy))
  }

  def changeGoal(goalX: Int, goalY: Int) =
    if (0 < skillMotionFrameRemain || 0 < skillBeforDelayRemain) this
    else Player(this.x, this.y, goalX, goalY, this.speed, this.normalMotionFrameRemain, this.skillMotionFrameRemain, this.normalBeforeDelayRemain, this.normalAfterDelayRemain, this.skillBeforDelayRemain, this.skillAfterDelayRemain, this.enemyOpt)

  def update(): Player = {
    DebugOverlay.log("==========Player==========")
    DebugOverlay.log(s"x:$x,y:$y,goalX:$goalX,goalY:$goalY")
    def decrementIfMoreThan(frame: Int) = if (0 < frame) frame - 1 else frame
    val updatedNormalAfterDelayRemain = decrementIfMoreThan(normalAfterDelayRemain)
    val updatedMotionFrameRemain: Int =
      if (normalBeforeDelayRemain == 1) {
        enemyOpt.map { enemy =>
          GameManager.enemies.filter(_.id == enemy.id).headOption.map(_.attacked(1)).foreach { enemy =>
            GameManager.enemies = GameManager.enemies.filterNot(_.id == enemy.id).+:(enemy)
          }
        }
        GameManager.skills = GameManager.skills.+:(Normal())
        Log.d("Player", s"通常攻撃発動 スキル後ディレイ残り$skillAfterDelayRemain")
        NormalMotionFrame
      } else if (skillBeforDelayRemain == 1) {
        enemyOpt.map { enemy =>
          GameManager.enemies.filter(_.id == enemy.id).headOption.map(_.attacked(5)).foreach { enemy =>
            GameManager.enemies = GameManager.enemies.filterNot(_.id == enemy.id).+:(enemy)
          }
        }
        Log.d("Player", s"スキル発動 通常攻撃後ディレイ残り$normalAfterDelayRemain")
        GameManager.skills = GameManager.skills.+:(SkillRen())
        SkillMotionFrame
      } else decrementIfMoreThan(skillMotionFrameRemain)
    val updatedSkillAfterDelayRemain = decrementIfMoreThan(skillAfterDelayRemain)
    val updatedNormalBeforeDelayRemain = decrementIfMoreThan(normalBeforeDelayRemain)
    val updatedSkillBeforDelayRemain = decrementIfMoreThan(skillBeforDelayRemain)
    val state = {
      val betweenGoalXAndThisXDistance = abs(goalX - x)
      val betweenGoalYAndThisYDistance = abs(goalY - y)
      if (betweenGoalXAndThisXDistance == 0 && betweenGoalYAndThisYDistance == 0 || 0 < skillMotionFrameRemain || 0 < skillBeforDelayRemain) ShouldNotMove
      else if (betweenGoalXAndThisXDistance < speed && betweenGoalYAndThisYDistance < speed) ShouldNotRunFullSpeed
      else ShouldRunFullSpeed
    }
    state match {
      case ShouldNotRunFullSpeed => Player(this.goalX, this.goalY, this.goalX, this.goalY, this.speed, this.normalMotionFrameRemain, updatedMotionFrameRemain, updatedNormalBeforeDelayRemain, updatedNormalAfterDelayRemain, updatedSkillBeforDelayRemain, updatedSkillAfterDelayRemain, this.enemyOpt)
      case ShouldNotMove => Player(this.x, this.y, this.goalX, this.goalY, this.speed, this.normalMotionFrameRemain, updatedMotionFrameRemain, updatedNormalBeforeDelayRemain, updatedNormalAfterDelayRemain, updatedSkillBeforDelayRemain, updatedSkillAfterDelayRemain, this.enemyOpt)
      case ShouldRunFullSpeed => {
        val vector = new Vector2D(this.goalX - this.x, this.goalY - this.y).unitVector()
        val updatedX = this.x + speed * vector.mX
        val updatedY = this.y + speed * vector.mY
        Player(updatedX.toInt, updatedY.toInt, goalX, goalY, speed, this.normalMotionFrameRemain, updatedMotionFrameRemain, updatedNormalBeforeDelayRemain, updatedNormalAfterDelayRemain, updatedSkillBeforDelayRemain, updatedSkillAfterDelayRemain, this.enemyOpt)
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

 