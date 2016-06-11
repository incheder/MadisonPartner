package com.wezen.madisonpartner.information.bottomsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.information.Category;
import com.wezen.madisonpartner.information.InformationFragment;

import java.util.List;

public class CategoriesAdapter extends
		RecyclerView.Adapter<ViewHolder> {

	private List<Category> categories;
	private Context context;
	private InformationFragment informationFragment;
	private boolean isAdding;
	private boolean isDynamic;


	public CategoriesAdapter(List<Category> categories, Context context,InformationFragment informationFragment,boolean isAdding, boolean isDynamic) {
		this.categories = categories;
		this.context = context;
		this.informationFragment = informationFragment;
		this.isAdding = isAdding;
		this.isDynamic = isDynamic;

	}

	public static class CategoryBottomSheetViewHolder extends ViewHolder {
		ImageView categoryImage;
		TextView categoryName;
		LinearLayout row;

		CategoryBottomSheetViewHolder(View itemView) {
			super(itemView);
			categoryImage = (ImageView)itemView.findViewById(R.id.bottomsheetCategoryImage);
			categoryName = (TextView)itemView.findViewById(R.id.bottomsheetCategoryName);
			row = (LinearLayout)itemView.findViewById(R.id.bottomsheetRow);
		}
	}

	@Override
	public int getItemCount() {
		return categories.size();
	}


	@SuppressLint("NewApi")
	@Override
	public void onBindViewHolder(ViewHolder viewholder, final int position) {
		final Category item = categories.get(position);
		final CategoryBottomSheetViewHolder holder = (CategoryBottomSheetViewHolder) viewholder;
		holder.categoryName.setText(item.getName());
		holder.categoryImage.setBackgroundColor(Color.parseColor(item.getMainColor()));
		Picasso.with(context).load(item.getImage()).into(holder.categoryImage);
		holder.categoryImage.setColorFilter(Color.WHITE);
		holder.row.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isDynamic){
					if(isAdding){
						informationFragment.addCategory(item, position);
					} else {
						//removeAt(position);
						informationFragment.removeCategory(item,position);
					}
				}
			}
		});


		/*Picasso.with(context).load(item.getImage()).into(new Target() {
			@Override
			public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
				Drawable color = new ColorDrawable(Color.parseColor(item.getMainColor()));
				Drawable image = new BitmapDrawable(context.getResources(),bitmap);
				LayerDrawable ld = new LayerDrawable(new Drawable[]{color,image});
				holder.categoryImage.setImageDrawable(ld);
				holder.categoryImage.invalidate();
			}

			@Override
			public void onBitmapFailed(Drawable errorDrawable) {

			}

			@Override
			public void onPrepareLoad(Drawable placeHolderDrawable) {

			}
		});*/



	}

	public void removeAt(int position) {
		categories.remove(position);
		notifyItemRemoved(position);
		notifyItemRangeChanged(position, categories.size());
	}

	@Override
	public CategoryBottomSheetViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_bottomsheet_categories, viewGroup, false);
		CategoryBottomSheetViewHolder pvh = new CategoryBottomSheetViewHolder(v);
		return pvh;
	}
}
