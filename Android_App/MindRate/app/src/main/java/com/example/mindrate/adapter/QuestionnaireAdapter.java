package com.example.mindrate.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mindrate.R;
import com.example.mindrate.gson.Questionnaire;

import java.util.List;


/**
 * Project: MindRate
 * Package: com.example.mindrate.adapter
 * Author: Ecko Tan
 * E-mail: eckotan@icloud.com
 * Created at 2017/1/23:11:14
 */

public class QuestionnaireAdapter extends ArrayAdapter<Questionnaire> {

    private int resourceID;

    public QuestionnaireAdapter(Context context, int resource, List<Questionnaire> objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Questionnaire questionnaire = getItem(position);
        View view = null;
        ViewHolder viewHolder;

        // improve the efficiency of ListView
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_questionnaireName = (TextView) view.findViewById(R.id.questionnaire_name);
            viewHolder.tv_beginTime = (TextView) view.findViewById(R.id.questionnaire_begin_time);
            viewHolder.tv_endTime = (TextView) view.findViewById(R.id.questionnaire_end_time);
            view.setTag(viewHolder); // store ViewHolder in View
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_questionnaireName.setText(questionnaire.getQuestionnaireID());

        // TODO: set beginTime
        viewHolder.tv_beginTime.setText(questionnaire.getBeginTime());

        // TODO: set endTime
        viewHolder.tv_endTime.setText(questionnaire.getEndTime());
        return view;
    }

    class ViewHolder {

        TextView tv_questionnaireName;
        TextView tv_beginTime;
        TextView tv_endTime;
    }
}