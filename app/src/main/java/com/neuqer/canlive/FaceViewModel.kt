package com.neuqer.canlive

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Time:2020/5/9 21:27
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
class FaceViewModel(application: Application) : AndroidViewModel(application) {
    var result: MutableLiveData<FaceppBean> = MutableLiveData()
    var error: MutableLiveData<Throwable> = MutableLiveData()
    var detectBitmap: MutableLiveData<Bitmap> = MutableLiveData()

    fun getResult(bitmap: Bitmap) {
        FaceApiRepository.getApiService().getFaceInfo(
            BuildConfig.APP_KEY,
            BuildConfig.APP_PWD,
            Utils.base64(bitmap),
            1,
            "gender,age,smiling,emotion,beauty"
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    result.value = it

                    val tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    val canvas = Canvas(tempBitmap)
                    val paint = Paint()
                    paint.color = Color.RED
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 10f

                    for (face in it.faces) {
                        val faceRectangle: FaceppBean.FacesBean.FaceRectangleBean = face.face_rectangle
                        val top: Int = faceRectangle.top
                        val left: Int = faceRectangle.left
                        val height: Int = faceRectangle.height
                        val width: Int = faceRectangle.width
                        canvas.drawRect(
                            left.toFloat(),
                            top.toFloat(),
                            left + width.toFloat(),
                            top + height.toFloat(),
                            paint
                        )
                    }
                    detectBitmap.value = tempBitmap
                }
            }, {
                error.value = it
            })
    }
}