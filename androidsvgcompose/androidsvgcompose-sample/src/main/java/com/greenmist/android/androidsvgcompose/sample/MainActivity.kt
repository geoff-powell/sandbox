package com.greenmist.android.androidsvgcompose.sample

import android.os.Bundle
import androidx.animation.AnimatedFloat
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.animation.animatedFloat
import androidx.ui.core.Draw
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.ColoredRect
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.Draggable
import androidx.ui.geometry.Offset
import androidx.ui.geometry.Rect
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutSize
import androidx.ui.material.*
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.caverock.androidsvg.SVG
import com.greenmist.android.androidsvgsompose.PreviewSvgConstants

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ui()
            }
        }
    }

    @Composable
    private fun draggableBox() {
        val posXAnim = animatedFloat(0f)
        val posYAnim = animatedFloat(0f)

        Draggable(
            dragDirection = DragDirection.Horizontal,
            dragValue = posXAnim,
            onDragValueChangeRequested = {
                posXAnim.snapTo(it)
            }) {
            Draggable(
                dragDirection = DragDirection.Vertical,
                dragValue = posYAnim,
                onDragValueChangeRequested = {
                    posYAnim.snapTo(it)
                }) {

                Column {
                    Text(text = "Pos X: ${posXAnim.value}")
                    Text(text = "Pos Y: ${posYAnim.value}")
//                Padding(start = posXAnim.value.dp, top = posYAnim.value.dp) {
                    ColoredRect(color = Color.Red, width = 200.dp, height = 200.dp)
//                }
                }
            }
        }
        Text("Pos: " + posYAnim.value.dp + " " + posYAnim.value.dp)
    }

    @Composable
    private fun ui() {
        Container(modifier = LayoutSize.Fill) {
            val svg = SVG.getFromString(PreviewSvgConstants.SVG)
            val openDialog = state { false }
            val animValue = animatedFloat(0f).apply {
                setBounds(0f, 100f)
            }
            Column {
                draggableBox()
                Switch(true, { })
                RadioGroup {
                    RadioButton(selected = false, onSelect = { })
                    RadioButton(selected = true, onSelect = { })
                    RadioButton(selected = false, onSelect = { })
                }
            }
//
//            renderAnim(animValue)
//
//            render(svg, {
//                openDialog.value = true
//            }, {
//                Log.d(TAG, "Drag - canDrag true")
//                true
//            }, object : ScaleObserver {
//                override fun onScale(scaleFactor: Float) {
//                    Log.d(TAG, "Scale - onScale $scaleFactor")
//                }
//            }, object : DragObserver {
//
//                override fun onStart(downPosition: PxPosition) {
//                    super.onStart(downPosition)
//                    Log.d(TAG, "Drag - OnStart $downPosition")
//                }
//
//                override fun onStop(velocity: PxPosition) {
//                    super.onStop(velocity)
//                    Log.d(TAG, "Drag - onStop $velocity")
//                }
//
//                override fun onDrag(dragDistance: PxPosition): PxPosition {
//                    Log.d(TAG, "Drag - onDrag $dragDistance")
//                    return super.onDrag(dragDistance)
//                }
//
//                override fun onCancel() {
//                    super.onCancel()
//                    Log.d(TAG, "Drag - onCancel")
//                }
//            })
            onDialog(openDialog)
        }
    }

    @Composable
    private fun renderAnim(animatedFloat: AnimatedFloat) {
        Column(arrangement = Arrangement.SpaceEvenly) {
            val paint = remember { Paint() }
            Container(modifier = LayoutSize.Min(250.dp, 250.dp)) {
                Draw { canvas, parentSize ->
                    val halfHeight = parentSize.height.value / 2
                    val halfWidth = parentSize.width.value / 2
                    val centerX = parentSize.width.value / 2
                    val centerY = parentSize.height.value / 2
                    val percent = animatedFloat.value / 100

                    paint.color = Color.Blue
                    canvas.drawRect(
                        Rect(
                            centerX - halfWidth * percent,
                            centerY - halfHeight * percent,
                            centerX + halfWidth * percent,
                            centerY + halfHeight * percent
                        ),
                        paint
                    )

                    paint.color = Color.White
                    canvas.drawCircle(
                        Offset(centerX, centerY),
                        radius = percent * (halfWidth / 4),
                        paint = paint
                    )
                }
            }

            Clickable({
                val target = if (animatedFloat.value > 0f) {
                    0f
                } else {
                    100f
                }
                animatedFloat.animateTo(target)
            }) {
                Text(text = "Click me to animate: Animated value: ${animatedFloat.value}")
            }
        }
    }

    @Composable
    private fun onDialog(open: MutableState<Boolean>) {
        if (open.value) {
            AlertDialog(
                onCloseRequest = {
                    open.value = false
                },
                title = {
                    Text(text = "Dialog Title")
                },
                text = {
                    Text("Here is a text ")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            open.value = false
                        }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            open.value = false
                        }) {
                        Text("Dismiss")
                    }
                },
                buttonLayout = AlertDialogButtonLayout.SideBySide
            )
        }
    }
}