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

public class ReMngClusterApp {

	public String finishedTime;
	public String amContainerLogs;
	public String trackingUI;
	public String state;
	public String user;
	public String id;
	public String clusterId;
	public String finalStatus;
	public String amHostHttpAddress;
	public String progress;
	public String name;
	public String startedTime;
	public String elapsedTime;
	public String diagnostics;
	public String trackingUrl;
	public String queue;
	public String allocatedMB;
	public String allocatedVCores;
	public String runningContainers;
	
	public ReMngClusterApp (String finishedTime, String amContainerLogs, String trackingUI, String state, String user, 
								String id, String clusterId, String finalStatus, String amHostHttpAddress, String progress,
								String name, String startedTime, String elapsedTime, String diagnostics, String trackingUrl, 
								String queue, String allocatedMB, String allocatedVCores, String runningContainers) {
		this.finishedTime = finishedTime;
		this.amContainerLogs = amContainerLogs;
		this.trackingUI = trackingUI;
		this.state = state;
		this.user = user;
		this.id = id;
		this.clusterId = clusterId ;
		this.finalStatus = finalStatus;
		this.amHostHttpAddress = amHostHttpAddress ;
		this.progress = progress;
		this.name = name;
		this.startedTime = startedTime ;
		this.elapsedTime = elapsedTime ;
		this.diagnostics = diagnostics ;
		this.trackingUrl = trackingUrl ;
		this.queue = queue ;
		this.allocatedMB = allocatedMB;
		this.allocatedVCores = allocatedVCores;
		this.runningContainers = runningContainers ;
	}
	
}
