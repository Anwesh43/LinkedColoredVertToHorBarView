package com.anwesh.uiprojects.coloredvtohview

/**
 * Created by anweshmishra on 30/05/20.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Canvas

val parts : Int = 3
val colors : Array<String> = arrayOf("#4CAF50", "#2196F3", "#F44336", "#009688", "#00BCD4")
val scGap : Float = 0.02f / parts
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
val sizeFactor : Float = 2.9f
val rot : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawColoredVToH(i : Int, sf : Float, w : Float, size : Float, paint : Paint) {

    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    val si : Float = 1f - 2 * i
    val x : Float = w / 2 - size / 2
    val sx : Float = w / 2 - size
    save()
    translate(i * sx + x * si * sf2, size * i)
    drawRect(RectF(size * (1 - sf1) * i, 0f, size * i + size * (1 - i) * sf1, size), paint)
    restore()
}

fun Canvas.drawColoredVToHBars(scale : Float, w : Float, size : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sf3 : Float = sf.divideScale(2, parts)
    for (j in 0..(parts - 2)) {
        save()
        rotate(rot * sf3)
        drawColoredVToH(j, sf, w, size, paint)
        restore()
    }
}

fun Canvas.drawCVTHNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val size : Float = Math.min(w, h) / sizeFactor
    paint.color = Color.parseColor(colors[i])
    save()
    translate(w / 2, h / 2)
    drawColoredVToHBars(scale, w, size, paint)
    restore()
}

class ColoredVToHView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += dir * scGap
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class CVTHNode(var i : Int, val state : State = State()) {

        private var next : CVTHNode? = null
        private var prev : CVTHNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = CVTHNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawCVTHNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CVTHNode {
            var curr : CVTHNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class ColoredVToHBar(var i : Int) {

        private var curr : CVTHNode = CVTHNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }
}