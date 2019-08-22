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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import android.graphics.Picture
import com.google.firebase.storage.FileDownloadTask
import java.io.File
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ListNews : Fragment() {

    private var listOfNews : MutableList<News> = mutableListOf()
    private var mAdapter: ListNewsAdapter ?= null
    private var categorie: Int = 0
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("news/news")
    private val firebase = FirebaseStorage.getInstance()
    private val storageRef : StorageReference = firebase.getReference()

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


            // Read from the database
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    addNewsToLIst(dataSnapshot)
                    mAdapter?.refreshAdapter(listOfNews as MutableList<News>)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })

        }


    fun addNewsToLIst(dataSnapshot: DataSnapshot) {
        for (child in dataSnapshot.children) {
            val article = child.getValue(News::class.java)
            if (article != null) {
                listOfNews.add(article)
                // To a file from a Google Cloud Storage URI
                val gsReference = firebase.getReferenceFromUrl("gs://javasampleapproach-storage.appspot.com/images/javasampleapproach.jpg")
            }
        }
    }


    fun loadDatabase(firebaseData: DatabaseReference) {
        val availableSalads: List<News> = mutableListOf(
            News("", "JS Kabylie : Réception en l’honneur des jeunes catégories", "Lundi dans la soirée, l’hôtel Ittourar de Tizi Ouzou a abrité une réception en l’honneur des jeunes catégories de la JS Kabylie et leurs staffs techniques.Les U13 et U12, champions de wilaya de l’année 2018/2019 dans leurs catégories respectives,","22 Avril 2019","img", "sport", "salima tlmssani" )
        )
        availableSalads.forEach {
            val key = firebaseData.push().key
            it.id = key!!
            if (key != null) {
                firebaseData.child("news").child(key).setValue(it)
            }
        }
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
