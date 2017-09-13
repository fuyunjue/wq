/**
 * 
 */
package com.cn.wq.entity;

import java.io.Serializable;

/**
 * 
 * @Title:  ModelSizes.java
 * 
 * @Description:  TODO<码数表>
 *
 * @author Terence
 * @data:  2016-1-7 下午10:13:41 
 * @version:  V1.0 
 *
 */
public class ModelSizes implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -474726584832287690L;
	private int id;
	private String sizes;
	private String createdate;
	private String group1;
	private int userid;
	
	/**
	 * @return the group1
	 */
	public String getGroup1() {
		return group1;
	}
	/**
	 * @param group1 the group1 to set
	 */
	public void setGroup1(String group1) {
		this.group1 = group1;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the sizes
	 */
	public String getSizes() {
		return sizes;
	}
	/**
	 * @param color the sizes to set
	 */
	public void setSizes(String sizes) {
		this.sizes = sizes;
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
	
	
}
