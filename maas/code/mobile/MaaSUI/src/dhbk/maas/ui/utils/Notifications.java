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
package dhbk.maas.ui.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import dhbk.maas.ui.R;

public class Notifications {

	public static void notifyCompleteJob (Context ct, String title, String content) {
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(ct)
		.setSmallIcon(R.drawable.smallicon)
		.setTicker(title)
		.setContentTitle(title)
		.setContentText(content) ;
		
		NotificationManager nm = (NotificationManager) ct.getSystemService(Context.NOTIFICATION_SERVICE) ;
		nm.notify(0, builder.build()) ;
	}
}
