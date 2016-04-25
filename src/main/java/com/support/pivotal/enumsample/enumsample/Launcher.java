package com.support.pivotal.enumsample.enumsample;

import java.util.HashMap;
import java.util.Map;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;

public class Launcher {
	public static void main(String[] args) {
		ClientCache cache = new ClientCacheFactory().set("cache-xml-file",
				"client-cache.xml").create();
		/**
		 * static Cache<String, Map> cache; static Map<String,Object> data;
		 */
		Region<String, Map<String, Object>> region = cache.getRegion("Test");
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("status", Status.QA);
		region.put("/test/data/enumobj", hashMap);

		HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
		hashMap1.put("status", Status.QA.toString());
		region.put("/test/data/enumasstr", hashMap1);

		Map<String, Object> map = region.get("/test/data/enumobj");
		Status object = (Status)map.get("status");
		System.out.println(object);
		Map<String, Object> map1 = region.get("/test/data/enumasstr");
		System.out.println(map1);
	}
}
