package com.example.mindrate.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.example.mindrate.gson.AllSensorEventListener;
import com.example.mindrate.gson.Questionnaire;
import com.example.mindrate.gson.TriggerEventManager;

import java.util.ArrayList;
import java.util.List;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE;
import static android.hardware.Sensor.TYPE_GRAVITY;
import static android.hardware.Sensor.TYPE_GYROSCOPE;
import static android.hardware.Sensor.TYPE_LIGHT;
import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;
import static android.hardware.Sensor.TYPE_ORIENTATION;
import static android.hardware.Sensor.TYPE_PRESSURE;
import static android.hardware.Sensor.TYPE_PROXIMITY;
import static android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY;

public class DeviceSensorService extends Service {
    private static final String TAG = "DeviceSensorService";
    private SensorManager sensorManager;
    private List<Sensor> allSensors;
    private List<AllSensorEventListener> allSensorEventListeners;
    private TriggerEventManager triggerEventManager;
    private List<Sensor> usedSensorList;
    //private MyBinder mBinder = new MyBinder();






    public DeviceSensorService() {

    }

    @Override
    public void onCreate(){
        super.onCreate();
        this.allSensors = new ArrayList<>() ;
        this.allSensorEventListeners = new ArrayList<>();
        this.usedSensorList = new ArrayList<>();
        //this.triggerEventManager =null;
        if(sensorManager == null){
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }
        allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);//maybe speciall sensor
        this.triggerEventManager = TriggerEventManager.getTriggerEventManager();
        for(Sensor sensor:allSensors){
            this.addSensorEventListener(sensor);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        //改成问卷需要的sensor。
        for(AllSensorEventListener listener : allSensorEventListeners){
            this.sensorManager.registerListener(listener,listener.getSensor(),SensorManager
                    .SENSOR_DELAY_UI);
        }
        Log.i(TAG,"Service onStart_____");


        // other things?
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (sensorManager!= null) {
            for(AllSensorEventListener listener : allSensorEventListeners ) {
                sensorManager.unregisterListener(listener);
            }

        }
    }

    public IBinder onBind(Intent intent) {
        Log.d(TAG,"return Binder");
        //return mBinder;
        return null;
    }


   /* public class MyBinder extends Binder{
        private TriggerEventManager savedTriggerEventManager;
        public void setTriggerEventManager(TriggerEventManager triggerEventManager){
            Log.d(TAG,"setTEM");
            this.savedTriggerEventManager = triggerEventManager;
        }
        public TriggerEventManager getSavedTriggerEventManager(){
            return this.savedTriggerEventManager;
        }
        public void transferTriggerEventManager(){
            Log.d(TAG,"tranTEM");
            triggerEventManager = this.getSavedTriggerEventManager();
        }

    }*/

    public void  addSensorEventListener(Sensor sensor){

        switch(sensor.getType()){
            case TYPE_ACCELEROMETER:
                AllSensorEventListener accelerometerSensor = new AllSensorEventListener(this.triggerEventManager,
                        sensor);
                this.allSensorEventListeners.add(accelerometerSensor);

                break;
            case TYPE_AMBIENT_TEMPERATURE:
                AllSensorEventListener ambientTemperatureSensor = new AllSensorEventListener(this.triggerEventManager,sensor);
                this.allSensorEventListeners.add(ambientTemperatureSensor);
                //
                break;
            case TYPE_GRAVITY:
                AllSensorEventListener gravitySensor = new AllSensorEventListener(this.triggerEventManager,sensor);
                this.allSensorEventListeners.add(gravitySensor);
                //
                break;
            case TYPE_GYROSCOPE:
                AllSensorEventListener gyroscopeSensor = new AllSensorEventListener(this.triggerEventManager,sensor);
                this.allSensorEventListeners.add(gyroscopeSensor);
                //
                break;
            case TYPE_LIGHT:
                AllSensorEventListener lightSensor = new AllSensorEventListener(this.triggerEventManager,sensor);
                this.allSensorEventListeners.add(lightSensor);
                //
                break;
            case TYPE_LINEAR_ACCELERATION:
                AllSensorEventListener linearAccelerationSensor = new AllSensorEventListener(this.triggerEventManager,sensor);
                this.allSensorEventListeners.add(linearAccelerationSensor);
                //
                break;
            case TYPE_MAGNETIC_FIELD:
                AllSensorEventListener magneticFieldSensor = new AllSensorEventListener(this.triggerEventManager, sensor);
                this.allSensorEventListeners.add(magneticFieldSensor);
                //
                break;
            case TYPE_ORIENTATION:
                AllSensorEventListener orientationSensor = new AllSensorEventListener(this.triggerEventManager,sensor);
                this.allSensorEventListeners.add(orientationSensor);
                //
                break;
            case TYPE_PRESSURE:
                AllSensorEventListener pressureSensor = new AllSensorEventListener(this.triggerEventManager,sensor);
                this.allSensorEventListeners.add(pressureSensor);
                //
                break;
            case TYPE_PROXIMITY:
                AllSensorEventListener proximitySensor = new AllSensorEventListener(this.triggerEventManager,sensor);
                this.allSensorEventListeners.add(proximitySensor);
                //
                break;
            case TYPE_RELATIVE_HUMIDITY:
                AllSensorEventListener relativeHumiditySensor = new AllSensorEventListener
                        (this.triggerEventManager,sensor);
                this.allSensorEventListeners.add(relativeHumiditySensor);
                //
                break;
            default :
                AllSensorEventListener rotationVectorSensor = new AllSensorEventListener(this.triggerEventManager,sensor);
                this.allSensorEventListeners.add(rotationVectorSensor);
                //
                break;




        }


    }

    public void setUsedSensor(){
        List<Integer>  indexOfUsedSensor = new ArrayList<>();
        List<Questionnaire> questionnaireList = this.triggerEventManager
                .getQuestionnaireList();
        for(Questionnaire questionnaire:questionnaireList){
            if(!questionnaire.isAnswered()){
                questionnaire.getTriggerEvent().setSensor();
                boolean[]sensorList = questionnaire.getTriggerEvent().getSensorList();
                for(int i=0;i<sensorList.length;i++){
                    //if()
                }
            }
        }
    }
}


