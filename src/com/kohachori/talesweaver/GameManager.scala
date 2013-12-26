package com.kohachori.talesweaver

import javax.microedition.khronos.opengles.GL10
import android.view.MotionEvent
import scala.math._
import android.support.v4.view.MotionEventCompat
import android.media.SoundPool
import android.media.AudioManager
import android.content.Context
import android.util.Log
import android.app.Fragment

object GameManager {
  var fragment: MainFragment = _
  lazy val soundManager = new SoundManager(fragment.getActivity())
  var mapTexture: Int = _
  var playerTexture: Int = _
  var zerripiTexture: Int = _
  var player = Player(x = 500, y = 1000, goalX = 500, goalY = 1000, speed = 15, normalMotionFrameRemain = 0, skillMotionFrameRemain = 0, normalBeforeDelayRemain = 0, normalAfterDelayRemain = 0, skillBeforeDelayRemain = 0, skillAfterDelayRemain = 0, enemyOpt = None)
  var enemies: Array[Enemy] = (0 to 0).map(id => Enemy(id = id, x = 300, y = 600, goalX = 300, goalY = 600, speed = 1, hp = 1000, time = 0)).toArray
  var skills = Array.empty[Skill]
  def run() {
    skills.foreach(_.sound)
  }
  def update() {
    run()
    enemies = enemies.filter(_.exists).map(_.update) // Enemyをtraitに
    skills = skills.map(_.update).filter(_.exists)
    player = player.update
  }
  def draw(gl: GL10) {
    Map.draw(gl)
    player.draw(gl)
    enemies.foreach(_.draw(gl))
    DebugOverlay.draw(gl)
  }
}
