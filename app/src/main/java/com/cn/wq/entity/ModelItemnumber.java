package com.cn.wq.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * V2.0需求 - 款號item
 * @author Terence
 *
 */
public class ModelItemnumber  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8654613741497214661L;

	
	private String id;
	private String styleNumber;	//款號
	private String purchaseid;

	private double price;	//入价
	private String harvestTime;	//預計收貨日期
	
	private List<ModelStyleNumberPhoto> styleNumberPhotos = new ArrayList<ModelStyleNumberPhoto>();

	
	
	public String getPurchaseid() {
		return purchaseid;
	}

	public void setPurchaseid(String purchaseid) {
		this.purchaseid = purchaseid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStyleNumber() {
		return styleNumber;
	}

	public void setStyleNumber(String styleNumber) {
		this.styleNumber = styleNumber;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getHarvestTime() {
		return harvestTime;
	}

	public void setHarvestTime(String harvestTime) {
		this.harvestTime = harvestTime;
	}

	public List<ModelStyleNumberPhoto> getStyleNumberPhotos() {
		return styleNumberPhotos;
	}

	public void setStyleNumberPhotos(List<ModelStyleNumberPhoto> styleNumberPhotos) {
		this.styleNumberPhotos = styleNumberPhotos;
	}
}
