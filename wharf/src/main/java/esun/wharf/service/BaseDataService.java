package esun.wharf.service;

import esun.wharf.utils.ResultUtil;

import java.util.List;

/**
 * @author test
 */
public interface BaseDataService {
	ResultUtil getBaseData(String dataName, int pageIndex, int pageSize, String criteria, int sort);

	ResultUtil addBaseData(List<?> list);

	ResultUtil deleteBaseData(List<?> list);

	ResultUtil updateBaseData(List<?> list);

	ResultUtil getWharf();

	int getLoadTimeOut();

	int getWaitTimeOut();
}
