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
public class ModelColors implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7442269932127040046L;
	
	private int id;
	private String color;
	private String code;
	private String createdate;
	private int userid;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
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
