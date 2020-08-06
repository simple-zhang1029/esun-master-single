package esun.dbhelper.utils;



import java.util.HashMap;
import java.util.Map;

/**
 * 返回结果工具类
 * @author xiaoliebin
 */
public class ResultUtil extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public ResultUtil() {
		put("code", 200);
		put("msg", "success");
	}
	
	public static ResultUtil error() {
		return error(500, "未知异常，请联系管理员或检查sql语句");
	}
	
	public static ResultUtil error(String msg) {
		return error(500, msg);
	}
	
	public static ResultUtil error(int code, String msg) {
		ResultUtil resultUtil = new ResultUtil();
		resultUtil.put("code", code);
		resultUtil.put("msg", msg);
		return resultUtil;
	}

	public static ResultUtil ok(String msg) {
		ResultUtil resultUtil = new ResultUtil();
		resultUtil.put("msg", msg);
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
}
