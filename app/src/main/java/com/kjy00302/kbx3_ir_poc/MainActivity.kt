package com.kjy00302.kbx3_ir_poc

import android.graphics.Color
import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var consumerIrManager: ConsumerIrManager

    private lateinit var redSeekBar: SeekBar
    private lateinit var greenSeekBar: SeekBar
    private lateinit var blueSeekBar: SeekBar
    private lateinit var whiteSeekBar: SeekBar
    private lateinit var colorView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        consumerIrManager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            redSeekBar = v.findViewById(R.id.redSeekBar)
            greenSeekBar = v.findViewById(R.id.greenSeekBar)
            blueSeekBar = v.findViewById(R.id.blueSeekBar)
            whiteSeekBar = v.findViewById(R.id.whiteSeekBar)
            colorView = v.findViewById(R.id.colorView)

            redSeekBar.setOnSeekBarChangeListener(onUpdateSeekBar)
            greenSeekBar.setOnSeekBarChangeListener(onUpdateSeekBar)
            blueSeekBar.setOnSeekBarChangeListener(onUpdateSeekBar)
            whiteSeekBar.setOnSeekBarChangeListener(onUpdateSeekBar)

            v.findViewById<Button>(R.id.sendButton).setOnClickListener {
                val pkt = KBX3.newShowColorPacket(
                    redSeekBar.progress.toByte(),
                    greenSeekBar.progress.toByte(),
                    blueSeekBar.progress.toByte(),
                    whiteSeekBar.progress.toByte(),
                )
                KBX3.scramblePacket(pkt)
                consumerIrManager.transmit(38000, KBX3.toIRPattern(pkt))
            }

            insets
        }
    }

    private val onUpdateSeekBar = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekbar: SeekBar, progress: Int, fromUser: Boolean) {
            // https://stackoverflow.com/a/51809506
            val r = redSeekBar.progress / 255f
            val g = greenSeekBar.progress / 255f
            val b = blueSeekBar.progress / 255f
            val w = whiteSeekBar.progress / 255f

            val w2 = (1.0f - (1.0f - w) * (1.0f - w))
            val alpha = 1.0f * w2
            colorView.setBackgroundColor(
                Color.rgb(
                    (((1.0f - alpha) * r + alpha) * 255).roundToInt(),
                    (((1.0f - alpha) * g + alpha) * 255).roundToInt(),
                    (((1.0f - alpha) * b + alpha) * 255).roundToInt(),
                )
            )
        }

        override fun onStartTrackingTouch(p0: SeekBar?) = Unit

        override fun onStopTrackingTouch(p0: SeekBar?) = Unit

    }
}