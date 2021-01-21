package com.example.service.v2;

import com.example.entity.CorpMstr;
import com.example.utils.ResultUtil;
import net.sf.json.JSONArray;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

public interface CorpService {
    /**
     * 分页获取公司信息
     * @param pageIndex
     * @param pageSize
     * @param criteriaList
     * @param corp
     * @return
     */
    ResultUtil getCorpInfoList(int pageIndex, int pageSize, String corp,List<Map<String, Object>> criteriaList);

    /**
     * 单条添加公司信息
     * @param corpMstr
     * @return
     */
    ResultUtil insertCorpInfo(CorpMstr corpMstr);

    /**
     * 批量添加公司信息
     * @param corpMstrList
     * @return
     */
    ResultUtil insertCorpInfoList(List<CorpMstr> corpMstrList);

    /**
     * 单条删除公司信息
     * @param corpMstr
     * @return
     */
    ResultUtil deleteCorpInfo(CorpMstr corpMstr);
    /**
     * 批量删除公司信息
     * @param corpMstrList
     * @return
     */
    ResultUtil deleteCorpInfolist(List<CorpMstr> corpMstrList);

    /**
     * 单条更新公司信息
     * @param corpMstr
     * @return
     */
    ResultUtil updateCorpInfo(CorpMstr corpMstr);

    /**
     * 批量更新公司信息
     * @param corpMstrList
     * @return
     */
    ResultUtil updateCorpInfolist(List<CorpMstr> corpMstrList);

    /**
     * 导入公司信息
     * @param workbook
     * @return
     */
    ResultUtil exportCorp(Workbook workbook);

    /**
     * 导出公司信息
     * @param corp
     * @param isDelete
     * @return
     */
    ResultUtil deriveCorp(String corp, boolean isDelete);
}
