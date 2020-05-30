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
