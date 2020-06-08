package com.ono.minipaint

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import android.graphics.Shader
import android.graphics.LinearGradient
import androidx.core.content.ContextCompat
import kotlin.math.abs

private const val STROKE_WIDTH = 12f

class MyCanvasView(context: Context) : View(context) {
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    private val colors = mutableListOf<Int>().apply {
        add( ResourcesCompat.getColor(resources, R.color.color1, null))
        add( ResourcesCompat.getColor(resources, R.color.color2, null))
        add( ResourcesCompat.getColor(resources, R.color.color3, null))
        add( ResourcesCompat.getColor(resources, R.color.color4, null))
        add( ResourcesCompat.getColor(resources, R.color.color5, null))
    }

    private val paint by lazy{
        Paint().apply {
            //color = drawColor
            isAntiAlias = true // 안티엘리어싱
            isDither = true // 디더링
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND // 선과 곡선이 겹칠 때
            strokeCap = Paint.Cap.ROUND // 끝모양
            strokeWidth = STROKE_WIDTH
            shader = LinearGradient(0F, 0F, width.toFloat(), height.toFloat(), colors.toIntArray(), null, Shader.TileMode.REPEAT)
        }
    }

    private var path = Path()

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private lateinit var defaultBitmap : Bitmap

    //최소한 touchTolerance 정도는 움직여야지 draw
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::extraBitmap.isInitialized)
            extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    //터치 시작
    fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    //드로우
    fun touchMove() {
        // 이전의 값과 현재 위치의 차이
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)

        if (dx >= touchTolerance || dy >= touchTolerance) {
            // 자연스러운 곡선을 그리기 위한 이차베지어 곡선(x1,y1,x2,y2)
            // https://blog.coderifleman.com/2016/12/30/bezier-curves/
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )

            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    //끝
    fun touchUp() {
        path.reset()
        extraCanvas.drawColor(backgroundColor)
    }
}