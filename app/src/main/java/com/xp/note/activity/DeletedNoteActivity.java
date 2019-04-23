package com.xp.note.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xp.note.R;
import com.xp.note.adapter.DeletedNoteAdapter;
import com.xp.note.model.Note_Deleted;
import com.xp.note.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class DeletedNoteActivity extends AppCompatActivity {

    private List<Note_Deleted> deletedNotelist = new ArrayList<>();
    private DeletedNoteAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyListTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletednote);

        getDeleteNoteFomeBmob();

        initViews();
    }

    private void initViews() {


        Toolbar toolbar = findViewById(R.id.deleted_toolbar);
        setSupportActionBar(toolbar);

        emptyListTextView = findViewById(R.id.empty);

        recyclerView = findViewById(R.id.deleted_list);
       //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DeletedNoteAdapter(deletedNotelist);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new DeletedNoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });


        setStatusBarColor();

        updateView();


    }

    private void getDeleteNoteFomeBmob() {

        SharedPreferencesUtil.init(this);
        BmobQuery<Note_Deleted> query = new BmobQuery<>();
        query.addWhereEqualTo("user",SharedPreferencesUtil.getUsername());
        query.findObjects(new FindListener<Note_Deleted>() {
            @Override
            public void done(List<Note_Deleted> list, BmobException e) {
                if (e == null){
                    deletedNotelist.addAll(list);
                    Log.d("TAG deleted",list.size()+"");
                    Log.d("TAG deleted notelist",deletedNotelist.size()+"");
                }else {
                    Toast.makeText(getApplicationContext(),"您暂无删除的笔记"+e,Toast.LENGTH_SHORT).show();
                }
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
        // 创建状态栏的管理实例
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // 激活状态栏设置
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#00574B"));
    }


    //空数据更新
    private void updateView() {
        if (deletedNotelist.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }


}
