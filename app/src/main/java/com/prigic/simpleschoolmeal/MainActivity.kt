package com.prigic.simpleschoolmeal

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val fragmentManager: FragmentManager = supportFragmentManager
    var next_day: ImageButton? = null
    var pre_day: ImageButton? = null
    var info: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        info = findViewById(R.id.info)
        pre_day = findViewById(R.id.pre_day)
        next_day = findViewById(R.id.next_day)

        fragmentManager.beginTransaction().add(R.id.page, FragmentMeal()).commit()

        pre_day!!.visibility = View.INVISIBLE
        next_day!!.visibility = View.INVISIBLE

    }
}
