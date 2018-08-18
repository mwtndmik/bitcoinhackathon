package sumo.co.jp.manpokei

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.R.id.edit
import android.widget.ArrayAdapter
import android.widget.ListView


class MainActivity : AppCompatActivity() ,SensorEventListener{
    private var numberOfStep = -1
    private var mSensorManager: SensorManager ?= null
    private var mSensor:Sensor ?=null
    private var txHosuu:TextView ?=null
    private var sharedPref:SharedPreferences ?= null
    companion object {
        val PREF_FILE_NAME: String ="sumo.co.jp.manpokei.preference"
        val HOSUU_KEY:String = "Hosuu"
    }
    private var teamsResult:Array<String> ?= arrayOf("チームA：100歩","チームB：200歩","チームC：300歩","チームD：400歩","チームE：500歩","チームF：600歩")
    private var lvTeamsHosuu:ListView ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txHosuu = findViewById(R.id.txHosuu)
        lvTeamsHosuu = findViewById(R.id.lvTeamsHosuu)
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
            txHosuu?.setText(numberOfStep.toString()+"歩")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        mSensorManager!!.registerListener(this,mSensor,
                SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        val editor = sharedPref?.edit()
        editor?.putInt(HOSUU_KEY, numberOfStep)
        editor?.commit()
    }
}
