package com.example.utils;



import java.util.HashMap;
import java.util.Map;

/**
 * 返回结果工具类
 * @author xiaoliebin
 */
public class ResultUtil extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public ResultUtil() {
		put("code", 10000);
		put("msg", "success");
		put("methodName",ResultUtil.class.getName());
	}

	public static ResultUtil error() {
		return error(10001, "未知异常，请联系管理员或检查sql语句");
	}

	public static ResultUtil error(String msg) {
		return error(10001, msg);
	}

	public static ResultUtil error(int code, String msg) {
		ResultUtil resultUtil = new ResultUtil();
		resultUtil.put("code", code);
		resultUtil.put("msg", msg);
		return resultUtil;
	}
	public static ResultUtil error(int code, String msg,String className) {
		ResultUtil resultUtil = new ResultUtil();
		resultUtil.put("code", code);
		resultUtil.put("msg", msg);
		resultUtil.put("methodName",className);
		return resultUtil;
	}

	public static ResultUtil error(String msg,String className) {
		ResultUtil resultUtil = new ResultUtil();
		resultUtil.put("code", 10001);
		resultUtil.put("msg", msg);
		resultUtil.put("methodName",className);
		return resultUtil;
	}

	public static ResultUtil ok(String msg) {
		ResultUtil resultUtil = new ResultUtil();
		resultUtil.put("msg", msg);
		return resultUtil;
	}
	public static ResultUtil ok(String msg,String methodName) {
		ResultUtil resultUtil = new ResultUtil();
		resultUtil.put("msg", msg);
		resultUtil.put("methodName",methodName);
		return resultUtil;
	}

	public static ResultUtil ok(Map<String, Object> map) {
		ResultUtil resultUtil = new ResultUtil();
		resultUtil.putAll(map);
		return resultUtil;
	}

	public static ResultUtil ok() {
		return new ResultUtil();
	}

	@Override
	public ResultUtil put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public ResultUtil setData(Object data){
		super.put("data",data);
		return this;
	}
}
