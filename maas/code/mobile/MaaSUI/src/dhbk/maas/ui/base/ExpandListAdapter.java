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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import dhbk.maas.ui.R;
import dhbk.maas.ui.saharaobject.Cluster;
import dhbk.maas.ui.saharaobject.Node;

public class ExpandListAdapter extends BaseExpandableListAdapter{

	private Context context ;
	private ArrayList<Cluster> cluster;
	private ArrayList<ArrayList<Node>> node ;
	
	public ExpandListAdapter (Context context, ArrayList<Cluster> cluster, ArrayList<ArrayList<Node>> node) {
		this.context = context;
		this.cluster = cluster;
		this.node = node;
	}
	
	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return node.get(arg0).get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return arg1;
	}

	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
			ViewGroup arg4) {
		// TODO Auto-generated method stub
		View v = arg3;
		if(v == null)
			v = LayoutInflater.from(context).inflate(R.layout.item_child, arg4, false) ;
		
		TextView tv = (TextView) v.findViewById(R.id.child_tv) ;
		tv.setText(node.get(arg0).get(arg1).getType() + " : " +node.get(arg0).get(arg1).getName());
		return v;
	}

	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return node.get(arg0).size();
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return cluster.get(arg0);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return cluster.size();
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
		// TODO Auto-generated method stub
		View v = arg2;
		if(v == null)
			v = LayoutInflater.from(context).inflate(R.layout.item_group, arg3, false) ;
		TextView tv = (TextView) v.findViewById(R.id.group_tv);
		tv.setText(cluster.get(arg0).getName()) ;
		
		return v;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
