package com.cn.wq.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cn.wq.entity.ModelPurchase;
import com.cn.wq.utils.SysAppCrashHandler;


                                                                                               
/**
 * 
 * @Title:  DbAdapter.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2016-1-5 下午3:09:57 
 * @version:  V1.0 
 *
 */
public class DbAdapter {
	
	private static String DATABASES_NAME="wq_data.db";
	private static int DATABASES_VERSION = 9;	//V2.0需求版本號為9，APP版本號：2.1.0
	public static final String COLORS = "colors";	//颜色表
	public static final String SIZES = "sizes";	//码数表
	public static final String PURCHASE = "purchase";	//采购信息表
	public static final String MODELS = "models";	//分類關聯表
	public static final String PURCHASE_LIST = "purchaselist";	//采购列表，不同颜色不同
	public static final String PHOTO = "photo";	//商品图片
	public static final String VENDORS = "vendors";	//门店信息
	public static final String VERSION = "version";	//版本控制
	public static final String DETIONARY = "detionary";	//字典表
	public static final String other = "other";	//其他
	
	/**
	 * V2.0需求新增表
	 */
	public static final String Itemnumber = "Itemnumber";	//款号信息
	public static final String StyleNumberPhoto = "StyleNumberPhoto";	//款号-圖片信息
	public static final String SizeInfo = "SizeInfo";	//每張圖片有多個Size
	
    private static Context context;                                                                                             
    private DateDatabases DBHelper;                                                                                           
    public SQLiteDatabase db=null;               
    private static DbAdapter dbAdapter;
    
    public static DbAdapter getDbAdapter(Context ctx) {
        context = ctx;
    	if(dbAdapter==null) {
    		dbAdapter = new DbAdapter();
    	}
        return dbAdapter;
    }  
    
    public DbAdapter() {
    	open();
    }
    
    public void Exec(String sql){
		db.execSQL(sql);    
    }
    
    public Object ExecSql(String sql){
    	String rtn="";
    	Cursor cursor=db.rawQuery(sql,null);
		if (cursor.moveToNext()) {
			rtn = cursor.getString(0);
		}	
		cursor.close();
		return rtn;
    }
    
