package net.jsiq.marketing.fragment;

import java.util.ArrayList;
import java.util.List;

import net.jsiq.marketing.R;
import net.jsiq.marketing.adapter.ContentAdapter;
import net.jsiq.marketing.constants.URLStrings;
import net.jsiq.marketing.model.ContentItem;
import net.jsiq.marketing.util.LoaderUtil;
import net.jsiq.marketing.util.MessageToast;
import net.jsiq.marketing.util.NetworkUtils;
import net.jsiq.marketing.util.ViewHelper;
import net.jsiq.marketing.view.ViewFlowHeaderView;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class ContentFragment extends SherlockFragment implements
		OnItemClickListener, OnClickListener {

	public static final String CATALOG_ID = "catalog_id";

	private Context context;

	private int catalogId;

	private ListView listview;
	private ContentAdapter adapter;
	private View loadingHintView, loadingFailedHintView;
	private View headerView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getSherlockActivity();
		// get catalogId
		catalogId = getArguments().getInt(CATALOG_ID);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View convertView = inflater.inflate(R.layout.headerview_list, null);
		listview = (ListView) convertView.findViewById(R.id.content_list);
		loadingHintView = convertView.findViewById(R.id.loadingHint);
		loadingFailedHintView = convertView
				.findViewById(R.id.loadingFailedHint);
		setListeners();
		return convertView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadContent();
	}

	private void loadContent() {
		listview.setVisibility(View.GONE);
		if (NetworkUtils.isNetworkConnected(context)) {
			loadingHintView.setVisibility(View.VISIBLE);
			loadingFailedHintView.setVisibility(View.GONE);
			String loadContentUrl = URLStrings.GET_CONTENTS_BY_CATALOGID_PAGENO
					+ catalogId + "/1";
			new LoadContentTask().execute(loadContentUrl);
		} else {
			MessageToast.showText(context, R.string.notConnected);
			loadingFailedHintView.setVisibility(View.VISIBLE);
		}
	}

	// list.addAll() or use local variables in AsyncTask
	class LoadContentTask extends AsyncTask<String, Void, List<ContentItem>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (adapter != null) {
				adapter.clear();
			}
		}

		@Override
		protected List<ContentItem> doInBackground(String... params) {
			try {
				return LoaderUtil.loadContentItems(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<ContentItem> result) {
			super.onPostExecute(result);
			if (result == null) {
				MessageToast.showText(context, R.string.loadFailed);
				loadingFailedHintView.setVisibility(View.VISIBLE);
			} else {
				initListView(result);
				listview.setVisibility(View.VISIBLE);
			}
			loadingHintView.setVisibility(View.GONE);
		}

	}

	private void initListView(List<ContentItem> result) {
		List<ContentItem> topShowContent = new ArrayList<ContentItem>();
		for (ContentItem item : result) {
			if (item.getTopShowFlag() == 1) {
				topShowContent.add(item);
			}
		}
		if (topShowContent.size() > 0) {
			headerView = new ViewFlowHeaderView(context, topShowContent);
			listview.addHeaderView(headerView);
			result.removeAll(topShowContent);
		}
		adapter = new ContentAdapter(context, result);
		listview.setAdapter(adapter);
	}

	private void setListeners() {
		loadingFailedHintView.setOnClickListener(this);
		listview.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		int selectedPos = position;
		if (headerView != null) {
			selectedPos = position - 1;
		}
		ContentItem selectedItem = adapter.getItem(selectedPos);
		ViewHelper.startContentDisplayActivityByContent(context, selectedItem);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loadingFailedHint:
			loadContent();
			break;
		default:
			break;
		}
	}

}
