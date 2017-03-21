package com.example.mindrate.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mindrate.R;
import com.example.mindrate.fragment.AboutUsFragment;
import com.example.mindrate.fragment.ChooseQuestionnaireFragment;
import com.example.mindrate.fragment.ProbandProfileFragment;
import com.example.mindrate.fragment.SettingFragment;
import com.example.mindrate.fragment.WelcomeFragment;
import com.example.mindrate.gson.DragScale;
import com.example.mindrate.gson.Duration;
import com.example.mindrate.gson.MultipleChoice;
import com.example.mindrate.gson.Option;
import com.example.mindrate.gson.Proband;
import com.example.mindrate.gson.Question;
import com.example.mindrate.gson.Questionnaire;
import com.example.mindrate.gson.SingleChoice;
import com.example.mindrate.gson.StepScale;
import com.example.mindrate.gson.TextAnswer;
import com.example.mindrate.gson.TriggerEvent;
import com.example.mindrate.gson.TriggerEventManager;
import com.example.mindrate.service.DeviceSensorService;
import com.example.mindrate.util.JsonUtil;
import com.example.mindrate.util.PreferenceUtil;
import com.example.mindrate.util.TimeUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * This is the fragment where the proband can switch to different fragements:
 * <p><code>AboutUsFragment</code></p>
 * <p><code>ChooseQuestionnaireFragment</code></p>
 * <p><code>ProbandProfileFragment</code></p>
 * <p><code>SettingFragment</code></p>
 * <p><code>WelcomeFragment</code></p>
 * <p>
 * <p>
 * <br>Project: MindRate</br>
 * <br>Package: com.example.mindrate.activity</br>
 * <br>Author: Ecko Tan</br>
 * <br>E-mail: eckotan@icloud.com</br>
 * <br>Created at 2017/2/13:22:11</br>
 * </p>
 */
public class OverviewActivity extends BaseActivity {

    private static final String TAG = "OverviewActivity";
    private static OverviewActivity instance = null;
    private static boolean needIntent = true;
    //myBroadcastReceiver receiver = new  myBroadcastReceiver();

    private Proband proband;

    private List<Questionnaire> allQuestionnaireList; // all questionnaires
    private List<Questionnaire> triggeredQuestionnaireList;

    private Questionnaire selectedQuestionnaire;
    private int selectedQuestionnaireIndex;
    private boolean isFirstLoad = true;
    private List<Questionnaire> triggeredByTimeQuestionnaireList = new ArrayList<>();
    private List<Questionnaire> triggeredByDateQuestionnaireList = new ArrayList<>();


    // ==================== View components ==================================
    private DrawerLayout mDrawerLayout;
    private Button btn_nav;
    private NavigationView navView;
    private TextView tv_title;
    //private SensorManager sensorManager;
    //private List<Sensor> allSensors;
    private TriggerEventManager triggerEventManager;
    private RelativeLayout title;

    // =======================================================================
    //    WelcomeFragment welcomeFragment = new WelcomeFragment();
    //    ProbandProfileFragment probandProfileFragment = new ProbandProfileFragment();
    //    ChooseQuestionnaireFragment chooseQuestionnaireFragment = new
    // ChooseQuestionnaireFragment();
    //    AboutUsFragment aboutUsFragment = new AboutUsFragment();
    //    SettingFragment settingFragment = new SettingFragment();

    WelcomeFragment welcomeFragment;
    ProbandProfileFragment probandProfileFragment;
    ChooseQuestionnaireFragment chooseQuestionnaireFragment;
    AboutUsFragment aboutUsFragment;
    SettingFragment settingFragment;


    // =======================================================================

