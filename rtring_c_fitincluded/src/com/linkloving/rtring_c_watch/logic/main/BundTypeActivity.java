package com.linkloving.rtring_c_watch.logic.main;

import java.util.ArrayList;

import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BundTypeActivity extends Activity {
	   private static final int TYPE_BAND = 0;
	    private static final int TYPE_WATCH = 1;
	    private static final int TYPE_BAND_VERSION_3 = 2; //纽扣手环 3.0

	    public static final String KEY_TYPE = "device_type";
	    public static final String KEY_TYPE_WATCH = "watch";
	    public static final String KEY_TYPE_BAND = "band";
	    public static final String KEY_TYPE_BAND_VERSION_3 = "band3.0";
	    private TypeAdapter typeAdapeter;
	    private ArrayList<TypeVo> typeList = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bund_type);
		ListView listview =  (ListView) findViewById(R.id.bundtype_recycler);
		typeList.add(TYPE_BAND,new TypeVo(R.drawable.bound_band_on,R.string.bound_link_band));
	    typeList.add(TYPE_BAND_VERSION_3,new TypeVo(R.drawable.bound_3_on,R.string.bound_link_niukouband));
		typeAdapeter = new TypeAdapter(BundTypeActivity.this,typeList);
	}
	
	
	 private  class  TypeAdapter extends BaseAdapter{
		 private  ArrayList<TypeVo> mList ;
		 private Context mContext ;
		  public ImageView image;
          //描述
          public TextView tv;
		 public TypeAdapter(Context context,ArrayList list){
			 this.mContext = context ;
			 this.mList = list ;
		 }

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			 View view = (View) LayoutInflater.from(mContext).inflate(R.layout.item_bundtype,null);
			 image = (ImageView) view.findViewById(R.id.bund_img);
			 tv = (TextView) view.findViewById(R.id.bund_txt);
			 image.setImageResource(typeList.get(position).getImgID());
	         tv.setText(typeList.get(position).getTextID());
			 return view;
		}
		 
		
	 }
	
	  private class TypeVo{

	        public TypeVo(int imgID, int textID) {
	            this.imgID = imgID;
	            this.textID = textID;
	        }
	        //自定义数据类型
	        private int textID;

	        private int imgID;

	        public int getTextID() {
	            return textID;
	        }

	        public void setTextID(int textID) {
	            this.textID = textID;
	        }

	        public int getImgID() {
	            return imgID;
	        }

	        public void setImgID(int imgID) {
	            this.imgID = imgID;
	        }
	    }
	
}
