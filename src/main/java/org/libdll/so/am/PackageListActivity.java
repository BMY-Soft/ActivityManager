package org.libdll.so.am;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PackageListActivity extends ListActivity implements SearchView.OnQueryTextListener {
	//private List<PackageInfo> package_list_info_list = new ArrayList<>();

	private final static Comparator<PackageInfo> package_name_comparator = new Comparator<PackageInfo>() {
		private int compare(String a, String b) {
			int len1 = a.length();
			int len2 = b.length();
			int i = 0;
			while(i < len1 && i < len2) {
				char c1 = a.charAt(i);
				char c2 = b.charAt(i);
				if(c1 < c2) return -1;
				else if(c1 > c2) return 1;
				i++;
			}
			if(len1 < len2) return -1;
			else if(len1 > len2) return 1;
			return 0;
		}
		@Override
		public int compare(PackageInfo lhs, PackageInfo rhs) {
			return compare(lhs.packageName, rhs.packageName);
		}
	};

	public class PackageListAdapter extends BaseAdapter {
		Context context;
		List<PackageInfo> package_list;

		public PackageListAdapter(Context context) {
			//super(context, R.layout.package_list_item);
			Log.i("method", "PackageListActivity.PackageListAdapter::<init>(Context)");
			this.context = context;
			package_list = context.getPackageManager().getInstalledPackages(0);
			Collections.sort(package_list, package_name_comparator);
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public int getCount() {
			return package_list == null ? 0 : package_list.size();
		}

		//@Override
		public void bindView(View view, PackageInfo info) {
			ImageView icon = (ImageView)view.findViewById(R.id.icon);
			TextView name = (TextView)view.findViewById(R.id.name);
			TextView description = (TextView)view.findViewById(R.id.description);
			icon.setImageDrawable(info.applicationInfo.loadIcon(getPackageManager()));
			name.setText(info.applicationInfo.loadLabel(getPackageManager()).toString());
			description.setText(info.packageName);
			view.setContentDescription(info.packageName);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//Log.i("method", String.format("PackageListAdapter::getView(%d, )", position));
			View r;
			if (convertView == null) {
				r = ((LayoutInflater)context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.package_list_item, parent, false);
			} else {
				r = convertView;
			}
			bindView(r, package_list.get(position));
			return r;
		}
	}

	private PackageListAdapter adapter;

	private void load_packages() {
		Log.i("method", "PackageListActivity::load_packages()");
		adapter = new PackageListAdapter(this);
		setListAdapter(adapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_package_list);
		load_packages();
		//Log.i("ActionBar", getActionBar() == null ? "N" : "Y");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_package_list, menu);
		getMenuInflater().inflate(R.menu.menu_main, menu);
		SearchView search_view = (SearchView)menu.findItem(R.id.action_search).getActionView();
		search_view.setOnQueryTextListener(this);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Log.i("onOptionsItemSel: id", String.valueOf(id));

		//noinspection SimplifiableIfStatement
		if(id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onListItemClick(ListView l, View v, int pos, long id) {
		//String package_name = ((TextView)v.findViewById(R.id.description)).toString();
		//Toast.makeText(this, v.getContentDescription(), Toast.LENGTH_SHORT).show();

		String package_name = v.getContentDescription().toString();
		if(package_name.isEmpty()) return;
		Intent intent = new Intent(null, Uri.fromParts("package", package_name, null));
		intent.setClass(this, ActivityListActivity.class);
		startActivity(intent);
	}

	public boolean onQueryTextSubmit(String query) {
		Toast.makeText(this, "Searching for: " + query + "...", Toast.LENGTH_SHORT).show();
		return true;
	}

	public boolean onQueryTextChange(String new_text) {
		Toast.makeText(this, "Net text: " + new_text, Toast.LENGTH_SHORT).show();
		return true;
	}
}
