package com.cn.wq.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.CheckBox;

import com.cn.wq.activty.ActivityUpdate;
import com.cn.wq.entity.ModelColors;
import com.cn.wq.entity.ModelDetionary;
import com.cn.wq.entity.ModelItemnumber;
import com.cn.wq.entity.ModelModels;
import com.cn.wq.entity.ModelOther;
import com.cn.wq.entity.ModelPhoto;
import com.cn.wq.entity.ModelPurchase;
import com.cn.wq.entity.ModelPurchaseList;
import com.cn.wq.entity.ModelSizeInfo;
import com.cn.wq.entity.ModelSizes;
import com.cn.wq.entity.ModelStyleNumberPhoto;
import com.cn.wq.entity.ModelVendors;
import com.cn.wq.entity.ModelVersion;

/**
 * 
 * @Title: DateUtils.java
 * 
 * @Description: TODO<请描述此文件是做什么的>
 * 
 * @author Terence
 * @data: 2015-12-30 上午1:09:21
 * @version: V1.0
 * 
 */
public class DataUtils {
	
	/**
	 * 获取采购列表
	 * 
	 * @return
	 */
	public static boolean getObjectByidAndTableName(Object value ,String columName, String tableName ,DbAdapter db) {
		Cursor cursor = db.db.rawQuery("select * from " + tableName + " where "+columName+"=?", new String[] {value+""});
		while (cursor.moveToNext()) {
			return true;
		}
		cursor.close();
		return false;
	}
	
	/**
	 * 根据表名，查询该表最后更新时间
	 * @param tableName
	 * @param db
	 */
	public static String getLastTimeByTableName(String tableName ,DbAdapter db) {
		Cursor cursor = db.db.rawQuery("select createdate from " + tableName + " ORDER BY createdate DESC limit 0,1", new String[] {});
		String date = "2000-01-01 01:01:01";
		while (cursor.moveToNext()) {
			date = cursor.getString(cursor.getColumnIndex("createdate"));
			break;
		}
		cursor.close();
		return date;
	}
	
