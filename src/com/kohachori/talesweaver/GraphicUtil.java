package com.kohachori.talesweaver;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Hashtable;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.opengl.GLUtils;

public class GraphicUtil {

	// 配列オブジェクトを保持する
	private static Hashtable<Integer, float[]> verticesPool = new Hashtable<Integer, float[]>();
	private static Hashtable<Integer, float[]> colorsPool = new Hashtable<Integer, float[]>();
	private static Hashtable<Integer, float[]> coordsPool = new Hashtable<Integer, float[]>();

	// プールの取得です （nは頂点数）
	public static float[] getVertices(int n) {
		if (verticesPool.containsKey(n)) {
			return verticesPool.get(n);
		}
		float[] vertices = new float[n];
		verticesPool.put(n, vertices);
		return vertices;
	}

	// プールの取得です （nは要素数）
	public static float[] getColors(int n) {
		if (colorsPool.containsKey(n)) {
			return colorsPool.get(n);
		}
		float[] colors = new float[n];
		colorsPool.put(n, colors);
		return colors;
	}

	public static float[] getCoords(int n) {
		if (coordsPool.containsKey(n)) {
			return coordsPool.get(n);
		}
		float[] coords = new float[n];
		coordsPool.put(n, coords);
		return coords;
	}

	// バッファオブジェクトを保持する
	private static Hashtable<Integer, FloatBuffer> squareVerticesPool = new Hashtable<Integer, FloatBuffer>();
	private static Hashtable<Integer, FloatBuffer> squareColorsPool = new Hashtable<Integer, FloatBuffer>();
	private static Hashtable<Integer, FloatBuffer> texCoordsPool = new Hashtable<Integer, FloatBuffer>();

	public static final FloatBuffer makeVerticesBuffer(float[] arr) {
		FloatBuffer fb = null;
		if (squareVerticesPool.containsKey(arr.length)) {
			fb = squareVerticesPool.get(arr.length);
			fb.clear();
			fb.put(arr);
			fb.position(0);
			return fb;
		}
		fb = makeFloatBuffer(arr);
		squareVerticesPool.put(arr.length, fb);
		return fb;
	}

	public static final FloatBuffer makeColorsBuffer(float[] arr) {
		FloatBuffer fb = null;
		if (squareColorsPool.containsKey(arr.length)) {
			fb = squareColorsPool.get(arr.length);
			fb.clear();
			fb.put(arr);
			fb.position(0);
			return fb;
		}
		fb = makeFloatBuffer(arr);
		squareColorsPool.put(arr.length, fb);
		return fb;
	}

	public static final FloatBuffer makeTexCoordsBuffer(float[] arr) {
		FloatBuffer fb = null;
		if (texCoordsPool.containsKey(arr.length)) {
			fb = texCoordsPool.get(arr.length);
			fb.clear();
			fb.put(arr);
			fb.position(0);
			return fb;
		}
		fb = makeFloatBuffer(arr);
		texCoordsPool.put(arr.length, fb);
		return fb;
	}

