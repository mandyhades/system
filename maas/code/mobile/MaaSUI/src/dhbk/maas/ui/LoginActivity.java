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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import dhbk.maas.ui.tab.service.SubmitJobService;
import dhbk.maas.ui.utils.Conf;

public class LoginActivity extends Activity{

	private EditText ed_address ;
	private EditText ed_user ;
	private EditText ed_pass ;
	private ProgressDialog pg_dialog ;
	
	private static final String DEFAULT_PORT_LOGIN = "5000" ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login) ;
		
		ed_address = (EditText) findViewById(R.id.login_ed_address) ;
		ed_user = (EditText) findViewById(R.id.login_ed_username) ;
		ed_pass = (EditText) findViewById(R.id.login_ed_pass) ;
		
		ed_address.setText(Conf.address) ;
		ed_user.setText(Conf.username) ;
		ed_pass.setText(Conf.pass) ;
		 
		Button btn_login = (Button) findViewById(R.id.login_btn_login) ;
		btn_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isConnectNetwork()) {
					new authenticationLogin().execute() ;
				} else {
					Toast.makeText(getApplicationContext(), "Check connect network again !", Toast.LENGTH_SHORT).show() ;	
				}
			}
		});
		
		Intent it = new Intent(getApplicationContext(), SubmitJobService.class);
		it.setAction(SubmitJobService.ACTION_NONE) ;
		startService(it) ;
	}
	
	private String authentication () throws ClientProtocolException, IOException, ParseException, 
											JSONException, IllegalArgumentException {
		Conf.address = ed_address.getText().toString();
		Conf.username = ed_user.getText().toString();
		Conf.pass = ed_pass.getText().toString();
		
		if("".equals(Conf.address)) {
			return null ;
		} else {
			String url = "http://" + Conf.address + ":" + DEFAULT_PORT_LOGIN + "/v2.0/tokens";
			String jsonAuthx = "{\"auth\": {\"tenantName\": \"" + Conf.username  	+ 
			"\", \"passwordCredentials\": {\"username\": \""   	+
			Conf.username + "\", \"password\": \"" + Conf.pass 	+ 
			"\"}}}";
			
			HttpClient httpClient = new DefaultHttpClient ();
			HttpPost request = new HttpPost(url) ;
			request.addHeader("Content-Type", "application/json");
			request.addHeader("Accept", "application/json");
			request.setEntity(new StringEntity(jsonAuthx, "UTF-8"));
			HttpResponse response = httpClient.execute(request) ;
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				JSONObject jso_parent = new JSONObject(EntityUtils.toString(response.getEntity())) ;
				JSONObject jso_access = jso_parent.getJSONObject("access") ;
				JSONObject jso_token  = jso_access.getJSONObject("token") ;
				JSONObject jso_tenant = jso_token.getJSONObject("tenant") ;
				Conf.tokenId = jso_token.getString("id");
				Conf.tenantId = jso_tenant.getString("id");
				
				return "SUCCESS" ;
			} else {
				return null;
			}
		}
		
	}
	
	private boolean isConnectNetwork () {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE) ;
		NetworkInfo netinfo = cm.getActiveNetworkInfo() ;
		if(netinfo != null && netinfo.isConnected())
			return true;
		else 
			return false ;
	}
	
	private void showProgressDialog () {
		if(pg_dialog == null ) {
			pg_dialog = new ProgressDialog(LoginActivity.this);
			pg_dialog.setCancelable(false) ;
			pg_dialog.setTitle("Connecting ...");
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

	private class authenticationLogin extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog() ;
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String s = null;
			try {
				s = authentication() ;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
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
			if(result == null) {
				Toast.makeText(getApplicationContext(), "Can't login. Try again !", Toast.LENGTH_SHORT).show() ; 
			} else {
				Intent i = new Intent(getApplicationContext(), ClusterSaharaActivity.class) ;
				startActivity(i) ;
			}
		}
	}
	
}
