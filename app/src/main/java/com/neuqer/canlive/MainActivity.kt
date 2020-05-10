package com.neuqer.canlive

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.tools.ToastUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        const val HAPPY_EMO = 1
        const val ANGRY_EMO = 2
        const val SAD_EMO = 3
        const val NATURE_EMO = 4
    }

    lateinit var plusImageView: ImageView
    lateinit var showImageView: ImageView
    lateinit var buttonUpload: Button
    lateinit var textResult: TextView
    lateinit var viewModel: FaceViewModel
    lateinit var currentBitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showImageView = findViewById(R.id.upload_img)
        plusImageView = findViewById(R.id.img_plus)

        buttonUpload = findViewById(R.id.main_upload)
        textResult = findViewById(R.id.txt_score)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(FaceViewModel::class.java)

        initViewData()

    }

    @SuppressLint("CheckResult")
    private fun initViewData() {
        addListener()
        addObserver()
    }

    private fun addObserver() {
        viewModel.result.observe(this, androidx.lifecycle.Observer {
            val facesBean = it.faces[0]

            val arrayList = ArrayList<Emotion>()
            arrayList.add(Emotion(1, facesBean.attributes.emotion.happiness))
            arrayList.add(Emotion(2, facesBean.attributes.emotion.anger))
            arrayList.add(Emotion(3, facesBean.attributes.emotion.sadness))
            arrayList.add(Emotion(4, facesBean.attributes.emotion.neutral))
            Collections.sort(arrayList, CompareUtil())


            txt_sex.text = if (facesBean.attributes.gender.value == "Male")  "性别：男" else "性别：女"
            textResult.text = ("颜值：" + facesBean.attributes.beauty.male_score.toString())
            txt_happy.text = ("喜：" + facesBean.attributes.emotion.happiness.toString() + "%")
            txt_angry.text = ("怒："+facesBean.attributes.emotion.anger.toString() + "%")
            txt_sad.text =  ("哀：" + facesBean.attributes.emotion.sadness.toString() + "%")
            txt_haha.text = ("平静：" + facesBean.attributes.emotion.neutral.toString() + "%")

            when(arrayList[0].id) {
                HAPPY_EMO -> img_deal.setImageResource(R.drawable.ic_happy)
                ANGRY_EMO -> img_deal.setImageResource(R.drawable.ic_anger_deal)
                SAD_EMO -> img_deal.setImageResource(R.drawable.ic_comfort)
                else -> img_deal.setImageResource(R.drawable.ic_nature)
            }

        })

        viewModel.detectBitmap.observe(this, androidx.lifecycle.Observer {
            showImageView.setImageBitmap(it)
        })
    }

    private fun addListener() {
        buttonUpload.setOnClickListener {
            if (upload_img.drawable != null) {
                viewModel.getResult((upload_img.drawable as BitmapDrawable).bitmap)
            } else {
                ToastUtils.s(this, "图片不能为空")
            }
        }

        plusImageView.setOnClickListener {
            //请求权限
            val rxPermissions = RxPermissions(this)
            rxPermissions.requestEach(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
                .subscribe {
                    if(it.granted) {
                        PictureSelector.create(this)
                            .openGallery(PictureMimeType.ofImage())
                            .loadImageEngine(GlideEngine.createGlideEngine())
                            .isCamera(true)
                            .maxSelectNum(1)
                            .imageSpanCount(3)
                            .freeStyleCropEnabled(true)
                            .selectionMode(PictureConfig.MULTIPLE)
                            .previewImage(true)
                            .enableCrop(true)
                            .compress(true)
                            .showCropFrame(true)
                            .showCropGrid(true)
                            .cameraFileName(System.currentTimeMillis().toString() + ".jpg") // 使用相机时保存至本地的文件名称,注意这个只在拍照时可以使用
                            .renameCompressFile(System.currentTimeMillis().toString() + ".jpg") // 重命名压缩文件名、 注意这个不要重复，只适用于单张图压缩使用
                            .renameCropFileName(System.currentTimeMillis().toString() + ".jpg") // 重命名裁剪文件名、 注意这个不要重复，只适用于单张图裁剪使用
                            .forResult(PictureConfig.CHOOSE_REQUEST)
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val compressList: MutableList<String> =
                ArrayList()
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    val selectList =
                        PictureSelector.obtainMultipleResult(data)
                    for (media in selectList) {
                        compressList.add(media.compressPath)
                        println("压缩路径：" + media.compressPath)
                    }
                    if (compressList.size != 0) {
                        uploadImage(compressList)
                    }
                }
            }
        }
    }

    private fun uploadImage(list: List<String>) {
        if (list.size == 1) {
            val decodeFile = BitmapFactory.decodeFile(list[0])
            upload_img.setImageBitmap(decodeFile)
        }
    }
}
