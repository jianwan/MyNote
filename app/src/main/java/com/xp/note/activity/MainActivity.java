package com.xp.note.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton addBtn;
    private DBManager dm;
    private List<Note> noteDataList = new ArrayList<>();
    private ListAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyListTextView;
    long waitTime = 2000;
    long touchTime = 0;
    int version = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        //第一：默认初始化
        Bmob.initialize(this, "bdc479c9f78d163df6442083ce8578e8");

    }

    //初始化
    private void init() {
        dm = new DBManager(this);
        dm.readFromDB(noteDataList);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        recyclerView = findViewById(R.id.list);
        addBtn = findViewById(R.id.add);
        emptyListTextView = findViewById(R.id.empty);
        addBtn.setOnClickListener(this);
        //反向展现数据 新建的note在最上面
        final List<Note> noteDataList2 = new ArrayList<>();
        for (int i=noteDataList.size()-1;i>=0;i--){
            noteDataList2.add(noteDataList.get(i));
        }
        adapter = new ListAdapter(noteDataList2);
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
                                          //TODO bug
                                          if (adapter.getItemCount() == 0){
                                              updateView();
                                          }

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
                BmobQuery<Note> query = new BmobQuery<>();
                query.addWhereGreaterThanOrEqualTo("id",0);
                query.order("-id").findObjects(new FindListener<Note>() {
                    @Override
                    public void done(List<Note> list, BmobException e) {
                        if (e==null){

                            swipeRefreshLayout.setRefreshing(false);

//                            for (int i = 0; i < noteDataList.size(); i++){
//                                dm.deleteNote(noteDataList.get(i).getId());
//                            }

                            dm.deleteAllNote(version++);
                            noteDataList.clear();
                            noteDataList2.clear();

                            for (int j = list.size() - 1; j >= 0; j--){
                                Note position = list.get(j);
                                dm.addToDB(position.getTitle(),position.getContent(),position.getTime(),position.getPriority());
                            }

                            adapter.updataView(list);
                            Log.d("TAG",list.get(0).getTitle());

                            updateView();

                            Toast.makeText(getApplicationContext(),"获取数据成功",Toast.LENGTH_SHORT).show();
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
                        .content("这是一个课程设计")
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
                                for (int id = 0; id < 100; id++)
                                    DBManager.getInstance(MainActivity.this).deleteNote(id);
                                adapter.removeAllItem();
                                updateView();
                            }
                        }).show();

                break;

            case R.id.action_sync:

                //同步逻辑，先删除该用户的所有笔记，再将本地的笔记上传至服务器

                BmobQuery<Note> queryObjectId = new BmobQuery<>();
                queryObjectId.addWhereGreaterThan("id",-1);
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
                                            Toast.makeText(getApplicationContext(),"表删除成功",Toast.LENGTH_SHORT).show();

                                        }else {

                                            Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            //删除成功则添加新数据
                            for (int i = 0; i<noteDataList.size();i++){
                                Note note = new Note();
                                note.setId(noteDataList.get(i).getId());
                                note.setTitle(noteDataList.get(i).getTitle());
                                note.setContent(noteDataList.get(i).getContent());
                                note.setTime(noteDataList.get(i).getTime());
                                note.setPriority(noteDataList.get(i).getPriority());
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
        long currentTime = System.currentTimeMillis();
        if ((currentTime - touchTime) >= waitTime) {
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
            touchTime = currentTime;
        } else {
            finish();
        }
    }
}
