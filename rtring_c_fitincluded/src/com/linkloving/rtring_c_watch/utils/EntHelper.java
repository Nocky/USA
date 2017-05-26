package com.linkloving.rtring_c_watch.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.eva.android.BitmapHelper;
import com.linkloving.rtring_c_watch.MyApplication;

public class EntHelper
{
	private final static String TAG = EntHelper.class.getSimpleName();
	
	/**
	 * 获得下载指定用户头像的URL（<b>服务端将根据用户本地缓存图片来
	 * 智能判断是否要下载</b>（服务器的文件名称与本地一样当然就不需要下载了））.
	 * 
	 * @param context
	 * @param userUid 要下载头像的用户uid
	 * @param userLocalCachedAvatar 缓存在本地的用户头像文件名称
	 * @return
	 */
	public static String getEntDownloadURL(Context context, String userUid, String file_name, String userLocalCachedAvatar)
	{
		return getEntDownloadURL(context, userUid, file_name, userLocalCachedAvatar, false);
	}
	/**
	 * 获得<b>无条件（不管该用户有无本地头像缓存）</b>下载指定用户头像的URL.
	 * 
	 * @param context
	 * @param userUid 要下载头像的用户uid
	 * @return
	 */
	public static String getEntDownloadURL(Context context, String userUid, String file_name)
	{
		return getEntDownloadURL(context, userUid, file_name, null, true);
	}
	/**
	 * 获得下载指定用户头像的完整http地址.
	 * <p>
	 * 形如：“http://192.168.88.138:8080/UserAvatarDownloadController?
	 * action=ad&user_uid=400007&user_local_cached_avatar=400007_91c3e0d81b2039caa9c9899668b249e8.jpg
	 * &enforceDawnload=0”。
	 * 
	 * @param context
	 * @param userUid 要下载头像的用户uid
	 * @param userLocalCachedAvatar 用户缓存在本地的头像文件名（本参数只在enforceDawnload==false时有意义）
	 * @param enforceDawnload true表示无论客户端有无提交缓存图片名称本方法都将无条件返回该用户头像（如果头像确实存在的话），否则
	 * 将据客户端提交上来的可能的本地缓存文件来判断是否需要下载用户头像（用户头像没有被更新过当然就不需要下载了！）
	 * @return 完整的http文件下载地址
	 */
	private static String getEntDownloadURL(Context context, String userUid, String file_name, String userLocalCachedAvatar
			, boolean enforceDawnload)
	{
		String fileURL = MyApplication.ENT_DOWNLOAD_CONTROLLER_URL_ROOT
				+"?action=ent_img_d"
				+"&user_uid="+userUid
				+"&file_name="+file_name
				+(userLocalCachedAvatar==null?"":"&user_local_cached_avatar="+userLocalCachedAvatar)
				+"&enforceDawnload="+(enforceDawnload?"1":"0");
		return fileURL;
	}
	
	/**
	 * 返回存储用户头像的目录.（结尾带反斜线）.
	 * 
	 * @param context
	 * @return 如果SDCard等正常则返回目标路径，否则返回null
	 */
	public static String getEntFileSavedDirHasSlash(Context context)
	{
		String dir = getEntFileSavedDir(context);
		
		return dir ==  null? null : (dir + "/");
	}
	/**
	 * 返回存储用户头像的目录.
	 * 
	 * @param context
	 * @return 如果SDCard等正常则返回目标路径，否则返回null
	 */
	public static String getEntFileSavedDir(Context context)
	{
		String dir = null;
		File sysExternalStorageDirectory = Environment.getExternalStorageDirectory();
		if(sysExternalStorageDirectory != null && sysExternalStorageDirectory.exists())
		{
			dir = sysExternalStorageDirectory.getAbsolutePath()+MyApplication.getInstance(context)._const.DIR_ENT_IMAGE_RELATIVE_DIR;
		}
		return dir;
	}
	/**
	 * 返回OAD的目录.
	 * 
	 * @param context
	 * @return 如果SDCard等正常则返回目标路径，否则返回null
	 */
	public static String getOADFileSavedDir(Context context)
	{
		String dir = null;
		File sysExternalStorageDirectory = Environment.getExternalStorageDirectory();
		if(sysExternalStorageDirectory != null && sysExternalStorageDirectory.exists())
		{
			dir = sysExternalStorageDirectory.getAbsolutePath()+MyApplication.getInstance(context)._const.DIR_KCHAT_OAD_RELATIVE_DIR;
		}
		return dir;
	}
	
	/**
	 * 返回群组背景图片的目录.
	 * 
	 * @param context
	 * @return 如果SDCard等正常则返回目标路径，否则返回null
	 */
	public static String getBGFileSavedDir(Context context)
	{
		String dir = null;
		File sysExternalStorageDirectory = Environment.getExternalStorageDirectory();
		if(sysExternalStorageDirectory != null && sysExternalStorageDirectory.exists())
		{
			dir = sysExternalStorageDirectory.getAbsolutePath()+MyApplication.getInstance(context)._const.DIR_KCHAT_BG_RELATIVE_DIR;
		}
		return dir;
	}
	
