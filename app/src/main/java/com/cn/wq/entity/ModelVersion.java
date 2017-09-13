package com.cn.wq.entity;

import java.io.Serializable;

/**
 * 
 * @author Terence
 *
 */
public class ModelVersion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5157026255509059389L;
	private int id;
	private String name;
	private String version;
	private String type;
	private String msg;
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
