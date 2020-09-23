package esun.core.service;

import esun.core.utils.ResultUtil;

import java.util.List;

/**
 * 路由模块业务接口
 * @author john.xiao
 * @date 2020-09-21 16:28
 */
public interface RouterService {

	ResultUtil getRouter(int groupId);

	ResultUtil addRouter(List list);

	ResultUtil deleteRouter(List list);

	ResultUtil routerList(String name);

	ResultUtil updateUserRouter(int groupId,List routerList);

	ResultUtil getGroup(int pageIndex,int pageSize);

	ResultUtil addGroup(List groupList);

	ResultUtil deleteGroup(List groupList);

	ResultUtil getUserGroup(String name);

	ResultUtil updateUserGroup(String name,List groupList);

}
