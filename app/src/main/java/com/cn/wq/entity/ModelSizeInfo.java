package com.cn.wq.entity;

import java.io.Serializable;

/**
 * 每個Size的信息。
 * 每個款號下的每張圖片-可以包含多個size
 * @author Terence
 *
 */
public class ModelSizeInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7917546040713226324L;
	
	private String styleNumberPhotoId;
	private String purchaseid;
	private String color;	//顏色
	private String size;	//碼數
	private Integer amount;	//數量
	
	public String getStyleNumberPhotoId() {
		return styleNumberPhotoId;
	}
	public void setStyleNumberPhotoId(String styleNumberPhotoId) {
		this.styleNumberPhotoId = styleNumberPhotoId;
	}
	public String getPurchaseid() {
		return purchaseid;
	}
	public void setPurchaseid(String purchaseid) {
		this.purchaseid = purchaseid;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
}
