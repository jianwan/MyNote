package com.xp.note.activity;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xp.note.R;
import com.xp.note.utils.SharedPreferencesUtil;

import cn.bmob.v3.Bmob;

/*
 *  启动页 一个activity + 一个fragment  具体样式查看activity_splash.xml + fragmet+splash.xml
 *  参考文章：http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2017/0418/7843.html  (即fragment + viewpager)
 *  Android倒计时实现(此处使用CountDownTimer)：https://juejin.im/post/58bead54ac502e006b2fe6e0
 */

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private int bgColors[];
    private int currentPosition;
    private ImageButton pre,next;
    private AppCompatButton finish;
    private ImageView[] indicators;
    private Button jump;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        initView();

        //第一：默认初始化
        Bmob.initialize(this, "bdc479c9f78d163df6442083ce8578e8");


        //初始化viewpager + adapter
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //设置viewpager滑动监听事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int colorUpdate = (Integer) new ArgbEvaluator().evaluate(positionOffset, bgColors[position], bgColors[position == 2 ? position : position + 1]);
                mViewPager.setBackgroundColor(colorUpdate);
            }
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                updateIndicators(position);
                mViewPager.setBackgroundColor(bgColors[position]);
                //根据viewpager位置让button显现或隐藏
                pre.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                next.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                finish.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });


        //倒计时
        timer = new CountDownTimer(6000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                jump.setText("还剩 " + millisUntilFinished / 1000 + " s");
            }

            @Override
            public void onFinish() {
                intentToNext();
            }
        }.start();


    }



    private void initView() {

        bgColors = new int[]{ContextCompat.getColor(this, R.color.app_color),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimaryDark)};

        indicators = new ImageView[] {
                findViewById(R.id.imageViewIndicator0),
                findViewById(R.id.imageViewIndicator1),
                findViewById(R.id.imageViewIndicator2) };

        pre = findViewById(R.id.imageButtonPre);
        next = findViewById(R.id.imageButtonNext);
        finish = findViewById(R.id.buttonFinish);
        jump = findViewById(R.id.jump);

        pre.setOnClickListener(this);
        next.setOnClickListener(this);
        finish.setOnClickListener(this);
        jump.setOnClickListener(this);

        setStatusBarColor();
    }


    //设置状态栏同色
    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 创建状态栏的管理实例
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // 激活状态栏设置
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#00574B"));
    }

    //随viewpager更改底部白点
    private void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.onboarding_indicator_selected : R.drawable.onboarding_indicator_unselected
            );
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageButtonPre:
                mViewPager.setCurrentItem(currentPosition - 1);
                break;
            case R.id.imageButtonNext:
                mViewPager.setCurrentItem(currentPosition + 1);
                break;
            case R.id.buttonFinish:
                //根据是否登录决定跳转的activity
                intentToNext();
//                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;

            case R.id.jump:
                intentToNext();
                break;
        }
    }



    private void intentToNext() {
        timer.cancel();
        SharedPreferencesUtil.init(this);
        if (SharedPreferencesUtil.getIsLogin()){
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }



    /* ------------------------------ 以下为fragment的代码 --------------------------------- */

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_splash, container, false);
            TextView textView = rootView.findViewById(R.id.section_intro);
            ImageView imageView = rootView.findViewById(R.id.section_img);

            int postion = getArguments().getInt(ARG_SECTION_NUMBER);
            if (postion == 1){
                textView.setText(getString(R.string.section_one));
                imageView.setImageResource(R.drawable.ic_speaker_notes_black_48dp);
            }else if (postion == 2){
                textView.setText(getString(R.string.section_two));
                imageView.setImageResource(R.drawable.ic_alarm_black_48dp);
            }else if (postion == 3){
                textView.setText(getString(R.string.section_three));
                imageView.setImageResource(R.drawable.ic_share_black_48dp);
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

}
