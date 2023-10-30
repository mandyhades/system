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
package dhbk.maas.api.httpconnect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

@SuppressWarnings("deprecation")
public class HttpConnect {

	private HttpClient httpClient;
	
	public HttpConnect () {
		httpClient = new DefaultHttpClient() ;
	}
	
	/**
	 * send request get
	 * @param url
	 * @param headers
	 * @return HttpResponse
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResponse sendRequestGet (String url, Header[] headers) 
						throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url) ;
		if(headers != null) {
			for(Header header : headers) 
				request.addHeader(header);
		}
		request.addHeader("Accept", "application/json");
		return httpClient.execute(request) ;
	}
	
	/**
	 * send request post
	 * @param url
	 * @param headers
	 * @param values = name : value
	 * @return HttpResponse
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResponse sendRequestPost (String url, Header[] headers, ArrayList<String[]> values) 
						throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(url) ;
		if(headers != null) {
			for(Header header : headers) 
				request.addHeader(header);
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>() ;
		for (String[] value : values) {
			params.add(new BasicNameValuePair(value[0], value[1])) ;
		}
		
		request.setEntity(new UrlEncodedFormEntity(params));
		
		return httpClient.execute(request) ;
	}
}
