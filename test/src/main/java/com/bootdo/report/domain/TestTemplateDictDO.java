package com.bootdo.report.domain;

import java.io.Serializable;



/**
 * 

* <p>Title: TestReportDO</p>  

* <p>Description: 测试报告字典</p>  

* @author   

* @date 2019年4月28日
 */
public class TestTemplateDictDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键自增
	private Integer id;
	//模板id
	private Integer templateId;
	//标签名称
	private String labelName;
	//标签关键字
	private String labelKey;
	//数据类型:1文本2图片3文件
	private Integer dateType;
	//创建人
	private String createBy;
	//创建时间
	private String createDate;
	//是否删除
	private Integer isDelete;
	
	private String value;
	
	private float picWidth; //图片默认宽度
	 
	private float picHeight;//图片默认长度
	
	private Integer startPage;//pdf开始页面
	
	private Integer endPage;//结束页面
	
	private String outputFilePath;

	public final String getOutputFilePath() {
		return outputFilePath;
	}
	public final void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * 设置：主键自增
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：主键自增
	 */
	public Integer getId() {
		return id;
	}
	public Integer getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}
	public String getLabelName() {
		return labelName;
	}
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getLabelKey() {
		return labelKey;
	}
	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}
	public Integer getDateType() {
		return dateType;
	}
	public void setDateType(Integer dateType) {
		this.dateType = dateType;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	public final float getPicWidth() {
		return picWidth;
	}
	public final void setPicWidth(float picWidth) {
		this.picWidth = picWidth;
	}
	public final float getPicHeight() {
		return picHeight;
	}
	public final void setPicHeight(float picHeight) {
		this.picHeight = picHeight;
	}
	public final Integer getStartPage() {
		return startPage;
	}
	public final void setStartPage(Integer startPage) {
		this.startPage = startPage;
	}
	public final Integer getEndPage() {
		return endPage;
	}
	public final void setEndPage(Integer endPage) {
		this.endPage = endPage;
	}
	public final String getValue() {
		return value;
	}
	
}
