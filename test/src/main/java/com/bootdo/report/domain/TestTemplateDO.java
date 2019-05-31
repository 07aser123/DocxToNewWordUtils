package com.bootdo.report.domain;

import java.io.Serializable;



/**
 * 

* <p>Title: TestTemplateDO</p>  

* <p>Description:测试模板 </p>  

* @author   

* @date 2019年4月28日
 */
public class TestTemplateDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键自增
	private Integer id;
	//检测方法id
	private Integer testMethodId;
	//检测方法名称
	private String testMethodName;
	//模板名称
	private String name;
	//文件路径
	private String path;
	//创建人
	private String createBy;
	//创建时间
	private String createDate;
	//是否删除
	private Integer isDelete;
	private String updateBy;
	
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTestMethodId() {
		return testMethodId;
	}
	public void setTestMethodId(Integer testMethodId) {
		this.testMethodId = testMethodId;
	}
	public String getTestMethodName() {
		return testMethodName;
	}
	public void setTestMethodName(String testMethodName) {
		this.testMethodName = testMethodName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
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
	


}
