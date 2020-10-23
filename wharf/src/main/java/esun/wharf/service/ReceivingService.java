package esun.wharf.service;

import esun.wharf.utils.ResultUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @author test
 */
public interface ReceivingService {

	ResultUtil getSupplierInfo(String supplierName);

	ResultUtil getSupplierInfo(List<?> list);


	ResultUtil getReceivingGoods(String startDate,String endDate,int pageIndex,int pageSize,String criteria,int sort,String supplier,String orderId,List<?> wharfList);

	ResultUtil addReceivingGoods(List<?> list);

	ResultUtil deleteReceivingGoods(List<?> list);

	ResultUtil updateReceivingGoods(List<?> list);

	ResultUtil exportReceivingGoods(Workbook workbook);

	ResultUtil deriveReceivingHistory(String startDate,String endDate,List<?> supplierList,String orderId,boolean isDelete);



	ResultUtil getReceivingStatus();

	ResultUtil getBoardInfo(String startDate,String endDate,int pageIndex,int pageSize,String criteria,int sort,List<?> wharfList);

	ResultUtil checkWharf(String wharf,String startDate,String endDate);


}
