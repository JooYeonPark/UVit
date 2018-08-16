package com.example.user.uvit_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016-10-06.
 */
public class FragmentSetting_Profile extends Fragment implements View.OnClickListener {
    MySQLiteOpenHelper dbHelper;
    String TAG = "profile";

    private View view = null;
    TextView targets_vitamid;
    EditText name, age;
    RadioButton radio_men, radio_women;
    Button save, type1, type2, type3, type4, type5, type6, exposure_90_upper, exposure_50_upper, exposure_10_upper, exposure_90_lower, exposure_50_lower, exposure_10_lower;

    String nameText, ageText , gender ;
    int upper , lower, skinType, targets_vitamind_sufficient, targets_vitamind_upperlimit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_setting_profile, container, false);
        }
        dbHelper = new MySQLiteOpenHelper(view.getContext());

        targets_vitamid = (TextView) view.findViewById(R.id.profile_targets_vitamind);

        name = (EditText) view.findViewById(R.id.profile_name);
        age = (EditText) view.findViewById(R.id.profile_age);
        radio_men = (RadioButton) view.findViewById(R.id.radio_men);
        radio_women = (RadioButton) view.findViewById(R.id.radio_women);

        type1 = (Button) view.findViewById(R.id.profile_skin1);
        type1.setOnClickListener(this);
        type2 = (Button) view.findViewById(R.id.profile_skin2);
        type2.setOnClickListener(this);
        type3 = (Button) view.findViewById(R.id.profile_skin3);
        type3.setOnClickListener(this);
        type4 = (Button) view.findViewById(R.id.profile_skin4);
        type4.setOnClickListener(this);
        type5 = (Button) view.findViewById(R.id.profile_skin5);
        type5.setOnClickListener(this);
        type6 = (Button) view.findViewById(R.id.profile_skin6);
        type6.setOnClickListener(this);
        exposure_90_upper = (Button) view.findViewById(R.id.profile_exposure_90_upper);
        exposure_90_upper.setOnClickListener(this);
        exposure_50_upper = (Button) view.findViewById(R.id.profile_exposure_50_upper);
        exposure_50_upper.setOnClickListener(this);
        exposure_10_upper = (Button) view.findViewById(R.id.profile_exposure_10_upper);
        exposure_10_upper.setOnClickListener(this);
        exposure_90_lower = (Button) view.findViewById(R.id.profile_exposure_90_lower);
        exposure_90_lower.setOnClickListener(this);
        exposure_50_lower = (Button) view.findViewById(R.id.profile_exposure_50_lower);
        exposure_50_lower.setOnClickListener(this);
        exposure_10_lower = (Button) view.findViewById(R.id.profile_exposure_10_lower);
        exposure_10_lower.setOnClickListener(this);
        save = (Button) view.findViewById(R.id.profile_save);
        save.setOnClickListener(this);
        radio_men = (RadioButton) view.findViewById(R.id.radio_men);
        radio_men.setOnClickListener(this);
        radio_men.setChecked(true);
        radio_women = (RadioButton) view.findViewById(R.id.radio_women);
        radio_women.setOnClickListener(this);

        setValues();