	/**
	 * 插入一条采购信息
	 * 
	 * @param purchase
	 * @return
	 */
	public static boolean insertPurchase(ModelPurchase purchase ,DbAdapter db) {
		try {
			if(getPurchaseByidBoolean(purchase.getId(), db)) {
				db.db.delete(DbAdapter.PURCHASE, " id=? ", new String[]{purchase.getId()});
				db.db.delete(DbAdapter.PURCHASE_LIST, " purchaseid=? ", new String[]{purchase.getId()});
				db.db.delete(DbAdapter.PHOTO, " purchaseid=? ", new String[]{purchase.getId()});
				/**
				 * V2.0需求
				 */
				db.db.delete(DbAdapter.Itemnumber, " purchaseid=? ", new String[]{purchase.getId()});
				db.db.delete(DbAdapter.StyleNumberPhoto, " purchaseid=? ", new String[]{purchase.getId()});
				db.db.delete(DbAdapter.SizeInfo, " purchaseid=? ", new String[]{purchase.getId()});
			} 
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				ContentValues values = new ContentValues();
				values.put("id", purchase.getId());
				values.put("userid", purchase.getUserid());
				values.put("createdate", purchase.getCreatedate());
				values.put("lastupdatedate", format.format(new Date()));
				values.put("desc_end", purchase.getDesc_end());
				values.put("desc_chi", purchase.getDesc_chi());
				values.put("company", purchase.getCompany());
				values.put("model_z", purchase.getModel_z());
				values.put("model_c", purchase.getModel_c());
				values.put("harvest_time", purchase.getHarvest_time());
				values.put("currency", purchase.getCurrency());
				values.put("deposit", purchase.getDeposit());
				values.put("remark", purchase.getRemark()+"");
				values.put("state", purchase.getState());
				values.put("hasupload", purchase.getHasupload());	//默认为未上传
				values.put("price", purchase.getPrice());
				values.put("others", purchase.getOthers());
				values.put("itemnumber", purchase.getItemnumber());
				/**
				 * V2.0需求
				 */
				values.put("v2", purchase.getV2()==null?0:1);
				values.put("companyId", purchase.getCompanyId());
				
				
				long insert = db.db.insert(DbAdapter.PURCHASE, null, values);
				List<ModelPurchaseList> purchaseLists = purchase.getPurchaseLists();
				for (ModelPurchaseList modelPurchaseList : purchaseLists) {
					modelPurchaseList.setPurchaseid(purchase.getId());
					insertPurchaseList(modelPurchaseList, db);
				}
				List<ModelPhoto> pics = purchase.getPics();
				for (ModelPhoto modelPhoto : pics) {
					modelPhoto.setPurchaseid(purchase.getId());
					insertPhoto(modelPhoto, db);
				}
				
				/**
				 * V2.0需求
				 */
				if(purchase.getV2()!=null && purchase.getV2()==1) {
					if(purchase.getCompanyPhoto()!=null) {
						purchase.getCompanyPhoto().setPurchaseid(purchase.getId());
						//保存檔口圖片
						insertPhoto(purchase.getCompanyPhoto(), db);
					}
					List<ModelItemnumber> itemnumbers = purchase.getItemnumbers();
					for (ModelItemnumber itemnumber : itemnumbers) {
						itemnumber.setPurchaseid(purchase.getId());
						//保存一個款號
						insertItemnumber(itemnumber, db);
						List<ModelStyleNumberPhoto> styleNumberPhotos = itemnumber.getStyleNumberPhotos();
						for (ModelStyleNumberPhoto numberPhoto : styleNumberPhotos) {
							numberPhoto.setPurchaseid(purchase.getId());
							numberPhoto.setItemnumberId(itemnumber.getId());
							//保存款號中的一個-文字信息
							insertStyleNumberPhoto(numberPhoto, db);
							
							List<ModelSizeInfo> sizeInfos = numberPhoto.getSizeInfos();
							for (ModelSizeInfo modelSizeInfo : sizeInfos) {
								modelSizeInfo.setStyleNumberPhotoId(numberPhoto.getId());
								modelSizeInfo.setPurchaseid(purchase.getId());
								insertSizeInfo(modelSizeInfo, db);
							}
							//保存款號中的圖片
							if(numberPhoto.getStyleNumberPhoto() != null) {
								numberPhoto.getStyleNumberPhoto().setPurchaseid(purchase.getId());
								numberPhoto.getStyleNumberPhoto().setItemnumberId(itemnumber.getId());
								numberPhoto.getStyleNumberPhoto().setIndex1(numberPhoto.getIndex1());	//與主表index1保持一致
								insertPhoto(numberPhoto.getStyleNumberPhoto(), db);
							}
						}
					}
				}
				
				if(insert==-1) return false;
				else return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 插入图片信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertPhotos(List<ModelPhoto> photos ,String purchaseId ,DbAdapter db) {
		try {
			db.db.delete(DbAdapter.PHOTO, " purchaseId=? ", new String[]{purchaseId});
			for (ModelPhoto photo : photos) {
				boolean insertPhoto = insertPhoto(photo, db);
				if(!insertPhoto) return false;
			}
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 插入一条图片信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertPhoto(ModelPhoto photo ,DbAdapter db) {
		try {
			ContentValues values = new ContentValues();
			values.put("id", photo.getId());
			values.put("filename", photo.getFilename());
			values.put("index1", photo.getIndex1());
			values.put("userid", photo.getUserid());
			values.put("createdate", photo.getCreatedate());
			values.put("hasupload", 0);	//默认为为未上传
			values.put("purchaseid", photo.getPurchaseid());
			values.put("mobilepath", photo.getMobilepath());
			values.put("type", photo.getType());
			values.put("itemnumberId", photo.getItemnumberId());
			long insert = db.db.insert(DbAdapter.PHOTO, null, values);
			if(insert==-1) return false;
			else return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 插入一条采购颜色信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertPurchaseList(ModelPurchaseList list ,DbAdapter db) {
		try {
			ContentValues values = new ContentValues();
			values.put("id", java.util.UUID.randomUUID().toString());
			values.put("colorid", list.getColorid());
			values.put("color", list.getColor());
			values.put("sizesid", list.getSizesid());
			values.put("sizes", list.getSizes());
			values.put("purchaseid", list.getPurchaseid());
			values.put("amount", list.getAmount());	
			long insert = db.db.insert(DbAdapter.PURCHASE_LIST, null, values);
			if(insert==-1) return false;
			else return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * V2.0需求，插入款號信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertItemnumber(ModelItemnumber itemnumber ,DbAdapter db) {
		try {
			ContentValues values = new ContentValues();
			values.put("id", itemnumber.getId());
			values.put("styleNumber", itemnumber.getStyleNumber());
			values.put("purchaseid", itemnumber.getPurchaseid());
			values.put("price", itemnumber.getPrice());
			values.put("harvestTime", itemnumber.getHarvestTime());
			long insert = db.db.insert(DbAdapter.Itemnumber, null, values);
			if(insert==-1) return false;
			else return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * V2.0需求，插入款號圖片信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertStyleNumberPhoto(ModelStyleNumberPhoto modelStyleNumberPhoto ,DbAdapter db) {
		try {
			ContentValues values = new ContentValues();
			values.put("id", modelStyleNumberPhoto.getId());
			values.put("purchaseid", modelStyleNumberPhoto.getPurchaseid());
			values.put("itemnumberId", modelStyleNumberPhoto.getItemnumberId());
			values.put("index1", modelStyleNumberPhoto.getIndex1());
			long insert = db.db.insert(DbAdapter.StyleNumberPhoto, null, values);
			if(insert==-1) return false;
			else return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * V2.0需求，插入碼數信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertSizeInfo(ModelSizeInfo sizeInfo ,DbAdapter db) {
		try {
			ContentValues values = new ContentValues();
			values.put("styleNumberPhotoId", sizeInfo.getStyleNumberPhotoId());
			values.put("color", sizeInfo.getColor());
			values.put("size", sizeInfo.getSize());
			values.put("amount", sizeInfo.getAmount());
			values.put("purchaseid", sizeInfo.getPurchaseid());
			long insert = db.db.insert(DbAdapter.SizeInfo, null, values);
			if(insert==-1) return false;
			else return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 插入采购颜色信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertColors(ModelColors modelColors ,DbAdapter db) {
		try {
			boolean objectByidAndTableName = getObjectByidAndTableName(modelColors.getId() ,"id", DbAdapter.COLORS, db);
			if(objectByidAndTableName) {
				//update
				db.db.execSQL("update " + DbAdapter.COLORS + " set code = ?,color = ?,createdate = ?,userid = ? where id =?  ", new String[] {
						modelColors.getCode() ,modelColors.getColor() ,modelColors.getCreatedate() ,modelColors.getUserid()+"" ,modelColors.getId()+"" });
				return true;
			} else {
				//insert
				ContentValues values = null;
				values = new ContentValues();
				values.put("id", modelColors.getId());
				values.put("code", modelColors.getCode());
				values.put("color", modelColors.getColor());
				values.put("createdate", modelColors.getCreatedate());
				values.put("userid", modelColors.getUserid());	
				long insert = db.db.insert(DbAdapter.COLORS, null, values);
				if(insert==-1) {
					return false;
				}
				return true;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	

	public static void deleteColors(DbAdapter db) {
		db.db.delete(DbAdapter.COLORS, null, null);
	}
	public static void deleteModels(DbAdapter db) {
		db.db.delete(DbAdapter.MODELS, null, null);
	}
	public static void deleteSizes(DbAdapter db) {
		db.db.delete(DbAdapter.SIZES, null, null);
	}
	public static void deleteDetionary(DbAdapter db) {
		db.db.delete(DbAdapter.DETIONARY, null, null);
	}

	public static void deleteOther(DbAdapter db) {
		db.db.delete(DbAdapter.other, null, null);
	}
	
	/**
	 * 插入分類信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertModels(ModelModels modelModels ,DbAdapter db) {
		try {
			boolean objectByidAndTableName = getObjectByidAndTableName(modelModels.getModel() ,"model", DbAdapter.MODELS, db);
			if(objectByidAndTableName) {
				//update
				db.db.execSQL("update " + DbAdapter.MODELS + " set desc_chi = ?,desc_eng = ?,createdate = ?,group1 = ?,users = ? where model =?  ", new String[] {
						modelModels.getDesc_chi() ,modelModels.getDesc_eng() ,modelModels.getCreatedate() ,modelModels.getGroup1() ,modelModels.getUsers(),modelModels.getModel() });
				return true;
			} else {
				//insert
				ContentValues values = null;
				values = new ContentValues();
				values.put("model", modelModels.getModel());
				values.put("desc_chi", modelModels.getDesc_chi());
				values.put("desc_eng", modelModels.getDesc_eng());
				values.put("createdate", modelModels.getCreatedate());
				values.put("group1", modelModels.getGroup1());	
				values.put("users", modelModels.getUsers());	
				long insert = db.db.insert(DbAdapter.MODELS, null, values);
				if(insert==-1) {
					return false;
				}
				return true;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 插入其他表信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertOther(ModelOther other ,DbAdapter db) {
		try {
			boolean objectByidAndTableName = getObjectByidAndTableName(other.getId() ,"id", DbAdapter.other, db);
			if(objectByidAndTableName) {
				//update
				db.db.execSQL("update " + DbAdapter.other + " set name = ?,orders = ?,state = ?,createdate = ?,userid = ? where id =?  ", new String[] {
						other.getName() ,other.getOrders()+"" ,other.getState()+"",other.getCreatedate() ,other.getUserid()+"" ,other.getId()+"" });
				return true;
			} else {
				//insert
				ContentValues values = null;
				values = new ContentValues();
				values.put("id", other.getId());
				values.put("name", other.getName());
				values.put("orders", other.getOrders());
				values.put("state", other.getState());
				values.put("createdate", other.getCreatedate());
				values.put("userid", other.getUserid());	
				long insert = db.db.insert(DbAdapter.other, null, values);
				if(insert==-1) {
					return false;
				}
			return true;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 插入采购码数信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertSizes(ModelSizes sizes ,DbAdapter db) {
		try {
			boolean objectByidAndTableName = getObjectByidAndTableName(sizes.getId() ,"id", DbAdapter.SIZES, db);
			if(objectByidAndTableName) {
				//update
				db.db.execSQL("update " + DbAdapter.SIZES + " set sizes = ?,group1 = ?,createdate = ?,userid = ? where id =?  ", new String[] {
						sizes.getSizes() ,sizes.getGroup1() ,sizes.getCreatedate() ,sizes.getUserid()+"" ,sizes.getId()+"" });
				return true;
			} else {
				//insert
				ContentValues values = null;
				values = new ContentValues();
				values.put("id", sizes.getId());
				values.put("sizes", sizes.getSizes());
				values.put("group1", sizes.getGroup1());
				values.put("createdate", sizes.getCreatedate());
				values.put("userid", sizes.getUserid());
				long insert = db.db.insert(DbAdapter.SIZES, null, values);
				if(insert==-1) {
					return false;
				}
				return true;
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 插入字典表
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertDetionary(ModelDetionary d ,DbAdapter db) {
		try {
			boolean objectByidAndTableName = getObjectByidAndTableName(d.getId(),"id", DbAdapter.DETIONARY, db);
			if(objectByidAndTableName) {
				//update
				db.db.execSQL("update " + DbAdapter.DETIONARY + " set type = ?,value = ?,createdate = ?,userid = ? where id =?  ", new String[] {
						d.getType() ,d.getValue() ,d.getCreatedate() ,d.getUserid()+"" ,d.getId()+"" });
				return true;
			} else {
				//insert
				ContentValues values = null;
				values = new ContentValues();
				values.put("id", d.getId());
				values.put("type", d.getType());
				values.put("value", d.getValue());
				values.put("createdate", d.getCreatedate());
				values.put("userid", d.getUserid());	
				long insert = db.db.insert(DbAdapter.DETIONARY, null, values);
				if(insert==-1) {
					return false;
				}
			return true;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	static void sendMsg(CheckBox cb ,String text ,Handler mHandler) {
		Message obtainMessage = mHandler.obtainMessage();
		Bundle data = new Bundle();
		data.putString("text", text);
		obtainMessage.setData(data);
		obtainMessage.obj = cb;
		obtainMessage.what = ActivityUpdate.SET_TEXT;
		mHandler.sendMessage(obtainMessage);
	}
	
	/**
	 * 插入门店信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertVendors(final List<ModelVendors> list ,DbAdapter db ,CheckBox cb ,Handler handler) {
		try {
			db.db.delete(DbAdapter.VENDORS, null, null);
			ContentValues values = null;
			for (final ModelVendors vendors : list) {
				values = new ContentValues();
				values.put("id", vendors.getId());
				values.put("address1", vendors.getAddress1());
				values.put("address2", vendors.getAddress2());
				values.put("address3", vendors.getAddress3());
				values.put("company", vendors.getCompany());
				values.put("desc_chi", vendors.getDesc_chi());
				values.put("createdate", vendors.getCreatedate());
				values.put("desc_end", vendors.getDesc_end());
				values.put("email", vendors.getEmail());
				values.put("faxno", vendors.getFaxno());
				values.put("phone1", vendors.getPhone1());
				values.put("phone2", vendors.getPhone2());
				values.put("phone3", vendors.getPhone3());
				long insert = db.db.insert(DbAdapter.VENDORS, null, values);
				if(insert==-1) {
					return false;
				}
				sendMsg(cb, "门店列表信息更新("+list.size()+"条):"+vendors.getCompany(),handler);
			}
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void deleteVendors(DbAdapter db) {
		db.db.delete(DbAdapter.VENDORS, null, null);
	}
	
	/**
	 * 插入门店信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertVendors(ModelVendors vendors ,DbAdapter db) {
		try {
			boolean objectByidAndTableName = getObjectByidAndTableName(vendors.getCompany() ,"company", DbAdapter.VENDORS, db);
			if(objectByidAndTableName) {
				//update
				db.db.execSQL("update " + DbAdapter.VENDORS + " set id = ?,address1 = ?,address2 = ?,address3 = ?,desc_chi = ?,desc_end = ?" +
						" ,email = ?,createdate = ?,faxno = ?,phone1 = ?,phone2 = ?,phone3 = ? where company =?  ", new String[] {
						vendors.getId()+"" ,vendors.getAddress1() ,vendors.getAddress2() ,vendors.getAddress3() ,vendors.getDesc_chi(),vendors.getDesc_end()
						,vendors.getEmail(),vendors.getCreatedate(),vendors.getFaxno(),vendors.getPhone1(),vendors.getPhone2(),vendors.getPhone3(),vendors.getCompany()});
				return true;
			} else {
				//insert
				ContentValues values = null;
				values = new ContentValues();
				values.put("id", vendors.getId());
				values.put("address1", vendors.getAddress1());
				values.put("address2", vendors.getAddress2());
				values.put("address3", vendors.getAddress3());
				values.put("company", vendors.getCompany());
				values.put("desc_chi", vendors.getDesc_chi());
				values.put("desc_end", vendors.getDesc_end());
				values.put("email", vendors.getEmail());
				values.put("createdate", vendors.getCreatedate());
				values.put("faxno", vendors.getFaxno());
				values.put("phone1", vendors.getPhone1());
				values.put("phone2", vendors.getPhone2());
				values.put("phone3", vendors.getPhone3());
				long insert = db.db.insert(DbAdapter.VENDORS, null, values);
				if(insert==-1) {
					return false;
				}
			return true;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	


	/**
	 * 根据ID、表名 更改上传状态
	 * 
	 * @param goods_id
	 */
	public static void updateUploadState(String id , String seiraynumber,String tableName ,DbAdapter db) {
		if(tableName.equals(DbAdapter.PHOTO)) {
			db.db.execSQL("update " + tableName + " set hasupload = ? where id =?  ", new String[] {"1",id });
		} else if(tableName.equals(DbAdapter.PURCHASE)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			db.db.execSQL("update " + tableName + " set hasupload = ? ,seiraynumber=? ,lastupdatedate = ? where id =?  ", new String[] {"1",seiraynumber, format.format(new Date()), id });
		}
	}
	
	/**
	 * 获取采购列表
	 * 
	 * @return
	 */
	public static boolean getPurchaseByidBoolean(String purchaseId ,DbAdapter db) {
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.PURCHASE + " where id=?", new String[] {purchaseId});
		while (cursor.moveToNext()) {
			return true;
		}
		cursor.close();
		return false;
	}
	
	
	/**
	 * 获取采购列表
	 * 
	 * @return
	 */
	public static ModelPurchase getPurchaseByid(String purchaseId ,DbAdapter db) {
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.PURCHASE + " where id=?", new String[] {purchaseId});
		ModelPurchase purchase = new ModelPurchase();
		while (cursor.moveToNext()) {
			purchase = initModelPurchase(cursor);
		}
		cursor.close();
		return purchase;
	}
	
	/**
	 * 获取采购列表
	 * 
	 * @return
	 */
	public static List<ModelPurchase> getPurchaseList(int user_id ,int limit ,int offset ,DbAdapter db) {
		List<ModelPurchase> goodsInfos = new ArrayList<ModelPurchase>();
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.PURCHASE + " where userid=? order by state asc ,lastupdatedate desc ,hasupload asc ,createdate desc ,itemnumber asc ,desc_chi asc limit ? offset ?", new String[] {user_id+"" ,limit+"" ,offset+ ""});
		while (cursor.moveToNext()) {
			goodsInfos.add(initModelPurchase(cursor));
		}
		cursor.close();
		
		for (ModelPurchase m : goodsInfos) {
			/**
			 * V2.0需求
			 */
			if(m.getV2()==1) {
				//查詢子關聯表
				m.setCompanyPhoto(getCompanyPhoto(m.getId(), db));
				//查詢所有款號
				m.setItemnumbers(getItemnumberByPurchaseid(m.getId(), db));
//				List<ModelItemnumber> itemnumbers = new ArrayList<ModelItemnumber>();
//				Cursor rawQuery3 = db.db.rawQuery("select * from " + DbAdapter.Itemnumber + " where purchaseid=?", new String[] {m.getId()+""});
//				while (rawQuery3.moveToNext()) {
//					ModelItemnumber initItemnumber = initItemnumber(rawQuery3);
//					m.getItemnumbers().add(initItemnumber);
//					itemnumbers.add(initItemnumber);
//					break;
//				}
//				rawQuery3.close();
//				
//				/**
//				 * 查詢款號列表
//				 */
//				for (ModelItemnumber itemnumber : itemnumbers) {
//					Cursor rawQuery4 = db.db.rawQuery("select * from " + DbAdapter.StyleNumberPhoto + " where purchaseid=? and itemnumberId=?", new String[] {m.getId()+"" ,itemnumber.getId()+""});
//					while (rawQuery4.moveToNext()) {
//						//查詢每個款號下的所有圖片文字信息
//						itemnumber.getStyleNumberPhotos().add(initStyleNumberPhoto(rawQuery4));
//						break;
//					}
//					rawQuery4.close();
//				}
//				
//				/**
//				 * 查詢檔口圖片信息
//				 */
//				Cursor rawQuery5 = db.db.rawQuery("select * from " + DbAdapter.PHOTO + " where purchaseid=? and type= ? order by index1 asc", new String[] {m.getId() ,"1"});
//				while (rawQuery5.moveToNext()) {
//					if(rawQuery5.getInt(rawQuery5.getColumnIndex("type"))==1) {
//						//檔口圖片
//						m.setCompanyPhoto(initPhoto(rawQuery5));
//					}
//				}
//				rawQuery5.close();
			} else {
				//列表界面图片不需要展示，所以不需要查询
				Cursor rawQuery = db.db.rawQuery("select * from " + DbAdapter.PHOTO + " where purchaseid=? order by index1 asc", new String[] {m.getId()+""});
				while (rawQuery.moveToNext()) {
					m.getPics().add(initPhoto(rawQuery));
				}
				rawQuery.close();
				
				Cursor rawQuery2 = db.db.rawQuery("select * from " + DbAdapter.PURCHASE_LIST + " where purchaseid=?", new String[] {m.getId()+""});
				while (rawQuery2.moveToNext()) {
					m.getPurchaseLists().add(initPurchaseList(rawQuery2));
				}
				rawQuery2.close();
			}
		}
		return goodsInfos;
	}
	
	/**
	 * 根據採購信息表ID查詢檔口圖片，只會存在一張
	 * 
	 * @param purchaseid
	 * @param db
	 * @return
	 */
	public static ModelPhoto getCompanyPhoto(String purchaseid ,DbAdapter db) {
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.PHOTO + " where purchaseid=? and type=?", new String[] {purchaseid ,"1"});
		while (cursor.moveToNext()) {
			return initPhoto(cursor);
		}
		cursor.close();
		return null;
	}


	/**
	 * 查询所有未上传的采购信息
	 * 
	 * @return
	 */
	public static List<ModelPurchase> uploadPurchaseList(int user_id ,DbAdapter db) {
		List<ModelPurchase> goodsInfos = new ArrayList<ModelPurchase>();
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.PURCHASE + " where userid=? and hasupload=? and state=?", new String[] {user_id+"" ,0+"" ,1+""});
		
		while (cursor.moveToNext()) {
			goodsInfos.add(initModelPurchase(cursor));
		}
		cursor.close();
		
		for (ModelPurchase m : goodsInfos) {
			if(m.getV2()!=null && m.getV2()==1) {
				/**
				 * V2.0需求，新增三張表上傳
				 */
				//查詢子關聯表
				m.setCompanyPhoto(getCompanyPhoto(m.getId(), db));
				//查詢所有款號
				m.setItemnumbers(getItemnumberByPurchaseid(m.getId(), db));
			} else {
				Cursor rawQuery = db.db.rawQuery("select * from " + DbAdapter.PHOTO + " where purchaseid=? and type!=? and type!=? order by index1 asc", new String[] {m.getId()+"" ,"1" ,"2"});
				while (rawQuery.moveToNext()) {
					m.getPics().add(initPhoto(rawQuery));
				}
				rawQuery.close();
				
				Cursor rawQuery2 = db.db.rawQuery("select * from " + DbAdapter.PURCHASE_LIST + " where purchaseid=?", new String[] {m.getId()+""});
				while (rawQuery2.moveToNext()) {
					m.getPurchaseLists().add(initPurchaseList(rawQuery2));
				}
				rawQuery2.close();
			}
		}
		return goodsInfos;
	}
	
	
	/**
	 * 查询所有未上传的图片
	 * 
	 * @return
	 */
	public static List<ModelPhoto> uploadPhotoList(DbAdapter db) {
		List<ModelPhoto> photos = new ArrayList<ModelPhoto>();
		Cursor rawQuery = db.db.rawQuery("select * from " + DbAdapter.PHOTO + " where hasupload=?", new String[] {0+""});
		while (rawQuery.moveToNext()) {
			photos.add(initPhoto(rawQuery));
		}
		rawQuery.close();
		return photos;
	}
	
	/**
	 * 根据关键字搜索门店列表
	 * 
	 * @return
	 */
	public static List<ModelVendors> getVendorsByKeyword(String keyword ,DbAdapter db) {
		List<ModelVendors> vendorsList = new ArrayList<ModelVendors>();
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.VENDORS + " where desc_chi like ? or desc_end like ? or phone1 like ? or phone2 like ? or phone3 like ? or address1 like ? or address2 like ? or address3 like ? order by desc_chi asc", new String[] {"%"+keyword+"%" ,"%"+keyword+"%" ,"%"+keyword+"%" ,"%"+keyword+"%" ,"%"+keyword+"%" ,"%"+keyword+"%" ,"%"+keyword+"%" ,"%"+keyword+"%"});
		while (cursor.moveToNext()) {
			vendorsList.add(initVendors(cursor));
		}
		cursor.close();
		return vendorsList;
	}
	
	/**
	 * 根据关键字搜索门店列表
	 * 
	 * @return
	 */
	public static List<ModelDetionary> getDetionaryByType(String type ,DbAdapter db) {
		List<ModelDetionary> list = new ArrayList<ModelDetionary>();
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.DETIONARY + " where type=?", new String[] {type});
		while (cursor.moveToNext()) {
			list.add(initDetionary(cursor));
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 根据关键字搜索分類
	 * 
	 * @return
	 */
	public static List<ModelModels> getModelsByModel(String model ,DbAdapter db) {
		List<ModelModels> models = new ArrayList<ModelModels>();
//		System.out.println(""+"select * from " + DbAdapter.MODELS + " where group1 = "+model);
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.MODELS + " where group1 = ?", new String[] {model});
		while (cursor.moveToNext()) {
			models.add(initModels(cursor));
		}
		cursor.close();
		return models;
	}
	
	/**
	 * 根据关键字搜索分類
	 * 
	 * @return
	 */
	public static List<ModelModels> getModelsByGroup(DbAdapter db) {
		List<ModelModels> models = new ArrayList<ModelModels>();
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.MODELS + " where group1 = ?", new String[] {"0000"});
		while (cursor.moveToNext()) {
			models.add(initModels(cursor));
		}
		cursor.close();
		return models;
	}
	
	/**
	 * 根据关键字搜索分類
	 * 
	 * @return
	 */
	public static List<ModelOther> getModelOtherByGroup(DbAdapter db) {
		List<ModelOther> models = new ArrayList<ModelOther>();
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.other + " where state=?", new String[] {"1"});
		while (cursor.moveToNext()) {
			models.add(initOther(cursor));
		}
		cursor.close();
		return models;
	}

	/**
	 * 根据关键字搜索门店列表
	 * 
	 * @return
	 */
	public static ModelVendors getVendorsByCompany(String company ,DbAdapter db) {
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.VENDORS + " where company=?", new String[] {company});
		while (cursor.moveToNext()) {
			ModelVendors v = initVendors(cursor);
			cursor.close();
			return v;
		}
		return null;
	}
	
	/**
	 * 根据关键字搜索颜色
	 * 
	 * @return
	 */
	public static List<ModelColors> getColorsByKeyword(String keyword ,DbAdapter db) {
		List<ModelColors> colorsList = new ArrayList<ModelColors>();
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.COLORS + 
				" where color like ? or code like ?", new String[] {"%"+keyword+"%" ,"%"+keyword+"%"});
		while (cursor.moveToNext()) {
			colorsList.add(initColors(cursor));
		}
		cursor.close();
		return colorsList;
	}
	
	/**
	 * 根据关键字搜索码数
	 * 
	 * @return
	 */
	public static List<ModelSizes> getSizesByKeyword(String keyword,int limit ,int offset ,DbAdapter db) {
		List<ModelSizes> sizesList = new ArrayList<ModelSizes>();
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.SIZES + " where sizes like ? group by group1 order by group1 asc limit ? offset ?", new String[] {"%"+keyword+"%" ,limit+"" ,offset+ ""});
		while (cursor.moveToNext()) {
			sizesList.add(initSizes(cursor));
		}
		cursor.close();
		return sizesList;
	}
	
	/**
	 * 根据关键字搜索颜色
	 * 
	 * @return
	 */
	public static ModelColors getColorsById(int id ,DbAdapter db) {
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.COLORS + " where id=? ", new String[] {""+id});
		while (cursor.moveToNext()) {
			return initColors(cursor);
		}
		return null;
	}
	
	/**
	 * 根据关键字搜索码数
	 * 
	 * @return
	 */
	public static ModelSizes getSizesById(int id ,DbAdapter db) {
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.SIZES + " where id=? ", new String[] {""+id});
		while (cursor.moveToNext()) {
			return initSizes(cursor);
		}
		return null;
	}



	/**
	 * 查询版本控制表
	 * 
	 * @return
	 */
	public static HashMap<String ,ModelVersion> getVersion(DbAdapter db) {
		HashMap<String ,ModelVersion> hashMap = new HashMap<String, ModelVersion>();
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.VERSION, new String[] {});
		while (cursor.moveToNext()) {
			ModelVersion v = initVersion(cursor);
			hashMap.put(v.getName(), v);
		}
		cursor.close();
		return hashMap;
	}
	
	
	/**
	 * 查询版本控制表
	 * 
	 * @return
	 */
	public static List<ModelVersion> getVersions(DbAdapter db) {
		List<ModelVersion> list = new ArrayList<ModelVersion>();
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.VERSION, new String[] {});
		while (cursor.moveToNext()) {
			list.add(initVersion(cursor));
		}
		cursor.close();
		return list;
	}

	
	/**
	 * 根据ID、表名 更改上传状态
	 * 
	 * @param goods_id
	 */
	public static void updateVersion(ModelVersion version ,DbAdapter db) {
		Cursor rawQuery = db.db.rawQuery("select * from " + DbAdapter.VERSION + " where id=?", new String[] {version.getId()+""});
		while (rawQuery.moveToNext()) {
			rawQuery.close();
			db.db.execSQL("update " + DbAdapter.VERSION + " set version = ? where id =?  ", new String[] {version.getVersion(), version.getId()+"" });
			return;
		}
		insertVersion(version, db);
	}
	
	/**
	 * 插入一条版本信息
	 * 
	 * @param photo
	 * @return
	 */
	public static boolean insertVersion(ModelVersion version ,DbAdapter db) {
		try {
			ContentValues values = new ContentValues();
			values.put("id", version.getId());
			values.put("name", version.getName());
			values.put("version", version.getVersion());
			values.put("type", version.getType());
			long insert = db.db.insert(DbAdapter.VERSION, null, values);
			if(insert==-1) 
				return false;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	/**
	 * @param cursor
	 * @return
	 */
	private static ModelVersion initVersion(Cursor cursor) {
		ModelVersion v = new ModelVersion();
		v.setId(cursor.getInt(cursor.getColumnIndex("id")));
		v.setName(cursor.getString(cursor.getColumnIndex("name")));
		v.setType(cursor.getString(cursor.getColumnIndex("type")));
		v.setVersion(cursor.getString(cursor.getColumnIndex("version")));
		return v;
	}
	
	/**
	 * @param cursor
	 * @return
	 */
	private static ModelSizes initSizes(Cursor cursor) {
		ModelSizes v = new ModelSizes();
		v.setId(cursor.getInt(cursor.getColumnIndex("id")));
		v.setSizes(cursor.getString(cursor.getColumnIndex("sizes")));
		v.setCreatedate(cursor.getString(cursor.getColumnIndex("createdate")));
		v.setGroup1(cursor.getString(cursor.getColumnIndex("group1")));
		v.setUserid(cursor.getInt(cursor.getColumnIndex("userid")));
		return v;
	}
	
	/**
	 * @param cursor
	 * @return
	 */
	private static ModelColors initColors(Cursor cursor) {
		ModelColors v = new ModelColors();
		v.setId(cursor.getInt(cursor.getColumnIndex("id")));
		v.setColor(cursor.getString(cursor.getColumnIndex("color")));
		v.setCreatedate(cursor.getString(cursor.getColumnIndex("createdate")));
		v.setUserid(cursor.getInt(cursor.getColumnIndex("userid")));
		return v;
	}
	
	/**
	 * @param cursor
	 * @return
	 */
	private static ModelVendors initVendors(Cursor cursor) {
		ModelVendors v = new ModelVendors();
		v.setId(cursor.getInt(cursor.getColumnIndex("id")));
		v.setCompany(cursor.getString(cursor.getColumnIndex("company")));
		v.setPhone1(cursor.getString(cursor.getColumnIndex("phone1")));
		v.setPhone2(cursor.getString(cursor.getColumnIndex("phone2")));
		v.setPhone3(cursor.getString(cursor.getColumnIndex("phone3")));
		v.setEmail(cursor.getString(cursor.getColumnIndex("email")));
		v.setDesc_chi(cursor.getString(cursor.getColumnIndex("desc_chi")));
		v.setDesc_end(cursor.getString(cursor.getColumnIndex("desc_end")));
		v.setAddress1(cursor.getString(cursor.getColumnIndex("address1")));
		v.setAddress2(cursor.getString(cursor.getColumnIndex("address2")));
		v.setAddress3(cursor.getString(cursor.getColumnIndex("address3")));
		v.setFaxno(cursor.getString(cursor.getColumnIndex("faxno")));
		v.setCreatedate(cursor.getString(cursor.getColumnIndex("createdate")));
		return v;
	}
	
	/**
	 * @param cursor
	 * @return
	 */
	private static ModelDetionary initDetionary(Cursor cursor) {
		ModelDetionary v = new ModelDetionary();
		v.setId(cursor.getInt(cursor.getColumnIndex("id")));
		v.setUserid(cursor.getInt(cursor.getColumnIndex("userid")));
		v.setType(cursor.getString(cursor.getColumnIndex("type")));
		v.setValue(cursor.getString(cursor.getColumnIndex("value")));
		v.setCreatedate(cursor.getString(cursor.getColumnIndex("createdate")));
		return v;
	}
	
	/**
	 * @param cursor
	 * @return
	 */
	private static ModelOther initOther(Cursor cursor) {
		ModelOther v = new ModelOther();
		v.setId(cursor.getInt(cursor.getColumnIndex("id")));
		v.setCreatedate(cursor.getString(cursor.getColumnIndex("createdate")));
		v.setName(cursor.getString(cursor.getColumnIndex("name")));
		v.setOrders(cursor.getInt(cursor.getColumnIndex("orders")));
		v.setState(cursor.getInt(cursor.getColumnIndex("state")));
		v.setUserid(cursor.getString(cursor.getColumnIndex("userid")));
		return v;
	}
	
	/**
	 * @param cursor
	 * @return
	 */
	private static ModelModels initModels(Cursor cursor) {
		ModelModels v = new ModelModels();
		v.setModel(cursor.getString(cursor.getColumnIndex("model")));
		v.setDesc_chi(cursor.getString(cursor.getColumnIndex("desc_chi")));
		v.setDesc_eng(cursor.getString(cursor.getColumnIndex("desc_eng")));
		v.setGroup1(cursor.getString(cursor.getColumnIndex("group1")));
		v.setUsers(cursor.getString(cursor.getColumnIndex("users")));
		v.setCreatedate(cursor.getString(cursor.getColumnIndex("createdate")));
		return v;
	}
	
	/**
	 * @param rawQuery2
	 * @return
	 */
	private static ModelItemnumber initItemnumber(Cursor rawQuery2) {
		ModelItemnumber itemnumber = new ModelItemnumber();
		itemnumber.setId(rawQuery2.getString(rawQuery2.getColumnIndex("id")));
		itemnumber.setPrice(rawQuery2.getDouble(rawQuery2.getColumnIndex("price")));
		itemnumber.setPurchaseid(rawQuery2.getString(rawQuery2.getColumnIndex("purchaseid")));
		itemnumber.setStyleNumber(rawQuery2.getString(rawQuery2.getColumnIndex("styleNumber")));
		itemnumber.setHarvestTime(rawQuery2.getString(rawQuery2.getColumnIndex("harvestTime")));
		return itemnumber;
	}
	
	
	/**
	 * @param rawQuery2
	 * @return
	 */
	private static ModelPurchaseList initPurchaseList(Cursor rawQuery2) {
		ModelPurchaseList purchaseList = new ModelPurchaseList();
		purchaseList.setId(rawQuery2.getString(rawQuery2.getColumnIndex("id")));
		purchaseList.setColorid(rawQuery2.getInt(rawQuery2.getColumnIndex("colorid")));
		purchaseList.setColor(rawQuery2.getString(rawQuery2.getColumnIndex("color")));
		purchaseList.setSizesid(rawQuery2.getInt(rawQuery2.getColumnIndex("sizesid")));
		purchaseList.setSizes(rawQuery2.getString(rawQuery2.getColumnIndex("sizes")));
		purchaseList.setAmount(rawQuery2.getInt(rawQuery2.getColumnIndex("amount")));
		purchaseList.setPurchaseid(rawQuery2.getString(rawQuery2.getColumnIndex("purchaseid")));
		return purchaseList;
	}
	
	/**
	 * @param rawQuery2
	 * @return
	 */
	private static ModelStyleNumberPhoto initStyleNumberPhoto(Cursor rawQuery) {
		ModelStyleNumberPhoto styleNumberPhoto = new ModelStyleNumberPhoto();
		styleNumberPhoto.setItemnumberId(rawQuery.getString(rawQuery.getColumnIndex("itemnumberId")));
		styleNumberPhoto.setPurchaseid(rawQuery.getString(rawQuery.getColumnIndex("purchaseid")));
		styleNumberPhoto.setId(rawQuery.getString(rawQuery.getColumnIndex("id")));
		styleNumberPhoto.setIndex1(rawQuery.getInt(rawQuery.getColumnIndex("index1")));
		return styleNumberPhoto;
	}
	
	/**
	 * @param rawQuery2
	 * @return
	 */
	private static ModelSizeInfo initSizeInfo(Cursor rawQuery) {
		ModelSizeInfo info = new ModelSizeInfo();
		info.setStyleNumberPhotoId(rawQuery.getString(rawQuery.getColumnIndex("styleNumberPhotoId")));
		info.setColor(rawQuery.getString(rawQuery.getColumnIndex("color")));
		info.setPurchaseid(rawQuery.getString(rawQuery.getColumnIndex("purchaseid")));
		info.setSize(rawQuery.getString(rawQuery.getColumnIndex("size")));
		info.setAmount(rawQuery.getInt(rawQuery.getColumnIndex("amount")));
		return info;
	}
	

	/**
	 * @param rawQuery
	 * @return
	 */
	private static ModelPhoto initPhoto(Cursor rawQuery) {
		ModelPhoto pic = new ModelPhoto();
		pic.setId(rawQuery.getString(rawQuery.getColumnIndex("id")));
		pic.setCreatedate(rawQuery.getString(rawQuery.getColumnIndex("createdate")));
		pic.setFilename(rawQuery.getString(rawQuery.getColumnIndex("filename")));
		pic.setHasupload(rawQuery.getInt(rawQuery.getColumnIndex("hasupload")));
		pic.setUserid(rawQuery.getInt(rawQuery.getColumnIndex("userid")));
		pic.setIndex1(rawQuery.getInt(rawQuery.getColumnIndex("index1")));
		pic.setPurchaseid(rawQuery.getString(rawQuery.getColumnIndex("purchaseid")));
		pic.setMobilepath(rawQuery.getString(rawQuery.getColumnIndex("mobilepath")));
		pic.setType(rawQuery.getInt(rawQuery.getColumnIndex("type")));
		pic.setItemnumberId(rawQuery.getString(rawQuery.getColumnIndex("itemnumberId")));
		return pic;
	}

	/**
	 * @param cursor
	 * @return
	 */
	public static ModelPurchase initModelPurchase(Cursor cursor) {
		ModelPurchase g = new ModelPurchase();
		g.setId(cursor.getString(cursor.getColumnIndex("id")));
		g.setDesc_end(cursor.getString(cursor.getColumnIndex("desc_end")));
		g.setDesc_chi(cursor.getString(cursor.getColumnIndex("desc_chi")));
		g.setCompany(cursor.getString(cursor.getColumnIndex("company")));
		g.setCreatedate(cursor.getString(cursor.getColumnIndex("createdate")));
		g.setLastupdatedate(cursor.getString(cursor.getColumnIndex("lastupdatedate")));
		g.setHasupload(cursor.getInt(cursor.getColumnIndex("hasupload")));
		g.setState(cursor.getInt(cursor.getColumnIndex("state")));
		g.setItemnumber(cursor.getString(cursor.getColumnIndex("itemnumber")));
		g.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
		g.setUserid(cursor.getInt(cursor.getColumnIndex("userid")));
		g.setOthers(cursor.getString(cursor.getColumnIndex("others")));

		g.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
		g.setDeposit(cursor.getDouble(cursor.getColumnIndex("deposit")));
		g.setHarvest_time(cursor.getString(cursor.getColumnIndex("harvest_time")));
		g.setModel_c(cursor.getString(cursor.getColumnIndex("model_c")));
		g.setModel_z(cursor.getString(cursor.getColumnIndex("model_z")));
		g.setCurrency(cursor.getString(cursor.getColumnIndex("currency")));
		g.setSeiraynumber(cursor.getString(cursor.getColumnIndex("seiraynumber")));
		
		g.setV2(cursor.getInt(cursor.getColumnIndex("v2")));
		g.setCompanyId(cursor.getString(cursor.getColumnIndex("companyId")));
		return g;
	}
	
	
	
	// =====================================V2.0需求====================================== //
	/**
	 * 查询臨時保存的采购信息-即做為草稿
	 * 
	 * @return
	 */
	public static ModelPurchase getDraftPurchase(int user_id ,DbAdapter db) {
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.PURCHASE + " where userid=? and v2=? and state=?", new String[] {user_id+"" ,"1" ,"0"});
		ModelPurchase purchase = null;
		while (cursor.moveToNext()) {
			purchase = initModelPurchase(cursor);
			break;
		}
		cursor.close();
		if(purchase!=null && purchase.getV2()==1) {
			//查詢子關聯表
			purchase.setCompanyPhoto(getCompanyPhoto(purchase.getId(), db));
			//查詢所有款號
			purchase.setItemnumbers(getItemnumberByPurchaseid(purchase.getId(), db));
		}
		return purchase;
	}
	
	/**
	 * 根據採購信息表ID查詢所有款號
	 * @param purchaseid
	 * @param db
	 * @return
	 */
	public static List<ModelItemnumber> getItemnumberByPurchaseid(String purchaseid ,DbAdapter db) {
		Cursor cursor = db.db.rawQuery("select * from " + DbAdapter.Itemnumber + " where purchaseid=?", new String[] {purchaseid});
		List<ModelItemnumber> list = new ArrayList<ModelItemnumber>();
		while (cursor.moveToNext()) {
			list.add(initItemnumber(cursor));
		}
		cursor.close();
		
		for (ModelItemnumber m : list) {
			/**
			 * 查詢款號下的所有文字信息
			 */
			Cursor rawQuery4 = db.db.rawQuery("select * from " + DbAdapter.StyleNumberPhoto + " where purchaseid=? and itemnumberId=?", new String[] {purchaseid ,m.getId()});
			while (rawQuery4.moveToNext()) {
				//查詢每個款號下的所有圖片文字信息
				m.getStyleNumberPhotos().add(initStyleNumberPhoto(rawQuery4));
			}
			rawQuery4.close();
			//查詢每個款號下的所有圖片
			List<ModelStyleNumberPhoto> styleNumberPhotos = m.getStyleNumberPhotos();
			for (ModelStyleNumberPhoto p : styleNumberPhotos) {
				Cursor rawQuery = db.db.rawQuery("select * from " + DbAdapter.PHOTO + " where purchaseid=? and itemnumberId= ? and type=? and index1=? order by index1 asc", new String[] {purchaseid ,m.getId() , "2" ,p.getIndex1()+""});
				while (rawQuery.moveToNext()) {
					p.setStyleNumberPhoto(initPhoto(rawQuery));
					break;
				}
				rawQuery.close();
				//查詢該圖片的所有SizeInfo
				Cursor cursor2 = db.db.rawQuery("select * from " + DbAdapter.SizeInfo + " where purchaseid=? and styleNumberPhotoId=?", new String[] {purchaseid ,p.getId()});
				while (cursor2.moveToNext()) {
					p.getSizeInfos().add(initSizeInfo(cursor2));
				}
				cursor2.close();
			}
		}
		return list;
	}
	
	
	/**
	 * 界面點擊了重置按鈕，清楚數據庫表草稿記錄
	 */
	public static void deleteDraftPurchase(DbAdapter db) {
		db.db.delete(DbAdapter.PURCHASE, " state=? ", new String[]{"0"});
	}
}
