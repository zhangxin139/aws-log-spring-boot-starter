package com.actionsoft.aop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.actionsoft.domain.SysWebLog;
import com.actionsoft.util.DbUtils;
import com.actionsoft.util.IpUtil;
import com.alibaba.fastjson.JSON;

@Component
@Aspect
public class WebLogAspect {
	@Autowired DataSource dataSource;

	@Value("${aws.log.table-name:sys_weblog}") private String tableName;

	private static final ThreadLocal<SysWebLog> THREAD_LOCAL = new ThreadLocal<>();

	/***
	 * 定义controller切入点拦截规则，拦截SysLog注解的方法
	 */
	@Pointcut("@annotation(com.actionsoft.aop.AwsLog)")
	public void webLog() {
	}

	private SysWebLog get() {
		SysWebLog sysLog = THREAD_LOCAL.get();
		if (sysLog == null) {
			return new SysWebLog();
		}
		return sysLog;
	}

	private void tryCatch(Consumer<String> consumer) {
		try {
			consumer.accept("");
		} catch (Exception e) {
			e.printStackTrace();
			THREAD_LOCAL.remove();
		}
	}

	@Before(value = "webLog()")
	public void recordLog(JoinPoint joinPoint) {
		tryCatch((val) -> {
			SysWebLog sysLog = get();
			sysLog.setSpendTime(System.currentTimeMillis());
			// 开始时间
			sysLog.setCreateTime(new Date());
			HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
			String url = request.getRequestURL().toString();
			// 设置请求的uri
			String uri = request.getRequestURI();
			sysLog.setUri(uri);
			sysLog.setUrl(url);
			// http://ip:port/
			if (url.endsWith(uri)) {
				sysLog.setBasePath(url.substring(0, url.length() - uri.length()));
			}

			sysLog.setIp(IpUtil.getIpAddr(request));
			// 获取方法
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			// 获取类的名称
			String targetClassName = joinPoint.getTarget().getClass().getName();
			Method method = signature.getMethod();
			sysLog.setMethod(targetClassName + "." + method.getName());
			//{"key_参数的名称":"value_参数的值"}
			sysLog.setParameter(getMethodParameter(method, joinPoint.getArgs()).toString());
			sysLog.setDescription(method.getAnnotation(AwsLog.class).value());
			THREAD_LOCAL.set(sysLog);
		});
	}

	/**
	 * 返回通知
	 *
	 * @param ret 正确返回结果
	 */
	@AfterReturning(returning = "ret", pointcut = "webLog()")
	public void doAfterReturning(Object ret) {
		try {
			SysWebLog sysLog = get();
			sysLog.setResult(JSON.toJSONString(ret));
			sysLog.setSpendTime(System.currentTimeMillis() - sysLog.getSpendTime());

			try (Connection connection = dataSource.getConnection()) {
				DbUtils.storeWebLog(connection, sysLog,tableName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			THREAD_LOCAL.remove();
		} catch (Exception e) {
			THREAD_LOCAL.remove();
			e.printStackTrace();
		}

	}

	/**
	 * 异常通知
	 */
	@AfterThrowing(pointcut = "webLog()", throwing = "e")
	public void doAfterThrowable(Throwable e) {
		try {
			SysWebLog sysLog = get();
			sysLog.setSpendTime(System.currentTimeMillis() - sysLog.getSpendTime());
			sysLog.setErrMessage(e.getMessage());
			sysLog.setErrInfo(getStackTraceInfo(e));
			try (Connection connection = dataSource.getConnection()) {
				DbUtils.storeWebLog(connection, sysLog,tableName);
			} catch (Exception d) {
				d.printStackTrace();
			}
			THREAD_LOCAL.remove();
		} catch (Exception s) {
			THREAD_LOCAL.remove();
			s.printStackTrace();
		}
	}

	/**
	 * 获取方法的执行参数
	 *
	 * @param method /
	 * @param args   /
	 * @return {"key_参数的名称":"value_参数的值"}
	 */
	private Object getMethodParameter(Method method, Object[] args) {
		Map<String, Object> methodParametersWithValues = new HashMap<>();
		LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		// 方法的形参名称
		String[] parameterNames = localVariableTableParameterNameDiscoverer.getParameterNames(method);
		for (int i = 0; i < Objects.requireNonNull(parameterNames).length; i++) {
			if ("password".equals(parameterNames[i]) || "file".equals(parameterNames[i])) {
				methodParametersWithValues.put(parameterNames[i], "受限的支持类型");
			} else {
				methodParametersWithValues.put(parameterNames[i], args[i]);
			}
		}
		return methodParametersWithValues;
	}

	/***
	 * 获取操作信息
	 * @return /
	 */
	public static String getMethodDescription(JoinPoint point) {
		try {
			// 获取连接点目标类名
			String targetName = point.getTarget().getClass().getName();
			// 获取连接点签名的方法名
			String methodName = point.getSignature().getName();

			//获取连接点参数
			Object[] args = point.getArgs();
			//根据连接点类的名字获取指定类
			Class<?> targetClass = Class.forName(targetName);
			//获取类里面的方法
			Method[] methods = targetClass.getMethods();
			String description = "";
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					Class[] clazzs = method.getParameterTypes();
					if (clazzs.length == args.length) {
						description = method.getAnnotation(AwsLog.class).value();
						break;
					}
				}
			}
			return description;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 获取e.printStackTrace() 的具体信息，赋值给String 变量，并返回
	 *
	 * @param e Exception
	 * @return e.printStackTrace() 中 的信息
	 */
	public static String getStackTraceInfo(Throwable e) {
		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
			e.printStackTrace(pw);//将出错的栈信息输出到printWriter中
			pw.flush();
			sw.flush();
			return sw.toString();
		} catch (Exception ex) {
			return "发生错误,保存失败";
		}
	}

}
