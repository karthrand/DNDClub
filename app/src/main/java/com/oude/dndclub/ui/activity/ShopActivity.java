package com.oude.dndclub.ui.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.oude.dndclub.R;
import com.oude.dndclub.adapter.DBListAdapter;
import com.oude.dndclub.bean.ItemsList;
import com.oude.dndclub.utils.ShopDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private String shopType, shopTitle, shopSource;
    //查询和插入按钮
    private Button bn_query, bn_insert;
    //下拉表按钮
    private Spinner sp_first, sp_second;
    private ArrayAdapter<String> adapter_first,adapter_second;
    String[] change;
    //下拉刷新
    private SwipeRefreshLayout swipeRefresh;
    //数据库相关
    private DBListAdapter adapter;
    private ShopDatabaseHelper shopdb;
    public static final String DB_NAME = "shop.db";
    public static final String Table_NAME = "";
    //用item表的name作为筛选条件，需要保证item的name的唯一性
    //3r的weapon表
    private String weapon_3r_name,weapon_3r_weapon_type,weapon_3r_atk_type,weapon_3r_usage_type,weapon_3r_dmg_type,weapon_3r_dmg_s,weapon_3r_dmg_m,weapon_3r_critical,weapon_3r_explain,weapon_3r_source;
    private Float weapon_3r_cost,weapon_3r_weight;
    private Integer weapon_3r_range_increment;
    //3r的armor表
    private String armor_3r_name,armor_3r_armor,armor_3r_explain,armor_3r_source;
    private Float  armor_3r_cost,armor_3r_weight;
    private Integer armor_3r_armor_shield_bonus,armor_3r_maximum_dex_bonus,armor_3r_armor_check_penalty,armor_3r_arcane_spell_failure_chance,armor_3r_speed_30ft,armor_3r_speed_20ft;
    //3r的item表
    private String item_3r_name,item_3r_type,item_3r_subtype,item_3r_explain,item_3r_source;
    private Float  item_3r_cost,item_3r_weight;
    //5e的armor表
    private String armor_5e_name,armor_5e_armor,armor_5e_ac,armor_5e_stealth,armor_5e_explain,armor_5e_source;
    private Float  armor_5e_cost,armor_5e_weight;
    private Integer armor_5e_strength;
    //5e的weapon表
    private String weapon_5e_name,weapon_5e_weapon_type,weapon_5e_atk_type,weapon_5e_dmg,weapon_5e_dmg_type,weapon_5e_properties,weapon_5e_explain,weapon_5e_source;
    private Float  weapon_5e_cost,weapon_5e_weight;
    //5e的item表
    private String item_5e_name,item_5e_type,item_5e_subtype,item_5e_explain,item_5e_source;
    private Float  item_5e_cost,item_5e_weight;
    //存储取出来的item名字

    private List<ItemsList> list = new ArrayList<>();

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
        shopSource = bundle.getString("shopSource");
        Log.i("ShopActivity", "source:" + shopSource);
        //转换版本
        switch (shopSource) {
            case "1":
                shopSource = "3R";
                setContentView(R.layout.activity_shop_3r);
                break;
            case "2":
                shopSource = "5E";
                setContentView(R.layout.activity_shop_5e);
                break;
        }

        //标题栏
        Toolbar toolbar = findViewById(R.id.shop_toolbar);
        toolbar.setTitle(shopTitle);
        setSupportActionBar(toolbar);

        //控件绑定
        bn_query = findViewById(R.id.shop_query);
        bn_insert = findViewById(R.id.shop_insert);
        sp_first = findViewById(R.id.shop_Spinner1);
        sp_second = findViewById(R.id.shop_Spinner2);

        Toast.makeText(ShopActivity.this, shopType + "：" + shopTitle + "：" + shopSource, Toast.LENGTH_SHORT).show();
    }


}
