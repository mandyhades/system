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

import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import dhbk.maas.api.hadoop.monitor.ResourceManage;
import dhbk.maas.api.hadoop.monitor.obj.ReMngClusterApp;
import dhbk.maas.ui.R;
import dhbk.maas.ui.utils.Conf;
import dhbk.maas.ui.utils.FormatTime;

public class ResourceTab extends Fragment{

	public ArrayList<ReMngClusterApp> clusterApps = new ArrayList<ReMngClusterApp> ();

	private AtomicBoolean isExe = new AtomicBoolean() ;
	private Handler handler; 
	private Runnable updateProgress = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isExe.get()) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new executeClusterApps().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new executeClusterApps().execute() ;
				}
				isExe.set(true) ;
			}
			handler.postDelayed(updateProgress, 3000) ;
		}
	};
	
	private TableLayout tableLayout; 
	
	
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
		View view = inflater.inflate(R.layout.tab_resource, container, false);
		
		tableLayout = (TableLayout) view.findViewById(R.id.tabresource_tableLayout) ;
		changeDataClusterAppIfNeed() ;
		handler = new Handler() ;
		handler.post(updateProgress) ;
		return view ;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		handler.removeCallbacks(updateProgress) ;
		handler = null; 
		super.onDestroyView();
		
	}
	
	private void loadClusterApps () {
		
		try {
			ResourceManage rm = new ResourceManage(Conf.address_node) ;
			clusterApps = rm.getClusterApps() ;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			isExe.set(false) ;
		}
	}
	
	private void changeDataClusterAppIfNeed () {
		if(clusterApps.size() > 0) {
			if(tableLayout.getChildCount() - 1 != clusterApps.size()) {
				tableLayout.removeViews(1, tableLayout.getChildCount() - 1) ;
				for(int i = clusterApps.size() - 1; i >=0 ; i--) {
					ReMngClusterApp ca = clusterApps.get(i) ;
					addTableRow(ca.id, ca.name, ca.startedTime, ca.elapsedTime, ca.progress, ca.state) ;
				}
			} else {
				TableRow tableRow = (TableRow) tableLayout.getChildAt(1) ;
				ProgressBar pgBar = (ProgressBar) ((LinearLayout)tableRow.getChildAt(4)).getChildAt(0);
				pgBar.setMax(100) ;
			    pgBar.setProgress((int)Float.parseFloat((clusterApps.get(clusterApps.size() - 1)).progress)) ;
			}
		}
	}
	
	private void addTableRow (String id, String name, String starttime, String duration, String progress, String state) {
		TableRow tableRow = (TableRow)LayoutInflater.from(getActivity()).inflate(R.layout.tabresource_tablerow, null);
		
	    ((TextView)tableRow.findViewById(R.id.tabres_tr_id)).setText(id.substring(12, id.length()));
	    ((TextView)tableRow.findViewById(R.id.tabres_tr_name)).setText(name);
	    ((TextView)tableRow.findViewById(R.id.tabres_tr_starttime)).setText(FormatTime.formatDateFromMiliseconds(Long.parseLong(starttime)));
	    ((TextView)tableRow.findViewById(R.id.tabres_tr_duration)).setText(duration);
	    ((TextView)tableRow.findViewById(R.id.tabres_tr_state)).setText(state);
	    ProgressBar pgBar = (ProgressBar) tableRow.findViewById(R.id.tabres_tr_progressbar);
	    pgBar.setMax(100) ;
	    pgBar.setProgress((int)Float.parseFloat(progress)) ;
	    
	    tableLayout.addView(tableRow);
	}
	
	
	
	private class executeClusterApps extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			loadClusterApps() ;
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			changeDataClusterAppIfNeed() ;
		}
	}
	
	
}
