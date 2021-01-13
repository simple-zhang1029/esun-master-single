package com.example.service.v1;

import com.example.utils.ResultUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 公司管理服务实现接口
 * @author john.xiao
 * @date 2020-12-17 09:48
 */
public interface CorpService {

	/**
	 * 获取公司信息
	 * @param pageIndex 页码数
	 * @param pageSize  分页大小
	 * @param criteriaList 排序列表
	 * @param corp 查询公司
	 * @return 返回结果工具类
	 */
	ResultUtil getCorp(int pageIndex, int pageSize, List<?> criteriaList, String corp);

	/**
	 * 批量添加公司
	 * @param list 公司列表
	 * @return 返回结果工具类
	 */
	ResultUtil addCorp(List<?> list);

	/**
	 * 批量删除公司
	 * @param list  公司列表
	 * @return 返回结果工具类
	 */
	ResultUtil deleteCorp(List<?> list);

	/**
	 * 批量更新公司信息
	 * @param list 公司列表
	 * @return 返回结果工具类
	 */
	ResultUtil updateCorp(List<?> list);

	/**
	 * 批量导入公司
	 * @param workbook 传入Excel文件
	 * @return 返回结果工具类
	 */
	ResultUtil exportCorp(Workbook workbook);

	/**
	 * 导出公司信息
	 * @param corp 公司
	 * @param isDelete 是否删除
	 * @return 返回结果工具类
	 */
	ResultUtil deriveCorp(String corp, boolean isDelete);



}
