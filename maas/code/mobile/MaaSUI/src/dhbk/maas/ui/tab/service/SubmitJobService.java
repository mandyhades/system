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
package dhbk.maas.ui.tab.service;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;
import dhbk.maas.ui.tab.service.AsynSubmitJob.SubmitJobRecommender;

public class SubmitJobService extends Service{

	public static final String ACTION_SUBMIT_NAIVEBAYES = "naive bayes" ;
	public static final String ACTION_SUBMIT_HIDDENMARKOVMODELS = "hidden markov modeks" ;
	public static final String ACTION_SUBMIT_LOGISTICREGRESSION = "logistic regression" ;
	public static final String ACTION_SUBMIT_RANDOMFOREST = "random forest" ;
	public static final String ACTION_SUBMIT_KMEANS = "k means" ;
	public static final String ACTION_SUBMIT_CANOPY = "canopy" ;
	public static final String ACTION_SUBMIT_FUZZYKMEANS = "fuzzy k means" ;
	public static final String ACTION_SUBMIT_RECOMMENDER = "recommender" ;
	public static final String ACTION_NONE = "none" ;
	
	public AtomicBoolean isExe = new AtomicBoolean();
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		isExe.set(false) ;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent != null) {
			String action = intent.getAction() ;
			Bundle b = intent.getExtras() ;
			String pathData = null;
			String pathOutput = "";
			try{
				pathData = b.getString("pathdata");
				pathOutput = b.getString("pathOutput") ;
			} catch (Exception e) {}
			
			if (pathData != null) {
				if(!isExe.get()) {
					if(ACTION_SUBMIT_RECOMMENDER.equals(action)) {
						AsynSubmitJob.SubmitJobRecommender sr = new SubmitJobRecommender(SubmitJobService.this) ;
						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							sr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pathData, pathOutput) ;
						} else {
							sr.execute(pathData, pathOutput);
						}
						isExe.set(true) ;
					}
				} else {
					Toast.makeText(getApplicationContext(), "Job is running !", Toast.LENGTH_SHORT).show() ;
				}
			} 
			
			intent.setAction(ACTION_NONE) ;
		}
		
		return START_STICKY ;
	}
	
}