	public static final void drawTexture(GL10 gl, float x, float y, float w,
			float h, // 描画先
			int texture, float u, float v, float tex_w, float tex_h, // 描画元
			float r, float g, float b, float a // 色
	) {
		// 要素数8の頂点バッファ用意 （XY(2値) * 4頂点 = 8要素）
		// これは描画先の頂点リストを示す
		float[] vertices = getVertices(8);
		/*
		 * vertices[0] = -0.5f * w + x; vertices[1] = -0.5f * h + y; vertices[2]
		 * = 0.5f * w + x; vertices[3] = -0.5f * h + y; vertices[4] = -0.5f * w
		 * + x; vertices[5] = 0.5f * h + y; vertices[6] = 0.5f * w + x;
		 * vertices[7] = 0.5f * h + y;
		 */
		int vi = 0;
		vertices[vi++] = x;
		vertices[vi++] = y + h;
		vertices[vi++] = x + w;
		vertices[vi++] = y + h;
		vertices[vi++] = x;
		vertices[vi++] = y;
		vertices[vi++] = x + w;
		vertices[vi++] = y;

		// 要素数16の色バッファ用意 （RGBA(4値) * 4頂点 = 16要素）
		float[] colors = getColors(16);
		for (int i = 0; i < 16; i++) {
			colors[i++] = r;
			colors[i++] = g;
			colors[i++] = b;
			colors[i] = a;
		}

		// 要素数8の頂点バッファ用意 （XY(2値) * 4頂点 = 8要素）
		// これは描画元の頂点リストを示す
		float[] coords = getCoords(8);
		coords[0] = u;
		coords[1] = v + tex_h;
		coords[2] = u + tex_w;
		coords[3] = v + tex_h;
		coords[4] = u;
		coords[5] = v;
		coords[6] = u + tex_w;
		coords[7] = v;

		FloatBuffer squareVertices = makeVerticesBuffer(vertices);
		FloatBuffer squareColors = makeColorsBuffer(colors);
		FloatBuffer texCoords = makeTexCoordsBuffer(coords);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, squareVertices);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, squareColors);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoords);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

	public static final void drawTexture(GL10 gl, float x, float y, float w,
			float h, // 描画先
			int texture, // 描画元 (座標指定無し)
			float r, float g, float b, float a // 色
	) {
		drawTexture(gl, x, y, w, h, texture, 0.0f, 0.0f, 1.0f, 1.0f, r, g, b, a);
	}

	public static final int loadTexture(GL10 gl, Resources resources,
			String path) {
		int[] textures = new int[1];
		Bitmap bmp = null;
		try {
			InputStream inputStream = resources.getAssets().open(path);
			// Bitmapの作成
			bmp = BitmapFactory.decodeStream(inputStream, new Rect(0, 0, 0, 0),
					options);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bmp == null) {
			return 0;
		}

		// OpenGL用のテクスチャを生成します
		gl.glGenTextures(1, textures, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

		// OpenGLへの転送が完了したので、VMメモリ上に作成したBitmapを破棄する
		bmp.recycle();

		return textures[0];
	}

	public static final int loadTexture(GL10 gl, Resources resources, int res) {
		int[] textures = new int[1];

		// Bitmapの作成
		Bitmap bmp = BitmapFactory.decodeResource(resources, res, options);
		if (bmp == null) {
			return 0;
		}

		// OpenGL用のテクスチャを生成します
		gl.glGenTextures(1, textures, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

		// OpenGLへの転送が完了したので、VMメモリ上に作成したBitmapを破棄する
		bmp.recycle();

		// TextureManagerに登録する
		TextureManager.addTexture(res, textures[0]);

		return textures[0];
	}

	private static final BitmapFactory.Options options = new BitmapFactory.Options();
	static {
		options.inScaled = false;// リソースの自動リサイズをしない
		options.inPreferredConfig = Config.ARGB_8888;// 32bit画像として読み込む
	}

	public static final void drawCircle(GL10 gl, Vector2D center, int divides,
			float radius, float r, float g, float b, float a) {
		drawCircle(gl, center.mX, center.mY, divides, radius, r, g, b, a);
	}

	/**
	 * x,yは円の中心座標
	 * 
	 * @param gl
	 * @param x
	 * @param y
	 * @param divides
	 * @param radius
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static final void drawCircle(GL10 gl, float x, float y, int divides,
			float radius, float r, float g, float b, float a) {
		float[] vertices = getVertices(divides * 3 * 2);

		int vertexId = 0;// 頂点配列の要素の番号を記憶しておくための変数
		for (int i = 0; i < divides; i++) {
			// i番目の頂点の角度(ラジアン)を計算します
			float theta1 = 2.0f / (float) divides * (float) i * (float) Math.PI;

			// (i + 1)番目の頂点の角度(ラジアン)を計算します
			float theta2 = 2.0f / (float) divides * (float) (i + 1)
					* (float) Math.PI;

			// i番目の三角形の0番目の頂点情報をセットします
			vertices[vertexId++] = x;
			vertices[vertexId++] = y;

			// i番目の三角形の1番目の頂点の情報をセットします (円周上のi番目の頂点)
			vertices[vertexId++] = (float) Math.cos((double) theta1) * radius
					+ x;// x座標
			vertices[vertexId++] = (float) Math.sin((double) theta1) * radius
					+ y;// y座標

			// i番目の三角形の2番目の頂点の情報をセットします (円周上のi+1番目の頂点)
			vertices[vertexId++] = (float) Math.cos((double) theta2) * radius
					+ x;// x座標
			vertices[vertexId++] = (float) Math.sin((double) theta2) * radius
					+ y;// y座標
		}
		FloatBuffer squareVertices = makeVerticesBuffer(vertices);

		// ポリゴンの色を指定します
		float[] colors = getColors(divides * 3 * 4);
		for (int i = 0; i < colors.length; i++) {
			colors[i++] = r;
			colors[i++] = g;
			colors[i++] = b;
			colors[i] = a;
		}
		FloatBuffer c = makeColorsBuffer(colors);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, c);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, squareVertices);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, divides * 3);
	}

	public static final void drawRectangle(GL10 gl, float x, float y, float w,
			float h, float r, float g, float b, float a) {
		float[] vertices = getVertices(8);
		vertices[0] = -0.5f * w + x;
		vertices[1] = -0.5f * h + y;
		vertices[2] = 0.5f * w + x;
		vertices[3] = -0.5f * h + y;
		vertices[4] = -0.5f * w + x;
		vertices[5] = 0.5f * h + y;
		vertices[6] = 0.5f * w + x;
		vertices[7] = 0.5f * h + y;

		float[] colors = getColors(16);
		for (int i = 0; i < 16; i++) {
			colors[i++] = r;
			colors[i++] = g;
			colors[i++] = b;
			colors[i] = a;
		}

		FloatBuffer squareVertices = makeVerticesBuffer(vertices);
		FloatBuffer squareColors = makeColorsBuffer(colors);

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, squareVertices);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, squareColors);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	}

	public static final void drawSquare(GL10 gl, float x, float y, float r,
			float g, float b, float a) {
		drawRectangle(gl, x, y, 1.0f, 1.0f, r, g, b, a);
	}

	public static final void drawSquare(GL10 gl, float r, float g, float b,
			float a) {
		drawSquare(gl, 0.0f, 0.0f, r, g, b, a);
	}

	public static final void drawSquare(GL10 gl) {
		drawSquare(gl, 1.0f, 0, 0, 1.0f);
	}

	public static final FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
}
