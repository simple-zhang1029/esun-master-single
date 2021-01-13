package com.example.service.v1;

import com.example.utils.ResultUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @author test
 */
public interface ExampleService {

	ResultUtil login(String userId, String password);

	ResultUtil getDeliveryGoods(String startDate, String endDate, int pageIndex, int pageSize, String customer, String orderId, List<?> criteriaList,List<?> wharfList);

	ResultUtil addDeliveryGoods(List<?> list);

	ResultUtil deleteDeliveryGoods(List<?> list);

	ResultUtil updateDeliveryGoods(List<?> list);

	ResultUtil exportDeliveryGoods(Workbook workbook);

	ResultUtil deriveDeliveryHistory(String startDate, String endDate, List<?> customerList, String orderId, boolean isDelete);



}
