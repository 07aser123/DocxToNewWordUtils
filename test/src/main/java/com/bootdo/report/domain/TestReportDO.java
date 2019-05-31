package com.bootdo.report.domain;

import java.io.Serializable;



/**
 * 

* <p>Title: TestReportDO</p>  

* <p>Description: 测试报告</p>  

* @author   

* @date 2019年4月28日
 */
public class TestReportDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键自增
	private Integer id;
	private String name;

	//测试任务id
	private Integer testTaskId;
	
	//测试任务
	private String taskName;
	
	private Integer methodId;
	//报告编号
	private String reportNo;
	//模板id
	private Integer templateId;
	//文件名称
	private String fileName;
	//文件路径
	private String filePath;
	//状态
	private Integer state;//报告状态：0初始化1待审核2审核成功3审核失败
	//客户id
	private Integer clientId;
	//创建人
	private String createBy;
	//创建时间
	private String createDate;
	private String updateBy;

	//是否删除
	private Integer isDelete;
	private String methodName;
	private String templateName;
	
	private String updateDate;

	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public Integer getMethodId() {
		return methodId;
	}
	public void setMethodId(Integer methodId) {
		this.methodId = methodId;
	}

	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
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
	public Integer getTestTaskId() {
		return testTaskId;
	}
	public void setTestTaskId(Integer testTaskId) {
		this.testTaskId = testTaskId;
	}
	public String getReportNo() {
		return reportNo;
	}
	public void setReportNo(String reportNo) {
		this.reportNo = reportNo;
	}
	public Integer getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getClientId() {
		return clientId;
	}
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public final String getTaskName() {
		return taskName;
	}
	public final void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
