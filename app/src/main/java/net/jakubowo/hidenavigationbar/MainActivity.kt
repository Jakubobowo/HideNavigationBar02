package net.jakubowo.hidenavigationbar

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var appExit: TextView
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        hideNavigationBar(window)

        initToolBar()


        appExit = findViewById(R.id.app_exit_button)
        appExit.setOnLongClickListener {
            exitFromApp()
        }
        appExit.setOnClickListener(DoubleClickListener(
            callback = object : DoubleClickListener.Callback {
                override fun doubleClicked() {
                    exitFromApp()
                }
            }
          )
        )

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_exit -> {
                finishAffinity()
                exitProcess(0)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    fun initToolBar() {
        toolbar = findViewById<Toolbar>(R.id.toolbar)

        toolbar.title = ""

        val overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu)

        toolbar.overflowIcon = overflowIcon

        setSupportActionBar(toolbar)
    }

    fun hideNavigationBar(mWindow: Window) {
        WindowCompat.setDecorFitsSystemWindows(mWindow, false)

        WindowInsetsControllerCompat(
            mWindow,
            mWindow.decorView.findViewById(android.R.id.content)
        ).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())

            // When the screen is swiped up at the bottom of the application, the navigationBar shall appear for some time
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }


    fun exitFromApp(): Boolean {
        finishAffinity()
        exitProcess(0)
    }

}

class DoubleClickListener(
    private val doubleClickTimeLimitMills: Long = 1000,
    private val callback: Callback,
) :
    View.OnClickListener {
    private var lastClicked: Long = -1L

    override fun onClick(v: View?) {
        lastClicked = when {
            lastClicked == -1L -> {
                System.currentTimeMillis()
            }
            isDoubleClicked() -> {
                callback.doubleClicked()
                -1L
            }
            else -> {
                System.currentTimeMillis()
            }
        }
    }

    private fun getTimeDiff(from: Long, to: Long): Long {
        return to - from
    }

    private fun isDoubleClicked(): Boolean {
        return getTimeDiff(
            lastClicked,
            System.currentTimeMillis()
        ) <= doubleClickTimeLimitMills
    }

    interface Callback {
        fun doubleClicked()
    }
}
