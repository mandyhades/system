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
package dhbk.maas.ui.base;

import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

public class TabFactory implements TabContentFactory{

	private final Context ct;
	
	public TabFactory(Context ct) {
		// TODO Auto-generated constructor stub
		this.ct = ct;
	}
	
	@Override
	public View createTabContent(String arg0) {
		// TODO Auto-generated method stub
		View view = new View(ct);
		view.setMinimumWidth(0);
		view.setMinimumHeight(0);
		return view;
	}

}
