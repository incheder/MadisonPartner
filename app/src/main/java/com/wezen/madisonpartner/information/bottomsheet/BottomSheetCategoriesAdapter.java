package com.wezen.madisonpartner.information.bottomsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.wezen.madisonpartner.R;

import java.util.List;

public class BottomSheetCategoriesAdapter extends
		RecyclerView.Adapter<ViewHolder> {

	private List<String> categories;
	private Context context;


	public BottomSheetCategoriesAdapter(List<String> categories, Context context) {
		this.categories = categories;
		this.context = context;
	}

	public static class CategoryBottomSheetViewHolder extends ViewHolder {
		CheckBox category;

		CategoryBottomSheetViewHolder(View itemView) {
			super(itemView);
			category = (CheckBox)itemView.findViewById(R.id.checkboxCategory);
		}
	}

	@Override
	public int getItemCount() {
		return categories.size();
	}


	@SuppressLint("NewApi")
	@Override
	public void onBindViewHolder(ViewHolder viewholder, int position) {
		String item = categories.get(position);
		CategoryBottomSheetViewHolder holder = (CategoryBottomSheetViewHolder) viewholder;
		holder.category.setText(item);

	}


	@Override
	public CategoryBottomSheetViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_bottomsheet_categories, viewGroup, false);
		CategoryBottomSheetViewHolder pvh = new CategoryBottomSheetViewHolder(v);
		return pvh;
	}
}
