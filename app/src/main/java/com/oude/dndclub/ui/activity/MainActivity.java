package com.oude.dndclub.ui.activity;

import com.oude.dndclub.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.*;

import com.ashokvarma.bottomnavigation.*;
import com.oude.dndclub.bean.updateInfo;
import com.oude.dndclub.ui.fragment.*;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.oude.dndclub.utils.T;
import com.oude.dndclub.utils.UpdateAppUtils;
import com.oude.dndclub.message.EventMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.*;
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    //公共布局
    private NavigationView navView;
    private DrawerLayout mDrawerLayout;
    private BottomNavigationBar bottomNavigationBar;
    //Fragment
    private int index;
    private int currentTabIndex = 0;
    private Fragment[] mFragments;
    private BookFragment mBookFragment;
    private ShopFragment mShopFragment;
    private ToolFragment mToolFragment;
    //更新
    boolean IsDownLoad = false;
    private String version_name, version_info, download_url;
    private Integer version_code;
    private updateInfo response = new updateInfo();
    //下载更新链接
    public static final String UPDATE_URL = "http://oudezhinu.site/download/dndclub.json";
    public static final int TimeOut = 504;
    public static final int InternalServerError = 500;
    public static final int BadGatWay = 502;
    public static final int HaveUpdate = 1;
    public static final int Latest = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initBottomNavigationBar();
        initFragment();
    }

    //初始化布局
    private void initView() {
        //加载布局文件
        setContentView(R.layout.activity_main);
        //设置标题栏
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        //设置侧滑栏
        navView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.mainDrawerLayout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.nav_settings);
        navView.setItemIconTintList(null);
        navView.setNavigationItemSelectedListener(new NavigationViewListener());
        //设置底部导航栏
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);

        //设置Fragment初始化
        mShopFragment = new ShopFragment();
        mBookFragment = new BookFragment();
        mToolFragment = new ToolFragment();
        mFragments = new Fragment[]{mShopFragment, mBookFragment, mToolFragment};

        //注册更新事件
        EventBus.getDefault().register(this);

    }

    //toolbar加载菜单布局文件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    //侧滑栏点击功能监听器
    class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(MenuItem p1) {
            int id = p1.getItemId();
            switch (id) {
                case R.id.nav_settings:
                    Intent intent_settings = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent_settings);
                    break;
                case R.id.nav_help:

                    break;
                case R.id.nav_information:
                    /*
                     Intent intent3 =new Intent(MainActivity.this, AboutActivity.class);
                     startActivity(intent3);
                     */
                    break;
                case R.id.nav_update:
                    if (IsDownLoad) {
                        T.shortToast(MainActivity.this, getResources().getString(R.string.update_downloading));
                    }
                    //升级app
                    getData();
                    break;
                case R.id.nav_feedback:
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.help:
                prompt();
                break;
            default:
        }
        return true;
    }

    //初始化底部导航栏
    private void initBottomNavigationBar() {
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
                .addItem(new BottomNavigationItem(R.drawable.ic_book, R.string.tab_book))
                .addItem(new BottomNavigationItem(R.drawable.ic_tool, R.string.tab_tool))
                //设置默认选择的按钮
                .setFirstSelectedPosition(0)
                .initialise();
    }

    //初始化Fragment
    private void initFragment() {
        //设置默认选择的Fragment
        FragmentTransaction init = getSupportFragmentManager().beginTransaction();
        init.add(R.id.fragment_main, mFragments[0]);
        init.show(mFragments[0]).commit();
        //设置lab点击事件
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 0:
                        index = 0;
                        break;
                    case 1:
                        index = 1;
                        break;
                    case 2:
                        index = 2;
                        break;
                }
                if (currentTabIndex != index) {
                    FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                    trx.hide(mFragments[currentTabIndex]);
                    if (!mFragments[index].isAdded()) {
                        trx.add(R.id.fragment_main, mFragments[index]);
                    }
                    trx.show(mFragments[index]).commit();
                }
                currentTabIndex = index;
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
    }

    //右上角提示功能
    private void prompt() {
        switch (index) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }
    }

    //------更新相关------
    //根据返回码判断反馈是否发送成功
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HaveUpdate:
                    version_name = response.getVersion();
                    version_code = response.getCode();
                    version_info = response.getInfo();
                    download_url = response.getUrl();
                    int Forced = 0;// 1：强制更新   0：不是
                    UpdateAppUtils.UpdateApp(MainActivity.this, null, version_code, version_name, version_info,
                            download_url, Forced == 1 ? true : false, true);
                    break;
                case Latest:
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.update_updated), Toast.LENGTH_SHORT).show();
                    break;
                case TimeOut:
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.Error_TimeOut), Toast.LENGTH_SHORT).show();
                    break;
                case InternalServerError:
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.Error_Intenet), Toast.LENGTH_SHORT).show();
                    break;
                case BadGatWay:
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.Error_BadGatway), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }

    });

    //获取当前应用版本名称
    public String getVersionName(Context ctx) {
        String appVersioName = "";
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            appVersioName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersioName;
    }

    //获取当前应用版本号
    public Integer getVersionCode(Context ctx) {

        Integer appVersioCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            appVersioCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersioCode;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExitappEvent(EventMessage messageEvent) {
        if (messageEvent.getMessageType() == EventMessage.Exitapp) {
            T.shortToast(MainActivity.this, getResources().getString(R.string.update_cancel));
        } else if (messageEvent.getMessageType() == EventMessage.CheckApp) {
            IsDownLoad = messageEvent.isDownLoading();
        }
    }

    private void getData() {
        OkHttpClient client = new OkHttpClient();
        //设置超时时间
        client.newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();

        //使用request，并指定GET方法
        Request request = new Request.Builder().get().url(UPDATE_URL).build();

        Call call = client.newCall(request);
        //异步调用并设置回调函数

        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call p1, IOException p2) {
                Log.d("AboutActivity", p2.toString());
                //判断超时异常
                if (p2 instanceof SocketTimeoutException) {
                    Message message = new Message();
                    message.what = TimeOut;
                    handler.sendMessage(message);

                }
                //判断链接异常
                if (p2 instanceof ConnectException) {
                    Message message1 = new Message();
                    message1.what = InternalServerError;
                    handler.sendMessage(message1);

                }
                if (p2 instanceof UnknownHostException) {
                    Message message2 = new Message();
                    message2.what = BadGatWay;
                    handler.sendMessage(message2);
                }

            }

            @Override
            public void onResponse(Call p1, Response p2) throws IOException {
                final String body = p2.body().string();
                try {
                    //获取服务器json文件并解析
                    JSONObject object = new JSONObject(body);
                    response.setVersion(object.getString("versionName"));
                    response.setCode(object.getInt("versionCode"));
                    response.setInfo(object.getString("info"));
                    response.setUrl(object.getString("url"));

                    if (getVersionCode(MainActivity.this) < response.getCode()) {
                        Message message = new Message();
                        message.what = HaveUpdate;
                        handler.sendMessage(message);
                    } else {
                        Message message1 = new Message();
                        message1.what = Latest;
                        handler.sendMessage(message1);
                    }

                } catch (JSONException e) {
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果是从设置界面返回,就继续判断权限
        if (requestCode == UpdateAppUtils.REQUEST_PERMISSION_SDCARD_SETTING) {
            getData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UpdateAppUtils.onActRequestPermissionsResult(requestCode, permissions, grantResults, MainActivity.this, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(this);
    }

}