//        int[] buttonIds = {
//                R.id.profile_skin1, R.id.profile_skin2, R.id.profile_skin3, R.id.profile_skin4, R.id.profile_skin5, R.id.profile_skin6,
//                R.id.profile_exposure_90_upper, R.id.profile_exposure_50_upper, R.id.profile_exposure_10_upper,
//                R.id.profile_exposure_90_lower,R.id.profile_exposure_50_lower,R.id.profile_exposure_10_lower,
//        };
//
//        for (int id : buttonIds) {
//            Button button = (Button) view.findViewById(id);
//            button.setOnClickListener(this);
//        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_men:
                if (radio_men.isChecked()) gender = "남";
                break;
            case R.id.radio_women:
                if (radio_women.isChecked()) gender = "여";
                break;
            case R.id.profile_skin1:
                type1.setBackgroundResource(R.drawable.img_skin1_check);
                type2.setBackgroundResource(R.drawable.img_skin2);
                type3.setBackgroundResource(R.drawable.img_skin3);
                type4.setBackgroundResource(R.drawable.img_skin4);
                type5.setBackgroundResource(R.drawable.img_skin5);
                type6.setBackgroundResource(R.drawable.img_skin6);
                skinType = 1;
                break;
            case R.id.profile_skin2:
                type1.setBackgroundResource(R.drawable.img_skin1);
                type2.setBackgroundResource(R.drawable.img_skin2_check);
                type3.setBackgroundResource(R.drawable.img_skin3);
                type4.setBackgroundResource(R.drawable.img_skin4);
                type5.setBackgroundResource(R.drawable.img_skin5);
                type6.setBackgroundResource(R.drawable.img_skin6);
                skinType = 2;
                break;
            case R.id.profile_skin3:
                type1.setBackgroundResource(R.drawable.img_skin1);
                type2.setBackgroundResource(R.drawable.img_skin2);
                type3.setBackgroundResource(R.drawable.img_skin3_check);
                type4.setBackgroundResource(R.drawable.img_skin4);
                type5.setBackgroundResource(R.drawable.img_skin5);
                type6.setBackgroundResource(R.drawable.img_skin6);
                skinType = 3;
                break;
            case R.id.profile_skin4:
                type1.setBackgroundResource(R.drawable.img_skin1);
                type2.setBackgroundResource(R.drawable.img_skin2);
                type3.setBackgroundResource(R.drawable.img_skin3);
                type4.setBackgroundResource(R.drawable.img_skin4_check);
                type5.setBackgroundResource(R.drawable.img_skin5);
                type6.setBackgroundResource(R.drawable.img_skin6);
                skinType = 4;
                break;
            case R.id.profile_skin5:
                type1.setBackgroundResource(R.drawable.img_skin1);
                type2.setBackgroundResource(R.drawable.img_skin2);
                type3.setBackgroundResource(R.drawable.img_skin3);
                type4.setBackgroundResource(R.drawable.img_skin4);
                type5.setBackgroundResource(R.drawable.img_skin5_check);
                type6.setBackgroundResource(R.drawable.img_skin6);
                skinType = 5;
                break;
            case R.id.profile_skin6:
                type1.setBackgroundResource(R.drawable.img_skin1);
                type2.setBackgroundResource(R.drawable.img_skin2);
                type3.setBackgroundResource(R.drawable.img_skin3);
                type4.setBackgroundResource(R.drawable.img_skin4);
                type5.setBackgroundResource(R.drawable.img_skin5);
                type6.setBackgroundResource(R.drawable.img_skin6_check);
                skinType = 6;
                break;
            case R.id.profile_exposure_10_lower:
                exposure_10_lower.setBackgroundResource(R.drawable.exposure_10_lower_check);
                exposure_50_lower.setBackgroundResource(R.drawable.exposure_50_lower);
                exposure_90_lower.setBackgroundResource(R.drawable.exposure_90_lower);
                lower = 5;
                break;
            case R.id.profile_exposure_50_lower:
                exposure_10_lower.setBackgroundResource(R.drawable.exposure_10_lower);
                exposure_50_lower.setBackgroundResource(R.drawable.exposure_50_lower_check);
                exposure_90_lower.setBackgroundResource(R.drawable.exposure_90_lower);
                lower = 25;
                break;
            case R.id.profile_exposure_90_lower:
                exposure_10_lower.setBackgroundResource(R.drawable.exposure_10_lower);
                exposure_50_lower.setBackgroundResource(R.drawable.exposure_50_lower);
                exposure_90_lower.setBackgroundResource(R.drawable.exposure_90_lower_check);
                lower = 45;
                break;
            case R.id.profile_exposure_10_upper:
                exposure_10_upper.setBackgroundResource(R.drawable.exposure_10_upper_check);
                exposure_50_upper.setBackgroundResource(R.drawable.exposure_50_upper);
                exposure_90_upper.setBackgroundResource(R.drawable.exposure_90_upper);
                upper = 5;
                break;
            case R.id.profile_exposure_50_upper:
                exposure_10_upper.setBackgroundResource(R.drawable.exposure_10_upper);
                exposure_50_upper.setBackgroundResource(R.drawable.exposure_50_upper_check);
                exposure_90_upper.setBackgroundResource(R.drawable.exposure_90_upper);
                upper = 25;
                break;
            case R.id.profile_exposure_90_upper:
                exposure_10_upper.setBackgroundResource(R.drawable.exposure_10_upper);
                exposure_50_upper.setBackgroundResource(R.drawable.exposure_50_upper);
                exposure_90_upper.setBackgroundResource(R.drawable.exposure_90_upper_check);
                upper = 45;
                break;

            case R.id.profile_save:
                addProfile();
