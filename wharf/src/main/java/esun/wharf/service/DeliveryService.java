package esun.wharf.service;

import esun.wharf.utils.ResultUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @author test
 */
public interface DeliveryService {
	ResultUtil getCustomerInfo(String customerName);

	ResultUtil getCustomerInfo(List<?> customerList);


	ResultUtil getDeliveryGoods(String startDate,String endDate,int pageIndex,int pageSize,String criteria,int sort,String customer,String orderId,List<?> wharf);

	ResultUtil addDeliveryGoods(List<?> list);

	ResultUtil deleteDeliveryGoods(List<?> list);

	ResultUtil updateDeliveryGoods(List<?> list);

	ResultUtil exportDeliveryGoods(Workbook workbook);

	ResultUtil deriveDeliveryHistory(String startDate,String endDate,List<?> customerList,String orderId,boolean isDelete);



	ResultUtil getDeliveryStatus();

	ResultUtil getBoardInfo(String startDate,String endDate,int pageIndex,int pageSize,String criteria,int sort,List<?> wharfList);

	ResultUtil checkWharf(String wharf,String startDate,String endDate);
}
