package esun.wharf.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author test
 */
public class TimeUtil {
	/**
	 * 根据当前时间校验是否超时
	 * @param start
	 * @param hours
	 * @return
	 */
	public static boolean checkTimeOut(String start,int hours){
		SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");
		String now =dateFormat.format(new Date());
		return  checkTimeOut(start,now,hours);
	}
	/**
	 * 	根据设定的开始结束时间校验是否超时
	 */
	public static boolean checkTimeOut(String start,String end,int hours){
		SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");
		Date startDate = new Date();
		Date endDate = new Date();
		int duration=0;
		try {
			startDate=dateFormat.parse(start);
			endDate=dateFormat.parse(end);
			duration=60*60*1000*hours;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return checkTimeOut(startDate,endDate,duration);
	}

	/**
	 * 校验超时
	 * @param start
	 * @param end
	 * @param duration
	 * @return
	 */
	public static boolean checkTimeOut(Date start,Date end,int duration){
		//间隔时间
		 long interval=start.getTime()-end.getTime();
		 if(interval>duration){
		 	return true;
		 }
		 return false;
	}
}
