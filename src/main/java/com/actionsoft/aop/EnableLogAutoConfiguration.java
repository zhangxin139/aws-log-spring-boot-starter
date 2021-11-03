package com.actionsoft.aop;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import com.actionsoft.LogAutoConfiguration;

/**
 * 启用 Log 的配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LogAutoConfiguration.class)
@Documented
@Inherited
public @interface EnableLogAutoConfiguration {
}
