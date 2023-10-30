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

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import dhbk.maas.api.hadoop.monitor.HistoryServer;
import dhbk.maas.api.hadoop.monitor.obj.HistoryJob;
import dhbk.maas.ui.R;
import dhbk.maas.ui.utils.Conf;
import dhbk.maas.ui.utils.FormatTime;

public class HistoryTab extends Fragment implements OnClickListener{

	ArrayList<HistoryJob> his_jobs = new ArrayList<HistoryJob>() ;
	private TableLayout tableLayout;
	private AtomicBoolean isExe = new AtomicBoolean();
	private ProgressDialog pg_dialog;
	
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
		View view = inflater.inflate(R.layout.tab_history, container, false);
		
		tableLayout = (TableLayout) view.findViewById(R.id.tabhis_tableLayout) ;
		ImageButton btn_refresh = (ImageButton) view.findViewById(R.id.tabhis_btn_refresh) ;
		btn_refresh.setOnClickListener(this) ;
		
		if(his_jobs.size() == 0) {
			if(!isExe.get()) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new executeHistoryJob().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new executeHistoryJob().execute() ;
				}
				isExe.set(true) ;
			}
		} else {
			changeDataHistoryIfNeed() ;
		}
		return view;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tabhis_btn_refresh :
			if(!isExe.get()) {
				if(isConnectNetwork()) {
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new executeHistoryJob().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						new executeHistoryJob().execute() ;
					}
					isExe.set(true) ;
				} else {
					Toast.makeText(getActivity().getApplicationContext(), 
									"Not connected network !", Toast.LENGTH_SHORT).show() ;
				}
			}
			break;
		default : break;
		}
	}
	
	public void loadHistory () {
		try {
			HistoryServer his_server = new HistoryServer(Conf.address_node) ;
			his_jobs = his_server.getHistoryJobs() ;
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			isExe.set(false) ;
			closeProgressDialog() ;
		}
	}
	
	public void changeDataHistoryIfNeed () {
		if(his_jobs.size()> 0){
			if(tableLayout.getChildCount() - 1 != his_jobs.size()) {
				tableLayout.removeViews(1, tableLayout.getChildCount() - 1) ;
				for(int i = his_jobs.size() - 1; i >= 0; i--) {
					HistoryJob job = his_jobs.get(i) ;
					addTableRow(job.id, job.name, job.starttime, job.finishtime, job.state) ;
				}
			}
		}
	}
	
	private void addTableRow (String id, String name, String starttime, String finishtime, String state) {
		TableRow tableRow = (TableRow)LayoutInflater.from(getActivity()).inflate(R.layout.tabhis_tablerow, null);
		
	    ((TextView)tableRow.findViewById(R.id.tabhis_tr_id)).setText(id.substring(4, id.length()));
	    ((TextView)tableRow.findViewById(R.id.tabhis_tr_name)).setText(name);
	    ((TextView)tableRow.findViewById(R.id.tabhis_tr_starttime)).setText(FormatTime.formatDateFromMiliseconds(Long.parseLong(starttime)));
	    ((TextView)tableRow.findViewById(R.id.tabhis_tr_finishtime)).setText(FormatTime.formatDateFromMiliseconds(Long.parseLong(finishtime)));
	    ((TextView)tableRow.findViewById(R.id.tabhis_tr_state)).setText(state);
	    
	    tableLayout.addView(tableRow);
	}
	
	private boolean isConnectNetwork () {
		ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE) ;
		NetworkInfo netinfo = cm.getActiveNetworkInfo() ;
		if(netinfo != null && netinfo.isConnected())
			return true;
		else 
			return false ;
	}
	
	private void showProgressDialog () {
		if(pg_dialog == null ) {
			pg_dialog = new ProgressDialog(HistoryTab.this.getActivity());
			pg_dialog.setCancelable(false) ;
			pg_dialog.setTitle("Processing");
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
	
	private class executeHistoryJob extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog() ;
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			loadHistory() ;
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			changeDataHistoryIfNeed() ;
		}
	}

}
