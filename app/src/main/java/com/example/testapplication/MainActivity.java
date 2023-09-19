package com.example.testapplication;

import static com.example.testapplication.DBServer.db;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //指示是否显示左下角菜单
    private boolean showList = false;
    //指示是否处在显示删除按钮状态
    private boolean isDeleting = false;
    /*
     *  以下用来存储获取的布局
     */
    private LinearLayout linearLayout;
    private LinearLayout search_linear;
    private View menu;
    //用于存放各条日记中的时间布局，为了方便控制其是否显示
    ArrayList<TextView> timeList = new ArrayList<>();
    //用于存放各条日记中的删除按钮布局，为了方便控制其是否显示
    ArrayList<RelativeLayout> deleteBtnList = new ArrayList<>();
    //删除按钮状态下点击其他地方的处理
    private void clickWrapper(){
        /*
         *  隐藏左下角菜单
         */
        showList=false;
        menu.setVisibility(View.INVISIBLE);
        isDeleting=false;
        /*
         *  隐藏所有的删除按钮，显示所有的日期
         */
        for(int i = 0;i < deleteBtnList.size(); i ++){
            deleteBtnList.get(i).setVisibility(View.INVISIBLE);
        }for(int i = 0;i < timeList.size(); i ++){
            timeList.get(i).setVisibility(View.VISIBLE);
        }
    }
    //px转dp，为了java和xml中统一单位
    private int px2dp(int val){
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics()));
    }
    //更新列表数据和创建布局
    private void initData(){
        /*
         *  清空数据和布局
         */
        timeList = new ArrayList<>();
        deleteBtnList = new ArrayList<>();
        linearLayout.removeAllViews();
        final ArrayList<Note> noteList = new ArrayList<>();
        /*
         *  从数据库中读取数据放入noteList中
         */
        Cursor cursor = db.rawQuery("select * from Note", null);
        while(cursor.moveToNext()) {
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
            int id = cursor.getInt(0);
            Note t = new Note(title, content, time, id);
            noteList.add(t);
        }
        cursor.close();
        /*
         *  根据数据创建布局
         */
        for(int i = 0;i < noteList.size(); i ++){
            /*
             *  最外层item
             */
            RelativeLayout item = new RelativeLayout(this);
            RelativeLayout.LayoutParams params_1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params_1.topMargin=px2dp(8);
            params_1.bottomMargin=px2dp(8);
            item.setLayoutParams(params_1);
            item.setPadding(px2dp(15),px2dp(8),px2dp(15),px2dp(8));
            item.setBackground(getDrawable(R.drawable.item_background));
            /*
             *  标题
             */
            TextView title = new TextView(this);
            RelativeLayout.LayoutParams params_2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params_2.addRule(RelativeLayout.CENTER_VERTICAL);
            title.setLayoutParams(params_2);
            title.setPadding(0,0,px2dp(60),0);
            title.setText(noteList.get(i).getTitle());
            title.setTextSize(16);
            title.setTextColor(Color.parseColor("#536773"));
            /*
             *  时间
             */
            TextView time = new TextView(this);
            RelativeLayout.LayoutParams params_3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params_3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params_3.addRule(RelativeLayout.CENTER_VERTICAL);
            time.setLayoutParams(params_3);
            time.setText(noteList.get(i).getTime());
            time.setTextSize(12);
            time.setTextColor(Color.parseColor("#B2536773"));
            item.addView(title);
            item.addView(time);
            /*
             *  删除按钮
             */
            RelativeLayout btn = new RelativeLayout(this);
            RelativeLayout.LayoutParams params_4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params_4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params_4.addRule(RelativeLayout.CENTER_VERTICAL);
            btn.setLayoutParams(params_4);
            btn.setBackground(getDrawable(R.drawable.delete_background));
            btn.setVisibility(View.INVISIBLE);
            /*
             *  删除按钮中的字
             */
            TextView btn_text = new TextView(this);
            RelativeLayout.LayoutParams params_5 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params_5.topMargin=px2dp(3);
            params_5.bottomMargin=px2dp(3);
            params_5.leftMargin=px2dp(14);
            params_5.rightMargin=px2dp(14);
            btn_text.setLayoutParams(params_5);
            btn_text.setText("删除");
            btn.addView(btn_text);
            item.addView(btn);
            /*
             *  为每个删除按钮绑定点击事件
             */
            int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                     *  找到并从布局和数据库中删除该条item
                     */
                    if (view == null) {
                        return;
                    }
                    int iIndex = -1;
                    for (int i = 0; i < deleteBtnList.size(); i++) {
                        if (deleteBtnList.get(i) == view) {
                            iIndex = i;
                            break;
                        }
                    }
                    if (iIndex >= 0) {
                        deleteBtnList.remove(iIndex);
                        linearLayout.removeViewAt(iIndex);
                        db.delete("Note", "id=?", new String[]{noteList.get(finalI).getId()+""});
                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            /*
             *  为每个item绑定点击事件
             */
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //判断是否是遮罩状态
                    if(isDeleting||showList){
                        clickWrapper();
                    }else{
                        /*
                         *  携带item信息进入详情页
                         */
                        Intent intent = new Intent(MainActivity.this, EditActivity.class);
                        intent.putExtra("title", noteList.get(finalI).getTitle());
                        intent.putExtra("content", noteList.get(finalI).getContent());
                        intent.putExtra("time", noteList.get(finalI).getTime());
                        intent.putExtra("id", noteList.get(finalI).getId()+"");
                        startActivity(intent);
                    }
                }
            });
            /*
             *  将布局引用存起来并将item放入已显示的布局中
             */
            timeList.add(time);
            deleteBtnList.add(btn);
            linearLayout.addView(item);
        }
    }
    //更新搜索列表数据和创建布局
    private void initSearchData(String keyword){
        /*
         *  清空数据和布局
         */
        search_linear.removeAllViews();
        final ArrayList<Note> searchList = new ArrayList<>();
        /*
         *  从数据库中模糊查询keyword获取数据放入searchList中
         */
        Cursor cursor = db.rawQuery(
                "select * from Note where title like ? OR content like ? OR time like ? ",
                new String[]{"%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%"});
        while(cursor.moveToNext()) {
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
            int id = cursor.getInt(0);
            Note t = new Note(title, content, time, id);
            searchList.add(t);
        }
        cursor.close();
        /*
         *  根据数据创建布局，同上
         */
        for(int i = 0;i < searchList.size(); i ++){
            RelativeLayout item = new RelativeLayout(this);
            RelativeLayout.LayoutParams params_1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params_1.topMargin=px2dp(8);
            params_1.bottomMargin=px2dp(8);
            item.setLayoutParams(params_1);
            item.setPadding(px2dp(15),px2dp(8),px2dp(15),px2dp(8));
            item.setBackground(getDrawable(R.drawable.item_background));
            TextView title = new TextView(this);
            RelativeLayout.LayoutParams params_2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params_2.addRule(RelativeLayout.CENTER_VERTICAL);
            title.setLayoutParams(params_2);
            title.setPadding(0,0,px2dp(60),0);
            title.setText(searchList.get(i).getTitle());
            title.setTextSize(16);
            title.setTextColor(Color.parseColor("#536773"));
            TextView time = new TextView(this);
            RelativeLayout.LayoutParams params_3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params_3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params_3.addRule(RelativeLayout.CENTER_VERTICAL);
            time.setLayoutParams(params_3);
            time.setText(searchList.get(i).getTime());
            time.setTextSize(12);
            time.setTextColor(Color.parseColor("#B2536773"));
            item.addView(title);
            item.addView(time);
            /*
             *  删除按钮布局其实在搜索中暂时不需要，但是为了样式统一先保留着
             */
            RelativeLayout btn = new RelativeLayout(this);
            RelativeLayout.LayoutParams params_4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params_4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params_4.addRule(RelativeLayout.CENTER_VERTICAL);
            btn.setLayoutParams(params_4);
            btn.setBackground(getDrawable(R.drawable.delete_background));
            btn.setVisibility(View.INVISIBLE);
            TextView btn_text = new TextView(this);
            RelativeLayout.LayoutParams params_5 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params_5.topMargin=px2dp(3);
            params_5.bottomMargin=px2dp(3);
            params_5.leftMargin=px2dp(14);
            params_5.rightMargin=px2dp(14);
            btn_text.setLayoutParams(params_5);
            btn_text.setText("删除");
            btn.addView(btn_text);
            item.addView(btn);
            /*
             *  为每个item绑定点击事件，同上
             */
            int finalI = i;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    intent.putExtra("title", searchList.get(finalI).getTitle());
                    intent.putExtra("content", searchList.get(finalI).getContent());
                    intent.putExtra("time", searchList.get(finalI).getTime());
                    intent.putExtra("id", searchList.get(finalI).getId()+"");
                    startActivity(intent);
                }
            });
            //应用布局
            search_linear.addView(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        /*
         *  获取布局
         */
        View menu_btn = findViewById(R.id.menu_btn);
        menu = findViewById(R.id.menu);
        RelativeLayout main_layout = findViewById(R.id.main);
        ScrollView scrollView = findViewById(R.id.scroll);
        View addBtn = findViewById(R.id.addBtn);
        EditText search = findViewById(R.id.search);
        View search_wrapper = findViewById(R.id.search_wrapper);
        ScrollView search_scroll = findViewById(R.id.search_scroll);
        linearLayout = findViewById(R.id.linear);
        search_linear = findViewById(R.id.search_linear);
        //更新列表数据并创建布局
        initData();
        /*
         *  处理好点击遮罩事件
         */
        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDeleting||showList){
                    clickWrapper();
                }
            }
        });
        scrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDeleting||showList){
                    clickWrapper();
                }
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDeleting||showList){
                    clickWrapper();
                }
            }
        });
        /*
         *  监听搜索框是否被聚集
         */
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //判断是否聚焦
                if(b){
                    //清除显示删除按钮状态
                    clickWrapper();
                    //显示搜索遮罩
                    search_wrapper.setVisibility(View.VISIBLE);
                    //显示搜索列表
                    search_scroll.setVisibility(View.VISIBLE);
                }else{
                    /*
                     *  收起软键盘
                     */
                    InputMethodManager manager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        //监听搜索框文字变化
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //更新搜索列表并创建视图
                initSearchData(search.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        /*
         *  处理好搜索遮罩被点击后的处理
         */
        search_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setText("");
                search.clearFocus();
                search_wrapper.setVisibility(View.INVISIBLE);
                search_scroll.setVisibility(View.INVISIBLE);
            }
        });
        search_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setText("");
                search.clearFocus();
                search_wrapper.setVisibility(View.INVISIBLE);
                search_scroll.setVisibility(View.INVISIBLE);
            }
        });
        search_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setText("");
                search.clearFocus();
                search_wrapper.setVisibility(View.INVISIBLE);
                search_scroll.setVisibility(View.INVISIBLE);
            }
        });
        //监听左下角列表按钮被点击
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 *  清除显示删除按钮状态，显示所有的时间，隐藏删除按钮
                 */
                isDeleting=false;
                for(int i = 0;i < deleteBtnList.size(); i ++){
                    deleteBtnList.get(i).setVisibility(View.INVISIBLE);
                }for(int i = 0;i < timeList.size(); i ++){
                    timeList.get(i).setVisibility(View.VISIBLE);
                }
                //判断当前是否已经显示菜单，从而隐藏或显示
                if(showList){
                    menu.setVisibility(View.INVISIBLE);
                    showList=false;
                }else{
                    menu.setVisibility(View.VISIBLE);
                    showList=true;
                }
            }
        });
        //监听菜单被点击（由于菜单只有一个选项，所以视为管理笔记按钮被点击）
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 *  显示删除按钮，隐藏时间
                 */
                isDeleting=true;
                showList=false;
                for(int i = 0;i < deleteBtnList.size(); i ++){
                    deleteBtnList.get(i).setVisibility(View.VISIBLE);
                }for(int i = 0;i < timeList.size(); i ++){
                    timeList.get(i).setVisibility(View.INVISIBLE);
                }
                //隐藏菜单
                menu.setVisibility(View.INVISIBLE);
            }
        });
        //右下角添加按钮被点击
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是否处于遮罩状态
                if(isDeleting||showList){
                    clickWrapper();
                }else {
                    /*
                     *  携带空item信息进入详情页
                     */
                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    intent.putExtra("title", "");
                    intent.putExtra("content", "");
                    intent.putExtra("time", "");
                    intent.putExtra("id", 0);
                    startActivity(intent);
                }
            }
        });
    }

    //重现本页时刷新列表数据及布局
    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }
}