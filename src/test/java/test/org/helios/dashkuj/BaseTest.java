/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package test.org.helios.dashkuj;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.Assert;

import org.bson.types.ObjectId;
import org.helios.dashkuj.core.Dashkuj;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.Widget;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

/**
 * <p>Title: BaseTest</p>
 * <p>Description: Base test case for Dashkuj tests</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>test.org.helios.dashkuj.BaseTest</code></p>
 */

public class BaseTest {
	/** The default host */
	protected static String defaultDashkuHost = "dashku";
	/** The default port */
	protected static int defaultDashkuPort = 3000;
	/** The default mongo database name */
	protected static String defaultDbName = "dashku_development";
	
	/** For generating random values */
	protected static Random RANDOM = new Random(System.currentTimeMillis());
	
	/** A map of user names keyed by apiKey */
	protected static final Map<String, String> apiKey2userId = new HashMap<String, String>();
	/** A map of apiKeys keyed by userName */
	protected static final Map<String, String> userId2apiKey = new HashMap<String, String>();
	/** A map of userIds keyed by userName */
	protected static final Map<String, String> userName2userId = new HashMap<String, String>();
	/** A map of userNames keyed by userId */
	protected static final Map<String, String> userId2userName = new HashMap<String, String>();
	
	
	/** The currently executing test name */
	@Rule public final TestName name = new TestName();
	/** The mongo connection for supporting morphia */
	protected static Mongo mongo = null;
	/** The mongo client for retriving data */
	protected static MongoClient mongoClient = null;
	/** The mongo db for retriving data */
	protected static DB mongoDb = null;
	
	/** The mongo morphia */
	protected static Morphia morphia = null;
	/** The mongo datastore */
	protected static Datastore mongoDs = null;
	
	/** The dashku factory */
	public static final Dashkuj DASH = Dashkuj.getInstance();
	
	
	
	/**
	 * Prints the name of the current catalog and the current test
	 */
	@Before
	public void printTestName() {
		log("\n\t==================================\n\tRunning Test [" + name.getMethodName() + "]\n\tThread:" + Thread.currentThread().toString() + "\n\t==================================\n");
		
	}
	
	/**
	 * Out logger
	 * @param obj the out message
	 */
	protected static void log(Object obj) {
		System.out.println(obj);
	}
	/**
	 * Error logger
	 * @param obj the error message
	 */
	protected static void loge(Object obj) {
		System.err.println(obj);
	}
	
	

	/**
	 * Initializes the MongoDB connection and populates the apiKey2userId and userId2apiKey maps.
	 * @throws Exception thrown on error connecting to mongo
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(mongoDs==null) {
			mongo = new Mongo(defaultDashkuHost);
			morphia = new Morphia();
			mongoDs = morphia.createDatastore(mongo, "dashku_development");
			mongoClient = new MongoClient(defaultDashkuHost);
			mongoDb = mongoClient.getDB(defaultDbName);
			DBCollection coll = mongoDb.getCollection("users");
			DBCursor cursor = null; 
			try {
				cursor = coll.find();
				while(cursor.hasNext()) {
					DBObject dbObj = cursor.next();
					String userId = dbObj.get("_id").toString();
					String apiKey = dbObj.get("apiKey").toString();
					String userName = dbObj.get("username").toString();
					apiKey2userId.put(apiKey, userId);
					userId2apiKey.put(userId, apiKey);
					userName2userId.put(userName, userId);
					userId2userName.put(userId, userName);
					log("Cached user\n\t:" + userName + "\n\t" + userId + "\n\t" + apiKey);
				}
			} finally {
				if(cursor!=null) cursor.close();
			}
			
		}		
	}

	/**
	 * Closes the mongoDB connectionvalue
	 */
	@AfterClass
	public static void tearDownAfterClass()  {
		if(mongoDs!=null) {
			mongoDs = null;
			morphia = null;
			mongo.close();
			mongo = null;
			mongoClient.close();
			mongoClient = null;
		}
	}

	/**
	 * Base class pre-test setup
	 * @throws Exception on any error in pre-test setup
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Base class post-test teardown
	 * @throws Exception on any error in post-test teardown
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Returns the dashboard from the mongo db
	 * @param dashboardId the id of the dashboard to fetch
	 * @return the dashboard
	 */
	protected Dashboard getDbDashboard(String dashboardId) {		
		return mongoDs.find(Dashboard.class, "_id", new ObjectId(dashboardId)).get();
	}
	
	
	
