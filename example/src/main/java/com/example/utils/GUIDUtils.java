package com.example.utils;

import java.util.UUID;

/**
 * GUID工具类
 * @author john.xiao
 */
public class GUIDUtils   {

	public static String create(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}
