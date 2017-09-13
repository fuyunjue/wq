package com.bowin.lib.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.cn.wq.R;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;

/**
 * 循环滚动切换图片(支持带标题,不带标题传null即可),自带适配器 支持显示本地res图片和网络图片，指定uri的图片
 * OnPagerClickCallback onPagerClickCallback 处理page被点击的回调接口,
 * 被用户手动滑动时，暂停滚动，手动可以无限滑动
 * 
 * @modify Minkex
 * 
 */
public class RollViewPager extends ViewPager {
	public RollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		viewPagerTask = new ViewPagerTask();
		myOnTouchListener = new MyOnTouchListener();
		initDefaultImageBg();
		initFinalBitmap();
	}

	public RollViewPager(Context context) {
		super(context);
		this.context = context;
		viewPagerTask = new ViewPagerTask();
		myOnTouchListener = new MyOnTouchListener();
		initDefaultImageBg();
		initFinalBitmap();
	}

	private Context context;
	private int currentItem;
	private String[] uriList;// 图片地址
	private String[] menuUrlList;// 图片点击链接
	private ArrayList<View> dots;// 点的位置不固定，所以需要让调用者传入
	private TextView title;// 用于显示每个图片的标题
	private String[] titles;
	private int[] resImageIds;
	private int dot_focus_resId;// 获取焦点的图片id
	private int dot_normal_resId;// 未获取焦点的图片id
	private OnPagerClickCallback onPagerClickCallback;
	private boolean isShowResImage = false;// 是否展示本地图片
	private MyOnTouchListener myOnTouchListener;
	private ViewPagerTask viewPagerTask;
	
	

	private long start = 0;

	public class MyOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				start = System.currentTimeMillis();
				handler.removeCallbacksAndMessages(null);
				break;
			case MotionEvent.ACTION_MOVE:
				handler.removeCallbacks(viewPagerTask);
				break;
			case MotionEvent.ACTION_CANCEL:
				startRoll();
				break;
			case MotionEvent.ACTION_UP:
				long duration = System.currentTimeMillis() - start;
				if (duration <= 400) {
					if (onPagerClickCallback != null)
						onPagerClickCallback.onPagerClick(currentItem ,currentItem>=menuUrlList.length?null:menuUrlList[currentItem]);
				}
				startRoll();
				break;
			}
			return true;
		}
	}

	public class ViewPagerTask implements Runnable {
		@Override
		public void run() {

			currentItem = (currentItem + 1)
					% (isShowResImage ? resImageIds.length : uriList.length);
			handler.obtainMessage().sendToTarget();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setCurrentItem(currentItem);
			startRoll();
		}
	};

	public void setDot(ArrayList<View> dots, int dot_focus_resId,
			int dot_normal_resId) {
		this.dots = dots;
		this.dot_focus_resId = dot_focus_resId;
		this.dot_normal_resId = dot_normal_resId;
	}

	public void setPagerCallback(OnPagerClickCallback onPagerClickCallback) {
		this.onPagerClickCallback = onPagerClickCallback;
	}

	/**
	 * 设置网络图片的url集合，也可以是本地图片的uri
	 * 图片uriList集合，可以是网络地址，如：http://www.ssss.cn/3.jpg，也可以是本地的uri,如：
	 * assest://image/3.jpg，uriList和resImageIds只需传入一个
	 * 
	 * @param uriList
	 */
	public void setUriList(String[] uriList ,String[] menuUrlList) {
		isShowResImage = false;
		this.uriList = uriList;
		this.menuUrlList = menuUrlList;
	}

	/**
	 * 设置res图片的id 图片uriList集合，可以是网络地址，如：http://www.ssss.cn/3.jpg，也可以是本地的uri,如：
	 * assest://image/3.jpg，uriList和resImageIds只需传入一个
	 * 
	 * @param resImageIds
	 */
	public void setResImageIds(int[] resImageIds) {
		isShowResImage = true;
		this.resImageIds = resImageIds;
	}

	/**
	 * 标题相关
	 * 
	 * @param title
	 *            用于显示标题的TextView
	 * @param titles
	 *            标题数组
	 */
	public void setTitle(TextView title, String[] titles) {
		this.title = title;
		this.titles = titles;
		if (title != null && titles != null && titles.length > 0)
			title.setText(titles[0]);// 默认显示第一张的标题
	}

	private boolean hasSetAdapter = false;
	private final int SWITCH_DURATION = 3500;

	public void setAadpter() {
		if (!hasSetAdapter) {
			hasSetAdapter = true;
			this.setOnPageChangeListener(new MyOnPageChangeListener());
			this.setAdapter(new ViewPagerAdapter());

		}
	}

	/**
	 * 开始滚动
	 */
	public void startRoll() {

		handler.postDelayed(viewPagerTask, SWITCH_DURATION);
	}

	class MyOnPageChangeListener implements OnPageChangeListener {
		int oldPosition = 0;

		@Override
		public void onPageSelected(int position) {
			currentItem = position;
			if (title != null && titles != null) {
				title.setText(titles[position]);
			}

			if (dots != null && dots.size() > 0) {

				if (position == 1 || position == getAdapter().getCount() - 1) {
					// 如果是第一图片被选中，则就将第一个点显示红
					dots.get(oldPosition).setBackgroundResource(
							dot_normal_resId);
					dots.get(1).setBackgroundResource(dot_focus_resId);

				} else if (position == 0
						|| position == getAdapter().getCount() - 2) {
					// 如果是最后一个图片被选中，则就将最后一个图片点显示红
					dots.get(oldPosition).setBackgroundResource(
							dot_normal_resId);
					dots.get(getAdapter().getCount() - 2)
							.setBackgroundResource(dot_focus_resId);
				} else {

					dots.get(position).setBackgroundResource(dot_focus_resId);
					dots.get(oldPosition).setBackgroundResource(
							dot_normal_resId);
				}

			}
			oldPosition = position;
		}

		/** onPageScrollStateChanged方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。 */
		@Override
		public void onPageScrollStateChanged(int arg0) {

			switch (arg0) {
			case 1:// 手势滑动，如果这里不设置，快速滑动的时候会有问题，可自行注释后测试
					// 当前为最后一张，此时从右向左滑，则切换到第一张
				if (getCurrentItem() == getAdapter().getCount() - 1) {
					setCurrentItem(1, false);
				}
				// 当前为第一张，此时从左向右滑，则切换到最后一张
				else if (getCurrentItem() == 0) {
					setCurrentItem(getAdapter().getCount() - 2, false);
				}
				break;
			case 2:// 界面切换中
				break;
			case 0:// 滑动结束，即切换完毕或者加载完毕
					// 当前为最后一张，此时从右向左滑，则切换到第一张
				if (getCurrentItem() == getAdapter().getCount() - 1) {
					setCurrentItem(1, false);
				}
				// 当前为第一张，此时从左向右滑，则切换到最后一张
				else if (getCurrentItem() == 0) {
					setCurrentItem(getAdapter().getCount() - 2, false);
				}

				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}
	
	/**
	 * 设置一个全局的图片加载的finalbitmap
	 */
	public FinalBitmap mFinalBitmap;
	public void initFinalBitmap(){
		if(null == mFinalBitmap){
			mFinalBitmap = FinalBitmap.create(context);
			mFinalBitmap.configMemoryCachePercent(0.8f);
			mFinalBitmap.configBitmapLoadThreadSize(40);
			mFinalBitmap.configLoadfailImage(DefaultImageBg);
			mFinalBitmap.configLoadingImage(DefaultImageBg);
		}
	}
	public static Bitmap DefaultImageBg;
	private static int DefaultImageBgId = R.drawable.image_loading_bg_point;
	/**
	 * 初始化一些需要用到的背景图为bitmap
	 */
	public void initDefaultImageBg() {
		if (null == DefaultImageBg) {
			DefaultImageBg = BitmapFactory.decodeResource(this.getResources(),
					DefaultImageBgId);
		}
	}

	/**
	 * 自带设配器,需要回调类来处理page的点击事件
	 * 
	 * 
	 */
	class ViewPagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return isShowResImage ? resImageIds.length : uriList.length;
		}

		@Override
		public Object instantiateItem(View container, final int position) {
			View view = View.inflate(context, R.layout.m_rollviewpager_item,
					null);
			((ViewPager) container).addView(view);
			view.setOnTouchListener(myOnTouchListener);
			ImageView imageView = (ImageView) view.findViewById(R.id.image);
			if (isShowResImage) {
				imageView.setImageResource(resImageIds[position]);
			} else {
				//显示图片
				mFinalBitmap.display(imageView, uriList[position]);
			}
			return view;
		}
		

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// 将ImageView从ViewPager移除
			((ViewPager) arg0).removeView((View) arg2);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		handler.removeCallbacksAndMessages(null);
		super.onDetachedFromWindow();
	}

	/**
	 * 处理page点击的回调接口
	 * 
	 * @author dance
	 * 
	 */
	public interface OnPagerClickCallback {
		public abstract void onPagerClick(int position, String url);
	}
}
