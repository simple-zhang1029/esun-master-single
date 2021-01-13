package com.example.service.v1;

import com.example.utils.ResultUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 域管理服务实现接口
 * @author john.xiao
 * @date 2020-12-17 09:48
 */
public interface DomainService {

	/**
	 * 获取域信息
	 * @param pageIndex 页码数
	 * @param pageSize  分页大小
	 * @param criteriaList 排序列表
	 * @param domain 查询域
	 * @return 返回结果工具类
	 */
	ResultUtil getDomain(int pageIndex, int pageSize, List<?> criteriaList,String domain);

	/**
	 * 批量添加域
	 * @param list 域列表
	 * @return 返回结果工具类
	 */
	ResultUtil addDomain(List<?> list);

	/**
	 * 批量删除域
	 * @param list  域列表
	 * @return 返回结果工具类
	 */
	ResultUtil deleteDomain(List<?> list);

	/**
	 * 批量更新域信息
	 * @param list 域列表
	 * @return 返回结果工具类
	 */
	ResultUtil updateDomain(List<?> list);

	/**
	 * 批量导入域
	 * @param workbook 传入Excel文件
	 * @return 返回结果工具类
	 */
	ResultUtil exportDomain(Workbook workbook);

	/**
	 * 导出域信息
	 * @param domain 域
	 * @param isDelete 是否删除
	 * @return 返回结果工具类
	 */
	ResultUtil deriveDomain(String domain, boolean isDelete);

	/**
	 * 获取用户域
	 * @param user 用户名
	 * @return 返回结果工具类
	 */
	ResultUtil getUserDomain(String user);

	/**
	 * 更新用户域
	 * @param user 用户名
	 * @param list 域信息列表
	 * @return 返回结果工具类
	 */
	ResultUtil updateUserDomain(String user,List<?> list);

}
