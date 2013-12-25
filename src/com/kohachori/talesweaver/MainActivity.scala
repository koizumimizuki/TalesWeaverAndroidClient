package com.kohachori.talesweaver

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.Window
import android.view.WindowManager

class MainActivity extends Activity {
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_main);
    getFragmentManager().beginTransaction()
      .replace(R.id.container_main, new MainFragment()).commit();
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater().inflate(R.menu.main, menu)
    return true
  }
}
//import android.app.Activity
//import android.os.Bundle
//import android.util.Log
//
//import android.opengl.GLSurfaceView
//import android.opengl.GLSurfaceView.Renderer
//import android.opengl.GLU
//import android.opengl.GLUtils
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.widget.ImageView
//import android.content.res.AssetManager
//import android.content.Context
//
//import android.graphics.Paint
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint.Style
//
//import javax.microedition.khronos.egl.EGLConfig
//import javax.microedition.khronos.opengles.GL10
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import java.nio.IntBuffer
//import java.lang.System
//
//class MainActivity extends Activity {
//  override def onCreate(bundle: Bundle) =
//    {
//      super.onCreate(bundle)
//      val view = new GLSurfaceView(this)
//      view.setRenderer(new GLRenderer)
//      setContentView(view)
//    }
//}
//
//class GLRenderer extends Renderer {
//  private var width = 1
//  private var height = 1
//
//  var text_drawer: TextDrawer = null
//
//  override def onSurfaceCreated(gl: GL10, config: EGLConfig) = {
//    gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
//    gl.glShadeModel(GL10.GL_FLAT)
//
//    val paint = new Paint
//    paint.setColor(0xFF000000)
//    paint.setStyle(Style.FILL)
//    paint.setTextSize(100)
//    text_drawer = new TextDrawer(gl, "hello world", paint)
//  }
//
//  override def onSurfaceChanged(gl: GL10, w: Int, h: Int) = {
//    width = w
//    height = h
//    gl.glViewport(0, 0, width, height)
//    gl.glMatrixMode(GL10.GL_PROJECTION)
//    gl.glLoadIdentity()
//    GLU.gluOrtho2D(gl, 0.0f, width, 0.0f, height)
//  }
//
//  var time: Long = System.currentTimeMillis
//  var a = "hello world"
//  var b = "hello world!!!!!!"
//
//  override def onDrawFrame(gl: GL10) = {
//    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
//
//    gl.glMatrixMode(GL10.GL_MODELVIEW)
//    gl.glLoadIdentity()
//    gl.glTranslatex(10 * 0x10000, 10 * 0x10000, 0)
//
//    gl.glScalef(0.5f, 0.5f, 1.0f)
//    text_drawer()
//
//    gl.glFlush()
//
//    val t = System.currentTimeMillis
//    if (t - time > 2000) {
//      val c = a
//      a = b
//      b = c
//      text_drawer.updateText(a, text_drawer.paint)
//      time = t
//    }
//  }
//
//  def getIntBuffer(table: Array[Int]): IntBuffer = {
//    val bb = ByteBuffer.allocateDirect(table.size * 4)
//    bb.order(ByteOrder.nativeOrder)
//    val ib: IntBuffer = bb.asIntBuffer()
//    ib.put(table)
//    ib.position(0)
//    return ib
//  }
//}
//
//class TextDrawer(private val gl: GL10, var text: String, var paint: Paint) {
//  private var width: Float = getTextWidth(text, paint)
//  private var height: Float = paint.getTextSize
//  private var texture_id = 1
//  private var img: Bitmap = Bitmap.createBitmap(toPow2(width.toInt), toPow2(height.toInt), Bitmap.Config.ARGB_8888)
//
//  img.eraseColor(0x00000000)
//  private val canvas = new Canvas(img)
//  canvas.drawText(text, 0f, paint.getTextSize - paint.descent, paint)
//
//  bindTexture
//
//  def apply() = {
//    gl.glEnable(GL10.GL_TEXTURE_2D)
//
//    gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE)
//
//    gl.glEnable(GL10.GL_BLEND)
//    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
//
//    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
//    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
//
//    val x: Int = 0x10000
//    val w = (width * x.toFloat).toInt
//    val h = (height * x.toFloat).toInt
//    val vertex = Array[Int](0, 0, w, 0, w, h, 0, h)
//
//    val tw = (width / img.getWidth.toFloat * x.toFloat).toInt
//    val th = (height / img.getHeight.toFloat * x.toFloat).toInt
//    val tvertex = Array[Int](0, th, tw, th, tw, 0, 0, 0)
//
//    gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_id)
//    gl.glVertexPointer(2, GL10.GL_FIXED, 0, getIntBuffer(vertex))
//    gl.glTexCoordPointer(2, GL10.GL_FIXED, 0, getIntBuffer(tvertex))
//    gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4)
//
//    gl.glDisable(GL10.GL_TEXTURE_2D)
//    gl.glDisable(GL10.GL_BLEND)
//    gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
//    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
//  }
//
//  def updateText(s: String, p: Paint): Unit = {
//    text = s
//    paint = p
//
//    width = getTextWidth(text, paint)
//    height = paint.getTextSize
//    val imgw = toPow2(width.toInt)
//    val imgh = toPow2(height.toInt)
//    val larger = (imgw > img.getWidth || imgh > img.getHeight)
//    if (larger) {
//      img.recycle
//      img = Bitmap.createBitmap(imgw, imgh, Bitmap.Config.ARGB_8888)
//    }
//
//    img.eraseColor(0x00000000)
//    val canvas = new Canvas(img)
//    canvas.drawColor(0)
//    canvas.drawText(text, 0f, paint.getTextSize - paint.descent, paint)
//
//    if (larger) {
//      val id: Array[Int] = Array(texture_id)
//      gl.glDeleteTextures(1, id, 0)
//      bindTexture
//    } else {
//      updateTexture
//    }
//  }
//
//  private def bindTexture = {
//    gl.glEnable(GL10.GL_TEXTURE_2D)
//    val id: Array[Int] = Array(texture_id)
//    gl.glGenTextures(1, id, 0)
//    texture_id = id(0)
//
//    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE)
//    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE)
//    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR)
//    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR)
//    gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_id)
//    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, img, 0)
//    gl.glDisable(GL10.GL_TEXTURE_2D)
//  }
//
//  private def updateTexture = {
//    gl.glEnable(GL10.GL_TEXTURE_2D)
//    gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_id)
//    GLUtils.texSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, img)
//    gl.glDisable(GL10.GL_TEXTURE_2D)
//  }
//
//  def getIntBuffer(table: Array[Int]): IntBuffer = {
//    val bb = ByteBuffer.allocateDirect(table.size * 4)
//    bb.order(ByteOrder.nativeOrder)
//    val ib: IntBuffer = bb.asIntBuffer()
//    ib.put(table)
//    ib.position(0)
//    return ib
//  }
//
//  def toPow2(a: Int): Int = a match {
//    case x if x <= 0 => 0
//    case _ =>
//      {
//        var b = 1
//        while (b < a) b *= 2
//        b
//      }
//  }
//
//  protected override def finalize = {
//    val id: Array[Int] = Array(texture_id)
//    gl.glDeleteTextures(1, id, 0)
//    super.finalize
//  }
//
//  private def getTextWidth(s: String, p: Paint): Float = {
//    val w = Array.fill(s.length)(0f)
//    p.getTextWidths(s, w)
//    w.sum
//  }
//}