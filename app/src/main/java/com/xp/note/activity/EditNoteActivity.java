package com.xp.note.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xp.note.R;
import com.xp.note.db.DBManager;
import com.xp.note.model.Note;
import com.xp.note.utils.TimeUtil;

import java.util.Calendar;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 笔记编辑页面
 *
 */
public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText titleEt;
    private EditText contentEt;
    private FloatingActionButton saveBtn;
    private int noteID = -1;
    private DBManager dbManager;

    private EditText settingTime;
    private String alarmClockTime;                    //闹铃时间

    private RadioGroup radioGroup;
    private RadioButton high,medium,low;              //优先级
    private RadioButton currentRadbtn;                //选中的优先级

    private Long clockTime;                           //闹钟时间（毫秒）


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);


        init();
        //初始化Mob的shareSDK（作用是分享到微信）
        initMob();

    }




    private void initMob() {
        //MobSDK.init(this);
        //配置微信配置
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","1");
        hashMap.put("SortId","1");
        hashMap.put("AppKey","297d0c9cf063c");
        hashMap.put("AppSecret","4e3b34e24cf1c9f5b3f0a741311e4ebe");
        hashMap.put("RedirectUrl","http://www.sharesdk.cn");
        hashMap.put("ShareByAppClient","true");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(WechatMoments.NAME,hashMap);
    }

    //初始化
    private void init() {
        dbManager = new DBManager(this);
        titleEt = findViewById(R.id.note_title);
        contentEt =  findViewById(R.id.note_content);
        saveBtn = findViewById(R.id.save);
        saveBtn.setOnClickListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //name，defaultValue
        noteID = getIntent().getIntExtra("id", -1);

        //初始化view
        settingTime = findViewById(R.id.editview_remindtime);
        radioGroup = findViewById(R.id.radiogroup_priority);
        high = findViewById(R.id.radiobutton_high);
        medium = findViewById(R.id.radiobutton_medium);
        low = findViewById(R.id.radiobutton_low);
        currentRadbtn = findViewById(R.id.radiobutton_low);

        if (noteID != -1) {
            showNoteData(noteID);
        }
        setStatusBarColor();


        //设置优先级
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                currentRadbtn = findViewById(i);
                Toast.makeText(getApplicationContext(), "按钮组值发生改变,你选了" + currentRadbtn.getText()+currentRadbtn.getId(), Toast.LENGTH_LONG).show();
            }
        });

        //设置闹铃时间
        settingTime.setFocusable(false);
        settingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = new DatePickerDialog(EditNoteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(EditNoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                int m = month + 1;
                                alarmClockTime = year + "-" + m + "-" + dayOfMonth + " " + hourOfDay + ":" + minute +" "+ TimeUtil.getCurrentWendday();
                                settingTime.setText(alarmClockTime);
                                clockTime = TimeUtil.transformateFromDateToMilis(year,month,dayOfMonth,hourOfDay,minute);
                                Toast.makeText(getBaseContext(),""+ clockTime,Toast.LENGTH_SHORT).show();
                            }

                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                        timePickerDialog.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });
    }

    //设置状态栏同色
    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#00574B"));
    }

    //显示更新的数据
    private void showNoteData(int id) {
        Note note = dbManager.readData(id);
        titleEt.setText(note.getTitle());
        contentEt.setText(note.getContent());
        settingTime.setText(TimeUtil.transformateFromMilisToDate(note.getClockTime()));
        clockTime = note.getClockTime();
        String priority = note.getPriority();
        if (priority.equals("高")){
            high.setChecked(true);
        }else if (priority.equals("中")){
            medium.setChecked(true);
        }else if (priority.equals("低")){
            low.setChecked(true);
        }
        //控制光标
        Spannable spannable = titleEt.getText();
        Selection.setSelection(spannable, titleEt.getText().length());
    }



    @Override
    public void onClick(View view) {
        String title = titleEt.getText().toString();
        String content = contentEt.getText().toString();
        String time = TimeUtil.getCurrentTime();
        String  priorityString = currentRadbtn.getText().toString();

        if (noteID == -1) {
            //默认添加
            dbManager.addToDB(title, content, time,priorityString,clockTime);
        } else {
            //更新
            dbManager.updateNote(noteID, title, content, time,priorityString,clockTime);
        }
        Intent i = new Intent(EditNoteActivity.this, MainActivity.class);
        startActivity(i);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.finish();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //截取当前屏幕图片分享
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());

        switch (item.getItemId()) {
            case R.id.action_share_weixin:
                //分享到微信
                Platform.ShareParams params = new Platform.ShareParams();
                params.setShareType(Platform.SHARE_TEXT);
                params.setImageData(bitmap);
                params.setText("text");
                params.setTitle("title");
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                wechat.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        Toast.makeText(getBaseContext(),"wechat",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
                wechat.share(params);
                break;
            case R.id.action_share_weixinmoment:
                //分享到微信朋友圈
                Platform.ShareParams params1 = new Platform.ShareParams();
                params1.setShareType(Platform.SHARE_IMAGE);
                params1.setImageData(bitmap);
                Platform wechat1 = ShareSDK.getPlatform(WechatMoments.NAME);
                wechat1.share(params1);
                Toast.makeText(getBaseContext(),"wechatMoment",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_share_weibo:
                Toast.makeText(getBaseContext(),"此分享待实现",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //按返回键时
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.finish();
    }


}
