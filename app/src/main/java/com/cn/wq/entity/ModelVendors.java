/**
 * 
 */
package com.cn.wq.entity;

import java.io.Serializable;

/**
 * 
 * @Title:  ModelVendors.java
 * 
 * @Description:  TODO<门店信息>
 *
 * @author Terence
 * @data:  2016-1-1 下午1:35:50 
 * @version:  V1.0 
 *
 */
public class ModelVendors implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2002696313551032325L;
	private int id;
	private String company;
	private String desc_end;
	private String desc_chi;
	private String address1;
	private String address2;
	private String address3;
	private String email;
	private String phone1;
	private String phone2;
	private String phone3;
	private String faxno;
	private String createdate;
	public String getCreatedate() {
		return createdate;
	}
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
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}
	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	/**
	 * @return the desc_end
	 */
	public String getDesc_end() {
		return desc_end;
	}
	/**
	 * @param desc_end the desc_end to set
	 */
	public void setDesc_end(String desc_end) {
		this.desc_end = desc_end;
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
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}
	/**
	 * @param address1 the address1 to set
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}
	/**
	 * @param address2 the address2 to set
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	/**
	 * @return the address3
	 */
	public String getAddress3() {
		return address3;
	}
	/**
	 * @param address3 the address3 to set
	 */
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the phone1
	 */
	public String getPhone1() {
		return phone1;
	}
	/**
	 * @param phone1 the phone1 to set
	 */
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
	/**
	 * @return the phone2
	 */
	public String getPhone2() {
		return phone2;
	}
	/**
	 * @param phone2 the phone2 to set
	 */
	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}
	/**
	 * @return the phone3
	 */
	public String getPhone3() {
		return phone3;
	}
	/**
	 * @param phone3 the phone3 to set
	 */
	public void setPhone3(String phone3) {
		this.phone3 = phone3;
	}
	/**
	 * @return the faxno
	 */
	public String getFaxno() {
		return faxno;
	}
	/**
	 * @param faxno the faxno to set
	 */
	public void setFaxno(String faxno) {
		this.faxno = faxno;
	}
}
