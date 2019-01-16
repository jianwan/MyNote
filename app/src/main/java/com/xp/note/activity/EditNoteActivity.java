package com.xp.note.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Selection;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xp.note.R;
import com.xp.note.db.DBManager;
import com.xp.note.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by XP on 2015/2/15.
 */
public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText titleEt;
    private EditText contentEt;
    private FloatingActionButton saveBtn;
    private int noteID = -1;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text);
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
        //name，defaultValue
        noteID = getIntent().getIntExtra("id", -1);
        if (noteID != -1) {
            showNoteData(noteID);
        }
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
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#00574B"));
    }

    //显示更新的数据
    private void showNoteData(int id) {
        Note note = dbManager.readData(id);
        titleEt.setText(note.getTitle());
        contentEt.setText(note.getContent());
        //控制光标
        Spannable spannable = titleEt.getText();
        Selection.setSelection(spannable, titleEt.getText().length());
    }

    @Override
    public void onClick(View view) {
        String title = titleEt.getText().toString();
        String content = contentEt.getText().toString();
        String time = getTime();
        if (noteID == -1) {
            //默认添加
            dbManager.addToDB(title, content, time);
        } else {
            //更新
            dbManager.updateNote(noteID, title, content, time);
        }
        Intent i = new Intent(EditNoteActivity.this, MainActivity.class);
        startActivity(i);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.finish();
    }

    //得到时间
    private String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm E");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
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