	/**
	 * Returns all the dashboards in the DB for the passed apiKey
	 * @param apiKey The apiKey to filter dashboards by
	 * @return all the dashboards in the DB
	 */
	protected Collection<Dashboard> getDbDashboardsByApiKey(String apiKey) {
		String userId = apiKey2userId.get(apiKey);
		Assert.assertNotNull("The user for apiKey [" + apiKey + "] was null", userId);
		String userName = userId2userName.get(userId);
		Assert.assertNotNull("The username for apiKey [" + apiKey + "] was null", userName);
		return mongoDs.find(Dashboard.class, "userId", new ObjectId(userId)).asList();
	}
	
	/**
	 * Returns all the dashboards in the DB for the passed user
	 * @param userName The user name to filter dashboards by
	 * @return all the dashboards in the DB
	 */
	protected Collection<Dashboard> getDbDashboardsByUser(String userName) {
		String userId = userName2userId.get(userName);
		Assert.assertNotNull("The userId for userName [" + userName + "] was null", userId);		
		return mongoDs.find(Dashboard.class, "userId", new ObjectId(userId)).asList();
	}
	
	
	/**
	 * Does a field by field assertive compare
	 * @param d1 One dashboard to compare
	 * @param d2 Another dashboard to compare
	 */
	protected void compareDashboards(Dashboard d1, Dashboard d2) {
		Assert.assertNotNull("Dashboard d1 was null",d1);
		Assert.assertNotNull("Dashboard d2 was null",d2);
		Assert.assertEquals("The IDs were not equal", d1.getId(), d2.getId());
		Assert.assertEquals("The CSSs were not equal", d1.getCss(), d2.getCss());
		Assert.assertEquals("The Names were not equal", d1.getName(), d2.getName());
		Assert.assertEquals("The ScreenWidths were not equal", d1.getScreenWidth(), d2.getScreenWidth());
		Assert.assertEquals("The UserIds were not equal", d1.getUserId(), d2.getUserId());
		Assert.assertEquals("The Dashboard Counts were not equal", d1.getWidgetMap().size(), d2.getWidgetMap().size());
		for(Map.Entry<String, Widget> entry: d1.getWidgetMap().entrySet()) {
			Widget w1 = entry.getValue();
			Assert.assertEquals("The Widget id was not the same as they map key", w1.getId(), entry.getKey());
			Widget w2 = d2.getWidget(w1.getId());
			compareWidgets(w1, w2);
		}
		for(Map.Entry<String, Widget> entry: d2.getWidgetMap().entrySet()) {
			Widget w2 = entry.getValue();
			Assert.assertEquals("The Widget id was not the same as they map key", w2.getId(), entry.getKey());
			Widget w1 = d1.getWidget(w2.getId());
			compareWidgets(w2, w1);
		}
		
		/** There's a time-zone issue here */
		//Assert.assertEquals("The Created Times were not equal", d1.getCreated(), d2.getCreated());
		//Assert.assertEquals("The Last Updated Times were not equal", d1.getLastUpdated(), d2.getLastUpdated());		
	}
	
	/**
	 * Does a field by field assertive compare
	 * @param w1 One widget to compare
	 * @param w2 Another widget to compare
	 */
	protected void compareWidgets(Widget w1, Widget w2) {
		Assert.assertNotNull("Widget w1 was null",w1);
		Assert.assertNotNull("Widget w2 was null",w2);
		Assert.assertEquals("The IDs were not equal", w1.getId(), w2.getId());
		Assert.assertEquals("The CSSs were not equal", w1.getCss(), w2.getCss());
		Assert.assertEquals("The DashboardIds were not equal", w1.getDashboardId(), w2.getDashboardId());
		Assert.assertEquals("The Heights were not equal", w1.getHeight(), w2.getHeight());
		Assert.assertEquals("The Widths were not equal", w1.getWidth(), w2.getWidth());
		Assert.assertEquals("The Htmls were not equal", w1.getHtml(), w2.getHtml());
		Assert.assertEquals("The Jsons were not equal", w1.getJson(), w2.getJson());
		Assert.assertEquals("The ScopedCsss were not equal", w1.getScopedCss(), w2.getScopedCss());
		Assert.assertEquals("The Scripts were not equal", w1.getScript(), w2.getScript());
		Assert.assertEquals("The ScriptTypes were not equal", w1.getScriptType(), w2.getScriptType());
		/** There's a time-zone issue here */
/*		Assert.assertEquals("The Created Times were not equal", w1.getCreated(), w2.getCreated());
		Assert.assertEquals("The Last Updated Times were not equal", w1.getLastUpdated(), w2.getLastUpdated());		
*/		
	}
}
