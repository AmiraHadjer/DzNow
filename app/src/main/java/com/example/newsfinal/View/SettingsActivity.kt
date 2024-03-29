package com.example.newsfinal.View

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.telephony.TelephonyManager
import android.widget.Toast
import com.example.newsfinal.Adapters.CustomAdapter
import com.example.newsfinal.Interface.ServiceInterface
import com.example.newsfinal.Model.Site
import com.example.newsfinal.Model.Thematique
import com.example.newsfinal.R
import com.example.newsfinal.Services.ServiceVolley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_settings.*
import org.json.JSONArray
import org.json.JSONObject

class SettingsActivity : AppCompatActivity() {

    private var listSites : MutableList<Site> ?= arrayListOf()
    private var mSitesAdapter: CustomAdapter ?=  null
    private var listThematiques : MutableList<Thematique> ?= arrayListOf()
    private var mThemesAdapter: CustomAdapter ?=  null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)



       l_Sites.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = LinearLayoutManager(context)

            // specify an viewAdapter (see also next example)
            adapter = CustomAdapter(listSites,  { partItem : com.example.newsfinal.Interface.Checkable -> partItemClicked(partItem as Site) })
            mSitesAdapter = adapter as CustomAdapter
        }
        getListSites()

        l_Thematiques.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = LinearLayoutManager(context)

            // specify an viewAdapter (see also next example)
            adapter = CustomAdapter(listThematiques,  { partItem : com.example.newsfinal.Interface.Checkable -> partItemClicked(partItem as Thematique) })
            mThemesAdapter = adapter as CustomAdapter
        }
        getListThematiques()

        validerChoix.setOnClickListener {
            var listSup = arrayListOf<String>()
            var listAdd = arrayListOf<String>()
           listSites?.forEach {
               if((it.preference!= 0) and (!it.checked)) {
                   deleteSitePreference(it)
               }
               else if ((it.preference ==0 ) and (it.checked)) {
                   addSitePreference(it)
               }
           }



            listThematiques?.forEach {
                if((it.preference!= 0) and (!it.checked)) {
                    deleteThematiquePreference(it)
                }
                else if ((it.preference ==0 ) and (it.checked)) {
                    addThematiquePreference(it)
                }
            }
        }

        }


    private fun partItemClicked(partItem : com.example.newsfinal.Interface.Checkable) {
    }


    fun getListSites() {
        val service: ServiceInterface = ServiceVolley()
        val imei = getUniqueIMEIId(this).toString()
        var path = "siteGet.php?imei=$imei"
        var list = listOf<Site>()
        service.get(path) { response ->
            if(response != null && response != "error")
            {
                val gson = Gson()
                val jsonArray = JSONArray(response)
                if (jsonArray != null) {
                    val list = gson.fromJson(jsonArray.toString(), Array<Site>::class.java)
                    if (list!= null && list?.size != 0) {
                        listSites = list.toMutableList()
                        mSitesAdapter?.refreshAdapter(listSites as MutableList<Site>)
                    }
                }
            }
        }
    }


    fun getListThematiques() {
        val service: ServiceInterface = ServiceVolley()
        val imei = getUniqueIMEIId(this).toString()
        var path = "thematiqueGet.php?imei=$imei"
        var list = listOf<Site>()
        service.get(path) { response ->
            if(response != null && response != "error")
            {
                val gson = Gson()
                val jsonArray = JSONArray(response)
                if (jsonArray != null) {
                    val list = gson.fromJson(jsonArray.toString(), Array<Thematique>::class.java)
                    if (list!= null && list?.size != 0) {
                        listThematiques = list.toMutableList()
                        mThemesAdapter?.refreshAdapter(listThematiques as MutableList<Thematique>)
                    }
                }
            }
        }
    }

    fun getUniqueIMEIId(context: Context): String {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return ""
            }
            val imei = telephonyManager.deviceId
            return if (imei != null && !imei.isEmpty()) {
                imei
            } else {
                android.os.Build.SERIAL
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "not_found"
    }



    fun addSitePreference(site: Site) {
        val path: String = "preferenceSitePost.php"

        val params = JSONObject()
        params.put("idSite", site.id)
        params.put("imei", getUniqueIMEIId(this))

        val service: ServiceInterface = ServiceVolley()
        service.post(path, params) { response ->
            Toast.makeText(this, "Modification avec succès", Toast.LENGTH_SHORT).show()
            site.preference = 1

        }
    }


    fun deleteSitePreference(site: Site) {
        val path: String = "preferenceSiteDelete.php"

        val params = JSONObject()
        params.put("idSite",site.id)
        params.put("imei", getUniqueIMEIId(this))

        val service: ServiceInterface = ServiceVolley()
        service.post(path, params) { response ->
            Toast.makeText(this, "Modification avec succès", Toast.LENGTH_SHORT).show()
            site.preference = 0
        }
    }



    fun addThematiquePreference(theme: Thematique) {
        val path: String = "preferenceThemePost.php"

        val params = JSONObject()
        params.put("idThematique", theme.id)
        params.put("imei", getUniqueIMEIId(this))

        val service: ServiceInterface = ServiceVolley()
        service.post(path, params) { response ->
            Toast.makeText(this, "Modification avec succès", Toast.LENGTH_SHORT).show()
            theme.preference = 1
        }
    }


    fun deleteThematiquePreference(theme: Thematique) {
        val path: String = "preferenceThemeDelete.php"

        val params = JSONObject()
        params.put("idThematique", theme.id)
        params.put("imei", getUniqueIMEIId(this))

        val service: ServiceInterface = ServiceVolley()
        service.post(path, params) { response ->
            Toast.makeText(this, "Modification avec succès", Toast.LENGTH_SHORT).show()
            theme.preference = 0
        }
    }

}
