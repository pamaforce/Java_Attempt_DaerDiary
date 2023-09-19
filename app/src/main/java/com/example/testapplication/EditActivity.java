package com.example.testapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.example.testapplication.DBServer.db;

public class EditActivity extends AppCompatActivity {
    //用于指示是否处于编辑状态
    private boolean isEdit = true;
    //用于指示是否涉及到增加数据
    private boolean needAdd = true;
    /*
     *  以下用来存储获取的布局
     */
    private EditText edit_title;
    private EditText edit_content;
    private TextView edit_time;
    private View sure_btn;
    private Intent intent;
    //由编辑状态切换到预览状态
    private void toPreviewMode(String title,String content,String time){
        isEdit = false;
        needAdd = false;
        sure_btn.setBackground(getDrawable(R.drawable.bottom_btn_5));
        edit_title.setText(title);
        edit_content.setText(content);
        edit_time.setText(time);
        edit_title.setFocusable(false);
        edit_title.setFocusableInTouchMode(false);
        edit_content.setFocusable(false);
        edit_content.setFocusableInTouchMode(false);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        /*
         *  获取当前日期
         */
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Date curDate = new Date(System.currentTimeMillis());
        String createDate = formatter.format(curDate);
        /*
         *  获取布局
         */
        View back_btn = findViewById(R.id.back_btn);
        RelativeLayout edit_main = findViewById(R.id.edit_main);
        RelativeLayout texts = findViewById(R.id.texts);
        edit_title = findViewById(R.id.edit_title);
        edit_content = findViewById(R.id.edit_content);
        edit_time = findViewById(R.id.edit_time);
        sure_btn = findViewById(R.id.sureBtn);
        //显示当前时间
        edit_time.setText(createDate);
        //获取MainActivity传来的数据
        intent = getIntent();
        //如果传来的是空数据
        if(!intent.getStringExtra("title").isEmpty()) {
            //切换到预览状态
            toPreviewMode(intent.getStringExtra("title"),intent.getStringExtra("content"),intent.getStringExtra("time"));
        }
        //监听返回按钮被点击
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回主页
                EditActivity.this.finish();
            }
        });
        //使编辑状态下主要区域被点击时聚焦到内容框中
        texts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEdit){
                    edit_content.requestFocus();
                    InputMethodManager manager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.showSoftInput(edit_content,0);
                }
            }
        });
        //使点击其他地方时清除焦点，收起软键盘
        edit_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_title.clearFocus();
                edit_content.clearFocus();
                InputMethodManager manager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                if (manager != null)
                    manager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        //监听右下角按钮被点击
        sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是否为编辑状态
                if(isEdit){
                    //判断是否标题为空
                    if (TextUtils.isEmpty(edit_title.getText().toString().trim())) {
                        Toast.makeText(EditActivity.this, "请输入标题", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //判断是否内容为空
                    if (TextUtils.isEmpty(edit_content.getText().toString().trim())) {
                        Toast.makeText(EditActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //判断是否需要添加数据
                    if(needAdd){
                        /*
                         *  向数据库中添加数据并返回主页
                         */
                        ContentValues values = new ContentValues();
                        values.put("title", edit_title.getText().toString());
                        values.put("content", edit_content.getText().toString());
                        values.put("time", edit_time.getText().toString());
                        db.insert("Note", null, values);
                        Toast.makeText(EditActivity.this, "添加成功OvO", Toast.LENGTH_SHORT).show();
                        EditActivity.this.finish();
                    }else{
                        /*
                         *  更新数据库中的数据并切换到预览状态
                         */
                        ContentValues values = new ContentValues();
                        values.put("title", edit_title.getText().toString());
                        values.put("content", edit_content.getText().toString());
                        values.put("time", edit_time.getText().toString());
                        db.update("Note", values, "id=?", new String[]{intent.getStringExtra("id")});
                        Toast.makeText(EditActivity.this, "编辑成功OvO", Toast.LENGTH_SHORT).show();
                        toPreviewMode(edit_title.getText().toString(),edit_content.getText().toString(),edit_time.getText().toString());
                    }
                }else {
                    /*
                     *  切换到编辑状态并指定焦点，显示软键盘
                     */
                    isEdit=true;
                    edit_title.setFocusableInTouchMode(true);
                    edit_title.setFocusable(true);
                    edit_content.setFocusableInTouchMode(true);
                    edit_content.setFocusable(true);
                    edit_content.requestFocus();
                    edit_content.setSelection(edit_content.getText().length());
                    InputMethodManager manager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.showSoftInput(edit_content,0);
                    sure_btn.setBackground(getDrawable(R.drawable.bottom_btn_4));
                }
            }
        });
    }
}

