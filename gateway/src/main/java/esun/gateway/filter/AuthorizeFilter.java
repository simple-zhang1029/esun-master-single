package esun.gateway.filter;

import com.alibaba.fastjson.JSON;
import esun.gateway.service.TokenService;
import esun.gateway.utils.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * @author test
 */
@Component
public class AuthorizeFilter implements GlobalFilter {
	private  static Logger logger= LoggerFactory.getLogger(AuthorizeFilter.class);

	@Autowired
	TokenService tokenService;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest serverHttpRequest= exchange.getRequest();
		ServerHttpResponse serverHttpResponse = exchange.getResponse();
		String uri = serverHttpRequest.getURI().getPath();

		//检查白名单
		if(uri.contains("/login") || uri.contains("/register")){
			serverHttpRequest=exchange.getRequest().mutate().header("Token-Checked","true").build();
			return chain.filter(exchange.mutate().request(serverHttpRequest).build());
		}
		//获取name和token
		String token = serverHttpRequest.getHeaders().getFirst("token");
		if(StringUtils.isBlank(token)){
			token = serverHttpRequest.getQueryParams().getFirst("token");
		}
		String name = serverHttpRequest.getHeaders().getFirst("name");
		if(StringUtils.isBlank(name)){
			name = serverHttpRequest.getQueryParams().getFirst("name");
		}
		//校验是否存在token参数
		if(StringUtils.isBlank(token)){
			serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
			logger.error(name+":token不存在"+token);
			return getVoidMono(serverHttpResponse,"token不存在");
		}
		ResultUtil result= tokenService.checkToken(token);
		if (HttpStatus.OK.value() != (int)result.get("code")){
			logger.error(name+":token校验失败"+token);
			return getVoidMono(serverHttpResponse,"token校验失败");
		}
		logger.info(name+":token校验成功"+token);
		//添加Token-Checked请求头
		serverHttpRequest=exchange.getRequest().mutate().header("Token-Checked","true").build();
		return chain.filter(exchange.mutate().request(serverHttpRequest).build());
	}

	private Mono<Void> getVoidMono(ServerHttpResponse serverHttpResponse, String message) {
		serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
		DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(ResultUtil.error(message)).getBytes());
		return serverHttpResponse.writeWith(Flux.just(dataBuffer));
	}
}
