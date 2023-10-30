/************************************************************************
 * Copyright 2014	Le Dai Cat, Tran Sy Dat
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

***************************************************************************/
package dhbk.maas.api.hadoop.monitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dhbk.maas.api.hadoop.monitor.obj.ReMngClusterApp;
import dhbk.maas.api.httpconnect.HttpConnect;

public class ResourceManage {

	public static final String HTTP = "http://";
	private static final String REMNG_CLUSTER_APPS_PATH = "/ws/v1/cluster/apps" ;
	
	public static final String REMNG_CLUSTER_APP_FINISHEDTIME = "finishedTime";
	public static final String REMNG_CLUSTER_APP_AMCONTAINERLOGS = "amContainerLogs";
	public static final String REMNG_CLUSTER_APP_TRACKINGUI = "trackingUI";
	public static final String REMNG_CLUSTER_APP_STATE = "state";
	public static final String REMNG_CLUSTER_APP_USER = "user";
	public static final String REMNG_CLUSTER_APP_ID = "id";
	public static final String REMNG_CLUSTER_APP_CLUSTERID = "clusterId";
	public static final String REMNG_CLUSTER_APP_FINALSTATUS = "finalStatus";
	public static final String REMNG_CLUSTER_APP_AMHOSTHTTPADDRESS = "amHostHttpAddress";
	public static final String REMNG_CLUSTER_APP_PROGRESS = "progress";
	public static final String REMNG_CLUSTER_APP_NAME = "name";
	public static final String REMNG_CLUSTER_APP_STARTEDTIME = "startedTime";
	public static final String REMNG_CLUSTER_APP_ELAPSEDTIME = "elapsedTime";
	public static final String REMNG_CLUSTER_APP_DIAGNOSTICS = "diagnostics";
	public static final String REMNG_CLUSTER_APP_TRACKINGURL = "trackingUrl";
	public static final String REMNG_CLUSTER_APP_QUEUE = "queue";
	public static final String REMNG_CLUSTER_APP_ALLOCATEDMB = "allocatedMB";
	public static final String REMNG_CLUSTER_APP_ALLOCATEDVCORES = "allocatedVCores";
	public static final String REMNG_CLUSTER_APP_RUNNINGCONTAINERS = "runningContainers";
	
	public static final String DEFAULT_PORT = "8088";
	
	private String url;
	private String address;
	private String port = DEFAULT_PORT;
	
	private HttpConnect httpConnect;
	
	public ResourceManage (String address) {
		this.address = address;
		httpConnect = new HttpConnect() ;
	}
	
	public ResourceManage (String address, String port) {
		this.address = address;
		this.port = port;
		httpConnect = new HttpConnect() ;
	}
	
