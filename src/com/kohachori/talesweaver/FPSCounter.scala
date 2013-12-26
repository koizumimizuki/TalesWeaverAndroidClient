package com.kohachori.talesweaver

import android.util.Log
import java.util.TimerTask

object FPSCounter {

  var basetime: Long = System.currentTimeMillis()
  var count: Int = _
  var framerate: Float = _

  def update() {
    count += 1
    val now = System.currentTimeMillis()
    if (now - basetime >= 1000) {
      framerate = (count * 1000).toFloat / (now - basetime).toFloat
      basetime = now
      count = 0
    }
  }
  def draw() {
    DebugOverlay.log(s"FPS:$framerate")
  }
}
