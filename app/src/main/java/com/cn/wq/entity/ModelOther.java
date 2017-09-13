/**
 * 
 */
package com.cn.wq.entity;

import java.io.Serializable;

/**
 * @Title:  ModelColors.java
 * 
 * @Description:  TODO<颜色表>
 *
 * @author Terence
 * @data:  2016-1-1 下午1:37:35 
 * @version:  V1.0 
 * 
 */
public class ModelOther implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5627292797972060082L;
	private int id;	//自增id
	private String name;	//中文值
	private int orders;	//顯示順序，由小到大
	private int state;	//是否啟動，1顯示，0隱藏
	private String createdate;	//創建日期
	private String userid;	//創建用戶
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrders() {
		return orders;
	}
	public void setOrders(int orders) {
		this.orders = orders;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	
}
