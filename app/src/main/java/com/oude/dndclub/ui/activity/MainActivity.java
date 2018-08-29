package com.oude.dndclub.ui.activity;

import android.app.*;
import android.os.*;
import com.oude.dndclub.R;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.content.*;
import android.support.v4.view.*;
import com.ashokvarma.bottomnavigation.*;

public class MainActivity extends AppCompatActivity 
{
    private NavigationView navView;
    private DrawerLayout mDrawerLayout;
    private BottomNavigationBar bottomNavigationBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initView();
        initBottomNavigationBar();
    }

    //初始化布局
    private void initView()
    {
        //加载布局文件
        setContentView(R.layout.activity_main);
        //设置标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        //设置侧滑栏
        navView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.nav_settings);
        navView.setItemIconTintList(null);
        navView.setNavigationItemSelectedListener(new NavigationViewListener());
        //设置底部导航栏
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);


    }

    //toolbar加载菜单布局文件
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    //侧滑栏点击功能监听器
    class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener
    {
        @Override
        public boolean onNavigationItemSelected(MenuItem p1)
        {
            int id = p1.getItemId();
            switch (id)
            {
                case R.id.nav_settings:
                    /*
                     Intent intent2 =new Intent(MainActivity.this, SettingActivity.class);
                     startActivity(intent2);
                     */
                    break;
                case R.id.nav_information:
                    /*
                     Intent intent3 =new Intent(MainActivity.this, AboutActivity.class);
                     startActivity(intent3);
                     */
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    //侧滑栏左上角功能设置(
    //点击弹出侧滑栏
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.help:
                //gethelp();                          
                break;
            default:
        }
        return true;
    }

    //初始化底部导航栏
    private void initBottomNavigationBar()
    {
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING)
            .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        //模式跟背景的设置都要在添加tab前面，不然不会有效果。
        bottomNavigationBar
            .setActiveColor(R.color.colorPrimary)
            //默认未选择颜色
            .setInActiveColor(R.color.grey)
            //默认背景色
            .setBarBackgroundColor(R.color.white);

        bottomNavigationBar
            .addItem(new BottomNavigationItem(R.drawable.ic_shop, R.string.tab_shop))
            .addItem(new BottomNavigationItem(R.drawable.ic_book, R.string.tab_library))
            //设置默认选择的按钮
            .setFirstSelectedPosition(0)
            .initialise();
    }

}
