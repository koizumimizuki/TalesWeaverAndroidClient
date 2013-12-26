package com.kohachori.talesweaver;

import android.app.Fragment
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnHoverListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.content.Context
import android.widget.TextView
import com.kohachori.talesweaver.Implicits._
import scala.util.Properties
import android.graphics.Color

class MainFragment extends Fragment {

  implicit class MyViewGroup(viewGroup: ViewGroup) {
    def withView(view: View) = {
      viewGroup.addView(view)
      viewGroup
    }
  }

  lazy val mTextView = new TextView(getActivity())

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    GameManager.fragment = this
    GameManager.soundManager
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup,
    savedInstanceState: Bundle) = {
    val glView = new GLSurfaceView(getActivity())
    glView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR)
    glView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    glView.setEGLContextClientVersion(1)
    glView.setPreserveEGLContextOnPause(true)
    glView.setRenderer(new MainRenderer(getActivity()))
    glView.setOnTouchListener(new OnTouchListener() {
      override def onTouch(v: View, ev: MotionEvent): Boolean = {
        MotionManager.onTouch(ev)
        true
      }
    })
    mTextView.setTextColor(Color.WHITE)
    new FrameLayout(getActivity()).withView(glView).withView(mTextView)
  }
  def log(log: Array[String]) = getActivity().runOnUiThread(mTextView.setText(Properties.lineSeparator + log.reverse.mkString(Properties.lineSeparator)))
}
