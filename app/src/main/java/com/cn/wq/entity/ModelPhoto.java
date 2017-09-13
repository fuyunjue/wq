/**
 * 
 */
package com.cn.wq.entity;

import java.io.Serializable;

/**
 * 
 * @Title:  ModelPhoto.java
 * 
 * @Description:  TODO<采购表图片列表>
 *
 * @author Terence
 * @data:  2016-1-1 下午1:32:35 
 * @version:  V1.0 
 *
 */
public class ModelPhoto implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -527819033863892853L;
	private String id;
	private String filename;
	private int index1;
	private int hasupload;
	private String purchaseid;
	private String localpath;
	private String mobilepath;
	private String fileContent;
	private String createdate;
	private int userid;
	
	/**
	 * V2.0需求
	 * 0表示V1.0需求，新增字段後給默認值
	 * 1表示檔口，命名規則：companyd_yyyyMMddHHmmSSS.jpg   
	 * 2表示V2.0需求，各款號下不同碼數的的圖片，命名規則：與V1.0相同，第一個“_”符號左邊公司名稱改為V2.0中隨機生成的companyid（companyd_kh_yyyyMMddHHmmSSS.jpg）
	 * 
	 */
	private int type;	//类型
	private String itemnumberId;	//款號ID
	
	
	public String getItemnumberId() {
		return itemnumberId;
	}
	public void setItemnumberId(String itemnumberId) {
		this.itemnumberId = itemnumberId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @return the userid
	 */
	public int getUserid() {
		return userid;
	}
	/**
	 * @param userid the userid to set
	 */
	public void setUserid(int userid) {
		this.userid = userid;
	}
	/**
	 * @return the createdate
	 */
	public String getCreatedate() {
		return createdate;
	}
	/**
	 * @param createdate the createdate to set
	 */
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	/**
	 * @return the fileContent
	 */
	public String getFileContent() {
		return fileContent;
	}
	/**
	 * @param fileContent the fileContent to set
	 */
	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the index1
	 */
	public int getIndex1() {
		return index1;
	}
	/**
	 * @param index1 the index to set
	 */
	public void setIndex1(int index1) {
		this.index1 = index1;
	}
	/**
	 * @return the hasupload
	 */
	public int getHasupload() {
		return hasupload;
	}
	/**
	 * @param hasupload the hasupload to set
	 */
	public void setHasupload(int hasupload) {
		this.hasupload = hasupload;
	}
	/**
	 * @return the purchaseid
	 */
	public String getPurchaseid() {
		return purchaseid;
	}
	/**
	 * @param purchaseid the purchaseid to set
	 */
	public void setPurchaseid(String purchaseid) {
		this.purchaseid = purchaseid;
	}
	/**
	 * @return the localpath
	 */
	public String getLocalpath() {
		return localpath;
	}
	/**
	 * @param localpath the localpath to set
	 */
	public void setLocalpath(String localpath) {
		this.localpath = localpath;
	}
	/**
	 * @return the mobilepath
	 */
	public String getMobilepath() {
		return mobilepath;
	}
	/**
	 * @param mobilepath the mobilepath to set
	 */
	public void setMobilepath(String mobilepath) {
		this.mobilepath = mobilepath;
	}
}
