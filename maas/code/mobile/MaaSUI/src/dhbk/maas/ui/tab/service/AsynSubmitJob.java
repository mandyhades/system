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

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;
import dhbk.maas.api.mahout.execute.Recommendations;
import dhbk.maas.ui.utils.Conf;
import dhbk.maas.ui.utils.Notifications;

public class AsynSubmitJob {

//	static String res = "";
	
	public static class SubmitJobRecommender extends AsyncTask<String, Void, String> {

		private SubmitJobService sjs ;
		
		public SubmitJobRecommender(SubmitJobService sjs) {
			// TODO Auto-generated constructor stub
			this.sjs = sjs ;
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Recommendations rcd = new Recommendations(Conf.address_node);
			String res = "";
			try {
				res = rcd.submitRecommender(arg0[0], arg0[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				sjs.isExe.set(false) ;
			}
			return res;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Notifications.notifyCompleteJob(sjs.getApplicationContext(), "Recommender", "Finish " + result) ;
		}
		
	}
}
