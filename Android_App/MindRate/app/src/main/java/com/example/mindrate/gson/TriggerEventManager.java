package com.example.mindrate.gson;

import android.util.Log;

import com.example.mindrate.activity.OverviewActivity;

import java.util.List;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class  represents a manager for Trigger Event.
 * <br>This class use design patterns "Singleton pattern" and Observer pattern.</br>
 * <br>Created by Renhan on 2017/1/25.</br>
 */
public class TriggerEventManager extends Observable{
    //================For test and debug================
    private static final String TAG = "TriggerEventManager";
    //==================================================

    //=============index of sensor===========================
    private static final int TYPE_ACCELEROMETER = 0;
    private static final int  TYPE_AMBIENT_TEMPERATURE = 1;
    private static final int  TYPE_GRAVITY = 2;
    private static final int  TYPE_GYROSCOPE = 3;
    private static final int  TYPE_LIGHT = 4;
    private static final int  TYPE_LINEAR_ACCELERATION = 5;
    private static final int  TYPE_MAGNETIC_FIELD = 6;
    private static final int  TYPE_ORIENTATION = 7;
    private static final int  TYPE_PRESSURE = 8;
    private static final int  TYPE_PROXIMITY = 9;
    private static final int  TYPE_RELATIVE_HUMIDITY = 10;
    private static final int  TYPE_ROTATION_VECTOR = 11;
    //======================================================

    private float[][] dataOfAllSensor;
    private List<Questionnaire> shouldAnswerQuestionnaire;
    private List<Questionnaire> questionnaireList;
    private OverviewActivity overviewActivity;
    //==================================================



    /**
     * Constructor
     *
     */
    private TriggerEventManager(){
        this.questionnaireList = null;
        this.dataOfAllSensor = new float[12][3];
        this.shouldAnswerQuestionnaire = new CopyOnWriteArrayList<>();
    }

    private static final TriggerEventManager TRIGGER_EVENT_MANAGER = new TriggerEventManager();

    /**
     * Get the Singleton of class TriggerEventManager
     *
     * @return TriggerEventManager. The manager for Trigger Event.
     */
    public static TriggerEventManager getTriggerEventManager(){
        return TRIGGER_EVENT_MANAGER;
    }

    // ==============getter and setter==================
    public float[][] getDataOfAllSensor() {
        return dataOfAllSensor;
    }
    public List<Questionnaire> getShouldAnswerQuestionnaire() {
        return shouldAnswerQuestionnaire;
    }
    public List<Questionnaire> getQuestionnaireList() {
        return questionnaireList;
    }
    public void setQuestionnaireList(List<Questionnaire> questionnaireList) {
        this.questionnaireList = questionnaireList;
    }
    //===================================================

    /**
     * Set data of sensor in the array "dataOfAllSensor".
     * When the date of sensor changed,the date will be sent to Questionnaire.
     * @param index        the index of Sensor
     * @param dataOfSensor the data of sensor
     */
    public void setDataOfSensor(int index,float[] dataOfSensor){
        this.dataOfAllSensor[index]=dataOfSensor;
        //======for test===========
        Log.d(TAG,"begin notify");
        setChanged();
        notifyObservers();
        //======for test===========
        Log.d(TAG,"finish notify");
        String sum = String.valueOf(this.shouldAnswerQuestionnaire.size());
        Log.d(TAG,sum);
        float[] lightTest1 = this.dataOfAllSensor[4];
        float[] temtest1 = this.dataOfAllSensor[1];
        float remp = temtest1[0];
        float lightTest = lightTest1[0];
        String a = String.valueOf(lightTest);
        String b = String.valueOf(remp);
        Log.d(TAG,a);
        //============================
       this.addQuestionnaireToOverviewActivity();

    }

    /**
     * When the data of sensor are eligible,then the questionnaire will be triggered.
     * <br>With this method  a triggered questionnaire will be added to the list of
     * questionnaire in TriggerEventManager and finally will be answered.</br>
     * @param questionnaire the triggered questionnaire.
     */
    public void addShouldAnswerQuestionnaire(Questionnaire questionnaire){
        boolean existQuestionnaire = false;
        for (Questionnaire questionnaire1 : this.shouldAnswerQuestionnaire) {
            if (questionnaire1.getQuestionnaireID().equals(questionnaire.getQuestionnaireID())) {
                existQuestionnaire = true;
            }
        }
        if (!existQuestionnaire) {
            this.shouldAnswerQuestionnaire.add(questionnaire);
        }
        //=========test und debug===============
        if(existQuestionnaire){
            Log.i(TAG,"true");
        }else{
            Log.i(TAG,"false");
        }
        //=================================
    }

    /**
     * Helper method.
     * <br>With this method a triggered questionnaire can be showed and answered.</br>
     */
    private void addQuestionnaireToOverviewActivity(){
            for(Questionnaire questionnaire:this.shouldAnswerQuestionnaire) {
                OverviewActivity.getInstance().addQuestionnaireToTriggeredQuestionnaireList
                        (questionnaire.getQuestionnaireID());

                this.shouldAnswerQuestionnaire.remove(questionnaire);

                }
    }

    /**
     * Set overview activity.
     *
     * @param overviewActivity the overview activity
     */
    public void setOverviewActivity(OverviewActivity overviewActivity){

        this.overviewActivity = overviewActivity;

    }


 /*
    private void removeQuestionnaireFromShouldList(Questionnaire questionnaire){
        boolean existQuestionnaire =false;
        String questionnaireID = questionnaire.getQuestionnaireID();
        for(Questionnaire questionnaire1:this.shouldAnswerQuestionnaire){
            if(questionnaire1.getQuestionnaireID().equals(questionnaireID)){
                existQuestionnaire = true;
            }

        }
        if(existQuestionnaire){
            int index = this.shouldAnswerQuestionnaire.indexOf(questionnaire);
            this.shouldAnswerQuestionnaire.remove(questionnaire);
        }
    }
    */

    /*public void addQuestionnaire(Questionnaire questionnaire){



        if(this.questionnaireList !=null){

            this.questionnaireList.add(questionnaire);

        }else{

            this.questionnaireList = new ArrayList<>();

            this.questionnaireList.add(questionnaire);

        }


    }*/








}
