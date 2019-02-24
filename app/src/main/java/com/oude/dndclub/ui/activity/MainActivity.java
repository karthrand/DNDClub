package com.oude.dndclub.ui.activity;

import com.oude.dndclub.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
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
    private String version_name, version_info, download_url,comment,email_encode,name_encode;
    private Integer version_code;
    private updateInfo response = new updateInfo();
    //下载更新链接
    public static final String UPDATE_URL = "http://oudezhinu.site/download/dndclub.json";
    public static final int Success = 200;
    public static final int Fail = -1;
    public static final int Repeat = 409;
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
                     Intent intent3 =new Intent(MainActivity.this, MainActivity.class);
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
                    feedBack();
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

    //Handle公共处理
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
                case Success:
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.comment_success), Toast.LENGTH_SHORT).show();
                    break;
                case Fail:
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.comment_fail), Toast.LENGTH_SHORT).show();
                    break;
                case Repeat:
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.comment_repeat), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }

    });

    //------更新相关------
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
                Log.d("MainActivity", p2.toString());
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

   //------反馈相关------
    private void feedBack(){
        //获取设置中当前用户名和邮箱
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String username = sp.getString("user_name", "匿名");
        String email = sp.getString("user_mail", "null");
        //获取之前存储的内容
        SharedPreferences sp1 = getSharedPreferences("comment",MODE_PRIVATE);
        String getComment = sp1.getString("pre_comment","");
        //加密信息以供反馈使用
        email_encode = java.net.URLEncoder.encode(email);
        name_encode = java.net.URLEncoder.encode(username);
        //显示反馈弹窗
        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        View detailView = LayoutInflater.from(MainActivity.this).inflate(R.layout.alertdialog_feedback, null);
        builder.setTitle(MainActivity.this.getResources().getText(R.string.feedback));
        builder.setView(detailView);
        TextView user_name = detailView.findViewById(R.id.user_name);
        TextView user_mail = detailView.findViewById(R.id.user_email);
        final EditText comments = (EditText) detailView.findViewById(R.id.comments);
        //取消前输入框中的内容
        comments.setText(getComment);
        user_name.setText(MainActivity.this.getResources().getText(R.string.current_user) + username);
        user_mail.setText(MainActivity.this.getResources().getText(R.string.current_email) + email);
        builder.setNegativeButton(MainActivity.this.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface p1, int p2)
            {
                //存储之前的输入内容
                SharedPreferences.Editor editor =  getSharedPreferences("comment",MODE_PRIVATE).edit();
                String commentData = comments.getText().toString();
                editor.putString("pre_comment",commentData);
                editor.apply();
                if(!commentData.equals("")){
                    Toast.makeText(MainActivity.this,MainActivity.this.getResources().getText(R.string.comment_cancel),Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setPositiveButton(MainActivity.this.getResources().getText(R.string.confirm), new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface p1, int p2)
            {
                SharedPreferences.Editor editor =  getSharedPreferences("comment",MODE_PRIVATE).edit();
                //获取评论内容
                comment = comments.getText().toString();
                comment = java.net.URLEncoder.encode("客户端问题反馈:" + "\n" + comment);
                sendRequestWithHttpClient();
                //清空之前的评论
                editor.putString("pre_comment","");
                editor.apply();

            }
        });
        builder.show();

    }

    //将反馈信息通过线程以评论方式发送给wordpress
    private void sendRequestWithHttpClient()
    {


        new Thread(new Runnable() {
            @Override
            public void run()
            {
                HttpURLConnection connection = null;
                String url_path = "http://oudezhinu.site/wp-comments-post.php";
                Integer statusCode =0;
                try
                {
                    //使用url加载路径
                    URL url = new URL(url_path);
                    //创建HttpURLConnection对象
                    connection = (HttpURLConnection)url.openConnection();
                    //设置头域
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //设置超时
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    //使用POST方法
                    connection.setRequestMethod("POST");
                    //使用输入流
                    connection.setDoInput(false);
                    //使用输出流
                    connection.setDoOutput(true);
                    //Post方式不能缓存,需手动设置为false
                    connection.setUseCaches(false);
                    //请求的body体
                    String data = "comment=" + comment + "&author=" + name_encode + "&email=" + email_encode + "&url=&submit=%E5%8F%91%E8%A1%A8%E8%AF%84%E8%AE%BA&comment_post_ID=925&comment_parent=0";
                    Log.d("MainActivity", data);
                    //设置输出流，写body体
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(data);
                    //outputStream.flush();
                    //获取返回码
                    statusCode = connection.getResponseCode();
                    //使用handle处理结果
                    Log.d("MainActivity", "返回码：" + statusCode.toString());

                    //关闭流
                    outputStream.close();

                    //关闭连接
                    connection.disconnect();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    //根据返回码返回不同的msg
                    switch (statusCode)
                    {
                        case 200:
                            Message message1 = new Message();
                            message1.what = Success;
                            handler.sendMessage(message1);
                            break;
                        case 409:
                            Message message2 = new Message();
                            message2.what = Repeat;
                            handler.sendMessage(message2);
                            break;
                        default:
                            Message message = new Message();
                            message.what = Fail;
                            handler.sendMessage(message);
                            break;
                    }
                }
            }
        }).start();

    }

}
