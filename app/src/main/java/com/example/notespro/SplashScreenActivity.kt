package com.example.notespro

import android.content.Intent
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.notespro.Utilities.SharedPreferencesManager
import com.example.notespro.Utilities.StatusBarManager
import kotlinx.coroutines.CoroutineScope

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(1400)

        installSplashScreen().apply {


            check()
        }


//        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.dialog_bg_color)
//
//        setContentView(R.layout.activity_splash_screen)
//
//        val icon = findViewById<ImageView>(R.id.splashIcon)
//
//        val animatedVectorDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.icon_anim)
//        animatedVectorDrawable?.let {
//            icon.setImageDrawable(it)
//            it.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
//                override fun onAnimationEnd(drawable: Drawable?) {
//                    super.onAnimationEnd(drawable)
//                    // Call your desired method here
//                    check()
//                }
//            })
//            (icon.drawable as? Animatable)?.start()
//        }

        //Animate(icon)
    }

//    private fun Animate(icon : ImageView){
//        icon.alpha = 0f
//        icon.animate()
//            .setDuration(2000)
//            .alpha(1f).withEndAction{
//                check()
//            }
//    }

    private fun check() {
        if (SharedPreferencesManager.getUser(this) == null) {
            startActivity(Intent(this, UserDetailActivity::class.java))
            finish()
        } else if (SharedPreferencesManager.getAppLockOnOff(this)) {
            startActivity(Intent(this, AppLockScreenActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}