//                setValues();
                Toast.makeText(view.getContext(),name.getText()+"님의 프로파일 저장이 완료되었습니다.",Toast.LENGTH_LONG).show();
                break;
            default:
                break;

        }
    }

    private void addProfile() {
        nameText = String.valueOf(name.getText());
        ageText = String.valueOf(age.getText());

        Log.d(TAG, nameText + ", " + ageText + "," + gender + "," + skinType + "," + upper+","+lower);

        if (radio_women.isSelected()==true) gender="여";
        else if (radio_men.isSelected()==true) gender="남";

        String[] vitamid = dbHelper.getVitamindData(ageText);
        targets_vitamind_sufficient = Integer.valueOf(vitamid[2]);
        targets_vitamind_upperlimit = Integer.valueOf(vitamid[3]);
        ValueObject vo = new ValueObject(nameText, Integer.parseInt(ageText), gender, skinType, upper, lower, targets_vitamind_sufficient, targets_vitamind_upperlimit);

        Intent intent = new Intent("SEND_PROFILE");
        intent.putExtra("NAME", vo.getName());
        intent.putExtra("AGE", vo.getAge());
        intent.putExtra("GENDER", vo.getGender());
        intent.putExtra("SKINTYPE", vo.getSkintype());
        intent.putExtra("EXPOSURE_UPPER", vo.getExposure_upper());
        intent.putExtra("EXPOSURE_LOWER", vo.getExposure_lower());
        intent.putExtra("TARGETS_VITAMIND_SUFFICIENT", vo.getTargets_vitamind_sufficient());
        intent.putExtra("TARGETS_VITAMIND_UPPERLIMIT", vo.getTargets_vitamind_upperlimit());
        view.getContext().sendBroadcast(intent);
        dbHelper.addProfileData(vo);
        setValues();

    }

    public void setValues() {
       String[] profile = dbHelper.getProfileData();
        for (int i = 0; i < profile.length; i++) {            Log.d(TAG, profile[i]);        }
        name.setText(profile[0]);
        age.setText(profile[1]);

        if (profile[2].equals("남"))
        {
            radio_men.setChecked(true);
            radio_women.setChecked(false);
        }
        if (profile[2].equals("여")) {
            radio_women.setChecked(true);
            radio_men.setChecked(false);}

        int temp = Integer.valueOf(profile[3]);
        if (temp == 1) type1.setBackgroundResource(R.drawable.img_skin1_check);
        else if (temp == 2) type2.setBackgroundResource(R.drawable.img_skin2_check);
        else if (temp == 3) type3.setBackgroundResource(R.drawable.img_skin3_check);
        else if (temp == 4) type4.setBackgroundResource(R.drawable.img_skin4_check);
        else if (temp == 5) type5.setBackgroundResource(R.drawable.img_skin5_check);
        else if (temp == 6) type6.setBackgroundResource(R.drawable.img_skin6_check);

        int temp1 = Integer.valueOf(profile[4]);
        if (temp1 == 45) exposure_90_upper.setBackgroundResource(R.drawable.exposure_90_upper_check);
        else if (temp1 == 25) exposure_50_upper.setBackgroundResource(R.drawable.exposure_50_upper_check);
        else if (temp1 == 5) exposure_10_upper.setBackgroundResource(R.drawable.exposure_10_upper_check);

        int temp2 = Integer.valueOf(profile[5]);
        if (temp2 == 45) exposure_90_lower.setBackgroundResource(R.drawable.exposure_90_lower_check);
        else if (temp2 == 25) exposure_50_lower.setBackgroundResource(R.drawable.exposure_50_lower_check);
        else if (temp2 == 5) exposure_10_lower.setBackgroundResource(R.drawable.exposure_10_lower_check);

        int temp3 = Integer.valueOf(profile[6]);
        int temp4 = Integer.valueOf(profile[7]);

        targets_vitamid.setText(String.valueOf(temp3));
    }
}