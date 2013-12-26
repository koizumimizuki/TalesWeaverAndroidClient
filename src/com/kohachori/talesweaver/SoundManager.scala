package com.kohachori.talesweaver

import android.media.SoundPool
import android.content.Context
import android.media.AudioManager
import android.util.Log

class SoundManager(context: Context) {
  val sound: SoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0)
  val slashSound = sound.load(context, R.raw.s0037, 1)
  val eishoSound = sound.load(context, R.raw.s0192, 1)
}

trait Skill {
  val time: Int
  val times: Array[Int]
  def exists: Boolean = times.exists(time <= _)
  def update(): Skill
  def sound()
}
object Skill {
  val leftVolume = 1
  val rightVolume = 1
  val loop = 0
  val rate = 1
  val soundRate = 0.3
}
case class Normal(time: Int = 0) extends Skill {
  val id = GameManager.soundManager.slashSound
  override def update: Normal = Normal(time + 1)
  override def sound() = times.filter(_ == time).headOption.map(_.toInt).foreach(play)
  def play(priority: Int) = GameManager.soundManager.sound.play(id, Skill.leftVolume, Skill.rightVolume, priority, Skill.loop, Skill.rate)
  val times = Array(0)
}
case class SkillRen(time: Int = 0) extends Skill {
  val id = GameManager.soundManager.slashSound
  val times = Array(0, 20, 30, 40, 60, 75).map(_.toFloat * Skill.soundRate).map(_.toInt)
  override def update: SkillRen = SkillRen(time + 1)
  override def sound() = times.filter(_ == time).headOption.map(_.toInt).foreach(play)
  def play(priority: Int) = GameManager.soundManager.sound.play(id, Skill.leftVolume, Skill.rightVolume, priority, Skill.loop, Skill.rate)
}
case class BeforeSkillRen(time: Int = 0) extends Skill {
  val id = GameManager.soundManager.eishoSound
  val times = Array(0)
  override def update: BeforeSkillRen = BeforeSkillRen(time + 1)
  override def sound() = times.filter(_ == time).headOption.map(_.toInt).foreach(play)
  def play(priority: Int) = GameManager.soundManager.sound.play(id, Skill.leftVolume, Skill.rightVolume, priority, Skill.loop, Skill.rate)
}
