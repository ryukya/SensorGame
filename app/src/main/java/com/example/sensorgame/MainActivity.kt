package com.example.sensorgame

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView


class MainActivity : AppCompatActivity(), SensorEventListener {

     var ball = ShapeDrawable()
     var sensorManager: SensorManager? = null
     var accelerometer: Sensor? = null
    var dWidth = 0
    var dHeight = 0
    var wTouched = false
    var animatedView: AnimatedView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        counter = 0
        animatedView = AnimatedView(this)
        val displayMetrics = DisplayMetrics()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        dHeight = displayMetrics.heightPixels - 150
        dWidth = displayMetrics.widthPixels - 150
        Log.v("Y Size:", Integer.toString(dHeight))
        Log.v("X Size:", Integer.toString(dWidth))
        xAcc = dWidth / 3
        yAcc = dHeight / 3
        if (accelerometer != null) {
            sensorManager!!.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
        setContentView(animatedView)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu
        menuInflater.inflate(R.menu.helpm, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item
        val id = item.getItemId()

        if (id == R.id.help) {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle(getString(R.string.h_m))
            builder.setMessage(getString(R.string.h_cont))
            builder.setNeutralButton(getString(R.string.h_btn)){_,_ ->
                Toast.makeText(applicationContext,getString(R.string.h_ok),Toast.LENGTH_SHORT).show()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            xAcc -= event.values[0].toInt()
            yAcc += event.values[1].toInt()
            if (xAcc > dWidth) {
                xAcc = dWidth
                LostFunc()
            } else if (xAcc < 0) {
                xAcc = 0
                LostFunc()
            }
            if (yAcc > dHeight - 100) {
                yAcc = dHeight - 100
                LostFunc()
            } else if (yAcc < 0) {
                yAcc = 0
                LostFunc()
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor,
        accuracy: Int
    ) {
    }

    fun LostFunc() {
        wTouched = true
        counter++
        resetPosition()
        if (counter in 1..3) {
            var life= 4 - counter
            onPause()
            Toast.makeText(
                applicationContext,
                " Only $life life left! Keep the ball from the walls!",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (counter>3){
            Toast.makeText(
                applicationContext,
                "You lost all your life!",
                Toast.LENGTH_SHORT
            ).show()
            showDialog()
        }
    }
    fun reset(){
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            startActivity(intent)
        }, 1000)
    }
    private fun showDialog(){
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.loseT))
        builder.setMessage(getString(R.string.yon))
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> reset()
                DialogInterface.BUTTON_NEGATIVE -> finishAffinity()
            }
        }
        // Set the alert dialog positive/yes button
        builder.setPositiveButton("YES",dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("NO",dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }
    fun resetPosition() {
        yAcc = dHeight / 2
        xAcc = dWidth / 2
    }

    inner class AnimatedView(context: Context?) :

        AppCompatImageView(context) {
        var paint = Paint()
        override fun onDraw(canvas: Canvas) {
            paint.color = Color.WHITE
            paint.isFakeBoldText= true
            paint.typeface= Typeface.SERIF
            paint.textSize = 60f
            ball.setBounds(
                xAcc,
                yAcc,
                xAcc + Companion.width,
                yAcc + Companion.height
            )
            canvas.drawColor(Color.parseColor("#FFFF5722"))
            canvas.drawText(
                "Lifes you have: " + Integer.toString(4-counter),
                150f,
                100f,
                paint
            )
            ball.draw(canvas)
            invalidate()
        }

        init {
            ball = ShapeDrawable(OvalShape())
            ball.paint.color = Color.parseColor("Black")
        }
    }

    companion object {
        const val width = 100
        const val height = 100
        var xAcc = 0
        var yAcc = 0
        var counter = 0
        
    }
}
