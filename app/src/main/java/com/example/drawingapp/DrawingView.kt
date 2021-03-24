package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mDrawPath: CustomPath? = null

    internal inner class CustomPath(
        var color: Int,
        var brushThickness: Float
    ) : Path() {

    }

    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint?.run {
            color = color
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvasBitmap?.let { canvas = Canvas(it) }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mCanvasBitmap?.let {
            canvas?.drawBitmap(it, 0f, 0f, mCanvasPaint)
        }

        for (path in mPaths) {
            mDrawPaint?.strokeWidth = path.brushThickness
            mDrawPaint?.color = path.color
            mDrawPaint?.let { paint ->
                canvas?.drawPath(path, paint)
            }
        }

        mDrawPath?.let {
            if (!it.isEmpty) {
                mDrawPaint?.strokeWidth = it.brushThickness
                mDrawPaint?.color = it.color
                mDrawPaint?.let { paint ->
                    canvas?.drawPath(it, paint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath?.color = color
                mDrawPath?.brushThickness = mBrushSize
                mDrawPath?.reset()
                Utility.ifNotNull(touchX, touchY) { _touchX, _touchY ->
                    mDrawPath?.moveTo(_touchX, _touchY)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                Utility.ifNotNull(touchX, touchY) { _touchX, _touchY ->
                    mDrawPath?.lineTo(_touchX, _touchY)
                }
            }

            MotionEvent.ACTION_UP -> {
                mDrawPath?.let { mPaths.add(it) }
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun setBrushSize(size: Float) {
        mBrushSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, resources.displayMetrics)
        mDrawPaint?.strokeWidth = mBrushSize
    }

    fun setBrushColor(color: String) {
        this.color = Color.parseColor(color)
        mDrawPaint?.color = this.color
    }
}