package com.example.kazumasatakaoka.manpokei

import android.content.Context
import android.content.SharedPreferences
import android.hardware.*
import com.google.common.io.ByteStreams.toByteArray
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.format.DateFormat
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.kazumasatakaoka.manpokei.R
import com.example.kazumasatakaoka.manpokei.Walkdata
import java.io.File
import java.util.*
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.bitcoinj.core.CashAddressFactory
import org.bitcoinj.core.Coin
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Transaction
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.script.ScriptBuilder
import org.bitcoinj.script.ScriptOpCodes.*
import org.bitcoinj.wallet.SendRequest
import org.bitcoinj.wallet.Wallet


class MainActivity : AppCompatActivity() ,SensorEventListener{
    private var numberOfStep = -1
    private var mSensorManager: SensorManager?= null
    private var mSensor: Sensor?=null
    private var txHosuu: TextView?=null
    private var sharedPref: SharedPreferences?= null
    companion object {
        val PREF_FILE_NAME: String ="sumo.co.jp.manpokei.preference"
        val HOSUU_KEY:String = "Hosuu"
    }
    private var teamsResult:Array<String> ?= arrayOf("チームA：100歩","チームB：200歩","チームC：300歩","チームD：400歩","チームE：500歩","チームF：600歩")
    private var lvTeamsHosuu: ListView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //txHosuu = findViewById(R.id.txHosuu)
        //lvTeamsHosuu = findViewById(R.id.lvTeamsHosuu)
        var arrayAdaptor = ArrayAdapter<String>(this,R.layout.teams_item,teamsResult)
        lvTeamsHosuu?.adapter=arrayAdaptor

        //センサーマネージャを取得
        mSensorManager =  getSystemService(SENSOR_SERVICE) as SensorManager
        //歩数センサーを取得
        mSensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //前回保存した歩数を取得する
        sharedPref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        numberOfStep = sharedPref?.getInt(HOSUU_KEY,-1) as Int
    }

    override fun onSensorChanged(event: SensorEvent?) {
        var sensor = event?.sensor
        var accuracy = event?.accuracy
        var timestamp = event?.timestamp
        //TYPE_STEP_COUNTER
        if(sensor?.getType() == Sensor.TYPE_STEP_COUNTER && accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH){
            numberOfStep++
            txHosuu?.text = numberOfStep.toString()+"歩"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()
        val today: Date = Calendar.getInstance().getTime()
        //val today_txt:String = DateFormat.format("yyyy/MM/dd", today)
        //val update_date = pref.getString("UPDATE_DATE", today)
        val fes_index = pref.getInt("FES_INDEX", 0)
        val late_fes_index: Int = 1
        val walk_counter = pref.getInt("WALK_COUNTER", 0)

        val dir = this.filesDir
        val filePrefix = "testnet"
        val params = TestNet3Params()
        val kit = WalletAppKit(params, dir, filePrefix).apply {
            startAsync()
            awaitRunning()
        }

        val wallet = kit.wallet().apply { allowSpendingUnconfirmedTransactions() }
        val balance = wallet.getBalance(Wallet.BalanceType.ESTIMATED)

        if (balance >= Coin.MILLICOIN) {
            editor.putBoolean("JOIN_STATE", true).apply()
        } else {
            editor.putBoolean("JOIN_STATE", false).apply()
        }

        val join_state = pref.getBoolean("JOIN_STATE", false)


        if (join_state == true) {
            if (late_fes_index != fes_index) {
                val txid = push(fes_index, walk_counter)
                saveData(txid)
                //server send
                //OO.text = "0"
                editor.putInt("FES_INDEX", late_fes_index).apply()
            }
            /*
            if (update_date != today) {
                push(fes_index, walk_counter)
                //server
                editor.putString("UPDATE_DATE", today).apply()
            }*/
        }
    }

    override fun onPause() {
        super.onPause()
        val editor = sharedPref?.edit()
        editor?.putInt(HOSUU_KEY, numberOfStep)
        editor?.commit()
    }

    fun push(fes_index:Int, walk_counter:Int):String{
        val message = "sumo," + fes_index.toString() + "," + walk_counter
        val params = TestNet3Params()
        val dir = File("context.filesDir")
        val filePrefix = "testnet"
        val kit = WalletAppKit(params, dir , filePrefix).apply {
            startAsync()
            awaitRunning()
        }

        val wallet  = kit.wallet().apply { allowSpendingUnconfirmedTransactions() }

        wallet.addCoinsReceivedEventListener { wallet, transaction, prev, new ->
        }

        //val balance = wallet.getBalance(Wallet.BalanceType.ESTIMATED)

        //val address = CashAddressFactory.create().getFromBase58(params, wallet.currentReceiveAddress().toBase58())

        val sendAmount = Coin.parseCoin("0")

        val script = ScriptBuilder()
                .op(OP_RETURN)
                .data(message.toByteArray())
                .build()

        val tx = Transaction(params).apply {
            addOutput(sendAmount, script)
        }
        val req = SendRequest.forTx(tx).apply {
            useForkId = true
        }

        wallet.completeTx(req)
        wallet.commitTx(req.tx)

        return tx.hashAsString
    }

    fun saveData(txid:String){
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val maxId = realm.where<Walkdata>().max("id")
            val nextId = (maxId?.toLong() ?: 0L) + 1L
            val walkdata = realm.createObject<Walkdata>(nextId)
            val today:Date = Calendar.getInstance().getTime()
            walkdata.date = today
            walkdata.txid = txid
            //walkdata.walkcount = //OO.text
        }
    }

}


