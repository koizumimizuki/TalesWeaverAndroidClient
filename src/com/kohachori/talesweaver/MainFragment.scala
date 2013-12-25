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

class MainFragment extends Fragment {

  private lazy val mGLView: GLSurfaceView = new GLSurfaceView(getActivity())

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup,
    savedInstanceState: Bundle) = {
    mGLView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR)
    mGLView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    mGLView.setEGLContextClientVersion(1)
    mGLView.setPreserveEGLContextOnPause(true)
    mGLView.setRenderer(new MainRenderer(getActivity()))
    mGLView.setOnTouchListener(new OnTouchListener() {
      override def onTouch(v: View, ev: MotionEvent): Boolean = {
        MotionManager.onTouch(ev)
        true
      }
    })
    GameManager.context = getActivity().getApplicationContext()
    GameManager.soundManager
    mGLView
  }
}
