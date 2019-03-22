package com.oude.dndclub.ui.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.oude.dndclub.R;
import com.oude.dndclub.adapter.CommonListAdapter;
import com.oude.dndclub.adapter.DBListAdapter;
import com.oude.dndclub.bean.ItemsList;
import com.oude.dndclub.utils.DBManager;
import com.oude.dndclub.utils.ShopDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private String shopType, shopTitle, shopSource;
    //查询和插入按钮
    private Button bn_query, bn_insert;
    //下拉表按钮
    private Spinner sp_first, sp_second;
    private ArrayAdapter<String> adapter_first, adapter_second;
    String[] change;
    //下拉刷新
    private SwipeRefreshLayout swipeRefresh;
    //数据库相关
    private DBListAdapter adapter;
    private ShopDatabaseHelper shopdb;
    public static final String DB_NAME = "shop.db";
    public String Table_NAME = "";
    //用item表的name作为筛选条件，需要保证item的name的唯一性
    //3r的weapon表
    private String weapon_3r_name, weapon_3r_weapon_type, weapon_3r_atk_type, weapon_3r_usage_type, weapon_3r_dmg_type, weapon_3r_dmg_s, weapon_3r_dmg_m, weapon_3r_critical, weapon_3r_explain, weapon_3r_source;
    private Float weapon_3r_cost, weapon_3r_weight;
    private Integer weapon_3r_range_increment;
    //3r的armor表
    private String armor_3r_name, armor_3r_armor, armor_3r_explain, armor_3r_source;
    private Float armor_3r_cost, armor_3r_weight;
    private Integer armor_3r_armor_shield_bonus, armor_3r_maximum_dex_bonus, armor_3r_armor_check_penalty, armor_3r_arcane_spell_failure_chance, armor_3r_speed_30ft, armor_3r_speed_20ft;
    //3r的item表
    private String item_3r_name, item_3r_type, item_3r_subtype, item_3r_explain, item_3r_source;
    private Float item_3r_cost, item_3r_weight;
    //5e的weapon表
    private String weapon_5e_name, weapon_5e_weapon_type, weapon_5e_atk_type, weapon_5e_dmg, weapon_5e_dmg_type, weapon_5e_properties, weapon_5e_explain, weapon_5e_source;
    private Float weapon_5e_cost, weapon_5e_weight;
    //5e的armor表
    private String armor_5e_name, armor_5e_armor, armor_5e_ac, armor_5e_stealth, armor_5e_explain, armor_5e_source;
    private Float armor_5e_cost, armor_5e_weight;
    private Integer armor_5e_strength;
    //5e的item表
    private String item_5e_name, item_5e_type, item_5e_subtype, item_5e_explain, item_5e_source;
    private Float item_5e_cost, item_5e_weight;
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

        //设置布局
        setContentView(R.layout.activity_shop);


        //创建数据库，并指定数据库文件名称和版本，完成初始化
        //数据库文件会自动在/data/data/<package name>/databases/目录下创建
        //如果item.db数据库已创建，不会重复调用MyDatabaseHelper的onCreate()方法创建
        shopdb = new ShopDatabaseHelper(this, DB_NAME, null, 1);
        //以读写操作方式打开数据库
        shopdb.getWritableDatabase();

        //转换版本
        switch (shopSource) {
            case "1":
                shopSource = "3R";
                switch (shopType) {
                    case "weapon":
                        Table_NAME = "weapon_3r";
                        break;
                    case "armor":
                        Table_NAME = "armor_3r";
                        break;
                    case "item":
                        Table_NAME = "item_3r";
                        break;
                    case "magic":
                        Table_NAME = "magic_3r";
                        break;
                    case "travel":
                        Table_NAME = "travel_3r";
                        break;
                }
                //初始化数据库数据为3R
                initItems(shopSource);
                break;
            case "2":
                shopSource = "5E";
                switch (shopType) {
                    case "weapon":
                        Table_NAME = "weapon_5e";
                        break;
                    case "armor":
                        Table_NAME = "armor_5e";
                        break;
                    case "item":
                        Table_NAME = "item_5e";
                        break;
                    case "magic":
                        Table_NAME = "magic_5e";
                        break;
                    case "travel":
                        Table_NAME = "travel_5e";
                        break;
                }
                //初始化数据库数据为5E
                initItems(shopSource);
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
        swipeRefresh = findViewById(R.id.shop_swipe_refresh);
        //下拉刷新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        //Recycle实现
        RecyclerView recycleView = (RecyclerView) findViewById(R.id.shop_RecyclerView1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);
        adapter = new DBListAdapter(ShopActivity.this, list);
        adapter.setOnItemClickListener(new mItemClickListener());
        recycleView.setAdapter(adapter);
        Toast.makeText(ShopActivity.this, shopType + "：" + shopTitle + "：" + shopSource, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭数据库，一个Activity中可以只使用一个数据库实例，节省性能
        shopdb.close();

    }

    //实现toolbar按钮功能
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shop_toolbar, menu);
        return true;
    }

    //菜单文件功能实现
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.imports:
                Toast.makeText(this, "导入功能待施工", Toast.LENGTH_SHORT).show();
                break;
            case R.id.exports:
                Toast.makeText(this, "导出功能待施工", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    //获取所有items资源并显示，初始化
    private void initItems(String sourceVersion) {
        list.clear();
        //以读写操作方式打开数据库
        SQLiteDatabase sqlLite = shopdb.getReadableDatabase();
        //取出数据
        Cursor cursor = sqlLite.query(Table_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        //取出所有的item项目时，如果数据库为空，则报错Indpex 0 requested, with a size of 0
        if (cursor.moveToPosition(0) != true) {
            Toast.makeText(this, ShopActivity.this.getResources().getText(R.string.hint_query_null), Toast.LENGTH_SHORT).show();
        } else {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                ItemsList item = new ItemsList(name);
                list.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    //Recycle的的点击和长按事件实现
    class mItemClickListener implements DBListAdapter.OnItemClickListener {

        @Override
        public void onLongClick(int position, View v) {

			ItemsList itmsList = list.get(position);
			switch (position)
			{
				default:
					DeleteItem(itmsList.getName(), position , v);
					break;
			}

        }

        @Override
        public void onClick(int position, View v) {
		    /*
			ItemsList itmsList = list.get(position);
			//在初始化详情之前获取当前列的name
			weapon_name = itmsList.getName();
			switch (position)
			{
				default:
				    Detail();
					break;
			}
			*/
        }

    }

    //下拉刷新功能
    private void refreshItems() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //因为本地刷新速度太快看不到效果，延迟下
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        initItems(shopSource);
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

        }).start();
    }

    //item长按删除功能
    public void DeleteItem(final String deleteName, final int pos, final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
        builder.setTitle(ShopActivity.this.getResources().getText(R.string.delete));
        builder.setIcon(R.drawable.timg);
        builder.setMessage(ShopActivity.this.getResources().getText(R.string.delete_confirm) + " " + deleteName + "？");
        builder.setPositiveButton(ShopActivity.this.getResources().getText(R.string.confirm), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface p1, int p2) {
                final SQLiteDatabase db = shopdb.getReadableDatabase();
                //现将要删除的内容获取,以取消
                Cursor cursor = db.query(Table_NAME, null, "name=?", new String[]{deleteName}, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        //获取将要删除的值，暂存，为撤销准备
                        switch (Table_NAME) {
                            case "weapon_3r":
                                weapon_3r_weapon_type = cursor.getString(cursor.getColumnIndex("weapon_type"));
                                weapon_3r_atk_type = cursor.getString(cursor.getColumnIndex("atk_type"));
                                weapon_3r_usage_type = cursor.getString(cursor.getColumnIndex("usage_type"));
                                weapon_3r_dmg_type = cursor.getString(cursor.getColumnIndex("dmg_type"));
                                weapon_3r_dmg_s = cursor.getString(cursor.getColumnIndex("dmg_s"));
                                weapon_3r_dmg_m = cursor.getString(cursor.getColumnIndex("dmg_m"));
                                weapon_3r_critical = cursor.getString(cursor.getColumnIndex("critical"));
                                weapon_3r_explain = cursor.getString(cursor.getColumnIndex("explain"));
                                weapon_3r_source = cursor.getString(cursor.getColumnIndex("source"));
                                weapon_3r_cost = cursor.getFloat(cursor.getColumnIndex("cost"));
                                weapon_3r_weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                                weapon_3r_range_increment = cursor.getInt(cursor.getColumnIndex("range_increment"));
                                break;
                            case "armor_3r":
                                armor_3r_armor = cursor.getString(cursor.getColumnIndex("armor"));
                                armor_3r_explain = cursor.getString(cursor.getColumnIndex("explain"));
                                armor_3r_source = cursor.getString(cursor.getColumnIndex("source"));
                                armor_3r_cost = cursor.getFloat(cursor.getColumnIndex("cost"));
                                armor_3r_weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                                armor_3r_armor_shield_bonus = cursor.getInt(cursor.getColumnIndex("armor_shield_bonus"));
                                armor_3r_maximum_dex_bonus = cursor.getInt(cursor.getColumnIndex("maximum_dex_bonus"));
                                armor_3r_armor_check_penalty = cursor.getInt(cursor.getColumnIndex("armor_check_penalty"));
                                armor_3r_arcane_spell_failure_chance = cursor.getInt(cursor.getColumnIndex("arcane_spell_failure_chance"));
                                armor_3r_speed_30ft = cursor.getInt(cursor.getColumnIndex("speed_30ft"));
                                armor_3r_speed_20ft = cursor.getInt(cursor.getColumnIndex("speed_20ft"));
                                break;
                            case "item_3r":
                                item_3r_type = cursor.getString(cursor.getColumnIndex("type"));
                                item_3r_subtype = cursor.getString(cursor.getColumnIndex("subtype"));
                                item_3r_explain = cursor.getString(cursor.getColumnIndex("explain"));
                                item_3r_source = cursor.getString(cursor.getColumnIndex("source"));
                                item_3r_cost = cursor.getFloat(cursor.getColumnIndex("cost"));
                                item_3r_weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                                break;
                            case "magic_3r":
                                break;
                            case "weapon_5e":
                                weapon_5e_weapon_type = cursor.getString(cursor.getColumnIndex("weapon_type"));
                                weapon_5e_atk_type = cursor.getString(cursor.getColumnIndex("atk_type"));
                                weapon_5e_dmg = cursor.getString(cursor.getColumnIndex("dmg"));
                                weapon_5e_dmg_type = cursor.getString(cursor.getColumnIndex("dmg_type"));
                                weapon_5e_properties = cursor.getString(cursor.getColumnIndex("properties"));
                                weapon_5e_explain = cursor.getString(cursor.getColumnIndex("explain"));
                                weapon_5e_source = cursor.getString(cursor.getColumnIndex("source"));
                                weapon_5e_cost = cursor.getFloat(cursor.getColumnIndex("cost"));
                                weapon_5e_weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                                break;
                            case "armor_5e":
                                armor_5e_armor = cursor.getString(cursor.getColumnIndex("armor"));
                                armor_5e_ac = cursor.getString(cursor.getColumnIndex("ac"));
                                armor_5e_stealth = cursor.getString(cursor.getColumnIndex("stealth"));
                                armor_5e_explain = cursor.getString(cursor.getColumnIndex("explain"));
                                armor_5e_source = cursor.getString(cursor.getColumnIndex("source"));
                                armor_5e_cost = cursor.getFloat(cursor.getColumnIndex("cost"));
                                armor_5e_weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                                armor_5e_strength = cursor.getInt(cursor.getColumnIndex("strength"));
                                break;
                            case "item_5e":
                                item_5e_type = cursor.getString(cursor.getColumnIndex("type"));
                                item_5e_subtype = cursor.getString(cursor.getColumnIndex("subtype"));
                                item_5e_explain = cursor.getString(cursor.getColumnIndex("explain"));
                                item_5e_source = cursor.getString(cursor.getColumnIndex("source"));
                                item_5e_cost = cursor.getFloat(cursor.getColumnIndex("cost"));
                                item_5e_weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                                break;
                            case "magic_5e":
                                break;
                        }

                    } while (cursor.moveToNext());
                }
                //数据库及Recycle中都需要进行删除
                db.delete(Table_NAME, "name=?", new String[]{deleteName});
                adapter.removeItem(pos);

                Snackbar.make(view, ShopActivity.this.getResources().getText(R.string.hint_delete_success), Snackbar.LENGTH_SHORT).setAction(ShopActivity.this.getResources().getText(R.string.hint_delete_undo), new View.OnClickListener() {

                    @Override
                    public void onClick(View p1) {
                        //重新插入数据
                        //更新数据库
                        ContentValues undoValue = new ContentValues();
                        switch (Table_NAME) {
                            case "weapon_3r":
                                undoValue.put("name", deleteName);
                                undoValue.put("weapon_type", weapon_3r_weapon_type);
                                undoValue.put("atk_type", weapon_3r_atk_type);
                                undoValue.put("usage_type", weapon_3r_usage_type);
                                undoValue.put("dmg_type", weapon_3r_dmg_type);
                                undoValue.put("dmg_s", weapon_3r_dmg_s);
                                undoValue.put("dmg_m", weapon_3r_dmg_m);
                                undoValue.put("critical", weapon_3r_critical);
                                undoValue.put("explain", weapon_3r_explain);
                                undoValue.put("source", weapon_3r_source);
                                undoValue.put("cost", weapon_3r_cost);
                                undoValue.put("weight", weapon_3r_weight);
                                undoValue.put("range_increment", weapon_3r_range_increment);
                                break;
                            case "armor_3r":
                                undoValue.put("name", deleteName);
                                undoValue.put("armor", armor_3r_armor);
                                undoValue.put("explain", armor_3r_explain);
                                undoValue.put("source", armor_3r_source);
                                undoValue.put("cost", armor_3r_cost);
                                undoValue.put("weight", armor_3r_weight);
                                undoValue.put("armor_shield_bonus", armor_3r_armor_shield_bonus);
                                undoValue.put("maximum_dex_bonus", armor_3r_maximum_dex_bonus);
                                undoValue.put("armor_check_penalty", armor_3r_armor_check_penalty);
                                undoValue.put("arcane_spell_failure_chance", armor_3r_arcane_spell_failure_chance);
                                undoValue.put("speed_30ft", armor_3r_speed_30ft);
                                undoValue.put("speed_20ft", armor_3r_speed_20ft);
                                break;
                            case "item_3r":
                                undoValue.put("name", deleteName);
                                undoValue.put("type", item_3r_type);
                                undoValue.put("subtype", item_3r_subtype);
                                undoValue.put("explain", item_3r_explain);
                                undoValue.put("source", item_3r_source);
                                undoValue.put("cost", item_3r_cost);
                                undoValue.put("weight", item_3r_weight);
                                break;
                            case "magic_3r":
                                break;
                            case "weapon_5e":
                                undoValue.put("name", deleteName);
                                undoValue.put("weapon_type", weapon_5e_weapon_type);
                                undoValue.put("atk_type", weapon_5e_atk_type);
                                undoValue.put("dmg", weapon_5e_dmg);
                                undoValue.put("dmg_type", weapon_5e_dmg_type);
                                undoValue.put("properties", weapon_5e_properties);
                                undoValue.put("explain", weapon_5e_explain);
                                undoValue.put("source", weapon_5e_source);
                                undoValue.put("cost", weapon_5e_cost);
                                undoValue.put("weight", weapon_5e_weight);
                                break;
                            case "armor_5e":
                                undoValue.put("name", deleteName);
                                undoValue.put("armor", armor_5e_armor);
                                undoValue.put("ac", armor_5e_ac);
                                undoValue.put("stealth", armor_5e_stealth);
                                undoValue.put("explain", armor_5e_explain);
                                undoValue.put("source", armor_5e_source);
                                undoValue.put("cost", armor_5e_cost);
                                undoValue.put("weight", armor_5e_weight);
                                undoValue.put("strength", armor_5e_strength);
                                break;
                            case "item_5e":
                                undoValue.put("name", deleteName);
                                undoValue.put("type", item_5e_type);
                                undoValue.put("subtype", item_5e_subtype);
                                undoValue.put("explain", item_5e_explain);
                                undoValue.put("source", item_5e_source);
                                undoValue.put("cost", item_5e_cost);
                                undoValue.put("weight", item_5e_weight);
                                break;
                            case "magic_5e":
                                break;
                        }

                        db.insert(Table_NAME, null, undoValue);
                        undoValue.clear();
                        Toast.makeText(ShopActivity.this, ShopActivity.this.getResources().getText(R.string.hint_delete_cancel), Toast.LENGTH_SHORT).show();
                        refreshItems();
                    }
                }).show();
                //关闭
                cursor.close();
            }
        });

        builder.setNegativeButton(ShopActivity.this.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface p1, int p2) {
            }
        });
        builder.show();
    }

}
