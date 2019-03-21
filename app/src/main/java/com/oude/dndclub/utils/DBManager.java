package com.oude.dndclub.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.oude.dndclub.*;

public class DBManager {
	private final int BUFFER_SIZE = 400000;
	public static final String PACKAGE_NAME = "com.oude.dndclub";
	// 在手机里存放数据库的位置
	public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME +"/databases";
	private Context context;

	public DBManager(Context context) {
		this.context = context;
	}
    
	public void importDB(String DB_NAME) {
		execImport(DB_PATH + "/" + DB_NAME);
	}

	private void execImport(String dbfile) {
		Log.d("DBManager",DB_PATH);
		try {
			//判断databases文件夹是否存在
			File file = new File(DB_PATH);
			if(!file.exists()){
				if(file.mkdir()){
					Log.d("DBManager","数据库文件夹创建成功");
				}else{
					Log.d("DBManager","数据库文件夹创建失败");	
				}
			}
			if (!(new File(dbfile).exists())) {
                // 判断数据库文件是否存在，若不存在则执行导入
                // 欲导入的数据库
				InputStream is = this.context.getResources().openRawResource(R.raw.shop);
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
			Log.d("DBManager","数据库导入成功");
		} catch (FileNotFoundException e) {
			Log.e("Database", "File not found");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("Database", "IO exception");
			e.printStackTrace();
		}
	}

}
