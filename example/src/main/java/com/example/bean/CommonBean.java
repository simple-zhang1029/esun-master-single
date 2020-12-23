package com.example.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * @author test
 */
@Component
public class CommonBean {
	@Bean(name = "loginSet")
	public HashSet<String> getLoginSetSet(){
		HashSet<String> loginSet=new HashSet<>();
		return loginSet;
	}

}
