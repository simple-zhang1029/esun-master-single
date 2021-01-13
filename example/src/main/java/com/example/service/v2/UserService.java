package com.example.service.v2;

import com.example.entity.UserMstr;
import com.example.utils.ResultUtil;
import com.netflix.client.http.HttpResponse;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author test
 */
public interface UserService {
	/**
	 * 登入
	 * @param userMstr 用户实体类
	 * @return
	 */
	ResultUtil login(UserMstr userMstr);


	/**
	 * 插入单条用户信息
	 * @param userMstr
	 * @return
	 */
	ResultUtil insertUserInfo(UserMstr userMstr);

	/**
	 * 更新单条信息
	 * @param userMstr
	 * @return
	 */
	ResultUtil updateUserInfo(UserMstr userMstr);

	/**
	 * 删除单条信息
	 * @param userMstr
	 * @return
	 */
	ResultUtil deleteUserInfo(UserMstr userMstr);


	/**
	 * 根据用户ID批量删除用户，该方法为精确查询
	 * @param userMstrList 用户实体类列表
	 * @return 结果封装类
	 */
	ResultUtil deleteUserInfoList(List<UserMstr> userMstrList);

	/**
	 * 根据用户ID批量插入用户
	 * @param userMstrList 用户实体类列表
	 * @return 结果封装类
	 */
	ResultUtil insertUserInfoList(List<UserMstr> userMstrList);

	/**
	 * 根据用户ID批量更新用户
	 * @param userMstrList 用户实体类列表
	 * @return 结果封装类
	 */
	ResultUtil updateUserInfoList(List<UserMstr> userMstrList);



	/**
	 * 分页模糊查询用户，
	 * @param pageIndex 页码数
	 * @param pageSize 分页大小
	 * @param userId 用户对象实体类
	 * @param criteriaList  排序列表
	 * @return 结果封装类
	 */
	ResultUtil getUserInfoList(int pageIndex, int pageSize, String userId, List<Map<String,Object>> criteriaList);

	/**
	 * 修改密码
	 * @param userMstr 用户对象实体类
	 * @param newPassword 新密码
	 * @return 结果封装类
	 */
	ResultUtil updatePassword( UserMstr userMstr, String newPassword);



	/**
	 * 导入Excel更新或添加用户信息
	 * @param workbook Excel文件
	 * @return 结果封装类
	 */
	ResultUtil batchUserInfoInsertOrUpdate(Workbook workbook);

	/**
	 * 模糊查询后导出用户信息
	 * @param userId
	 * @return
	 */
	void exportUserInfo(String userId);
}