   /* private DeviceSensorService.MyBinder myBinder;
    private ServiceConnection connection = new ServiceConnection(){

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
        @Override
        public void onServiceConnected(ComponentName name,  IBinder service) {
            Log.d(TAG,"bindStart");
            myBinder = (DeviceSensorService.MyBinder)service;
            //Log.d(TAG,"setTEM");
            myBinder.setTriggerEventManager(triggerEventManager);
            //Log.d(TAG,"tranTEM");
            myBinder.transferTriggerEventManager();
        }
    };*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        initFragment();

        if (this.allQuestionnaireList == null) {
            this.allQuestionnaireList = new ArrayList<>();
        }
        if (this.triggeredQuestionnaireList == null) {
            this.triggeredQuestionnaireList = new ArrayList<>();
        }

        if (needIntent) {
            initFromIntent();
            needIntent = false;
        }

        initView();

        //        initTestData();
        addTriggeredByTimeQuestionnaire();
        addTriggeredByDatetimeQuestionnaire();

        Log.i(TAG, "TEM created in Activity");
        instance = this;


    }

    /**
     * Initialize fragments
     */
    private void initFragment() {
        welcomeFragment = new WelcomeFragment();
        probandProfileFragment = new ProbandProfileFragment();
        chooseQuestionnaireFragment = new ChooseQuestionnaireFragment();
        aboutUsFragment = new AboutUsFragment();
        settingFragment = new SettingFragment();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        triggerEventManager = TriggerEventManager.getTriggerEventManager();
        triggerEventManager.setOverviewActivity(instance);

        //=============================
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        setEveryTriggeredDateQuestionnaireAlarm(alarmManager);
        setEveryTriggeredTimeQuestionnaireAlarm(alarmManager);

        Intent intent = getIntent();
        String fromIntent = intent.getStringExtra("notityToAnswer");
        if (!TextUtils.isEmpty(fromIntent)) {
            if (fromIntent.equals("chooseQuestionnaireFragment")) {
                isFirstLoad = false;
                replaceFragment(chooseQuestionnaireFragment);
            }

        }
        Intent startServiceIntent = new Intent(OverviewActivity.this, DeviceSensorService.class);
        //===========stop service===============

        startService(startServiceIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(receiver);
        Log.i(TAG, "has unregist....");

    }

    /**
     * Initialize data from intent
     */
    private void initFromIntent() {

        Intent intent = getIntent();

        // questionnaires

        String questionnaireJSON = intent.getStringExtra("questionnaire_JSON");
        if (TextUtils.isEmpty(questionnaireJSON)) {
            questionnaireJSON = PreferenceUtil.getString("questionnaireJSON", "");
        }
        this.allQuestionnaireList = JsonUtil.fromJsonToQuestionnaireList(questionnaireJSON);

        TriggerEventManager.getTriggerEventManager().setQuestionnaireList(allQuestionnaireList);
        for (Questionnaire questionnaire : allQuestionnaireList) {
            TriggerEvent triggerEvent = questionnaire.getTriggerEvent();
            if (triggerEvent.getSensorList() == null) {
                triggerEvent.setSensorList(new boolean[12]);
            }
            triggerEvent.setSensor();
            TriggerEventManager.getTriggerEventManager().addObserver(questionnaire);
        }
        //        TriggerEventManager.getTriggerEventManager().setQuestionnaireList
        // (allQuestionnaireList);

        // proband
        Proband probandFromLogIn = intent.getParcelableExtra("proband");
        if (probandFromLogIn != null) {
            this.proband = probandFromLogIn;
        } else {
            String probandJSON = PreferenceUtil.getString("probandJSON",
                                                          "");
            this.proband = JsonUtil.fromJsonToProband(probandJSON);
        }

        // pendingIntent from Notification
        //        String fromIntent = intent.getStringExtra("notityToAnswer");
        //        if (!TextUtils.isEmpty(fromIntent)) {
        //            if (fromIntent.equals("chooseQuestionnaireFragment")) {
        //                isFirstLoad = false;
        //                replaceFragment(chooseQuestionnaireFragment);
        //            }
        //
        //        }

    }


    /**
     * Initialize the view of activity
     */
    private void initView() {

        title = (RelativeLayout) findViewById(R.id.title_overview);

        // =================== TextView title_overview ===============================
        tv_title = (TextView) findViewById(R.id.title_title);
        // ==================================================================

        if (isFirstLoad) {
            replaceFragment(welcomeFragment);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // =================== Button nav ===================================
        btn_nav = (Button) findViewById(R.id.nav);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        // ==================================================================

        // =================== Navigation Menu ==============================
        navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        }
        navView.setItemTextAppearance(R.style.TextAppearance_FontPath);
        navView.setCheckedItem(R.id.nav_profile);
        navView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        tv_title.setText(R.string.nav_profile);
                        replaceFragment(probandProfileFragment);
                        //                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_questionnaire_list:
                        tv_title.setText(R.string.nav_questionnaire);
                        replaceFragment(chooseQuestionnaireFragment);
                        //                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_about_us:
                        tv_title.setText(R.string.nav_about_us);
                        replaceFragment(aboutUsFragment);
                        //                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_settings:
                        tv_title.setText(R.string.nav_settings);
                        replaceFragment(settingFragment);
                        //                        mDrawerLayout.closeDrawers();
                        break;
                    default:

                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        // ==================================================================

    }

    /**
     * replace the current fragment with <code>fragment</code>
     *
     * @param fragment fragment that the proband wants to switch to
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transition = fragmentManager.beginTransaction();
        transition.replace(R.id.switch_fragment,
                           fragment);
        transition.addToBackStack(null);
        transition.commit();
    }


    /**
     * Remove the questionnaire whose index is <code>selectedQuestionnaireIndex</code> from
     * <code>triggeredQuestionnaireList</code>
     */
    private void removeSelectedQuestionByIndex() {
        this.triggeredQuestionnaireList.remove(this.selectedQuestionnaireIndex);
    }

    /**
     * Get the questionnaire with id <code>questionnaireId</code>
     *
     * @param questionnaireID target questionnaire's id
     * @return <br>
     * <li>questionnaire instance whose id is <coded>questionnaireID</coded></li>
     * <li>null, if the target questionnaire is not in <code>allQuestionnaireList</code></li>
     */
    private Questionnaire getQuestionnaire(String questionnaireID) {
        Questionnaire questionnaire = null;
        for (Questionnaire tempQuestionnaire : this.allQuestionnaireList) {
            if (tempQuestionnaire.getQuestionnaireID()
                    .equals(questionnaireID)) {
                questionnaire = tempQuestionnaire;
            }
        }
        return questionnaire;
    }


    /**
     * Add questionnaire with id <code>questionnaireID</code> to
     * <code>triggeredQuestionnaireList</code>
     *
     * @param questionnaireID id of the questionnaire that will be added
     */
    public void addQuestionnaireToTriggeredQuestionnaireList(String questionnaireID) {
        Questionnaire questionnaire = getQuestionnaire(questionnaireID);
        this.addQuestionnaireToTriggeredQuestionnaireList(questionnaire);
    }


    /**
     * Add <code>questionnaire</code> to <code>triggeredQuestionnaireList</code>
     *
     * @param questionnaire questionnaire to be added
     */
    private void addQuestionnaireToTriggeredQuestionnaireList(Questionnaire questionnaire) {

        questionnaire.trigger(OverviewActivity.this);
        Questionnaire q = questionnaire.cloneItself();
        //        q.setTriggerTime(TimeUtil.getCurrentTime());

        if (!this.triggeredQuestionnaireList.isEmpty()) {

            this.triggeredQuestionnaireList.add(q);
            if (chooseQuestionnaireFragment.getAdapter() != null) {
                chooseQuestionnaireFragment.getAdapter().notifyDataSetChanged();
            }
        } else {
            this.triggeredQuestionnaireList.add(q);
        }
    }


    @Override
    protected void onActivityResult(int requestCode,
            int resultCode,
            Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //                    final String answeredQuestionnaireID = data
                    // .getStringExtra("answered " +
                    //
                    //            "questionnaire " +
                    //
                    //            "ID");
                    // remove the answered questionnaire from allQuestionnaireList
                    //                    removeQuestionnaireFromTriggeredQuestionnaireList
                    // (answeredQuestionnaireID);
                    //                    this.selectedQuestionnaire.setAnswered(true);
                    removeSelectedQuestionByIndex();
                    // save the new triggeredQuestionnaireList locally
                    PreferenceUtil.commitString("questionnaireJSON",
                                                JsonUtil.createJSON(
                                                        this.triggeredQuestionnaireList));
                    // notify allQuestionnaireList's adapter that the list has been changed
                    chooseQuestionnaireFragment.getAdapter()
                            .notifyDataSetChanged();
                }
                break;
            default:

                break;
        }
    }

    /**
     * Change app's language to <code>language</code> immediately
     *
     * @param language <br>
     *                 <li>"en" for English</li>
     *                 <li>"de" for Deutsch</li>
     */
    public void switchLanguageImmediately(String language) {
        super.switchLanguage(language);
        finish();
        Intent intent = new Intent(OverviewActivity.this,
                                   OverviewActivity.class);
        startActivity(intent);

    }

    /**
     * Get actionBar
     *
     * @return <code>title</code>
     */
    public View ActionBar() {
        return this.title;
    }

    // ======================= setters and getters =============================

    public Proband getProband() {
        return proband;
    }

    public void setProband(Proband proband) {
        this.proband = proband;
    }

    public List<Questionnaire> getAllQuestionnaireList() {
        return allQuestionnaireList;
    }

    public void setAllQuestionnaireList(List<Questionnaire> allQuestionnaireList) {
        this.allQuestionnaireList = allQuestionnaireList;
    }

    public Questionnaire getSelectedQuestionnaire() {
        return selectedQuestionnaire;
    }

    public void setSelectedQuestionnaire(Questionnaire selectedQuestionnaire) {
        this.selectedQuestionnaire = selectedQuestionnaire;
    }

    public List<Questionnaire> getTriggeredQuestionnaireList() {
        return triggeredQuestionnaireList;
    }

    public void setTriggeredQuestionnaireList(List<Questionnaire> triggeredQuestionnaireList) {
        this.triggeredQuestionnaireList = triggeredQuestionnaireList;
    }

    public int getSelectedQuestionnaireIndex() {
        return selectedQuestionnaireIndex;
    }

    public void setSelectedQuestionnaireIndex(int selectedQuestionnaireIndex) {
        this.selectedQuestionnaireIndex = selectedQuestionnaireIndex;
    }

    // =========================================================================

    // test data
    private void initTestData() {

        //        String list = PreferenceUtil.getString("questionnaireJSON", "");
        //        if (!TextUtils.isEmpty(list)) {
        //            allQuestionnaireList = JsonUtil.fromJsonToQuestionnaireList(list);
        //        } else {
        //
        //            allQuestionnaireList = new ArrayList<>();
        //
        //            Questionnaire questionnaire = new Questionnaire("A", "2017.1.2 14:00",
        // "2017.2.2 " +
        //                    "14:00");
        //
        //            // q1
        //            ArrayList<Option> optionList = new ArrayList<>();
        //            optionList.add(new Option("At home", "Q3"));
        //            optionList.add(new Option("At work", "Q3"));
        //            optionList.add(new Option("on the way", "Q2"));
        //            Question q1 = new Question("Where are you?", new SingleChoice(optionList),
        // "Q1");
        //            questionnaire.addQuestion(q1);
        //
        //            // q2
        //            Question q2 = new Question("Where are you heading to?", new TextAnswer(),
        // "Q2");
        //            questionnaire.addQuestion(q2);
        //
        //            // q3
        //            Question q3 = new Question("How are you feeling?", new DragScale(10), "Q3");
        //            questionnaire.addQuestion(q3);
        //
        //            // q4
        //            ArrayList<Option> optionArrayList = new ArrayList<>();
        //            optionArrayList.add(new Option("Swimming", null));
        //            optionArrayList.add(new Option("Reading", null));
        //            optionArrayList.add(new Option("Coding", null));
        //            optionArrayList.add(new Option("Studying", null));
        //            Question q4 = new Question("What's ur hobby?", new MultipleChoice
        // (optionArrayList),
        //                                       "Q4");
        //            questionnaire.addQuestion(q4);
        //
        //            // q5
        //            ArrayList<Option> options = new ArrayList<>();
        //            options.add(new Option("very bad", null));
        //            options.add(new Option("bad", null));
        //            options.add(new Option("so so", null));
        //            options.add(new Option("good", null));
        //            options.add(new Option("very good!", null));
        //            Question q5 = new Question("Do you like this app?", new StepScale(options),
        // "Q5");
        //            questionnaire.addQuestion(q5);
        //
        //
        //            allQuestionnaireList.add(questionnaire);
        //            allQuestionnaireList.add(new Questionnaire("B", "2017.1.2", "2017.2.2"));
        //            allQuestionnaireList.add(new Questionnaire("C", "2017.1.3", "2017.2.2"));
        //        }


        // allQuestionnaireList = new ArrayList<>();

        Questionnaire questionnaireA = new Questionnaire("A", new Duration(48, 0, 0));
        // q1
        ArrayList<Option> optionList = new ArrayList<>();
        optionList.add(new Option("happy",
                                  "Q2"));
        optionList.add(new Option("unhappy",
                                  "Q3"));
        Question q1 = new Question("Are you happy?",
                                   new SingleChoice(optionList),
                                   "Q1", true);
        questionnaireA.addQuestion(q1);

        // q2
        Question q2 = new Question("Why happy?",
                                   new TextAnswer(),
                                   "Q2", false);
        questionnaireA.addQuestion(q2);

        // q3
        Question q3 = new Question("Why unhappy?",
                                   new TextAnswer(),
                                   "Q3", false);
        questionnaireA.addQuestion(q3);

        // q4
        ArrayList<Option> optionArrayList = new ArrayList<>();
        optionArrayList.add(new Option("Swimming",
                                       null));
        optionArrayList.add(new Option("Reading",
                                       null));
        optionArrayList.add(new Option("Coding",
                                       null));
        optionArrayList.add(new Option("Studying",
                                       null));
        Question q4 = new Question("What's ur hobby?",
                                   new MultipleChoice(optionArrayList),
                                   "Q4", true);
        questionnaireA.addQuestion(q4);

        // q5
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("very bad",
                               null));
        options.add(new Option("bad",
                               null));
        options.add(new Option("so so",
                               null));
        options.add(new Option("good",
                               null));
        options.add(new Option("very good!",
                               null));
        Question q5 = new Question("Do you like this app?",
                                   new StepScale(options),
                                   "Q5", true);
        questionnaireA.addQuestion(q5);

        // q6
        Question q6 = new Question("Will you recommand our app to your friend?", new DragScale
                (10), "Q6", true);
        questionnaireA.addQuestion(q6);

        TriggerEvent triggerEvent1 = new TriggerEvent(questionnaireA.getQuestionnaireID());
        triggerEvent1.setLight(true);
        triggerEvent1.setLightMinValue(1000);
        triggerEvent1.setLightMaxValue(2000);
        triggerEvent1.setMinTimeSpace(5);
        triggerEvent1.setAirTemperature(true);
        //triggerEvent1.setTime("10-55-10");
        //Date date = TimeUtil.getCurrentTime();
        // Calendar calendar = Calendar.getInstance();
        //calendar.setTime(TimeUtil.getCurrentTime());
        //calendar.add(Calendar.SECOND, 10);
        //triggerEvent1.setDateTime(calendar.getTime());
        //        questionnaireA.setTriggerEvent(triggerEvent1);
        triggerEvent1.setAmbientTemperatureMaxValue(20);
        triggerEvent1.setAmbientTemperatureMinValue(19);
        questionnaireA.setTriggerEvent(triggerEvent1);
        questionnaireA.setValid(true);

        allQuestionnaireList.add(questionnaireA);
        //TriggerEventManager triggerEventManager = TriggerEventManager.getTriggerEventManager();
        TriggerEventManager.getTriggerEventManager().setQuestionnaireList(allQuestionnaireList);
        for (Questionnaire questionnaire1 : allQuestionnaireList) {
            TriggerEventManager.getTriggerEventManager().addObserver(questionnaire1);
        }

        //        triggeredQuestionnaireList.add(questionnaireA);


        //        List<Questionnaire> testquestionnaireList = new ArrayList<>();
        //        testquestionnaireList.add(questionnaireA);
        //        questionnaireList.add(new Questionnaire("B", "2017.1.2", "2017.2.2"));
        //        questionnaireList.add(new Questionnaire("C", "2017.1.3", "2017.2.2"));

    }

    /**
     * Helper method.Gets instance for class overviewActivity.
     *
     * @return the instance,overviewActivity itself.
     */
    public static OverviewActivity getInstance() {
        return instance;
    }


    /**
     * Select questionnaire,which will everyday triggered by Time.
     */
    private void addTriggeredByTimeQuestionnaire() {
        for (Questionnaire questionnaire : allQuestionnaireList) {
            if (questionnaire.getTriggerEvent().getTime() != null) {
                this.triggeredByTimeQuestionnaireList.add(questionnaire);
            }
        }
    }

    /**
     * Select questionnaire,which will triggered by Date.
     */
    private void addTriggeredByDatetimeQuestionnaire() {
        for (Questionnaire questionnaire : allQuestionnaireList) {
            if (questionnaire.getTriggerEvent().getDateTime() != null) {
                this.triggeredByDateQuestionnaireList.add(questionnaire);
            }
        }
    }

    /**
     * Helper method for each Questionnaire, which will everyday triggered by Time.
     * <br>For these Questionnaire a alarm will be set.</br>
     *
     * @param alarmManager This class provides access to the system alarm services.
     */
    private void setEveryTriggeredTimeQuestionnaireAlarm(AlarmManager alarmManager) {
        AlarmManager alarmManager1 = alarmManager;
        int index = 0;
        long oneDay = 1000 * 60 * 60 * 24;
        for (Questionnaire questionnaire : triggeredByTimeQuestionnaireList) {
            //格式是否正确？
            long triggeredTime = this.transferTriggeredTimeToTriggerAtMillis(questionnaire
                                                                                     .getTriggerEvent()
                                                                                     .getTime());
            Intent intent = new Intent("addQuestionnaireToList");
            //intent.setAction("addQuestionnaireToList");
            Log.d(TAG, intent.getAction());
            intent.putExtra("questionnaireID",
                            questionnaire.getTriggerEvent().getQuestionnaireID());
            Log.d(TAG, intent.getStringExtra("questionnaireID"));
            PendingIntent sender = PendingIntent.getBroadcast(OverviewActivity.this, index, intent,
                                                              PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, triggeredTime, oneDay, sender);
            index++;
            Log.d(TAG, String.valueOf(index));
        }

    }

    /**
     * Helper method for each Questionnaire, which will triggered by Date.
     * <br>For these Questionnaire a alarm will be set.</br>
     *
     * @param alarmManager This class provides access to the system alarm services.
     */

    private void setEveryTriggeredDateQuestionnaireAlarm(AlarmManager alarmManager) {
        AlarmManager alarmManager1 = alarmManager;
        int index = 0;
        for (Questionnaire questionnaire : triggeredByDateQuestionnaireList) {
            //格式是否正确？
            long triggeredtDate = questionnaire.getTriggerEvent().getDateTime().getTime();

            if (triggeredtDate >= TimeUtil.getCurrentTime().getTime()) {
                Log.d(TAG, String.valueOf(triggeredtDate - TimeUtil.getCurrentTime().getTime()));
                //要不要判断时间点是否已经过去
                Intent intent = new Intent("addQuestionnaireToList");
                //intent.setAction("addQuestionnaireToList");
                Log.d(TAG, intent.getAction());
                intent.putExtra("questionnaireID",
                                questionnaire.getTriggerEvent().getQuestionnaireID());
                Log.d(TAG, intent.getStringExtra("questionnaireID"));
                PendingIntent sender = PendingIntent
                        .getBroadcast(OverviewActivity.this, index, intent,
                                      PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager1.set(AlarmManager.RTC_WAKEUP, triggeredtDate, sender);
                index++;
                Log.d(TAG, String.valueOf(index));

            } else {
                Log.d(TAG, "Sorry,time is out,the questionnaire will not be added");
            }

        }

    }

    /**
     * Helper Method.For Method setEveryTriggeredTimeQuestionnaireAlarm.
     * <br>Input time is as string,output time is as long value.</br>
     * <p>
     *
     * @param time Questionnaire's triggered time
     * @return time as a long value.
     */
    private long transferTriggeredTimeToTriggerAtMillis(String time) {
        long timetest;
        boolean todayshouldtriggered = false;
        String inputTime = time;
        String[] inputTimeList = inputTime.split("-");
        Integer[] inputTimeListOfInteger = new Integer[inputTimeList.length];
        for (int i = 0; i < inputTimeList.length; i++) {
            inputTimeListOfInteger[i] = Integer.valueOf(inputTimeList[i]);
            //System.out.println(String.valueOf(time2[i]));
        }
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1"));
        Calendar calendar1 = Calendar.getInstance();
        Log.i(TAG, calendar1.getTimeZone().toString());
        //calendar1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date currentDate = calendar1.getTime();
        DateFormat sdf = new SimpleDateFormat("HH-mm-ss");
        String currentTime = sdf.format(currentDate);
        String[] currentTimeList = currentTime.split("-");
        Integer[] currentTimeListOfInteger = new Integer[currentTimeList.length];
        for (int i = 0; i < currentTimeList.length; i++) {
            currentTimeListOfInteger[i] = Integer.valueOf(currentTimeList[i]);
            //System.out.println(String.valueOf(time4[i]));
        }
        long setTime = inputTimeListOfInteger[0] * 60 * 60 + inputTimeListOfInteger[1] * 60 +
                inputTimeListOfInteger[2];
        long nowTime = currentTimeListOfInteger[0] * 60 * 60 + currentTimeListOfInteger[1] * 60 +
                currentTimeListOfInteger[2];
        Log.i(TAG, String.valueOf(setTime - nowTime));
        if (setTime >= nowTime) {
            todayshouldtriggered = true;
        }
        //============test==============
        if (todayshouldtriggered) {
            Log.i(TAG, "will add a questionnaire");
        } else {
            Log.i(TAG, "will Tomorrow add a questionnaire");
        }

        if (todayshouldtriggered) {
            calendar1.set(Calendar.HOUR_OF_DAY, inputTimeListOfInteger[0]);
            calendar1.set(Calendar.MINUTE, inputTimeListOfInteger[1]);
            calendar1.set(Calendar.SECOND, inputTimeListOfInteger[2]);
        } else {
            calendar1.add(Calendar.DAY_OF_MONTH, 1);
            calendar1.set(Calendar.HOUR_OF_DAY, inputTimeListOfInteger[0]);
            calendar1.set(Calendar.MINUTE, inputTimeListOfInteger[1]);
            calendar1.set(Calendar.SECOND, inputTimeListOfInteger[2]);
        }
        return calendar1.getTime().getTime();


    }


}
