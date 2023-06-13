package com.zjh.watermark

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.widget.ImageView

/**
 * @author zjh
 * on 2023/6/2
 */
class WaterMark(
    private val context: Context,
    private val originBitmap: Bitmap
) {

    /**
     * 原始宽高
     */
    private val originWidth = originBitmap.width
    private val originHeight = originBitmap.height

    /**
     * 绘制后的Bitmap
     */
    private var finalBitmap: Bitmap? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isDither = true
        isFilterBitmap = true
    }

    /**
     * 要绘制的水印图片集合
     */
    private val waterMarkImageList = mutableListOf<WaterMarkImage>()

    /**
     * 要绘制的水印文字集合
     */
    private val waterMarkTextList = mutableListOf<WaterMarkText>()

    /**
     * 添加水印图片
     */
    fun addWaterMarkImage(waterMarkImage: WaterMarkImage): WaterMark {
        waterMarkImageList.add(waterMarkImage)
        return this
    }

    /**
     * 添加水印文字
     */
    fun addWaterMarkText(waterMarkText: WaterMarkText): WaterMark {
        waterMarkTextList.add(waterMarkText)
        return this
    }

    /**
     * 绘制水印
     */
    fun draw(): WaterMark {
        val newBitmap = originBitmap.copy(originBitmap.config, true)
        val canvas = Canvas(newBitmap)

        // 画图片
        waterMarkImageList.forEach { drawImage(canvas, it) }
        // 画文字
        waterMarkTextList.forEach { drawText(canvas, it) }

        finalBitmap = newBitmap
        return this
    }

    /**
     * 获取最终的Bitmap
     */
    fun getFinalBitmap(): Bitmap = finalBitmap ?: originBitmap

    /**
     * 加载到ImageView中
     */
    fun loadIntoImage(imageView: ImageView) {
        // 测试注释
        imageView.setImageBitmap(finalBitmap ?: originBitmap)
        imageView.setImageBitmap(finalBitmap ?: originBitmap)
    }

    /**
     * 添加水印图片到原始图片上
     */
    private fun drawImage(canvas: Canvas, waterMarkImage: WaterMarkImage) {
        // 水印图片Bitmap
        val waterMarkImageBitmap = getWaterMarkBitmap(waterMarkImage)
        // 位置
        val padding: Pair<Float, Float> = when (val position = waterMarkImage.position) {
            // 左上
            is WaterMarkPosition.LeftTop -> Pair(position.paddingLeft.dp, position.paddingTop.dp)
            // 左下
            is WaterMarkPosition.LeftBottom -> Pair(
                position.paddingLeft.dp,
                originHeight - waterMarkImageBitmap.height - position.paddingBottom.dp
            )
            // 右上
            is WaterMarkPosition.RightTop -> Pair(
                originWidth - waterMarkImageBitmap.width - position.paddingRight.dp,
                position.paddingTop.dp
            )
            // 右下
            is WaterMarkPosition.RightBottom -> Pair(
                originWidth - waterMarkImageBitmap.width - position.paddingRight.dp,
                originHeight - waterMarkImageBitmap.height - position.paddingBottom.dp
            )
            // 中间
            is WaterMarkPosition.Center -> Pair(
                (originWidth - waterMarkImageBitmap.width).toFloat() / 2,
                (originHeight - waterMarkImageBitmap.height).toFloat() / 2,
            )
        }
        // 画水印图片
        canvas.drawBitmap(waterMarkImageBitmap, padding.first, padding.second, null)
    }

    /**
     * 添加水印文字到原始图片上
     */
    private fun drawText(canvas: Canvas, waterMarkText: WaterMarkText) {
        paint.color = waterMarkText.textColor
        paint.textSize = waterMarkText.textSize.sp
        val text = waterMarkText.text
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        // 位置
        val padding: Pair<Float, Float> = when (val position = waterMarkText.position) {
            // 左上
            is WaterMarkPosition.LeftTop -> Pair(
                position.paddingLeft.dp,
                position.paddingTop.dp + bounds.height()
            )
            // 左下
            is WaterMarkPosition.LeftBottom -> Pair(
                position.paddingLeft.dp,
                originHeight - position.paddingBottom.dp
            )
            // 右上
            is WaterMarkPosition.RightTop -> Pair(
                originWidth - bounds.width() - position.paddingRight.dp,
                position.paddingTop.dp + bounds.height()
            )
            // 右下
            is WaterMarkPosition.RightBottom -> Pair(
                originWidth - bounds.width() - position.paddingRight.dp,
                originHeight - position.paddingBottom.dp
            )
            // 中间
            is WaterMarkPosition.Center -> Pair(
                (originWidth - bounds.width()).toFloat() / 2,
                (originHeight + bounds.height()).toFloat() / 2,
            )
        }
        canvas.drawText(text, padding.first, padding.second, paint)
    }

    /**
     * 获取水印图片Bitmap
     */
    private fun getWaterMarkBitmap(waterMarkImage: WaterMarkImage): Bitmap {
        waterMarkImage.waterMarkBitmap?.let {
            return it
        }
        val resId = waterMarkImage.waterMarkResId ?: throw NullPointerException("waterMarkBitmap或waterMarkResId必须设置其中一个")
        val options = BitmapFactory.Options()
        // 设置为true之后，不会将整个图像加载到内存中，只会解析图像的边界信息
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, resId, options)
        // 图片宽高
        val width = waterMarkImage.width.ifZero { options.outWidth }
        val height = waterMarkImage.height.ifZero { options.outHeight }
        // 设置为false，可以加载整个图像
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)
        // createScaledBitmap - 将原始的Bitmap缩放为指定宽高的Bitmap
        return Bitmap.createScaledBitmap(bitmap, width.dp, height.dp, true)
    }

    private fun Int.ifZero(defaultValue: () -> Int) = if (this == 0) defaultValue() else this

    private inline val Float.dp
        get() = dip2px()

    private inline val Int.dp
        get() = toFloat().dip2px().toInt()

    private inline val Float.sp
        get() = sp2px()

    private fun Float.dip2px(): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    )

    private fun Float.sp2px(): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        context.resources.displayMetrics
    )
}

data class WaterMarkImage(
    var waterMarkBitmap: Bitmap? = null,
    var waterMarkResId: Int? = null,
    var width: Int = 0,
    var height: Int = 0,
    var position: WaterMarkPosition = WaterMarkPosition.LeftTop()
)

data class WaterMarkText(
    val text: String,
    var textColor: Int = Color.WHITE,
    var textSize: Float = 20F,
    var position: WaterMarkPosition = WaterMarkPosition.LeftTop()
)

sealed class WaterMarkPosition {
    data class LeftTop(val paddingLeft: Float = 0F, val paddingTop: Float = 0F) : WaterMarkPosition()
    data class LeftBottom(val paddingLeft: Float = 0F, val paddingBottom: Float = 0F) : WaterMarkPosition()
    data class RightTop(val paddingRight: Float = 0F, val paddingTop: Float = 0F) : WaterMarkPosition()
    data class RightBottom(val paddingRight: Float = 0F, val paddingBottom: Float = 0F) : WaterMarkPosition()
    object Center : WaterMarkPosition()
}