	/**
	 * get list cluster application
	 * @param nTop
	 * @return array list object ReMngClusterApp
	 * @throws IOException
	 * @throws JSONException
	 */
	public ArrayList<ReMngClusterApp> getClusterApps () throws IOException, JSONException {
		ArrayList<ReMngClusterApp> clusterApps = new ArrayList<>() ;
		
		this.url = HTTP + this.address + ":" + this.port + REMNG_CLUSTER_APPS_PATH ;
		
		HttpResponse response = httpConnect.sendRequestGet(this.url, null) ;
		
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			
			String buff = EntityUtils.toString(response.getEntity()) ;
			if(buff.length() > 15 ) {
				JSONObject jso_parent = new JSONObject(buff) ;
				JSONObject jso_apps = jso_parent.getJSONObject("apps") ;
				JSONArray jsaClusterApps = jso_apps.getJSONArray("app") ;
				
				for(int i = 0 ; i < jsaClusterApps.length(); i++) {
					JSONObject jsoClusterApp = jsaClusterApps.getJSONObject(i) ;
					
					clusterApps.add(new ReMngClusterApp(jsoClusterApp.getString(REMNG_CLUSTER_APP_FINISHEDTIME), 
							jsoClusterApp.getString(REMNG_CLUSTER_APP_AMCONTAINERLOGS), jsoClusterApp.getString(REMNG_CLUSTER_APP_TRACKINGUI), 
							jsoClusterApp.getString(REMNG_CLUSTER_APP_STATE), jsoClusterApp.getString(REMNG_CLUSTER_APP_USER), 
							jsoClusterApp.getString(REMNG_CLUSTER_APP_ID), jsoClusterApp.getString(REMNG_CLUSTER_APP_CLUSTERID), 
							jsoClusterApp.getString(REMNG_CLUSTER_APP_FINALSTATUS), jsoClusterApp.getString(REMNG_CLUSTER_APP_AMHOSTHTTPADDRESS), 
							jsoClusterApp.getString(REMNG_CLUSTER_APP_PROGRESS), jsoClusterApp.getString(REMNG_CLUSTER_APP_NAME), 
							jsoClusterApp.getString(REMNG_CLUSTER_APP_STARTEDTIME), jsoClusterApp.getString(REMNG_CLUSTER_APP_ELAPSEDTIME), 
							jsoClusterApp.getString(REMNG_CLUSTER_APP_DIAGNOSTICS),  jsoClusterApp.getString(REMNG_CLUSTER_APP_TRACKINGURL), 
							jsoClusterApp.getString(REMNG_CLUSTER_APP_QUEUE), jsoClusterApp.getString(REMNG_CLUSTER_APP_ALLOCATEDMB), 
							jsoClusterApp.getString(REMNG_CLUSTER_APP_ALLOCATEDVCORES), jsoClusterApp.getString(REMNG_CLUSTER_APP_RUNNINGCONTAINERS)));
					
				}
				
				if(clusterApps.size() > 0) {
					Collections.sort(clusterApps, new Comparator<ReMngClusterApp>() {
	
						@Override
						public int compare(ReMngClusterApp o1, ReMngClusterApp o2) {
							// TODO Auto-generated method stub
							return o1.id.compareToIgnoreCase(o2.id);
						}
						
					});
				}
			}
		}
		
		return clusterApps ;
	}
	
	/**
	 * get information cluster application
	 * @param appId
	 * @return object ReMngClusterApp if execute success
	 * 		   null if execute failure
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public ReMngClusterApp getClusterApp (String appId) throws ClientProtocolException, IOException, JSONException {
		ReMngClusterApp clusterApp = null;
		
		this.url = HTTP + this.address + ":" + this.port + REMNG_CLUSTER_APPS_PATH + "/" + appId;
		
		HttpResponse response = httpConnect.sendRequestGet(this.url, null) ;
		
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			
			String buff = EntityUtils.toString(response.getEntity()) ;
			if(buff.length() > 7 ) {
				JSONObject jso_parent = new JSONObject(buff) ;
				JSONObject jsoClusterApp = jso_parent.getJSONObject("app") ;
				
				clusterApp = new ReMngClusterApp(jsoClusterApp.getString(REMNG_CLUSTER_APP_FINISHEDTIME), 
						jsoClusterApp.getString(REMNG_CLUSTER_APP_AMCONTAINERLOGS), jsoClusterApp.getString(REMNG_CLUSTER_APP_TRACKINGUI), 
						jsoClusterApp.getString(REMNG_CLUSTER_APP_STATE), jsoClusterApp.getString(REMNG_CLUSTER_APP_USER), 
						jsoClusterApp.getString(REMNG_CLUSTER_APP_ID), jsoClusterApp.getString(REMNG_CLUSTER_APP_CLUSTERID), 
						jsoClusterApp.getString(REMNG_CLUSTER_APP_FINALSTATUS), jsoClusterApp.getString(REMNG_CLUSTER_APP_AMHOSTHTTPADDRESS), 
						jsoClusterApp.getString(REMNG_CLUSTER_APP_PROGRESS), jsoClusterApp.getString(REMNG_CLUSTER_APP_NAME), 
						jsoClusterApp.getString(REMNG_CLUSTER_APP_STARTEDTIME), jsoClusterApp.getString(REMNG_CLUSTER_APP_ELAPSEDTIME), 
						jsoClusterApp.getString(REMNG_CLUSTER_APP_DIAGNOSTICS), jsoClusterApp.getString(REMNG_CLUSTER_APP_TRACKINGURL), 
						jsoClusterApp.getString(REMNG_CLUSTER_APP_QUEUE), "", "", "");
			}
		}
		
		return clusterApp ;
	}
	
}
