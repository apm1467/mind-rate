package com.example.mindrate.activity;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.mindrate.R;
import com.example.mindrate.fragment.AboutUsFragment;
import com.example.mindrate.fragment.ChooseQuestionnaireFragment;
import com.example.mindrate.fragment.ProbandProfileFragment;
import com.example.mindrate.fragment.WelcomeFragment;
import com.example.mindrate.gson.Proband;

public class OverviewActivity extends BaseActivity {

    private Proband proband;

    // ==================== View components ==================================
    private DrawerLayout mDrawerLayout;
    private Button btn_nav;
    private NavigationView navView;

    // =======================================================================

    ProbandProfileFragment probandProfileFragment = new ProbandProfileFragment();
    ChooseQuestionnaireFragment chooseQuestionnaireFragment = new ChooseQuestionnaireFragment();
    AboutUsFragment aboutUsFragment = new AboutUsFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        initFromIntent();
        initView();

//        tv_questionText.setText(Utility.createJSON(this.proband));
    }

    private void initFromIntent() {
        Intent intent = getIntent();
        String questionnaire = intent.getStringExtra("questionnaire_JSON");
        this.proband = (Proband) intent.getParcelableExtra("proband");
    }

    private void initView() {

        replaceFragment(new WelcomeFragment());

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
        navView.setCheckedItem(R.id.nav_profile);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_profile:
                        replaceFragment(probandProfileFragment);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_questionnaire_list:
                        replaceFragment(chooseQuestionnaireFragment);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_about_us:
                        replaceFragment(aboutUsFragment);
                        mDrawerLayout.closeDrawers();
                        break;
                    default:

                        break;
                }
                return true;
            }
        });


        // ==================================================================
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transition = fragmentManager.beginTransaction();
        transition.replace(R.id.switch_fragment, fragment);
        transition.commit();
    }

    public Proband getProband() {
        return proband;
    }

    public void setProband(Proband proband) {
        this.proband = proband;
    }
}