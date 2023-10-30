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
package dhbk.maas.api.mahout.execute;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import dhbk.maas.api.httpconnect.HttpConnect;

public class Recommendations {

	private static final String HTTP = "http://";
	public static final String RECOMMENDER_PATH = "/MaaSServlet/recommender" ;
	public static final String DEFAULT_PORT = "11000" ;
	
	private String url ;
	private String address ;
	private String port = DEFAULT_PORT ;
	
	HttpConnect httpConnect ;
	
	public Recommendations (String address) {
		this.address = address ;
		httpConnect = new HttpConnect() ;
	}
	
	public Recommendations (String address, String port) {
		this.address = address ;
		this.port = port;
		httpConnect = new HttpConnect() ;
	}
	
	public String submitRecommender (String pathDataUse, String pathDataOut) throws ClientProtocolException, IOException {
		this.url = HTTP + this.address + ":" + this.port + RECOMMENDER_PATH ;
		
		ArrayList<String[]> values = new ArrayList<>();
		values.add(new String[] {"pathInput", pathDataUse}) ;
		values.add(new String[] {"pathOutput", pathDataOut}) ;
		
		HttpResponse response = httpConnect.sendRequestPost(this.url, null, values);
		
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String buff = EntityUtils.toString(response.getEntity()) ;
			return "SUCCESS".equals(buff.toString())?"SUCCESS":"FAILURE";
		} else
			return "FAILURE";
		
	}
	
}
