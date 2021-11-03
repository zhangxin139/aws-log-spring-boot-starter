package com.actionsoft.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;

import com.actionsoft.domain.SysWebLog;

public class DbUtils {



	//	public static final String CREATE_SYS_WEBLOG = "CREATE TABLE IF NOT EXISTS sys_weblog (id int(32) NOT NULL auto_increment comment '自增id' primary key, base_path varchar(255) null comment '根路径') ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	public static final String CREATE_SYS_WEBLOG = "CREATE TABLE IF NOT EXISTS %s (id int(32) NOT NULL auto_increment comment '自增id' primary key, base_path varchar(255) null comment '根路径',uri varchar(255) null comment 'URI',url varchar(255) null comment 'URL',method varchar(255) null comment '请求类型',parameter varchar(2000) null comment '请求参数',result varchar(2000) null comment '返回结果',err_message varchar(255) null comment '返回结果',err_info text null comment '异常信息',description varchar(255) null comment '操作描述',ip varchar(255) null comment 'IP地址',operate_type varchar(255) null comment '操作类型',spend_time int null comment '消耗时间',create_time datetime null) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	public static final String INSERT_NORMAL_WEB_LOG = "insert into sys_weblog(base_path,uri,url,method,parameter,result,description,ip,spend_time,create_time) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String INSERT_ERR_WEB_LOG = "insert into sys_weblog(base_path,uri,url,method,parameter,err_message,err_info,description,ip,spend_time,create_time) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String INSERT_WEB_LOG = "insert into %s(base_path,uri,url,method,parameter,result,err_message,err_info,description,ip,spend_time,create_time) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

	public static void storeWebLog(Connection connection, SysWebLog webLog,String tableName) {
		try {
			QueryRunner queryRunner = new QueryRunner();
			queryRunner.update(connection, String.format(INSERT_WEB_LOG,tableName), webLog.getBasePath(), webLog.getUri(), webLog.getUrl(), webLog.getMethod(), webLog.getParameter(), webLog.getResult(), webLog.getErrMessage(), webLog.getErrInfo(), webLog.getDescription(), webLog.getIp(), webLog.getSpendTime(), webLog.getCreateTime());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
