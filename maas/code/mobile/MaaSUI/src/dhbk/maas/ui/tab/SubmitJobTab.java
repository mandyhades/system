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
package dhbk.maas.ui.tab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import dhbk.maas.api.hadoop.monitor.HDFSManage;
import dhbk.maas.api.hadoop.monitor.ResourceManage;
import dhbk.maas.api.hadoop.monitor.obj.ListADirectory;
import dhbk.maas.api.httpconnect.HttpConnect;
import dhbk.maas.ui.R;
import dhbk.maas.ui.tab.service.SubmitJobService;
import dhbk.maas.ui.utils.Conf;

public class SubmitJobTab extends Fragment implements OnClickListener{

	private ArrayList<String[]> listAlgorithm = new ArrayList<String[]>();
	private ArrayList<String[]> listData = new ArrayList<String[]>() ;
	private ArrayAdapter<String[]> adapterAlgotithm ;
	private ArrayAdapter<String[]> adapterData;
	ListView lv_data ;
	TextView tv_algorithm, tv_data;
	
	private int pos_algorithm = -1, pos_data = -1;
	private AtomicBoolean isExe = new AtomicBoolean() ;
	private ProgressDialog pg_dialog = null;
	
	private ArrayList<ListADirectory> listADirectory = new ArrayList<ListADirectory>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isExe.set(false) ;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.tab_submit_job, container, false) ;
		ImageButton btn_refresh = (ImageButton) view.findViewById(R.id.tabsub_btn_refresh) ;
		btn_refresh.setOnClickListener(this) ;
		Button btn_submit = (Button) view.findViewById(R.id.tabsub_btn_submit) ;
		btn_submit.setOnClickListener(this) ;
		
		tv_algorithm = (TextView) view.findViewById(R.id.tabsub_tv_algorithm) ;
		tv_data = (TextView) view.findViewById(R.id.tabsub_tv_data) ;
		if(pos_algorithm >= 0)
			tv_algorithm.setText(listAlgorithm.get(pos_algorithm)[0]);
		if(pos_data >= 0)
			tv_data.setText(listData.get(pos_data)[0]) ;
		
		adapterAlgotithm = new ArrayAdapter<String[]>(getActivity(), android.R.layout.simple_list_item_2,
				android.R.id.text1, listAlgorithm){
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent) ;
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);
		        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
		        text1.setTextSize(16) ;
		        text2.setTextSize(12) ;
		        text1.setText(listAlgorithm.get(position)[0]) ;
		        text2.setText(listAlgorithm.get(position)[1]) ;
				return view;
			}
		} ;
		
		adapterData = new ArrayAdapter<String[]>(getActivity(), android.R.layout.simple_list_item_2,
				android.R.id.text1, listData){
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent) ;
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);
		        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
		        text1.setTextSize(16) ;
		        text2.setTextSize(12) ;
		        text1.setText(listData.get(position)[0]) ;
		        text2.setText(listData.get(position)[1]) ;
				return view;
			}
		} ;
		
		ListView lv_algorithm = (ListView) view.findViewById(R.id.tabsub_lv_algorithm) ;
		lv_data = (ListView) view.findViewById(R.id.tabsub_lv_data) ;
		lv_algorithm.setAdapter(adapterAlgotithm) ;
		lv_data.setAdapter(adapterData) ;
		
		lv_algorithm.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				pos_algorithm = arg2;
				tv_algorithm.setText(listAlgorithm.get(arg2)[0]) ;
			}
		});
		
		lv_data.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				pos_data = arg2;
				tv_data.setText(listData.get(arg2)[0]) ;
			}
		});
		
		if(listAlgorithm.size() == 0)
			loadListAlgorithm() ;
		else
			adapterAlgotithm.notifyDataSetChanged() ;
		
		if(listData.size() == 0) {
			if(!isExe.get()) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new exeListADirectory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "user/hadoop/" + Conf.username);
				} else {
					new exeListADirectory().execute("user/hadoop/" + Conf.username) ;
				}
				isExe.set(true) ;
			}
		} else {
			changeListDataIfNeed() ;
		}
		
		return view ;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tabsub_btn_refresh :
			if(!isExe.get()) {
				if(isConnectNetwork()) {
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new exeListADirectory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "user/hadoop/" + Conf.username);
					} else {
						new exeListADirectory().execute("user/hadoop/" + Conf.username) ;
					}
					isExe.set(true) ;
				} else {
					Toast.makeText(getActivity().getApplicationContext(), 
							"Not connected network !", Toast.LENGTH_SHORT).show() ;
				}
			}
			break;
		case R.id.tabsub_btn_submit :
			if(isConnectNetwork()) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new startSubmitJobService().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new startSubmitJobService().execute() ;
				}
			} else {
				Toast.makeText(getActivity().getApplicationContext(), 
							"Not connected network !", Toast.LENGTH_SHORT).show() ;
			}
			break;
		default : break;
		}
	}
	
	private void loadListAlgorithm () {
		String[] algorithm_name = getResources().getStringArray(R.array.algorithm_name) ;
		String[] algorithm_group = getResources().getStringArray(R.array.algorithm_group) ;
		
		listAlgorithm.clear() ;
		for(int i = 0; i < algorithm_name.length; i++) {
			listAlgorithm.add(new String[] {algorithm_name[i], algorithm_group[i]}) ;
		}
		
		adapterAlgotithm.notifyDataSetChanged() ;
	}
	
	private void loadData (String path) {
		
		try {
			HDFSManage hdfs = new HDFSManage(Conf.address_node) ;
			listADirectory = hdfs.getListADirectory(path) ;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			isExe.set(false) ;
			closeProgressDialog() ;
		}
	}
	
	private void changeListDataIfNeed () {
		if(listADirectory.size() > 0){
			if(listADirectory.size() != lv_data.getChildCount()) {
				listData.clear() ;
				for(ListADirectory lad : listADirectory) {
					listData.add(new String[] {lad.pathSuffix, lad.type}) ;
				}
				adapterData.notifyDataSetChanged() ;
			}
		}
	}
	
	private boolean isConnectNetwork () {
		ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE) ;
		NetworkInfo netinfo = cm.getActiveNetworkInfo() ;
		if(netinfo != null && netinfo.isConnected())
			return true;
		else 
			return false ;
	}
	
	private void showProgressDialog (String title) {
		if(pg_dialog == null ) {
			pg_dialog = new ProgressDialog(SubmitJobTab.this.getActivity());
			pg_dialog.setCancelable(false) ;
			pg_dialog.setTitle(title);
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
	
	private class exeListADirectory extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog("Loading ...") ;
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			loadData(params[0]) ;
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			changeListDataIfNeed() ;
		}
	}
	
	private class startSubmitJobService extends AsyncTask<String, Void, Integer>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog("Starting Job") ;
		}
		
		@Override
		protected Integer doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			int isStart = -1 ;
			try {
				isStart =  jobIsRunning() ;
			} catch (Exception e) {
			} finally {
				closeProgressDialog() ;
			}
			return isStart;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result != null) {
				if(isStartSubmitJobService(result)) {
					startService() ;
				}
			}
		}
	}
	
	private boolean isStartSubmitJobService (int resultCheck) {
		// -1: Exception;	0 : not fulfill;	1 : is running;	2 : connect fail;	3 : success
		if(resultCheck == -1) {
			Toast.makeText(getActivity(), "Connect fail ! ", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(resultCheck == 0) {
			Toast.makeText(getActivity().getApplicationContext(), "Choose Algorithm & Data", Toast.LENGTH_SHORT).show() ;
			return false;
		}
		
		int i = 0;
		while (i < 10) {
			i++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(resultCheck == 1) {
			Toast.makeText(getActivity(), "Job is Running ", Toast.LENGTH_SHORT).show();
			return false;
		} else if (resultCheck == 2) {
			Toast.makeText(getActivity(), "Connect fail ! ", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private void startService () {
		
		Intent it = new Intent(getActivity().getApplicationContext(), SubmitJobService.class);
		
		switch (pos_algorithm) {
		case 0 : it.setAction(SubmitJobService.ACTION_SUBMIT_RECOMMENDER); break;
		case 1 : it.setAction(SubmitJobService.ACTION_SUBMIT_NAIVEBAYES); break;
		case 2 : it.setAction(SubmitJobService.ACTION_SUBMIT_HIDDENMARKOVMODELS); break;
		case 3 : it.setAction(SubmitJobService.ACTION_SUBMIT_LOGISTICREGRESSION); break;
		case 4 : it.setAction(SubmitJobService.ACTION_SUBMIT_RANDOMFOREST); break;
		case 5 : it.setAction(SubmitJobService.ACTION_SUBMIT_KMEANS); break;
		case 6 : it.setAction(SubmitJobService.ACTION_SUBMIT_CANOPY); break;
		case 7 : it.setAction(SubmitJobService.ACTION_SUBMIT_FUZZYKMEANS); break;
		default:
			break;
		}
		
		if(pos_data >= 0) {
			Bundle b = new Bundle();
			b.putString("pathdata", Conf.username + "/" + listData.get(pos_data)[0]);
			b.putString("pathOutput", Conf.username) ;
			it.putExtras(b) ;
		}
		
		if(pos_algorithm >= 0 && pos_data >= 0) {
			getActivity().startService(it) ;
			Toast.makeText(getActivity().getApplicationContext(), "Starting job ...", Toast.LENGTH_SHORT).show() ;
		} 
	}
	
	private int jobIsRunning () {
		if(pos_algorithm < 0 || pos_data < 0) {
			return 0;
		}
		
		HttpConnect conn = new HttpConnect() ;
		String url1 = ResourceManage.HTTP + Conf.address_node + ":" + ResourceManage.DEFAULT_PORT + 
					"/ws/v1/cluster/appstatistics?states=accepted,running&applicationTypes=mapreduce" ;
		HttpResponse response1 = null;
		try {
			response1 = conn.sendRequestGet(url1, null) ;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;
		}
		
		if(response1 == null)
			return 2;
		else {
			try {
				JSONObject jso_parent = new JSONObject(EntityUtils.toString(response1.getEntity())) ;
				JSONObject jso_appStatInfo = jso_parent.getJSONObject("appStatInfo") ;
				JSONArray jsa_statItem = jso_appStatInfo.getJSONArray("statItem") ;
				for(int i = 0; i < jsa_statItem.length(); i++) {
					JSONObject jso_item = jsa_statItem.getJSONObject(i) ;
					int count = jso_item.getInt("count") ;
					if(count > 0)
						return 1;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 2;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 2;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 2;
			}
		}
		
		return 3;
	}
}
