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
package dhbk.maas.api.hadoop.monitor.obj;

public class HistoryJob {

	public String submitTime;
	public String state;
	public String user;
	public String reducestotal;
	public String mapscompleted;
	public String starttime;
	public String id;
	public String name;
	public String reducescompleted;
	public String mapstotal;
	public String queue;
	public String finishtime;
	
	public HistoryJob (String submitTime, String state, String user, String reducesToal, String mapsCompleted, 
			String startTime, String id, String name, String reducesCompleted, String mapsTotal, 
			String queue, String finish) {
		this.submitTime = submitTime;
		this.finishtime = finish;
		this.id = id;
		this.mapscompleted = mapsCompleted;
		this.mapstotal = mapsTotal;
		this.name = name ;
		this.queue = queue;
		this.reducescompleted = reducesCompleted;
		this.reducestotal = reducesToal;
		this.starttime = startTime;
		this.state = state;
		this.user = user ;
	}
}
