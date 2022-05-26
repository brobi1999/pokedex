package hu.bme.aut.pokedex.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.view.PixelCopy
import android.view.View
import androidx.annotation.RequiresApi
import hu.bme.aut.pokedex.R
import hu.bme.aut.pokedex.databinding.LabelTypeBinding
import hu.bme.aut.pokedex.databinding.LayoutStatBinding
import hu.bme.aut.pokedex.model.ui.Poke

class MyUtil {
    companion object {


        /**
         * Take the screenshot of the view. Its deprecated after API 27. Use
         * @see getBitmapFromView
         * @param activity
         * @param callback as Bitmap
         */
        private fun takeScreenShot(activity: Activity, callback: (Bitmap) -> Unit) {
            val view = activity.window.decorView
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()


            val b1 = view.drawingCache
            val frame = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(frame)
            val statusBarHeight = frame.top

            val display = activity.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val width = size.x
            val height = size.y


            val b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight)
            view.destroyDrawingCache()

            callback(b)
        }


        /**
         * Take screenshot using PixelCopy api for android Oreo and above
         * @param view
         * @param activity
         * @param callback as Bitmap
         */
        @RequiresApi(Build.VERSION_CODES.O)
        private fun takeScreenShotOreo(activity: Activity, callback: (Bitmap) -> Unit) {
            activity.window?.let { window ->

                val view = activity.window.decorView

                val frame = Rect()
                activity.window.decorView.getWindowVisibleDisplayFrame(frame)
                val statusBarHeight = frame.top

                val bitmap = Bitmap.createBitmap(view.width, view.height - statusBarHeight, Bitmap.Config.ARGB_8888)
                val locationOfViewInWindow = IntArray(2)
                view.getLocationInWindow(locationOfViewInWindow)
                try {
                    PixelCopy.request(window, Rect(locationOfViewInWindow[0], locationOfViewInWindow[1], locationOfViewInWindow[0] + view.width, locationOfViewInWindow[1] + view.height), bitmap, { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            callback(bitmap)
                        }
                    }, Handler(activity.mainLooper))
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }


        fun screenShot(activity: Activity, callback: (Bitmap) -> Unit) =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) takeScreenShotOreo(activity, callback)
            else takeScreenShot(activity, callback)

        fun configureLabelTypeLayout(labelTypeLayout: LabelTypeBinding, pokeType: String, context: Context){
            when(pokeType){
                "grass" -> {
                    labelTypeLayout.let {
                        it.labelBackgroundCardView.setCardBackgroundColor(Color.GREEN)
                        it.tvLabelType.text = context.getString(R.string.grass)
                        it.tvLabelType.setTextColor(Color.BLACK)
                    }
                }
                "electric" -> {
                    labelTypeLayout.let {
                        it.labelBackgroundCardView.setCardBackgroundColor(Color.YELLOW)
                        it.tvLabelType.text = context.getString(R.string.electric)
                        it.tvLabelType.setTextColor(Color.BLACK)
                    }
                }
                "fire" -> {
                    labelTypeLayout.let {
                        it.labelBackgroundCardView.setCardBackgroundColor(Color.RED)
                        it.tvLabelType.text = context.getString(R.string.fire)
                        it.tvLabelType.setTextColor(Color.WHITE)
                    }
                }
                else -> {
                    labelTypeLayout.let {
                        it.labelBackgroundCardView.setCardBackgroundColor(Color.LTGRAY)
                        it.tvLabelType.text = pokeType
                        it.tvLabelType.setTextColor(Color.BLACK)
                    }
                }
            }
        }

        fun configureStatLayout(statLayout: LayoutStatBinding, hp: Int?, def: Int?, atk: Int?, sp: Int?, context: Context){
            statLayout.let {
                it.tvAtk.text = context.getString(R.string.stat_atk,  atk.toString())
                it.tvDef.text = context.getString(R.string.stat_def,  def.toString())
                it.tvHp.text = context.getString(R.string.stat_hp,  hp.toString())
                it.tvSp.text = context.getString(R.string.stat_Sp,  sp.toString())
            }
        }

        fun shouldIncludeType(type: String, type2: String, filterFire: Boolean, filterGrass: Boolean, filterElectric: Boolean): Boolean{
            return when{
                !filterFire && !filterGrass && !filterElectric -> true
                !filterFire && !filterGrass && filterElectric -> type == "electric" || type2 == "electric"
                !filterFire && filterGrass && !filterElectric -> type == "grass" || type2 == "grass"
                !filterFire && filterGrass && filterElectric -> (type == "grass" && type2 == "electric") || (type == "electric" && type2 == "grass")
                filterFire && !filterGrass && !filterElectric -> type == "fire" || type2 == "fire"
                filterFire && !filterGrass && filterElectric -> (type == "fire" && type2 == "electric") || (type == "electric" && type2 == "fire")
                filterFire && filterGrass && !filterElectric -> (type == "fire" && type2 == "grass") || (type == "grass" && type2 == "fire")
                filterFire && filterGrass && filterElectric -> false
                else -> false
            }
        }

        fun shouldIncludeType(type: String, filterFire: Boolean, filterGrass: Boolean, filterElectric: Boolean): Boolean{
            return when{
                !filterFire && !filterGrass && !filterElectric -> true
                !filterFire && !filterGrass && filterElectric -> type == "electric"
                !filterFire && filterGrass && !filterElectric -> type == "grass"
                !filterFire && filterGrass && filterElectric -> false
                filterFire && !filterGrass && !filterElectric -> type == "fire"
                filterFire && !filterGrass && filterElectric -> false
                filterFire && filterGrass && !filterElectric -> false
                filterFire && filterGrass && filterElectric -> false
                else -> false
            }
        }

    }



}
