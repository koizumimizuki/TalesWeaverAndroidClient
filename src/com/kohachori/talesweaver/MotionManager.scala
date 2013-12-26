package com.kohachori.talesweaver

import android.view.MotionEvent
import android.support.v4.view.MotionEventCompat
import android.util.Log

sealed class Action
case class NoneMove extends Action
case class Move(x: Int, y: Int) extends Action
case class Attack(x: Int, y: Int) extends Action
case class InvokeSkill(x: Int, y: Int) extends Action
object MotionManager {

  def onTouch(event: MotionEvent) {
    val worldX = event.getX.toInt + Camera.cameraX
    val worldY = event.getY.toInt + Camera.cameraY
    val action: Action = MotionEventCompat.getActionMasked(event) match {
      case MotionEvent.ACTION_MOVE => MotionEventCompat.getPointerCount(event) match {
        case 1 => GameManager.enemies.filter(_.isThereEnemyAtTouchedAsix(worldX, worldY))
          .headOption.map { _ =>
            if (GameManager.player.canNormalAttack) Attack(worldX, worldY)
            else NoneMove()
          }.getOrElse(Move(worldX, worldY))
        case _ => NoneMove()
      }
      case MotionEvent.ACTION_POINTER_DOWN | MotionEvent.ACTION_DOWN => MotionEventCompat.getPointerCount(event) match {
        case 1 => GameManager.enemies.filter(_.isThereEnemyAtTouchedAsix(worldX, worldY))
          .headOption.map(_ => Attack(worldX, worldY)).getOrElse(Move(worldX, worldY))
        case 2 => {
          val x0 = event.getX(0)
          val y0 = event.getY(0)
          val x1 = event.getX(1)
          val y1 = event.getY(1)
          val centerX = ((x0 + x1) / 2).toInt + Camera.cameraX
          val centerY = ((y0 + y1) / 2).toInt + Camera.cameraY
          GameManager.enemies.filter(_.isThereEnemyAtTouchedAsix(centerX, centerY))
            .headOption.map { _ =>
              if (GameManager.player.canSkillAttack) InvokeSkill(centerX, centerY)
              else NoneMove()
            }.getOrElse(NoneMove())
        }
        case _ => NoneMove()
      }
      case MotionEvent.ACTION_MOVE =>
        if (MotionEventCompat.getPointerCount(event) == 1) Move(worldX, worldY)
        else NoneMove()
      case _ => NoneMove()
    }
    //    if (action.toString == "NoneMove" == false && action.isInstanceOf[Move] == false) Log.d("", action.toString())
    action match {
      case Attack(x, y) =>
        GameManager.enemies.filter(_.isThereEnemyAtTouchedAsix(x, y)).headOption.foreach { enemy =>
          if (GameManager.player.canNormalAttack) GameManager.player = GameManager.player.normalAttack(enemy)
        }
      case InvokeSkill(x, y) =>
        GameManager.enemies.filter(_.isThereEnemyAtTouchedAsix(x, y)).headOption.foreach { enemy =>
          if (GameManager.player.canSkillAttack) GameManager.player = GameManager.player.skillAttack(enemy)
        }
      case Move(x, y) => GameManager.player = GameManager.player.changeGoal(x, y)
      case NoneMove() =>
    }
  }
}