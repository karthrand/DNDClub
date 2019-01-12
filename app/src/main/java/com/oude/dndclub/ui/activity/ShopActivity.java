package com.oude.dndclub.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.oude.dndclub.R;

public class ShopActivity extends AppCompatActivity {

    private String shopType, shopTitle, source;
    //查询和插入按钮
    private Button bn_query,bn_insert;
    //下拉表按钮
    private Spinner sp_first,sp_second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    //初始化布局
    private void initView() {
        //接收设置
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        shopType = bundle.getString("shopType");
        shopTitle = bundle.getString("shopTitle");
        source = bundle.getString("shopSource");

        //转换版本
        switch (source) {
            case "1":
                source = "3R";
                setContentView(R.layout.activity_shop_3r);
                break;
            case "2":
                source = "5E";
                setContentView(R.layout.activity_shop_5e);
                break;
        }

        //标题栏
        Toolbar toolbar = findViewById(R.id.shop_toolbar);
        toolbar.setTitle(shopTitle);
        setSupportActionBar(toolbar);

        //控件绑定
        bn_query = (Button) findViewById(R.id.shop_query);
        bn_insert = (Button) findViewById(R.id.shop_insert);
        sp_first = (Spinner) findViewById(R.id.shop_Spinner1);
        sp_second = (Spinner) findViewById(R.id.shop_Spinner2);

        Toast.makeText(ShopActivity.this, shopType + " " + source, Toast.LENGTH_SHORT).show();
    }
}
