package com.example.kazumasatakaoka.manpokei

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_walkdata.*
import kotlinx.android.synthetic.main.content_walkdata.*
import io.realm.Realm

import org.jetbrains.anko.startActivity

class WalkdataActivity : AppCompatActivity() {

        private lateinit var realm: Realm

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            setSupportActionBar(toolbar)
            realm = Realm.getDefaultInstance()

            val walkdata = realm.where(Walkdata::class.java).findAll()
            listView.adapter = WalkdataAdapter(walkdata)

            listView.setOnItemClickListener { parent, view, position, id ->
                val walkdata = parent.getItemAtPosition(position) as Walkdata
                startActivity<WalkdataDetail>(
                        "walkdata_id" to walkdata.id )
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            realm.close()
        }
    }

