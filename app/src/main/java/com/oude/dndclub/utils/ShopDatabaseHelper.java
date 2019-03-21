package com.oude.dndclub.utils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

//SQLiteOpenHelper是官方给的抽象帮助类，需要我们自己去实现具体功能
public class ShopDatabaseHelper extends SQLiteOpenHelper
{
    private Context mContext;
    //使用sql语言创建表，+ 换行这种方式是为了更加直观,数据类型为
    //interger(整型)、real(浮点数)、text(文本)、blob(二进制)
    //此处使用_id作为主键，表为龙与地下城桌游中的物品列表
    //public static final String CREATE_ITEM = "create table item ("
    //+ "_id integer primary key autoincrement,"
    // + "name text,"
    //+ "type text,"
    // + "price real,"
    // + "weight real,"
    // + "source text,"
    // + "explain text)";
    //构造函数
    public ShopDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        mContext = context;
    }
    
    @Override
    public void onCreate(SQLiteDatabase p1)
    {
        //创建数据库,使用上面的item字符常量
        //p1.execSQL(CREATE_ITEM);
       // Toast.makeText(mContext,"item数据库创建",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
    {
        //更新数据库
        //如果需要更新，先删除已存在的表，,否则报错
        //然后再在DatabaseActivity如同创建一样调用，版本需要大于原来数据库版本
        //当然，更新肯定是需要有新的，比如item表的格式之类变化或者直接新增表，这里不进行演示
        //p1.execSQL("drop table if exists item");
        //调用onCreate()方法，所以有更新在onCreate()下改变
        //onCreate(p1);
    }

}
