/**
 * 
 */
package com.cn.wq.entity;

import java.io.Serializable;

/**
 * @Title: ModelPurchaseList.java
 * 
 * @Description: TODO<采购信息-颜色等规格表>
 * 
 * @author Terence
 * @data: 2016-1-1 下午1:40:09
 * @version: V1.0
 * 
 */
public class ModelPurchaseList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7204124227872250144L;

	private String id;
	private int colorid;
	private String color;
	private int sizesid;
	private String sizes;
	private String purchaseid;
	private int amount;

	
	/**
	 * @return the sizesid
	 */
	public int getSizesid() {
		return sizesid;
	}

	/**
	 * @param sizesid the sizesid to set
	 */
	public void setSizesid(int sizesid) {
		this.sizesid = sizesid;
	}

	/**
	 * @return the sizes
	 */
	public String getSizes() {
		return sizes;
	}

	/**
	 * @param sizes the sizes to set
	 */
	public void setSizes(String sizes) {
		this.sizes = sizes;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the colorid
	 */
	public int getColorid() {
		return colorid;
	}

	/**
	 * @param colorid
	 *            the colorid to set
	 */
	public void setColorid(int colorid) {
		this.colorid = colorid;
	}

	/**
	 * @return the purchaseid
	 */
	public String getPurchaseid() {
		return purchaseid;
	}

	/**
	 * @param purchaseid
	 *            the purchaseid to set
	 */
	public void setPurchaseid(String purchaseid) {
		this.purchaseid = purchaseid;
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}
}
