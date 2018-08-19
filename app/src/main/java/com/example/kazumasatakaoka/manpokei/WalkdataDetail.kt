package com.example.kazumasatakaoka.manpokei

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_walkdata_detail.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class WalkdataDetail : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walkdata_detail)
        realm = Realm.getDefaultInstance()

        val walkdataId = intent?.getLongExtra("walkdata_id", -1L)
        if (walkdataId != -1L) {
            val walkdata = realm.where<Walkdata>()
                    .equalTo("id", walkdataId).findFirst()
            dateEdit.setText(
                    DateFormat.format("yyyy/MM/dd", walkdata?.date))
            walkcountEdit.setText(walkdata?.walkcount.toString())
            txId.setText(walkdata?.txid)
        }
    }


        override fun onDestroy() {
            super.onDestroy()
            realm.close()
        }

        fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"): Date? {
            val sdFormat = try {
                SimpleDateFormat(pattern)
            } catch (e: IllegalArgumentException) {
                null
            }
            val date = sdFormat?.let {
                try {
                    it.parse(this)
                } catch (e: ParseException) {
                    null
                }
            }
            return date
        }

}
