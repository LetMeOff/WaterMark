package com.zjh.watermark.demo

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import com.zjh.watermark.R
import com.zjh.watermark.WaterMark
import com.zjh.watermark.WaterMarkImage
import com.zjh.watermark.WaterMarkPosition
import com.zjh.watermark.WaterMarkText
import com.zjh.watermark.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.button.setOnClickListener {
            val filePath = "/storage/emulated/0/DCIM/DemoApp/WaterPicture_047.jpg"
            // 原始图片的Bitmap
            val originBitmap = BitmapFactory.decodeFile(filePath)

            val waterImage1 = WaterMarkImage(
                waterMarkResId = R.mipmap.img_dog,
                width = 200,
                height = 300,
                position = WaterMarkPosition.LeftTop()
            )

            val waterImage2 = WaterMarkImage(
                waterMarkResId = R.mipmap.img_dog,
                position = WaterMarkPosition.RightBottom()
            )

            val waterImage3 = WaterMarkImage(
                waterMarkResId = R.mipmap.img_dog,
                width = 200,
                height = 300,
                position = WaterMarkPosition.Center
            )

            val waterMartText1 = WaterMarkText(
                text = "这是一个文字",
                position = WaterMarkPosition.LeftTop()
            )

            val waterMartText2 = WaterMarkText(
                text = "这是一个文字",
                position = WaterMarkPosition.RightTop()
            )

            val waterMartText3 = WaterMarkText(
                text = "这是一个文字",
                position = WaterMarkPosition.LeftBottom()
            )
            val waterMartText4 = WaterMarkText(
                text = "这是一个文字",
                position = WaterMarkPosition.RightBottom()
            )
            val waterMartText5 = WaterMarkText(
                text = "这是一个文字",
                position = WaterMarkPosition.Center,
                textColor = getColor(R.color.teal_200),
                textSize = 50F
            )

            WaterMark(this, originBitmap)
//                .addWaterMarkImage(waterImage1)
//                .addWaterMarkImage(waterImage2)
//                .addWaterMarkImage(waterImage3)
                .addWaterMarkText(waterMartText1)
                .addWaterMarkText(waterMartText2)
                .addWaterMarkText(waterMartText3)
                .addWaterMarkText(waterMartText4)
                .addWaterMarkText(waterMartText5)
                .draw()
                .loadIntoImage(binding.image)
        }
    }

}