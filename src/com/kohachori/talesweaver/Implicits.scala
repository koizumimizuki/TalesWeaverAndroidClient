package com.kohachori.talesweaver

import scala.util.Left

import org.json.JSONObject

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.BitmapDrawable
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast

object Implicits {

  implicit def func2OnClickListener(func: => Unit): View.OnClickListener = new View.OnClickListener() { def onClick(v: View) = func }
  implicit def func2OnCancelListener(func: => Unit): DialogInterface.OnCancelListener = new DialogInterface.OnCancelListener() { def onCancel(dialog: DialogInterface) = func }
  implicit def func2OnDissmissListener(func: => Unit): DialogInterface.OnDismissListener = new DialogInterface.OnDismissListener() { def onDismiss(dialog: DialogInterface) = func }
  implicit def func2Runnable(func: => Unit): Runnable = new Runnable() { def run() = func }
  implicit def TextView2StringOpt(editText: TextView) = if (editText.getText().toString() == "") None else Some(editText.getText().toString())
  implicit def TextView2String(editText: TextView) = editText.getText().toString()
  def runOnUiThread(f: => Unit)(implicit activity: FragmentActivity) = activity.runOnUiThread(f)
  def findViewById(id: Int)(implicit activity: FragmentActivity) = activity.findViewById(id)
  def findViewByIdAsOpt(id: Int)(implicit activity: FragmentActivity) = {
    val v = findViewById(id)(activity)
    if (v == null) None else Some(v)
  }

  implicit class RichObject(objec: Object) {
    def log = Log.v("", objec.toString)
    def toastLong(implicit context: Context) = Toast.makeText(context, objec.toString(), Toast.LENGTH_LONG).show()
    def toastShort(implicit context: Context) = Toast.makeText(context, objec.toString(), Toast.LENGTH_SHORT).show()
  }

  implicit class RichImageView(imageView: ImageView) {
    def getBitmapAsOpt = {
      val drawable = imageView.getDrawable().asInstanceOf[BitmapDrawable]
      if (drawable == null) None else Some(drawable.getBitmap())
    }
  }

  implicit class RichTextView(textView: TextView) {
    def getTextAsOpt = {
      val text = textView.getText().toString()
      if (text == "") None else Some(text)
    }
  }

  implicit class RichRadioGroup(radioGroup: RadioGroup) {
    def getCheckedRadioButtonIdAsOpt = {
      if (radioGroup.getCheckedRadioButtonId() == -1) None else Some(radioGroup.getCheckedRadioButtonId())
    }
  }

  implicit class RichJSONObject(json: JSONObject) {
    def getJSONObjectAsOpt(name: String) = try { Some(json.getJSONObject(name)) } catch { case t: Throwable => None }
    def getLongAsOpt(name: String) = try { Some(json.getLong(name)) } catch { case t: Throwable => None }
    def getStringAsOpt(name: String) = try { Some(json.getString(name)) } catch { case t: Throwable => None }
    def getIntAsOpt(name: String) = try { Some(json.getInt(name)) } catch { case t: Throwable => None }
  }
}