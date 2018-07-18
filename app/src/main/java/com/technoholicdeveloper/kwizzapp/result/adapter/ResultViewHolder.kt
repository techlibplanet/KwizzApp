package com.technoholicdeveloper.kwizzapp.result.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.graphics.Bitmap
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.gms.common.images.ImageManager
import com.technoholicdeveloper.kwizzapp.R
import com.technoholicdeveloper.kwizzapp.viewmodels.ResultViewModel


class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(context : Context, resultViewModel: ResultViewModel, position: Int){
        val textViewPlayerName = itemView.findViewById<TextView>(R.id.textViewPlayerName)
        val textViewRightAnswer = itemView.findViewById<TextView>(R.id.textViewRightAnswer)
//        val textViewLoosePoints = itemView.findViewById<TextView>(R.id.textViewLoosePoints)
//        val textViewWinPoints = itemView.findViewById<TextView>(R.id.textViewWinPoints)
//        val textViewTotalPoints = itemView.findViewById<TextView>(R.id.textViewTotalPoints)
        val imageViewProfile = itemView.findViewById<ImageView>(R.id.playerDisplayImage)

        textViewPlayerName.text = resultViewModel.playerName
        textViewRightAnswer.text = resultViewModel.rightAnswers

        val mgr = ImageManager.create(context)
        mgr.loadImage(imageViewProfile, resultViewModel.imageUri)


//        textViewPlayerName.text = resultViewModel.playerName
//        textViewBidPoints.text = resultViewModel.
//        textViewLoosePoints.text = resultViewModel.loosePoints
//        textViewWinPoints.text = resultViewModel.winPoints
//        Glide.with(context).load(R.mipmap.ic_launcher).apply(RequestOptions().circleCrop()).into(imageViewProfile)
//        Glide.with(context)
//                .load(resultViewModel.imageUri)
//                .apply(RequestOptions.bitmapTransform(CircleCrop()))
//                .into(imageViewProfile)
//        textViewTotalPoints.text = resultViewModel.totalPoints

    }
}