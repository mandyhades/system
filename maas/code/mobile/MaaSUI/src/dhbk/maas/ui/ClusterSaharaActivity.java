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
package dhbk.maas.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import dhbk.maas.ui.base.ExpandListAdapter;
import dhbk.maas.ui.saharaobject.Cluster;
import dhbk.maas.ui.saharaobject.Node;
import dhbk.maas.ui.utils.Conf;

public class ClusterSaharaActivity extends Activity{

	private static final String DEFAULT_PORT_CLUSTER = "8386" ;
	
	private ArrayList<Cluster> arr_cluster = new ArrayList<Cluster>();
	private ArrayList<ArrayList<Node>> arr_node = new ArrayList<ArrayList<Node>>() ;
	private ExpandListAdapter elAdapter ;
	private ProgressDialog pg_dialog ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clustersahara);
		
		ExpandableListView elv = (ExpandableListView) findViewById(R.id.cluster_elv) ;
		elAdapter = new ExpandListAdapter(getApplicationContext(), arr_cluster, arr_node) ;
		elv.setAdapter(elAdapter) ;
		elv.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2,
					int arg3, long arg4) {
				// TODO Auto-generated method stub
				if(arr_cluster.get(arg2).isActive()) {
					if(arr_node.get(arg2).get(arg3).isMaster()) {
						Conf.address_node = arr_node.get(arg2).get(arg3).getIp() ;
						Intent it = new Intent(getApplicationContext(), MainActivity.class) ;
						startActivity(it) ;
					} else 
						Toast.makeText(getApplicationContext(), "Please choose Master node !", Toast.LENGTH_SHORT).show();
				} else 
					Toast.makeText(getApplicationContext(), "Cluster is not active !", Toast.LENGTH_SHORT).show();
				return false;
			}
		}) ;
		
		new loadClustersSahara().execute() ;
	}
	
	private String loadcluster () throws ClientProtocolException, IOException, ParseException, JSONException {
		
		String url = "http://" + Conf.address + ":" + DEFAULT_PORT_CLUSTER + "/v1.0/" + Conf.tenantId + "/clusters" ;
		HttpClient httpClient = new DefaultHttpClient ();
		HttpGet request = new HttpGet(url) ;
		request.addHeader("Content-Type", "application/json");
		request.addHeader("X-Auth-Token", Conf.tokenId);
		HttpResponse response = httpClient.execute(request) ;
		
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			JSONObject jso_parent = new JSONObject(EntityUtils.toString(response.getEntity())) ;
			JSONArray jsa_clusters = jso_parent.getJSONArray("clusters") ;
			for(int i = 0; i < jsa_clusters.length(); i++) {
				JSONObject jso_cluster = jsa_clusters.getJSONObject(i) ;
				JSONArray jsa_nodegroups = jso_cluster.getJSONArray("node_groups") ;
				
				Cluster cluster = new Cluster(jso_cluster.getString("status"), jso_cluster.getString("name")) ;
				arr_cluster.add(cluster) ;
				
				ArrayList<Node> nodes = new ArrayList<Node>() ;
				for(int j = 0; j < jsa_nodegroups.length() ; j ++) {
					JSONObject jso_nodegroup = jsa_nodegroups.getJSONObject(j) ;
					
					JSONArray jsa_instances = jso_nodegroup.getJSONArray("instances") ;
					JSONObject jso_instance = jsa_instances.getJSONObject(0) ;
					
					nodes.add(new Node(jso_instance.getString("instance_name"), 
											jso_instance.getString("management_ip"), 
											jso_nodegroup.getJSONArray("node_processes").length()==2
											?"Slave":"Master")) ;
				}
				arr_node.add(nodes) ;
			}
			if(jsa_clusters.length() == 0) 
				return "Empty" ;
			else
				return "Success" ;
		}
		
		return null;
	}
	
	private void showProgressDialog () {
		if(pg_dialog == null ) {
			pg_dialog = new ProgressDialog(ClusterSaharaActivity.this);
			pg_dialog.setCancelable(false) ;
			pg_dialog.setTitle("Loading ...");
			pg_dialog.setMessage("Please Wait !!!");
			pg_dialog.setIndeterminate(false);
			pg_dialog.show();
		} 
	}
	
	private void closeProgressDialog () {
		if(pg_dialog != null) {
			if(pg_dialog.isShowing())
				pg_dialog.dismiss();
			pg_dialog = null;
		}
	}
	
	private class loadClustersSahara extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog() ;
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			String s = null;
			try {
				s = loadcluster() ;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				closeProgressDialog() ;
			}
			return s;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			elAdapter.notifyDataSetChanged() ;
			if(result == null) {
				Toast.makeText(getApplicationContext(), "Connect Fail !", Toast.LENGTH_SHORT).show() ;
			} else {
				if("Empty".equalsIgnoreCase(result)) {
					Toast.makeText(getApplicationContext(), "Not found cluster", Toast.LENGTH_SHORT).show() ;
				} else if ("Success".equalsIgnoreCase(result)){
				}
			}
		}
		
	}
}
