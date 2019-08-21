package com.example.newsfinal.Model

import java.io.Serializable



data class News(
                val id: Int? = null,
                val title: String, val description: String, val date: String, val image: String, val categorie: String, val author: String) : Serializable {

}