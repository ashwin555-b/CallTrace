
package com.example.calltrace

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.telecom.TelecomManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity
import android.graphics.Color
import android.view.ViewGroup

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 40, 32, 40)
            setBackgroundColor(Color.rgb(20, 18, 35))
        }

        val title = TextView(this).apply {
            text = "CallTrace"
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
        }

        val subtitle = TextView(this).apply {
            text = "Caller ID • Country • Region • Place"
            textSize = 17f
            gravity = Gravity.CENTER
            setTextColor(Color.LTGRAY)
            setPadding(0, 24, 0, 24)
        }

        val button = Button(this).apply {
            text = "Enable Caller Identification"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val telecom =
                        getSystemService(TELECOM_SERVICE) as TelecomManager

                    startActivity(
                        telecom.createManageCallScreeningRoleIntent()
                    )
                } else {
                    startActivity(
                        Intent(Settings.ACTION_SETTINGS)
                    )
                }
            }
        }

        layout.addView(
            title,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.addView(
            subtitle,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.addView(
            button,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        setContentView(layout)
    }
}
