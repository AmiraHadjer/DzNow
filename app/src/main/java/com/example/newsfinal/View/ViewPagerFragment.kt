package com.example.newsfinal.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newsfinal.R

class ViewPagerFragment : Fragment() {
    private var categorie: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root =inflater.inflate(R.layout.fragment_view_pager, container, false)
        val childFragment = ListNews.newInstance(categorie)
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_list, childFragment).commit()
        return root

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(categorie: Int): ViewPagerFragment {
            val fragment = ViewPagerFragment()
            fragment.categorie = categorie
            return fragment
        }
    }
}