package com.cn.wq.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * V2.0需求 - 款號下圖片，可多張
 * @author Terence
 *
 */
public class ModelStyleNumberPhoto  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -527819033863892853L;
	
	private String id;
	//文字部分
	private String purchaseid;
	private String itemnumberId;	//款號id
	private List<ModelSizeInfo> sizeInfos = new ArrayList<ModelSizeInfo>();	//多條Size信息
	private Integer index1;	//編號，對應ModelPhoto中的index1
	
	private ModelPhoto styleNumberPhoto;//圖片本地地址，用於加載顯示
	
	
	public Integer getIndex1() {
		return index1;
	}
	public void setIndex1(Integer index1) {
		this.index1 = index1;
	}
	public List<ModelSizeInfo> getSizeInfos() {
		return sizeInfos;
	}
	public void setSizeInfos(List<ModelSizeInfo> sizeInfos) {
		this.sizeInfos = sizeInfos;
	}
	public ModelPhoto getStyleNumberPhoto() {
		return styleNumberPhoto;
	}
	public void setStyleNumberPhoto(ModelPhoto styleNumberPhoto) {
		this.styleNumberPhoto = styleNumberPhoto;
	}
	public String getPurchaseid() {
		return purchaseid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setPurchaseid(String purchaseid) {
		this.purchaseid = purchaseid;
	}
	public String getItemnumberId() {
		return itemnumberId;
	}
	public void setItemnumberId(String itemnumberId) {
		this.itemnumberId = itemnumberId;
	}
}
