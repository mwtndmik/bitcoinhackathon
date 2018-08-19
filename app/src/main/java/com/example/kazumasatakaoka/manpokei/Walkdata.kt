package com.example.kazumasatakaoka.manpokei

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Walkdata: RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var date: Date = Date()
    var txid: String = ""
    var walkcount: Long = 0
}