	/**
	 * 尝试删除指定uid用户缓存在本地的头像（图片文件）.
	 * 
	 * @param context
	 * @param uidToDelete 要删除的用户
	 * @param fileNameExceptToDelete 本参数可为null，不为空则表示：删除时要保留的文件名，此参数用于：刚下载完最新头像后又
	 * 要清理老头像时那么就应该跳过这个最新下载的，否则岂不是白白下载罗！
	 */
	public static void deleteUserAvatar(Context context, String uidToDelete, String fileNameExceptToDelete)
	{
		String avatarDirStr = EntHelper.getEntFileSavedDir(context);
		File avatarTempDir = new File(avatarDirStr);
		if(avatarTempDir != null && avatarTempDir.exists())
		{
			// 遍历缓存目录下的所有头像
			File[] allCachedAvatars = avatarTempDir.listFiles();
			if(allCachedAvatars != null && allCachedAvatars.length > 0)
			{
				for(File cachedAvatar : allCachedAvatars)
				{
					// 从文件名中取出缓存的用户uid（文件存在的格式形如：400002_0b272fca28252641231a94f63d8e25fa.jpg）
					String cachedAvatarFileName = cachedAvatar.getName();
					int separatorIndex = cachedAvatarFileName.indexOf("_");
					if(cachedAvatarFileName != null && (separatorIndex != -1))
					{
						String cachedUid = cachedAvatarFileName.substring(0, separatorIndex);
						// 如果该缓存正好是本用户的，就意味着这是老头像，删除它吧
						if(cachedUid != null && cachedUid.equals(uidToDelete))
						{
							// 如果要删除的这个文件刚好在受保护之列（应删除但排除在本次之外的文件），则本次保留之
							if(fileNameExceptToDelete != null && fileNameExceptToDelete.equals(cachedAvatarFileName))
								;
							else
								// 删除之
								cachedAvatar.delete();
						}
					}
				}
			}
		}
		else
		{
			Log.d(TAG, "【ChangeAvatar】用户的头像缓存目录不存，上传自已头像操作时无需尝试删除自已的头像缓存.");
		}
	}
	
//	/**
//	 * 返回指定用户缓存在本地的头像Bitmap对象.
//	 * 
//	 * @param context
//	 * @param uid
//	 * @param reqWidth、reqHeight 要转换成的像素数，比如：原图是640*640的大图，但用到的地方只需要200*200的图，
//	 * 那么此值设为200*200为佳，因这将使得返回的Bitmap对象占用的内存为200*200而非640*640
//	 * @return
//	 * @see #getUserCachedAvatar(Context, String)
//	 */
//	public static Bitmap getEntFileBitmap(Context context, String uid ,String fileName, int reqWidth, int reqHeight)
//	{
//		Bitmap bp = null;
//		File cachedAvatar = getEntCachedFile(context, uid, fileName);
//		if(cachedAvatar != null && cachedAvatar.exists())
//		{
//			bp = BitmapFactory.decodeFile(cachedAvatar.getAbsolutePath()
//					// 裁剪图片的Bitmap（从而缩减内存占用）：裁剪内存占用不影响任何本地文件
//					, BitmapHelper.computeSampleSize2(cachedAvatar.getAbsolutePath(), reqWidth, reqHeight));
//		}
//		return bp;
//	}
//	/**
//	 * 返回指定用户缓存在本地地的头像File引用.
//	 * <p>
//	 * 注意：如果同一用户在该用户本地错误地存在多个头像（应该是在更新
//	 * 缓存时没有删除成功的情况下发生的），则只返回修改时间为最新的文件（就这样吧，
//	 * 更新缓存时会主动删除老的，也就等于程序有机会纠正这个错误）.
//	 * 
//	 * @param context
//	 * @param uid
//	 * @return 如果指定uid的用户存在本在头像缓存则返回File对象，否则返回null
//	 */
//	public static File getEntCachedFile(Context context, String uid, String fileName)
//	{
//		String avatarDirStr = EntHelper.getEntFileSavedDir(context);
//		File avatarTempDir = new File(avatarDirStr);
//		
//		File cachedAvatarForRet = null;
//		if(uid != null && fileName != null && avatarTempDir != null && avatarTempDir.exists())
//		{
//			// 遍历缓存目录下的所有头像
//			File[] allCachedAvatars = avatarTempDir.listFiles();
//			if(allCachedAvatars != null && allCachedAvatars.length > 0)
//			{
//				for(File cachedAvatar : allCachedAvatars)
//				{
//					// 从文件名中取出缓存的用户uid（文件存在的格式形如：400002_0b272fca28252641231a94f63d8e25fa.jpg）
//					String cachedAvatarFileName = cachedAvatar.getName();
//					int uidIndex = cachedAvatarFileName.indexOf("_");
//					int separatorIndex = cachedAvatarFileName.lastIndexOf("_");
//					if(cachedAvatarFileName != null && (uidIndex != -1) && (separatorIndex != -1))
//					{
//						String cachedUid = cachedAvatarFileName.substring(0, uidIndex);
//						String topString = cachedAvatarFileName.substring(0, separatorIndex);
//						// 如果该缓存正好是本用户的，那就直接返回
//						if(cachedUid != null && cachedUid.equals(uid) && fileName.startsWith(topString))
//						{
//							// 如果存在多张该用户的头像则取修改时间为最新的一张！
//							if(cachedAvatarForRet == null
//								|| cachedAvatar.lastModified() > cachedAvatarForRet.lastModified())
//							{
//								cachedAvatarForRet = cachedAvatar;
//							}
//						}
//					}
//				}
//			}
//		}
//		
//		return cachedAvatarForRet;
//	}

}
