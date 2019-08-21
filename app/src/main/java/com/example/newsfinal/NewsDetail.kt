package com.example.newsfinal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.newsfinal.Model.News
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.activity_news_detail.*


class NewsDetail : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        bindData()
        var intentThatStartedThisActivity = getIntent()

        likeButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {

            }

            override fun unLiked(likeButton: LikeButton) {

            }
        })
    }

    fun bindData() {
        categorie_news.text = article?.categorie
        title_article.text = article?.title
        date.text = article?.date
        categorie_article.text = article?.categorie
        auteur.text = article?.author
        txt_descreption.text = article?.description
        Glide.with(this).load(article?.image).into(image)


    }
    companion object {
        var article : News? = null
    }

}

