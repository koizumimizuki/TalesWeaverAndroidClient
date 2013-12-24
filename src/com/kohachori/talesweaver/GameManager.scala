package com.kohachori.talesweaver

import javax.microedition.khronos.opengles.GL10
import android.view.MotionEvent

object GameManager {
  var mapTexture: Int = _
  var playerTexture: Int = _
  var zerripiTexture: Int = _
  var player = Player(300, 1000, 300, 1000, 15)
  var enemy = Enemy(500, 900, 500, 900, 1, 10, 0)
  def onTouch(ev: MotionEvent) {
    ev.getAction match {
      case MotionEvent.ACTION_DOWN | MotionEvent.ACTION_MOVE => {
        val x = ev.getX.toInt + Camera.cameraX
        val y = ev.getY.toInt + Camera.cameraY
        player = player.changeGoal(x, y)
      }
      case MotionEvent.ACTION_HOVER_ENTER => println("MotionEvent.ACTION_HOVER_ENTER")
      case MotionEvent.ACTION_HOVER_MOVE => println("MotionEvent.ACTION_HOVER_MOVE")
      case MotionEvent.ACTION_HOVER_EXIT => println("MotionEvent.ACTION_HOVER_EXIT")
      case _ =>
    }
  }
  def update() {
    enemy = enemy.update()
    player = player.update()
  }
  def draw(gl: GL10) {
    Map.draw(gl)
    player.draw(gl)
    enemy.draw(gl)
  }
}