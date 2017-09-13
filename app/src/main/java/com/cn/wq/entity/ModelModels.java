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
public class ModelModels implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6338912719637660304L;
	private String model;
	private String desc_eng;
	private String desc_chi;
	private String group1;
	private String users;
	private String createdate;
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * @return the desc_eng
	 */
	public String getDesc_eng() {
		return desc_eng;
	}
	/**
	 * @param desc_eng the desc_eng to set
	 */
	public void setDesc_eng(String desc_eng) {
		this.desc_eng = desc_eng;
	}
	/**
	 * @return the desc_chi
	 */
	public String getDesc_chi() {
		return desc_chi;
	}
	/**
	 * @param desc_chi the desc_chi to set
	 */
	public void setDesc_chi(String desc_chi) {
		this.desc_chi = desc_chi;
	}
	/**
	 * @return the group
	 */
	public String getGroup1() {
		return group1;
	}
	/**
	 * @param group the group to set
	 */
	public void setGroup1(String group1) {
		this.group1 = group1;
	}
	/**
	 * @return the users
	 */
	public String getUsers() {
		return users;
	}
	/**
	 * @param users the users to set
	 */
	public void setUsers(String users) {
		this.users = users;
	}
}
