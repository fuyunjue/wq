/**
 * 
 */
package com.cn.wq.entity;

import java.io.Serializable;

/**
 * @Title:  ModelDetionary.java
 * 
 * @Description:  TODO<字典表>
 *
 * @author Terence
 * @data:  2016-1-15 下午4:51:40 
 * @version:  V1.0 
 * 
 */
public class ModelDetionary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1866535272031999825L;
	private int id;
	private String type;
	private String value;
	private String createdate;
	private int userid;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
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