    public HashMap<String,Object> ExecSqlRow(String sql){
    	HashMap<String,Object> hm=new HashMap<String,Object>();  
	    	Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				for(int i=0;i<cursor.getColumnCount();i++){
					hm.put(cursor.getColumnName(i), cursor.getString(i));
					Log.e("�ֶΣ�"+cursor.getColumnName(i), cursor.getString(i)+"$");
				}
			}		
			cursor.close();
		return hm;
    }
    
    /** 
     * @param sql ��Ҫ��ѯ��SQL��䡣
     * @return ��List��ʽ���ز�ѯ�����ݡ�
     */
    public List<HashMap<String,Object>> ExecSqlList(String sql){
    	List<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
    	Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()) {
			HashMap<String,Object> hm=new HashMap<String,Object>(); 
			for(int i=0;i<cursor.getColumnCount();i++){
				hm.put(cursor.getColumnName(i), cursor.getString(i));
			}
			list.add(hm);
		}		
		cursor.close();
		return list;
    }
    
    public List<HashMap<String,Object>> ExecSqlListFiled(String sql){
    	List<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
    	Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()) {
			HashMap<String,Object> hm=new HashMap<String,Object>(); 
			for(int i=0;i<cursor.getColumnCount();i++){
				String ftype="";
				hm.put(cursor.getColumnName(i), cursor.getString(i));
			}
			list.add(hm);
		}		
		cursor.close();
		return list;
    }
    
    
    class DateDatabases extends SQLiteOpenHelper {
    	
    	public DateDatabases(Context context) {
    		super(context, DATABASES_NAME, null, DATABASES_VERSION);
    	}

    	@Override
    	public void onCreate(SQLiteDatabase db) {
    		db.beginTransaction();
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + PURCHASE + "("
    				+ "id varchar(64),"
    				+ "userid INTEGER,"
    				+ "createdate DATETIME DEFAULT (datetime('now','localtime')) ,"
    				+ "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')) ,"
    				+ "company varchar(20),"
    				+ "price double,"
    				+ "itemnumber varchar(50),"
    				+ "desc_end varchar(50),"
    				+ "desc_chi varchar(50),"
    				+ "model_z varchar(10),"
    				+ "model_c varchar(10),"
    				+ "remark varchar(500),"
    				+ "deposit double,"
    				+ "harvest_time varchar(20),"
    				+ "currency varchar(100),"
    				+ "seiraynumber varchar(20),"
    				+ "others varchar(100),"
    				+ "state INTEGER,"
    				+ "hasupload INTEGER);");
    		
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + COLORS + "("
    				+ "id INTEGER,"
    				+ "code varchar(10),"
    				+ "color varchar(20),"
    				+ "createdate varchar(20),"
    				+ "userid INTEGER);");
    		
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + MODELS + "("
    				+ "model varchar(10),"
    				+ "desc_eng varchar(255),"
    				+ "desc_chi varchar(255),"
    				+ "createdate varchar(20),"
    				+ "group1 varchar(255),"
    				+ "users varchar(255));");
    		
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + PURCHASE_LIST + "("
    				+ "id varchar(64),"
    				+ "colorid INTEGER,"
    				+ "purchaseid varchar(64),"
    				+ "color varchar(20),"
    				+ "sizes varchar(20),"
    				+ "sizesid INTEGER,"
    				+ "amount INTEGER);");
    		
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + SIZES + "("
    				+ "id INTEGER,"
    				+ "sizes varchar(20),"
    				+ "createdate varchar(20),"
    				+ "group1 varchar(10),"
    				+ "userid INTEGER);");
    		
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + DETIONARY + "("
    				+ "id INTEGER,"
    				+ "type varchar(20),"
    				+ "value varchar(100),"
    				+ "createdate varchar(20),"
    				+ "userid INTEGER);");
    		
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + PHOTO + "("
    				+ "id varchar(64),"
    				+ "filename varchar(50),"
    				+ "index1 INTEGER,"
    				+ "purchaseid varchar(64),"
    				+ "localpath varchar(200),"
    				+ "hasupload INTEGER,"
    				+ "createdate varchar(20),"
    				+ "userid INTEGER,"
    				+ "mobilepath varchar(200));");
    		 
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + VENDORS + "("
    				+ "id INTEGER,"
    				+ "createdate varchar(20),"
    				+ "company varchar(20),"
    				+ "desc_end varchar(50),"
    				+ "desc_chi varchar(50),"
    				+ "address1 varchar(50),"
    				+ "address2 varchar(50),"
    				+ "address3 varchar(50),"
    				+ "email varchar(50),"
    				+ "phone1 varchar(30),"
    				+ "phone2 varchar(30),"
    				+ "phone3 varchar(30),"
    				+ "faxno varchar(30));");
    		
    		
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + VERSION + "("
    				+ "id INTEGER,"
    				+ "name  varchar(200),"
    				+ "version varchar(10),"
    				+ "msg varchar(1000),"
    				+ "type varchar(10));");
    		
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + other + "("
    				+ "id INTEGER,"
    				+ "name  varchar(20),"
    				+ "orders INTEGER,"
    				+ "state INTEGER,"
    				+ "createdate varchar(20),"
    				+ "userid varchar(20));");
    		/**
    		 * V2.0需求新增表
    		 */
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + Itemnumber + "("
					+ "id varchar(64),"
					+ "styleNumber varchar(50),"
    				+ "purchaseid varchar(64),"
    				+ "price double,"
    				+ "harvestTime varchar(20));");
    		
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + StyleNumberPhoto + "("
    				+ "id varchar(64),"
    				+ "index1 INTEGER,"
    				+ "purchaseid varchar(64),"
    				+ "itemnumberId varchar(64));");
    		
    		db.execSQL(" CREATE TABLE IF NOT EXISTS " + SizeInfo + "("
    				+ "styleNumberPhotoId varchar(64),"
    				+ "purchaseid varchar(64),"
    				+ "color varchar(20),"
    				+ "size varchar(20),"
    				+ "amount INTEGER);");
    		db.setTransactionSuccessful();
    		db.endTransaction();
    	}
    	
    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		if(oldVersion == 7) {
	    		db.execSQL("DROP TABLE IF EXISTS " + COLORS);
		    	db.execSQL("DROP TABLE IF EXISTS " + SIZES);
		    	db.execSQL("DROP TABLE IF EXISTS " + MODELS);
		    	db.execSQL("DROP TABLE IF EXISTS " + VENDORS);
		    	db.execSQL("DROP TABLE IF EXISTS " + VERSION);
		    	db.execSQL("DROP TABLE IF EXISTS " + DETIONARY);
		    	db.execSQL("DROP TABLE IF EXISTS " + other);
    			if(!checkColumnExists2(db, PURCHASE, "state"))
    				db.execSQL("ALTER TABLE "+PURCHASE+" ADD state INTEGER;");
    		} else if (oldVersion == 8){
    			/**
        		 * V2.0需求新增表
        		 */
    			//表新增字段
    			if(!checkColumnExists2(db, PHOTO, "type"))
    				db.execSQL("ALTER TABLE "+PHOTO+" ADD type INTEGER;");
    			if(!checkColumnExists2(db, PHOTO, "itemnumberId"))
    				db.execSQL("ALTER TABLE "+PHOTO+" ADD itemnumberId  varchar(64);");
    			if(!checkColumnExists2(db, PURCHASE, "v2"))
    				db.execSQL("ALTER TABLE "+PURCHASE+" ADD v2 INTEGER;");
    			if(!checkColumnExists2(db, PURCHASE, "companyId"))
    				db.execSQL("ALTER TABLE "+PURCHASE+" ADD companyId varchar(100);");
//    			if(!checkColumnExists2(db, StyleNumberPhoto, "index1"))
//    				db.execSQL("ALTER TABLE "+StyleNumberPhoto+" ADD index1 INTEGER;");
    			db.execSQL(" CREATE TABLE IF NOT EXISTS " + SizeInfo + "("
        				+ "styleNumberPhotoId varchar(64),"
        				+ "purchaseid varchar(64),"
        				+ "color varchar(20),"
        				+ "size varchar(20),"
        				+ "amount INTEGER);");
    		
    			//給V1.0版本中上列字段賦予默認值
    			updatePhotoType(db);
    			updatePurhaseVersion(db);
    		} else if(oldVersion == 9) {

    		}else {
    			/**
    			 * 查詢PURCHASE表，state字段為空或null時，根據hasupload的值，為1時state為1，否則判斷預計收貨日期是否為空或null、入价是否為0，滿足其一時
    			 * state為0，否則為1
    			 */
    			List<ModelPurchase> purchaseListForUpdate = getPurchaseListForUpdate(db);
    			try {
    				SysAppCrashHandler.saveLog("getPurchaseListForUpdate size: " + purchaseListForUpdate.size());
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			for (ModelPurchase modelPurchase : purchaseListForUpdate) {
					if(modelPurchase.getState()==0) {	//未完成
						
						
					} else if(modelPurchase.getState()==1) {	//已完成
						
					} else {
						if(modelPurchase.getHasupload()==1) {
							//已上傳，修改state為1,預計收貨日期修改為創建日期
							updatePurhase(modelPurchase.getId(), "1", modelPurchase.getCreatedate(), db);
						} else {
							if(modelPurchase.getHarvest_time()==null || modelPurchase.getHarvest_time().length()==0 || modelPurchase.getPrice()==0) {
								//state修改為0
								updatePurhase(modelPurchase.getId(), "0", modelPurchase.getCreatedate(), db);
							} else {
								//state修改為1
								updatePurhase(modelPurchase.getId(), "1", modelPurchase.getCreatedate(), db);
							}
						}
					}
				}
    		}
    		onCreate(db);
    	}

    }
    
    //==========================================================================================================//
    //===============================================V2.0需求====================================================//
    //=========================================================================================================//
    /**
	 * 給V1.0版本中上列字段賦予默認值
	 * 
	 * @param goods_id
	 */
	public static void updatePhotoType(SQLiteDatabase db) {
		try {
			SysAppCrashHandler.saveLog("update " + DbAdapter.PHOTO + " set type=0 where type!=1 and type!=2");
			db.execSQL("update " + DbAdapter.PHOTO + " set type = ? where type != ? and type != ?", new String[] {"0" ,"1" ,"2"});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void updatePurhaseVersion(SQLiteDatabase db) {
		try {
			SysAppCrashHandler.saveLog("update " + DbAdapter.PURCHASE + " set v2=0 where v2!=1");
			db.execSQL("update " + DbAdapter.PURCHASE + " set v2 = ? where v2!=?", new String[] {"0" ,"1"});
//			db.execSQL("update " + DbAdapter.PURCHASE + " set v2 = ?", new String[] {"0"});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//==========================================================================================================//
    //===============================================分割線====================================================//
    //=========================================================================================================//
	
	
	
	
    /**
	 * 根据ID、表名 更改上传状态
	 * 
	 * @param goods_id
	 */
	public static void updatePurhase(String id , String state ,String lastupdatedate,SQLiteDatabase db) {
		try {
			SysAppCrashHandler.saveLog("update " + DbAdapter.PURCHASE + " set state = " + state +" ,lastupdatedate="+ lastupdatedate +"  where id ="+ id);
			db.execSQL("update " + DbAdapter.PURCHASE + " set state = ? ,lastupdatedate=?  where id =?  ", new String[] {state,lastupdatedate, id });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
	 * 获取采购列表
	 * 
	 * @return
	 */
	public static List<ModelPurchase> getPurchaseListForUpdate(SQLiteDatabase db) {
		List<ModelPurchase> goodsInfos = new ArrayList<ModelPurchase>();
		Cursor cursor = db.rawQuery("select * from " + DbAdapter.PURCHASE + " where state=? or state=? or lastupdatedate=? or lastupdatedate=?", new String[] {"" ,"null" ,"" ,"null"});
		while (cursor.moveToNext()) {
			goodsInfos.add(DataUtils.initModelPurchase(cursor));
		}
		cursor.close();
		return goodsInfos;
	}
    
    private boolean checkColumnExists2(SQLiteDatabase db, String tableName, String columnName) {
    	boolean result = false ;
    	Cursor cursor = null ;
    	try{
    		cursor = db.rawQuery( "select * from sqlite_master where name = ? and sql like ?"
    	         , new String[]{tableName , "%" + columnName + "%"} );
    	    result = null != cursor && cursor.moveToFirst() ;
    	} catch (Exception e) {
    	    e.printStackTrace();
    	} finally {
    		if(null != cursor && !cursor.isClosed()){
    			cursor.close() ;
    		}
    	}
    	return result ;
    }
    
    public SQLiteDatabase open() throws SQLException { 
    	DBHelper = new DateDatabases(context); 
    	db=DBHelper.getWritableDatabase();
    	//db.beginTransaction();
    	
        return db;                                                                                                                
    }                                                                                                                

	public void close() // synchronized
	{
		try {
			// db.setTransactionSuccessful();
			// db.endTransaction();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			DBHelper.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}                                                  
}                                                                                                                                 
