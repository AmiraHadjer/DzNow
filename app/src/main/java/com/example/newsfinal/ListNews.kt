package com.example.newsfinal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.LinearLayoutManager
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.example.newsfinal.Interface.ServiceInterface
import com.example.newsfinal.Model.News
import com.example.newsfinal.Services.ServiceVolley
import com.google.gson.Gson


import kotlinx.android.synthetic.main.fragment_list_news.*
import org.json.JSONArray

class ListNews : Fragment() {

    private var listOfNews : List<News>? = listOf()
    private var mAdapter: ListNewsAdapter ?= null
    private var categorie: Int = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_list_news, container, false)
        }


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            list_recycler_view.apply {
                layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager?
                adapter = ListNewsAdapter(listOfNews, { partItem : News -> partItemClicked(partItem) })
                mAdapter = adapter as ListNewsAdapter
                }
            getListNews(categorie)
        }

    fun getListNews(categorie: Int) {
        val service: ServiceInterface = ServiceVolley()
        var path = ""
        if(categorie == 0)
            path = "http://192.168.1.16/API-NEWS/newsGet.php"
        else {
            var ctg = ""
            when (categorie) {
                1 -> {
                    ctg = "sport"
                }
                2 -> {
                  ctg = "politique"
                }
                3 -> {
                    ctg = "culture"
                }
                4 -> {
                    ctg = "international"
                }
            }
            path = "http://192.168.1.16/API-NEWS/newsGetCategorie.php?categorie=" + ctg
        }
        var list = listOf<News>()
        service.get(path) { response ->
            if(response != null && response != "error")
            {
                val gson = Gson()
                val jsonArray = JSONArray(response)
                if (jsonArray != null) {
                    val list = gson.fromJson(jsonArray.toString(), Array<News>::class.java)
                    if (list!= null && list?.size != 0) {
                        listOfNews= list.toMutableList()
                        mAdapter?.refreshAdapter(listOfNews as MutableList<News>)
                    }
                }
            }
        }
    }


    private fun partItemClicked(partItem : News) {
        Toast.makeText(this.context, "Titre: ${partItem.title}", Toast.LENGTH_LONG).show()

        // Launch second activity, pass part ID as string parameter
        val showDetailActivityIntent = Intent(this.context, NewsDetail::class.java)
        NewsDetail.article = partItem
        startActivity(showDetailActivityIntent)
    }


    companion object {
            fun newInstance(categorie: Int) :
                ListNews {
                val fragment = ListNews()
                fragment.categorie = categorie
                return fragment
            }
        }

}
