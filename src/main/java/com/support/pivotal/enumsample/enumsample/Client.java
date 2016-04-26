package com.support.pivotal.enumsample.enumsample;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.DataPolicy;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionFactory;
import com.gemstone.gemfire.cache.Scope;
import com.gemstone.gemfire.cache.client.PoolManager;
import com.gemstone.gemfire.distributed.DistributedSystem;

/**
 * This example shows how to use the client APIs to create two different types
 * of clients that will connect to a cache server.
 * <p>
 * See the <code>README.html</code> for instructions on how to run the example.
 * <p>
 * See the source code in <code>Client.java</code> to see how to use the client
 * APIs.
 *
 * <p>
 * The two types of clients are:
 * <ol>
 * <li>A client acting as a publisher. It does not want to store any data in the
 * client cache but simply send it to the server.
 * <li>A client acting as a subscriber. It just wants events any time a
 * publisher adds some data.
 * </ol>
 * 
 * @since 5.7
 */
public class Client {
	/**
	 * Sets up this client's {@link DistributedSystem}.
	 * <p>
	 * Logging and statistics are enabled because it is best to have these
	 * recorded.
	 * <p>
	 * cache-xml-file is disabled since this example uses API to create the
	 * client's pools and regions.
	 * <p>
	 * An alternative would be to use a <code>gemfire.properties</code> file
	 * containing: <tt>
	 * mcast-port=0
	 * locators=
	 * log-file=name.log
	 * statistic-archive-file=name.gfs
	 * statistic-sampling-enabled=true
	 * cache-xml-file=
	 * </tt> and to call {@link DistributedSystem#connect} with
	 * <code>null</code> allowing it to use the values from the property file.
	 *
	 * @param name
	 *            the base name to use for the log file and stats file.
	 * @return the created {@link DistributedSystem}
	 */
	public static DistributedSystem connectStandalone(String name) {
		Properties p = new Properties();
		p.setProperty("mcast-port", "0");
		p.setProperty("locators", "");
		p.setProperty("log-file", name + ".log");
		p.setProperty("statistic-archive-file", name + ".gfs");
		p.setProperty("statistic-sampling-enabled", "true");
		p.setProperty("cache-xml-file", "");
		return DistributedSystem.connect(p);
	}

	/**
	 * The number of puts the publisher should do and the subscriber should
	 * receive.
	 */
	private static final int NUM_PUTS = 10;

	/**
	 * The port the locator is listening on.
	 */
	private static final int LOCATOR_PORT = Integer.getInteger("locatorPort",
			10334).intValue();

	private static void runPublisher() {
		DistributedSystem distributedSystem = connectStandalone("publisher");
		/*
		 * To declare in a cache.xml do this: <!DOCTYPE cache PUBLIC
		 * "-//GemStone Systems, Inc.//GemFire Declarative Caching 5.7//EN"
		 * "http://www.gemstone.com/dtd/cache5_7.dtd"> <cache> </cache>
		 */
		Cache cache = CacheFactory.create(distributedSystem);
		/*
		 * To declare in a cache.xml do this: <pool name="publisher"> <locator
		 * host="localhost" port="41111"/> </pool>
		 */
		PoolManager.createFactory().addLocator("localhost", 10334)
				.create("publisher");

		/*
		 * To declare in a cache.xml do this: <region name="DATA">
		 * <region-attributes data-policy="empty" pool-name="publisher"
		 * scope="local"> </region-attributes> </region>
		 */
		Region<String, Map<String, Object>> region = new RegionFactory<String, Map<String, Object>>()
				.setDataPolicy(DataPolicy.EMPTY).setScope(Scope.LOCAL)
				.setPoolName("publisher").create("Test");

		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("status", Status.QA);
		region.put("/test/data/enumobj", hashMap);

		HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
		hashMap1.put("status", Status.QA.toString());
		region.put("/test/data/enumasstr", hashMap1);
		cache.close();
		distributedSystem.disconnect();
	}

	private static void runSubscriber() throws InterruptedException {
		DistributedSystem distributedSystem = connectStandalone("subscriber");

		/*
		 * To declare in a cache.xml do this: <!DOCTYPE cache PUBLIC
		 * "-//GemStone Systems, Inc.//GemFire Declarative Caching 5.7//EN"
		 * "http://www.gemstone.com/dtd/cache5_7.dtd"> <cache> </cache>
		 */
		Cache cache = CacheFactory.create(distributedSystem);
		/*
		 * To declare in a cache.xml do this: <pool name="subscriber"
		 * subscription-enabled="true"> <locator host="localhost" port="41111"/>
		 * </pool>
		 */
		PoolManager.createFactory().addLocator("localhost", 10334)
				.setSubscriptionEnabled(true).create("subscriber");
		/*
		 * To declare in a cache.xml do this: <region name="DATA">
		 * <region-attributes data-policy="empty" pool-name="subscriber"
		 * scope="local"> <subscription-attributes interest-policy=all/>
		 * <cache-listener>
		 * <class-name>clientAPI.SubscriberListener</class-name>
		 * </cache-listener> </region-attributes> </region>
		 */
		Region<String, Map<String, Object>> region = new RegionFactory<String, Map<String, Object>>()
				.setDataPolicy(DataPolicy.EMPTY)
				.setPoolName("subscriber")
				.setScope(Scope.LOCAL)
				.create("Test");
		Map<String, Object> map = region.get("/test/data/enumobj");
		Status object = (Status)map.get("status");
		System.out.println(object);
		Map<String, Object> map1 = region.get("/test/data/enumasstr");
		System.out.println(map1);

		cache.close();
		distributedSystem.disconnect();
	}

	/**
	 * Instances of the class should never be created. It simply has static
	 * methods.
	 */
	private Client() {
	}

	/**
	 * See the the <code>README.html</code> for instructions on how to run the
	 * class from the command line.
	 * 
	 * @param args
	 *            must contain one element whose value is "publisher" or
	 *            "subscriber".
	 */
	public static void main(String[] args) throws Exception {
		//runPublisher();
		runSubscriber();
	}
}
