/**
 * 
 */
package com.cn.wq.entity;

import java.io.Serializable;

/**
 * @Title:  ModelUserInfo.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2015-12-30 下午2:36:30 
 * @version:  V1.0 
 * 
 */
public class ModelUserInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9124522583636411520L;
	private int id;
	private String phone;
	private String name;
	private String password;
	private int sys;
	private String createdate;
	private int action;	//1为登录，2为新增用户,3为查询用户列表，4为删除用户
	private int state;	//用户当前状态，1为正常，0为已冻结
	
	private boolean outline = false;	//當前登錄模式是否為離綫模式,默認為否
	
	
	/**
	 * @return the outline
	 */
	public boolean isOutline() {
		return outline;
	}
	/**
	 * @param outline the outline to set
	 */
	public void setOutline(boolean outline) {
		this.outline = outline;
	}
	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * @return the action
	 */
	public int getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(int action) {
		this.action = action;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @return the sys
	 */
	public int getSys() {
		return sys;
	}
	/**
	 * @param sys the sys to set
	 */
	public void setSys(int sys) {
		this.sys = sys;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
