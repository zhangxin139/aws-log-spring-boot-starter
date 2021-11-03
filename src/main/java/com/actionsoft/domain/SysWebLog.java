package com.actionsoft.domain;

import java.util.Date;

/**
 * Web的日志记录
 */

public class SysWebLog {
	private Integer id;
	/**
	 * 根路径
	 */
	private String basePath;
	/**
	 * URI
	 */
	private String uri;

	/**
	 * URL
	 */
	private String url;

	/**
	 * 请求类型
	 */
	private String method;

	/**
	 * 请求参数
	 */
	private String parameter;

	/**
	 * 返回结果
	 */
	private String result;

	/**
	 * 异常信息
	 */
	private String errMessage;
	/**
	 * 异常信息
	 */
	private String errInfo;
	/**
	 * 操作描述
	 */
	private String description;

	/**
	 * IP地址
	 */
	private String ip;

	/**
	 * 操作类型(预留字段)
	 */
	private String operateType;
	/**
	 * 消耗时间
	 */
	private Long spendTime;
	/**
	 * 创建时间
	 */
	private Date createTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	public String getErrInfo() {
		return errInfo;
	}

	public void setErrInfo(String errInfo) {
		this.errInfo = errInfo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public Long getSpendTime() {
		return spendTime;
	}

	public void setSpendTime(Long spendTime) {
		this.spendTime = spendTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "SysWebLog{" + "id=" + id + ", basePath='" + basePath + '\'' + ", uri='" + uri + '\'' + ", url='" + url + '\'' + ", method='" + method + '\'' + ", parameter='" + parameter + '\'' + ", result='" + result + '\'' + ", errMessage='" + errMessage + '\'' + ", errInfo='" + errInfo + '\'' + ", description='" + description + '\'' + ", ip='" + ip + '\'' + ", operateType='" + operateType + '\'' + ", spendTime=" + spendTime + ", createTime=" + createTime + '}';
	}

}