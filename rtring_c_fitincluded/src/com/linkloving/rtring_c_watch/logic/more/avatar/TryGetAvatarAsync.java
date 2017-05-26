package com.linkloving.rtring_c_watch.logic.more.avatar;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.eva.android.BitmapHelper;
import com.eva.android.HttpFileDownloadHelper;

/**
 * 尝试从网络读取用户头像数据的异步线程.
 * <p>
 * 是否从服务端下载最新头像由服务端据本地提交的缓存头像文件名来判定（与服务端最新头像
 * 文件名比较即知），如果需要下载则本类将保证下载之，否则服务端将返回0大小的文件数据，
 * 此情况下本类也就什么都不做了.
 * 
 * @author Jack Jiang, 2013-12-13
 * @version 1.0
 */
public abstract class TryGetAvatarAsync extends AsyncTask<Object, String, String>
{
	private Activity parentActivity = null;
	private String uidForWho = null;
//	private ImageView viewAvatar = null;
	/** 
	 * 要转换成的像素数，比如：原图是640*640的大图，但用到的地方只需要200*200的图，
	 * 那么此值设为200*200为佳，因这将使得返回的Bitmap对象占用的内存为200*200而非640*640 */
	private int reqWidth = 1;
	private int reqHeight = 1;

	/**
	 * 构造方法.
	 * 
	 * @param parentActivity 父Activity
	 * @param uidForWho 要尝试更新头像的用户uid
	 * @param viewAvatar 如果服务端判定头像要更新后，下载完成时要更新到的UI组件引用
	 */
	public TryGetAvatarAsync(Activity parentActivity, String uidForWho
//			, ImageView viewAvatar
			, int reqWidth, int reqHeight
			)
	{
		this.parentActivity = parentActivity;
		this.uidForWho = uidForWho;;
//		this.viewAvatar = viewAvatar;
		this.reqWidth = reqWidth;
		this.reqHeight = reqHeight;
	}

	@Override
	protected String doInBackground(Object... params)
	{
		try
		{
			// 尝试读取该用户的缓存头像文件信息
			File cachecAvatarFile = AvatarHelper.getUserCachedAvatar(parentActivity, uidForWho);
			// 尝试下载并更并新用户头像（服务端会据提交上去的本地缓存头像文件名来判断是否需要下载）
			Object[] ret = HttpFileDownloadHelper.downloadFileEx(
					// 头像下载URL
					AvatarHelper.getUserAvatarDownloadURL(parentActivity, uidForWho
							, cachecAvatarFile==null?null:cachecAvatarFile.getName())
							// 如果服务端判定需要更新头像到本地缓存时的保存目录
							, AvatarHelper.getUserAvatarSavedDir(parentActivity), 0, null, true);
			if(ret != null && ret.length >=2)
			{
				String savedPath = (String)ret[0];
				int fileLength = (Integer)ret[1];

				System.out.println("111111111111111111111111111111savedPath="+savedPath+"fileLength="+fileLength);
				// 文件大小大于0时表示服务端判定需要下载且已经下载完成
				if(savedPath != null && fileLength > 0)
				{
					// 已经下载了最新的头像，那么该用户之前的老头像（理论上只有1张，但如果在更
					// 新头像等过程中删除老头像出错后就可能遗留有多于1张的头像的情况发生哦）
					// 应该被删除了，但要注意一点：这张新更新的头像不在此时的删除之列哦
					File latestUserAvatarFile = new File(savedPath);
					if(latestUserAvatarFile.exists())
					{
						AvatarHelper.deleteUserAvatar(parentActivity, uidForWho
								, latestUserAvatarFile.getName() // 新下载的图像不删除哦（否则岂不是白下载了！）
								);
					}
					
					return savedPath;
				}
			}
		}
		catch (Exception e)
		{
			Log.w("GetAvatar", "更新用户头像时出错，", e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) 
	{
		// 结果不为空即意味服务端返回了最新的头像（服务端已判定需要更新）
		if(result != null)
		{
			try
			{
				// 用最新的头像更新到UI上
				String savedPath = (String)result;
				Bitmap fromNet = BitmapHelper.loadLocalBitmap(savedPath
						// 裁剪图片的Bitmap（从而缩减内存占用）：裁剪内存占用不影响任何本地文件
						, BitmapHelper.computeSampleSize2(savedPath, reqWidth, reqHeight));
//				viewAvatar.setImageBitmap(fromNet);
				avatarDowloadOver(fromNet);

				// 取得新头像后要做的事
				afterAvatarDowloadOver();

//					Bitmap fromNet = ToolKits.loadHttpBitmap("http://192.168.88.138:8080/UserAvatarDownloa" +
//						"dController?action=ad&user_uid=400007&user_local_cached_avatar=400007_91c3e0d81b2039caa9c9899668b249e8.jpg&enforceDawnload=0");
//					System.out.println("111111111111111111111111111111fromNet="+fromNet);
			}
			catch(Exception e)
			{
				Log.w("GetAvatar", "更新用户头像时出错，", e);
			}
		}
	}
	
	/**
	 * 更新Bitmap对象（到UI组件上？）.
	 * 
	 * @param fromNet
	 */
	protected abstract void avatarDowloadOver(Bitmap fromNet);
//	{
//		if(viewAvatar != null)
//			viewAvatar.setImageBitmap(fromNet);
//	}

	/**
	 * 取得新头像后要做的事.
	 * <p>
	 * 比如：释放老头像的资源等.
	 */
	protected void afterAvatarDowloadOver()
	{
		// default do nothing
	}
}