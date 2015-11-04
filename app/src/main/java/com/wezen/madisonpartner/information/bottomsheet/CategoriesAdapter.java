package com.wezen.madisonpartner.information.bottomsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wezen.madisonpartner.R;
import com.wezen.madisonpartner.information.Category;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoriesAdapter extends
		RecyclerView.Adapter<ViewHolder> {

	private List<Category> categories;
	private Context context;


	public CategoriesAdapter(List<Category> categories, Context context) {
		this.categories = categories;
		this.context = context;
	}

	public static class CategoryBottomSheetViewHolder extends ViewHolder {
		ImageView categoryImage;
		TextView categoryName;

		CategoryBottomSheetViewHolder(View itemView) {
			super(itemView);
			categoryImage = (ImageView)itemView.findViewById(R.id.bottomsheetCategoryImage);
			categoryName = (TextView)itemView.findViewById(R.id.bottomsheetCategoryName);
		}
	}

	@Override
	public int getItemCount() {
		return categories.size();
	}


	@SuppressLint("NewApi")
	@Override
	public void onBindViewHolder(ViewHolder viewholder, int position) {
		final Category item = categories.get(position);
		final CategoryBottomSheetViewHolder holder = (CategoryBottomSheetViewHolder) viewholder;
		holder.categoryName.setText(item.getName());
		holder.categoryImage.setBackgroundColor(Color.parseColor(item.getMainColor()));
		Picasso.with(context).load(item.getImage()).into(holder.categoryImage);


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


	@Override
	public CategoryBottomSheetViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_bottomsheet_categories, viewGroup, false);
		CategoryBottomSheetViewHolder pvh = new CategoryBottomSheetViewHolder(v);
		return pvh;
	}
}
