package com.example.easygo.view.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.amap.api.services.core.PoiItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.easygo.R
import com.example.easygo.utility.amap.AMapUtil

/**
 * Created by chene on @date 2020/7/14
 * databinding的一些便捷方法设定
 */
@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, poi: PoiItem) {
    if (poi.photos.isNullOrEmpty()) return

    val options = RequestOptions.bitmapTransform(
        RoundedCorners(20)
    ).override(
        view.width, view.height
    )
    Glide.with(view.context)
        .load(poi.photos[0].url)
        .apply(options)
        .error(R.drawable.img_load_error)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(view)
}
@BindingAdapter("setDistance")
fun setDistance(view: TextView, poi: PoiItem){
    view.text = AMapUtil.getFriendlyLength(poi.distance)
}
@BindingAdapter("setOneTypeDes")
fun setOneTypeDes(view: TextView, poi: PoiItem){
    val type = poi.typeDes.split(";")
    view.text = type[0]
}
@BindingAdapter("setAllTypeDes")
fun setAllTypeDes(view: TextView, poi: PoiItem){
    val type = poi.typeDes.split(";")
    var types = ""
    type.forEach {
        types += "$it  "
    }
    view.text = types
}
@BindingAdapter("setCallClick")
fun setCallClick(view: View, poi: PoiItem){
    if (poi.tel.isNullOrEmpty()){
        view.visibility = View.GONE
    }else{
        view.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${poi.tel.split(";")[0]}")
                Log.d("debugF", "setCallClick: ${poi.tel}")
            }
            view.context.startActivity(intent)
        }
    }
}