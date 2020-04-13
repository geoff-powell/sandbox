package com.greenmist.android.androidsvgsompose

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Direction
import androidx.ui.core.Text
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.ScaleObserver
import androidx.ui.foundation.Clickable
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import com.caverock.androidsvg.SVG

@Composable
fun render(
    svg: SVG,
    onClick: () -> Unit = {},
    canDrag: ((Direction) -> Boolean)? = null,
    onScale: ScaleObserver,
    onDrag: DragObserver
) {

    Clickable(onClick = onClick) {
        // content that you want to make clickable
        Text(text = "You have clicked this text")
    }

//    ScaleGestureDetector(scaleObserver = onScale) {
//        TouchSlopDragGestureDetector(
//            dragObserver = onDrag,
//            canDrag = canDrag,
//            startDragImmediately = true
//        ) {

    //        Clickable(onClick = onClick) {
//            Draw { canvas: Canvas, parentSize: PxSize ->
//                svg.renderToCanvas(canvas.nativeCanvas)
//            }
//        }
//        }
//    }
}