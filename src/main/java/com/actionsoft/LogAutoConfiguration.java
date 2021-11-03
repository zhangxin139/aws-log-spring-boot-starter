package com.actionsoft;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.actionsoft.aop.WebLogAspect;
import com.actionsoft.util.DbUtils;

/**
 * 日志自动配置
 * 启动条件： 存在web环境
 * 注解 EnableAutoConfigureLog
 */
@EnableAsync
@Configuration
@ConditionalOnWebApplication
public class LogAutoConfiguration {
	@Value("${aws.log.table-name:sys_weblog}") private String tableName;

	@Bean
	@ConditionalOnMissingBean
	public WebLogAspect sysLogAspect(DataSource dataSource) {
		try (Connection connection = dataSource.getConnection()) {
			QueryRunner queryRunner = new QueryRunner();
			queryRunner.execute(connection, String.format(DbUtils.CREATE_SYS_WEBLOG, tableName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new WebLogAspect();
	}

}
