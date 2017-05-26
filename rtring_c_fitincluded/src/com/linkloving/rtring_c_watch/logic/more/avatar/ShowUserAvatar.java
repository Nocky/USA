package com.linkloving.rtring_c_watch.logic.more.avatar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.linkloving.rtring_c_watch.R;

/**
 * 显示用户头像包装类.
 * <p>
 * <b>本类在显示用户头像的逻辑上是这样的：</b><br>
 * 1) 先无条件查找本地有否该用户的缓存头像，如果有则显示之;<br>
 * 2) 尝试去服务端更新最新头像（如果服务端已经有最新的头像了）.
 * 
 * @author Jack Jiang, 2013-12-13
 * @version 1.0
 */
public class ShowUserAvatar
{
	private Activity parentActivity = null;
	private String uidForWho = null;
	private ImageView viewAvatar = null;
	/** 
	 * 本地用缓存头像的情况下：是否需要尝试从服务端更新用户头像.
	 * true表示需要查询看看服务端有无最新头像，否则将不去查询.
	 * <p>
	 * 换句话说，如果本地没有用户缓存头像则无论本参数如何设置也会去服务端试读取最新头像哦  */
	private boolean needTryGerAvatarFromServer = true;
	/** 
	 * 要转换成的像素数，比如：原图是640*640的大图，但用到的地方只需要200*200的图，
	 * 那么此值设为200*200为佳，因这将使得返回的Bitmap对象占用的内存为200*200而非640*640 */
	private int reqWidth = 1;
	private int reqHeight = 1;
	
	/**
	 * 构造方法.
	 * @param parentActivity 父Activity
	 * @param uidForWho 要尝试更新头像的用户uid
	 * @param viewAvatar 如果服务端判定头像要更新后，下载完成时要更新到的UI组件引用.本参数可为null，但为null
	 * 的情况下请自行overide方法 {@link #avatarUpdate(Bitmap)}否则本类将只负责数据获取而不负责如何使用它哦
	 * @param 本地用缓存头像的情况下：是否需要尝试从服务端更新用户头像.
	 */
	public ShowUserAvatar(Activity parentActivity, String uidForWho
			, ImageView viewAvatar, boolean needTryGerAvatarFromServer, int reqWidth, int reqHeight)
	{
		this.parentActivity = parentActivity;
		this.uidForWho = uidForWho;;
		this.viewAvatar = viewAvatar;
		this.needTryGerAvatarFromServer = needTryGerAvatarFromServer;
		this.reqWidth = reqWidth;
		this.reqHeight = reqHeight;
	}
	
	public void showCahedAvatar()
	{
		boolean localHasCached = false;
		// 先尝试用本地缓存的头像更新到UI上先
		final Bitmap cachedAvatar = AvatarHelper.getUserCachedAvatarBitmap(parentActivity
				, uidForWho, reqWidth, reqHeight);
		
		if(cachedAvatar != null)
		{
//			viewAvatar.setImageBitmap(cachedAvatar);
			avatarUpdate(cachedAvatar);
			localHasCached = true;
		}
		else
		{
			avatarUpdate(BitmapFactory.decodeResource(parentActivity.getResources(), R.drawable.menu_default_head));
		}
		
		// 如果本地有缓存头像但不需要尝试从服务端取最新头像
		if(localHasCached && !needTryGerAvatarFromServer)
			;
		// 本地没有缓存 或者 本地有缓存但需要尝试从服务端取最新头像
		else
		{
			// 向服务端提交请求，更新用户头像（如果服务端判定需要更新才会返回头像图片数据哦）
			new TryGetAvatarAsync(parentActivity, uidForWho
//					, viewAvatar
//					, maxNumOfPixels
					, reqWidth, reqHeight
					)
			{
				@Override
				protected void avatarDowloadOver(Bitmap fromNet)
				{
//					avatarUpdate(fromNet);
					avatarUpdateForDownload(fromNet);
				}
				
				@Override
				protected void afterAvatarDowloadOver()
				{
					// 释放原来的老头像Bitmmap对象，释放内存
					if(cachedAvatar != null && cachedAvatar.isRecycled())
						cachedAvatar.recycle();
				}
			}.execute();
		}
	}
	
	/**
	 * 更新Bitmap对象.
	 * <p>默认实现是将Bitmap对象更新到UI组件上（如果UI组件存在的话）.
	 * 
	 * @param fromNet
	 */
	protected void avatarUpdate(Bitmap cachedAvatar)
	{
		if(viewAvatar != null)
			viewAvatar.setImageBitmap(cachedAvatar);
	}
	
	/**
	 * 当尝试从网络更新头像成功后要调用的方法.
	 * <p>
	 * 本方法默认直接调用方法 {@link #avatarUpdate(Bitmap)}来实现.
	 * 
	 * @param cachedAvatar
	 * @see #avatarUpdate(Bitmap)
	 */
	protected void avatarUpdateForDownload(Bitmap cachedAvatar)
	{
		avatarUpdate(cachedAvatar);
	}

	/**
	 * 本地用缓存头像的情况下：是否需要尝试从服务端更新用户头像.
	 * true表示需要查询看看服务端有无最新头像，否则将不去查询.
	 * <p>
	 * 换句话说，如果本地没有用户缓存头像则无论本参数如何设置也会去服务端试读取最新头像哦.
	 * <p>
	 * <b>说明：</b>可随时调用本方法设置之，设置将在下次调用 {@link #showCahedAvatar()}时生效.
	 * 
	 * @param needTryGerAvatarFromServer
	 */
	public void setNeedTryGerAvatarFromServer(boolean needTryGerAvatarFromServer)
	{
		this.needTryGerAvatarFromServer = needTryGerAvatarFromServer;
	}
	
	public boolean isNeedTryGerAvatarFromServer()
	{
		return needTryGerAvatarFromServer;
	}
}