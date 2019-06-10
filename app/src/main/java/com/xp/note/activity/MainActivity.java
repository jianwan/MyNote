package com.xp.note.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xp.note.R;
import com.xp.note.adapter.ListAdapter;
import com.xp.note.db.DBManager;
import com.xp.note.model.Note;
import com.xp.note.model.Note_Deleted;
import com.xp.note.receiver.ClockReceiver;
import com.xp.note.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/*
 *
 *  Toolbar的使用：(1) https://juejin.im/post/5a30de4051882531d828680d
 *                (2) https://blog.csdn.net/MonaLisaTearr/article/details/78415585
 *
 *  AlarmManager(闹钟)的使用：(1) https://blog.csdn.net/coder_pig/article/details/49423531
 *                           (2) https://blog.csdn.net/wei_chong_chong/article/details/51258336
 *                           (3) https://blog.csdn.net/fan7983377/article/details/51993793
 *  github demo 参考：https://github.com/jsbintask22/memo
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton addBtn;
    private DBManager dm;
    private List<Note> noteDataList = new ArrayList<>();
    private ListAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyListTextView;

    private long waitTime = 2000;
    private long touchTime = 0;
    private int version = 2;


    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            } else {
                //TODO do something you need
            }
        }

        init();

        //第一：默认初始化
        Bmob.initialize(this, "bdc479c9f78d163df6442083ce8578e8");


        //定时闹钟实现
        List<Note> clockTimeList = new ArrayList<>();
        dm = new DBManager(this);
        dm.readFromDBByClockTime(clockTimeList);

        AlarmManager[] alarmManager = new AlarmManager[clockTimeList.size()+1];
        List<PendingIntent> intentArray = new ArrayList<>();
        for(int i=0; i < clockTimeList.size(); i++){
            Intent intent = new Intent(MainActivity.this, ClockReceiver.class);
            intent.putExtra ("content", clockTimeList.get(i).getContent());
            pendingIntent = PendingIntent.getBroadcast(this, i , intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager[i].set(AlarmManager.RTC_WAKEUP,clockTimeList.get(i).getClockTime() ,pendingIntent);

            intentArray.add(pendingIntent);

        }


    }


    //初始化
    private void init() {

        dm = new DBManager(this);
        dm.readFromDBById(noteDataList);


        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        recyclerView = findViewById(R.id.list);
        addBtn = findViewById(R.id.add);
        emptyListTextView = findViewById(R.id.empty);
        addBtn.setOnClickListener(this);

        //反向展现数据 新建的note在最上面
        Collections.reverse(noteDataList);
        adapter = new ListAdapter(this,noteDataList);
        recyclerView.setAdapter(adapter);

        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int noteId = adapter.getItem(position).getId();
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("id", noteId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                final Note note = adapter.getItem(position);
                final int id = note.getId();
                new MaterialDialog.Builder(MainActivity.this)
                        .content(R.string.are_you_sure)
                        .positiveText(R.string.delete)
                        .negativeText(R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                                      @Override
                                      public void onPositive(MaterialDialog dialog) {
                                          DBManager.getInstance(MainActivity.this).deleteNote(id);
                                          adapter.removeItem(position);
                                          addNoteToDeleted(note.getTitle(),note.getContent(),note.getPriority(),SharedPreferencesUtil.getUsername());
                                      }

                                      private void addNoteToDeleted(String title, String content, String priority,String user) {
                                          Note_Deleted note_deleted = new Note_Deleted();
                                          note_deleted.setTitle(title);
                                          note_deleted.setContent(content);
                                          note_deleted.setPriority(priority);
                                          note_deleted.setUser(user);
                                          note_deleted.save(new SaveListener<String>() {
                                              @Override
                                              public void done(String s, BmobException e) {
                                                  if(e==null){
                                                     Toast.makeText(getBaseContext(),"删除成功",Toast.LENGTH_SHORT).show();
                                                  }else{
                                                      Toast.makeText(getBaseContext(),"删除失败" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                                          });
                                      }

                                  }
                        ).show();

            }
        });
        setStatusBarColor();
        updateView();


        //同步数据到本地
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferencesUtil.init(getApplicationContext());
                BmobQuery<Note> query = new BmobQuery<>();
                query.addWhereGreaterThanOrEqualTo("id",0);
                query.addWhereEqualTo("user",SharedPreferencesUtil.getUsername());
                query.order("-id").findObjects(new FindListener<Note>() {
                    @Override
                    public void done(List<Note> list, BmobException e) {
                        if (e==null){

                            swipeRefreshLayout.setRefreshing(false);

                            dm.deleteAllNote(version++);
                            Collections.reverse(list);
                            noteDataList = list;

                            for (int i = 0; i < list.size(); i++){
                                Note position = list.get(i);
                                dm.addToDB(position.getTitle(),position.getContent(),position.getTime(),position.getPriority(),position.getClockTime());

                            }

                            List<Note> showlist = new ArrayList();
//                            dm.readFromDBByClockTime(showlist);
                            adapter.updataView(list);

                            updateView();

                            if (list.size() == 0 ){
                                Toast.makeText(getApplicationContext(),"你暂时还没有笔记",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(),"获取数据成功",Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getApplicationContext(),"您暂无笔记"+e,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    //空数据更新
    private void updateView() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
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

    //点击新建新的记录事项
    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, EditNoteActivity.class);
        switch (view.getId()) {
            case R.id.add:
                startActivity(i);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title(R.string.about)
                        .content("这是我的毕业设计")
                        .positiveText("确定")
                        .build();

                dialog.show();
                break;
            case R.id.action_clean:
                new MaterialDialog.Builder(MainActivity.this)
                        .content(R.string.are_you_sure)
                        .positiveText(R.string.clean)
                        .negativeText(R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {

                                dm.deleteAllNote(version++);

//                                noteDataList.clear();
//                                adapter.updataView(noteDataList);

                                recyclerView.setVisibility(View.GONE);
                                emptyListTextView.setVisibility(View.VISIBLE);
                            }
                        }).show();

                break;

            case R.id.action_sync:

                //同步逻辑，先删除该用户的所有笔记，再将本地的笔记上传至服务器
                BmobQuery<Note> queryObjectId = new BmobQuery<>();
                SharedPreferencesUtil.init(getApplicationContext());
                String username = SharedPreferencesUtil.getUsername();
                queryObjectId.addWhereEqualTo("user" , username);
                queryObjectId.findObjects(new FindListener<Note>() {
                    @Override
                    public void done(List<Note> list, BmobException e) {
                        if (e == null){
                            Note note1 = new Note();
                            //依次删除表中数据
                            for (int i = 0; i < list.size();i++){
                                note1.setObjectId(list.get(i).getObjectId());
                                note1.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null){
//                                            Toast.makeText(getApplicationContext(),"表删除成功",Toast.LENGTH_SHORT).show();

                                        }else {

                                            Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            //删除成功则添加新数据
                            for (int i = 0; i<noteDataList.size();i++){
                                Note note = new Note();
                                note.setUser(SharedPreferencesUtil.getUsername());
                                note.setId(noteDataList.get(i).getId());
                                note.setTitle(noteDataList.get(i).getTitle());
                                note.setContent(noteDataList.get(i).getContent());
                                note.setTime(noteDataList.get(i).getTime());
                                note.setPriority(noteDataList.get(i).getPriority());
                                note.setClockTime(noteDataList.get(i).getClockTime());
                                note.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e==null){
                                            Toast.makeText(getApplicationContext(),"同步数据成功",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                        }
                                    }
                                });
                            }

                        }else {

                        }
                    }
                });


                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //按返回键时
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
                touchTime = currentTime;
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            // Handle the camera action
            Toast.makeText(this,"回收站",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,DeletedNoteActivity.class);
            startActivity(intent);

        }else if (id == R.id.login_out){
            //退出登录逻辑
            BmobUser.logOut();

            SharedPreferencesUtil.init(this);
            SharedPreferencesUtil.setUsername(null);
            SharedPreferencesUtil.setPassword(null);
            SharedPreferencesUtil.setIsLogin(false);
            dm.deleteAllNote(version++);

            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
