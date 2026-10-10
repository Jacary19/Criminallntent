
package com.example.criminallntent

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            setContentView(R.layout.activity_splash)

            Handler(Looper.getMainLooper()).postDelayed({
                if (!isFinishing && !isDestroyed) {
                    setContentView(R.layout.activity_main)

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            CrimeListFragment()
                        )
                        .commit()
                }
            }, 2000)

        } else {
            setContentView(R.layout.activity_main)
        }
    }
}
