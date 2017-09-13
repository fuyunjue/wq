/**
 * 
 */
package com.cn.wq.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @Title:  ModelPurchase.java
 * 
 * @Description:  TODO<采购信息表>
 *
 * @author Terence
 * @data:  2016-1-1 下午1:33:31 
 * @version:  V1.0 
 *
 */
public class ModelPurchase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3898716126125038833L;
	
	private String id;
	private int userid;	//创建人ID
	private String createdate;	//创建时间
	private String company;	//门店ID
	private double price;	//入价
	private double deposit;	//定金
	private String harvest_time;	//預計收貨日期
	private String remark;	//備註
	private String model_z;	//分類id
	private String model_c;	//分類id
	private String itemnumber;	//款号
	private int hasupload;	//是否已提交，1为已提交，0为否
	private String desc_end;
	private String others;	//other表id，以逗號,隔開
	private String lastupdatedate;
	private int state;	//是否已完成，1为已完成，0为未完成，顯示為綠色

	private String desc_chi;
	
	private String currency;	//貨幣類型
	private String seiraynumber;	//流水號
	
	
	private List<ModelPurchaseList> purchaseLists = new ArrayList<ModelPurchaseList>();
	private List<ModelPhoto> pics = new ArrayList<ModelPhoto>();
	
	
	
	
	/**
	 * V2.0需求
	 * @return
	 */
	private Integer v2;	//該字段標識為V2.0需求，V1的默認為0，V2為1，因兩版本字段結構不同，需區分開
	//V2.0需求實體類直接關聯另一張表，方便開發、升級
	private String companyId;	//檔口id-對應一張圖片
	/**
	 * 檔口圖片信息
	 */
	private ModelPhoto companyPhoto;//圖片本地地址，用於加載顯示
	private List<ModelItemnumber> itemnumbers = new ArrayList<ModelItemnumber>();	//款號列表，一條採購記錄可以添加多個款號
	
	

	
	public String getCompanyId() {
		return companyId;
	}
	public ModelPhoto getCompanyPhoto() {
		return companyPhoto;
	}
	public void setCompanyPhoto(ModelPhoto companyPhoto) {
		this.companyPhoto = companyPhoto;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public List<ModelItemnumber> getItemnumbers() {
		return itemnumbers;
	}
	public void setItemnumbers(List<ModelItemnumber> itemnumbers) {
		this.itemnumbers = itemnumbers;
	}
	public Integer getV2() {
		return v2;
	}
	public void setV2(Integer v2) {
		this.v2 = v2;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getLastupdatedate() {
		return lastupdatedate;
	}
	public void setLastupdatedate(String lastupdatedate) {
		this.lastupdatedate = lastupdatedate;
	}
	public String getOthers() {
		return others;
	}
	public void setOthers(String others) {
		this.others = others;
	}
	/**
	 * @return the seiraynumber
	 */
	public String getSeiraynumber() {
		return seiraynumber;
	}
	/**
	 * @param seiraymumber the seiraynumber to set
	 */
	public void setSeiraynumber(String seiraynumber) {
		this.seiraynumber = seiraynumber;
	}
	
	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/**
	 * @return the model_z
	 */
	public String getModel_z() {
		return model_z;
	}
	/**
	 * @param model_z the model_z to set
	 */
	public void setModel_z(String model_z) {
		this.model_z = model_z;
	}
	/**
	 * @return the model_c
	 */
	public String getModel_c() {
		return model_c;
	}
	/**
	 * @param model_c the model_c to set
	 */
	public void setModel_c(String model_c) {
		this.model_c = model_c;
	}
	/**
	 * @return the harvest_time
	 */
	public String getHarvest_time() {
		return harvest_time;
	}
	/**
	 * @param harvest_time the harvest_time to set
	 */
	public void setHarvest_time(String harvest_time) {
		this.harvest_time = harvest_time;
	}
	/**
	 * @return the deposit
	 */
	public double getDeposit() {
		return deposit;
	}
	/**
	 * @param deposit the deposit to set
	 */
	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}
	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the purchaseLists
	 */
	public List<ModelPurchaseList> getPurchaseLists() {
		return purchaseLists;
	}

	/**
	 * @param purchaseLists the purchaseLists to set
	 */
	public void setPurchaseLists(List<ModelPurchaseList> purchaseLists) {
		this.purchaseLists = purchaseLists;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the itemnumber
	 */
	public String getItemnumber() {
		return itemnumber;
	}

	/**
	 * @param itemnumber the itemnumber to set
	 */
	public void setItemnumber(String itemnumber) {
		this.itemnumber = itemnumber;
	}

	/**
	 * @return the hasupload
	 */
	public int getHasupload() {
		return hasupload;
	}

	/**
	 * @param hasupload the hasupload to set
	 */
	public void setHasupload(int hasupload) {
		this.hasupload = hasupload;
	}

	/**
	 * @return the pics
	 */
	public List<ModelPhoto> getPics() {
		return pics;
	}

	/**
	 * @param pics the pics to set
	 */
	public void setPics(List<ModelPhoto> pics) {
		this.pics = pics;
	}
	
}
