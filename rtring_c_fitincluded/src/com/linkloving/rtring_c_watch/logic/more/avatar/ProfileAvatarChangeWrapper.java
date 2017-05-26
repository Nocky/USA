package com.linkloving.rtring_c_watch.logic.more.avatar;

import java.io.File;

import com.eva.android.BitmapHelper;
import com.eva.android.PictureHelper;
import com.eva.android.UriToFileHelper;
import com.eva.android.widget.ChoiceItemPopWindow;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.epc.common.file.FileHelper;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.SharedPreferencesUtil;
import com.rtring.buiness.logic.dto.UserEntity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 用户头像管理包装实现类.
 * 
 * @author Jack Jiang, 2013-12-12
 * @version 1.0
 */
public class ProfileAvatarChangeWrapper
{
	private final static String TAG = ProfileAvatarChangeWrapper.class.getSimpleName();

	/** 回调常量之：拍照 */
	private static final int TAKE_BIG_PICTURE = 991;
	/** 回调常量之：拍照后裁剪 */
	private static final int CROP_BIG_PICTURE = 993;
//	/** 回调常量之：从相册中选取 */
//	private static final int CHOOSE_BIG_PICTURE = 995;
	/** 回调常量之：从相册中选取2 */
	private static final int CHOOSE_BIG_PICTURE2 = 996;

	/** 图像保存大小（微信的也是这个大小） */
	private static final int AVATAR_SIZE = 640;

	private final static String HINT_FOR_SDCARD_ERROR = "Your sdcard has problems, please try again!";

	private Activity parentActivity = null;
	/** 选项弹出窗口的相对显示位置将以此组件为父进行调整显示位置  */
	private View parentViewForShow = null;

	private ViewGroup layoutOfAvatar = null;
	//自定义的弹出框类
	private ProfileAvatarChangePopWindow menuWindow = null;
	private ImageView viewLocalAvatar = null;

	// 修改头像的临时文件存放路径（头像修改成功后，会自动删除之）
	private String __tempImageFileLocation = null;

	public ProfileAvatarChangeWrapper(Activity parentActivity, View parentViewForShow)
	{
		this.parentActivity = parentActivity;
		this.parentViewForShow = parentViewForShow;
		
		initViews();
		initListeners();
	}

	private void initViews()
	{
		layoutOfAvatar = (ViewGroup) parentActivity.findViewById(R.id.user_info_avatarRL);
		viewLocalAvatar = (ImageView) parentActivity.findViewById(R.id.main_more_settings_avatarView);
	}

	private void initListeners()
	{
		// 点击弹出头像修改操作对话框
		layoutOfAvatar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{ 
				shotAvatarChage();
			}
		});
	}
	
	public void shotAvatarChage()
	{
		//为弹出窗口实现监听类
		final OnClickListener  itemsOnClick = new OnClickListener(){
			public void onClick(View v)
			{
				menuWindow.dismiss();
				switch (v.getId()) 
				{
				case R.id.main_more_change_avatar_dialog_btn_take_photo:
				{
					PictureHelper.takePhoto(parentActivity, TAKE_BIG_PICTURE, getTempImageFileUri());
					break;
				}
				case R.id.main_more_change_avatar_dialog_btn_pick_photo:	
				{
					PictureHelper.choosePhoto2(parentActivity, CHOOSE_BIG_PICTURE2);
					break;
				}
				default:
					break;
				}
			}
		};
		//实例化SelectPicPopupWindow
		menuWindow = new ProfileAvatarChangePopWindow(parentActivity, itemsOnClick);
		//显示窗口
		menuWindow.showAtLocation(parentViewForShow
				, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
	}

	/**
	 * 要由父类调用的回调处理方法.
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onParantActivityResult(int requestCode, int resultCode, Intent data)
	{
		//			super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != Activity.RESULT_OK)
		{
			//result is not correct
			Log.d(TAG, "【ChangeAvatar】requestCode = " + requestCode);
			Log.d(TAG, "【ChangeAvatar】resultCode = " + resultCode);
			Log.d(TAG, "【ChangeAvatar】data = " + data);
			return;
		}
		else
		{
			// 无法成功取到临时文件存放路径（可能是用户没有SD卡或其它原因哦），当然就为往下走了
			final Uri uri = getTempImageFileUri();
			final String tempImaheLocation = getTempImageFileLocation();
			if(uri == null || tempImaheLocation == null)
			{
				WidgetUtils.showToast(parentActivity, HINT_FOR_SDCARD_ERROR, ToastType.WARN);
				return;
			}

			switch (requestCode) 
			{
				case TAKE_BIG_PICTURE:// 拍照完成则新拍的文件将会存放于指定的位置（即uri、tempImaheLocation所表示的地方）
				{
//					Log.d(TAG, "TAKE_BIG_PICTURE: data = " + data);//it seems to be null
					Log.d(TAG, "【ChangeAvatar】TAKE_BIG_PICTURE: data = " + data
							+",data.getdata="+(data!= null?data.getData():"null"));//it seems to be null
					// sent to crop
					PictureHelper.cropImageUri(parentActivity, uri,tempImaheLocation,  AVATAR_SIZE, AVATAR_SIZE, CROP_BIG_PICTURE);
					break;
				}
				// 裁切完成后的处理（上传头像）
				case CROP_BIG_PICTURE://from crop_big_picture
				{
//					Log.d(TAG, "TAKE_BIG_PICTURE: data = " + data);//it seems to be null
					Log.d(TAG, "【ChangeAvatar】CROP_BIG_PICTURE: data = " + data+",uri=="+uri);//it seems to be null
					if(uri != null)
						processAvatarUpload(uri);
					break;
				}
//				// @deprecated
//				case CHOOSE_BIG_PICTURE:
//				{
//					Log.d(TAG, "【ChangeAvatar】CHOOSE_BIG_PICTURE: data = " + data+",uri=="+uri);//it seems to be null
//					if(uri != null)
//						processAvatarUpload(uri);
//					break;
//				}
				//【新的从相册选取图片的裁切实现方法自2014-01-03日启用】，原因如下：
				//    2014-01-02日在Android3.0平台上测试从相册选册时没有问题（这么简单当然没有问题），但是貌似系统自已
				//    提供的裁切功能不能正常使用（经常卡死，原因不明），估计又是Android奇葩的不同厂商兼容性问题，干脆裁切
				//	     就使用自行实现（其实是来自开源）裁切功能而再也不利用系统功能了（目前裁切利用跟拍照一样的原理实现！）
				case CHOOSE_BIG_PICTURE2:// 图片选取完成时，其实该图片还有原处，如要裁剪则应把它复制出来一份（以免裁剪时覆盖原图）
				{
//					Log.d(TAG, "TAKE_BIG_PICTURE: data = " + data);//it seems to be null
					if(data == null || data.getData() == null)
						return;
					// 相册中选择相片的Uri
					Uri originalPhotoForChoose = data.getData();
					// 该原始相片Uri所对应的File文件
					File originalPhotoForChooseCopySrc = UriToFileHelper.uri2File(parentActivity, originalPhotoForChoose);
					// 将选择的原始图片复制1份（以便裁切）
					File originalPhotoForChooseCopyDest = new File(tempImaheLocation);
					if(originalPhotoForChooseCopySrc != null)
					{
						boolean copyOK = false;
						try{
							// 将选择的原始图片复制1份（以便裁切）
							copyOK = FileHelper.copyFile(originalPhotoForChooseCopySrc, originalPhotoForChooseCopyDest);
						}
						catch (Exception e){
							Log.e(TAG, e.getMessage(), e);
						}
						
						// 要裁切的图片复制ok（其实就是复制到temp文件的位置，相机里是拍完照自动就存到了temp位置，所以没有这么烦）
						if(copyOK)
						{
							Log.d(TAG, "【ChangeAvatar】CHOOSE_BIG_PICTURE2: data = " + data//+",uri=="+uri
									+",originalPhotoForChoose="+originalPhotoForChoose);//it seems to be null
							// 复制完成，进入裁切处理
							if(originalPhotoForChoose != null)
								PictureHelper.cropImageUri(// uri
										parentActivity, originalPhotoForChoose, originalPhotoForChooseCopyDest.getAbsolutePath()
										, AVATAR_SIZE, AVATAR_SIZE, CROP_BIG_PICTURE);
						}
						else
							WidgetUtils.showToast(parentActivity, HINT_FOR_SDCARD_ERROR+"[2]", ToastType.WARN);
					}
					else
						WidgetUtils.showToast(parentActivity, HINT_FOR_SDCARD_ERROR+"[3]", ToastType.WARN);
					break;
				}
				default:
					break;
			}
		}
	}
	
	/**
	 * 处理用户头像的上传.
	 * 
	 * @param avatarTermpImgUri
	 */
	private void processAvatarUpload(final Uri avatarTermpImgUri)
	{
		//********************************************************** 【1】压缩头像文件
		// 先将拍好的临时文件decode成bitmap
		Bitmap bmOfTempAvatar = null;
		try 
		{
			bmOfTempAvatar = BitmapHelper.decodeUriAsBitmap(parentActivity, avatarTermpImgUri);
		} 
		// 显示处理下OOM使得APP更健壮（OOM只能显示抓取，否则会按系统Error的方式报出从而使APP崩溃哦）
		catch (OutOfMemoryError e)
		{
			WidgetUtils.showToast(parentActivity
					, parentActivity.getString(R.string.user_info_avatar_upload_faild3)
					, ToastType.WARN);
			Log.e(TAG, "【ChangeAvatar】将头像文件数据decodeUriAsBitmap到内存时内存溢出了，上传没有继续！", e);
		}
		
		if(bmOfTempAvatar == null)
		{
			WidgetUtils.showToast(parentActivity
//					, "Avatar update faild, please check your sdcard!"
					, parentActivity.getString(R.string.user_info_avatar_upload_faild1)
					, ToastType.WARN);
			return;
		}
		// ** 再将该bitmap压缩后覆盖原临时文件
		final File fileOfTempAvatar = new File(getTempImageFileLocation());
		if(bmOfTempAvatar != null)
		{
			try
			{
				// # 据测试，微信将640*640的图片裁剪、压缩后的大小约为34K左右，经测试估计是质量75哦
				// # 调整此值可压缩图像大小，经测试，再小于75后，压缩大小就不明显了
				// # 经Jack Jiang在Galaxy sIII上测试：原拍照裁剪完成的60K左右的头像按75压缩后大小约为34K左右，
				//   从高清图片中选取的裁剪完成时的200K左右按75压缩后大小依然约为34K左右，所以75的压缩比率在头
				//   像质量和文件大小上应是一个较好的平衡点
				com.eva.android.BitmapHelper.saveBitmapToFile(bmOfTempAvatar, 75, fileOfTempAvatar);
				Log.d(TAG, "【ChangeAvatar】尝试压缩本地用户头像临时文件已成功完成.");
			}
			catch(Exception e)
			{
				Log.e(TAG, "【ChangeAvatar】要更新的本地用户头像在尝试压缩临时文件时出错了，"+e.getMessage()+"，压缩将不能继续，但不影响继续上传处理！", e);
			}
		}
		
		//********************************************************** 【2】开始上传
		// 计算出头像文件的MD5码
		byte[] fileData = null;
		try{
			// 看看要上传的头像文件是否过大（按目前压缩比和测试结果，一般都是100K以内
			// ，但无法排除个别奇葩机器或其它原因导致头像文件很大，还是判断一下使得代码更健壮）
			if(fileOfTempAvatar != null 
					&& fileOfTempAvatar.length() > MyApplication.getInstance(parentActivity)._const.LOCAL_AVATAR_FILE_DATA_MAX_LENGTH)
			{
				WidgetUtils.showToast(parentActivity
//						, "Sorry, your avatar update failed (image is so large)!"
						, parentActivity.getString(R.string.user_info_avatar_upload_faild2)
						, ToastType.WARN);
				Log.e(TAG, "【ChangeAvatar】要上传的用户头像文件大小大于"
						+ MyApplication.getInstance(parentActivity)._const.LOCAL_AVATAR_FILE_DATA_MAX_LENGTH
						+ "字节，上传没有继续！");
				return;
			}
			
			// 将要上传的头像文件数据读取出来
			fileData = FileHelper.readFileWithBytes(fileOfTempAvatar);
		}
		// 显示处理下OOM使得APP更健壮（OOM只能显示抓取，否则会按系统Error的方式报出从而使APP崩溃哦）
		catch (OutOfMemoryError e){
			WidgetUtils.showToast(parentActivity
//					, "Sorry, your avatar update failed (OOM)!"
					, parentActivity.getString(R.string.user_info_avatar_upload_faild3)
					, ToastType.WARN);
			Log.e(TAG, "【ChangeAvatar】将头像文件数据读取到内存时内存溢出了，上传没有继续！", e);
		}
		catch (Exception e){
			Log.e(TAG, "【ChangeAvatar】尝试将本地头像临时文件数据读取出来时出错了，"+e.getMessage()+"，上传将不能继续！", e);
		}
		
		// 读取出来的数据是空的，上传当然就没有必要继续了
		if(fileData == null)
			return;
		
		// 计算头像文件的MD5码
		final String fileMd5 = getTempFileMD5(fileData);
		// 本地用户信息
		final UserEntity localUser = MyApplication.getInstance(parentActivity).getLocalUserInfoProvider();
		System.out.println("【ChangeAvatar】========================fileMd5="+fileMd5+", fileLength="+fileData.length);
		if(fileMd5 != null && localUser != null)
		{
			// 本户存放于数据库、本地缓存的文件名格式是："uid_MD5码.jpg",形如:"400002_0b272fca28252641231a94f63d8e25fa.jpg"
			final String fileNameUsedMd5 = AvatarHelper.getUserCachedAvatarFileName(localUser.getUser_id()
					, fileMd5);//localUser.getUser_uid()+"_"+fileMd5+".jpg";

			// 异步头像上传执行线程
			new AvatarHelper.AvatarUploadAsync(parentActivity){
				@Override
				protected void afterSucess(Bitmap bmOfTempAvatar)
				{
					//** 头像上传成功则将新头像更新到ui上于以显示
//					Bitmap bitmap = decodeUriAsBitmap(avatarTermpImgUri);
					viewLocalAvatar.setImageBitmap(bmOfTempAvatar);//bitmap);
					WidgetUtils.showToast(parentActivity
							//, "头像更新成功！"
//							, "Your avatar update sucess!" 
							, parentActivity.getString(R.string.user_info_avatar_upload_sucess)
							, ToastType.OK);
					
					//清除本地家庭账号存储（确保当前用户信息被更改后，下次打开家庭号列表时能从网络取一份最新的，否则就不一致了）
					SharedPreferencesUtil.saveSharedPreferences(parentActivity, "__ue_list__", "");

					//** 处理本地缓存文件
					try
					{
						if(fileOfTempAvatar != null && fileOfTempAvatar.exists())
						{
							// 先尝试删除所有自已的以前老的头像缓存（否则占用用户SD卡的空间）
							AvatarHelper.deleteUserAvatar(parentActivity, localUser.getUser_id(), null);
							// 先将临时文件（也就是本次拍好或选好的新头像）复制一份（更名
							// 为正式的本地用户头像缓存文件名）作为本地用户图像的正式文件
							FileHelper.copyFile(fileOfTempAvatar, new File(fileOfTempAvatar.getParent()+"/"+fileNameUsedMd5));
							// 再删除用完的临时文件（也就是本次拍好或选好的新头像），因为
							// 正式的本地缓存文件已生成，本临时文件就失去意义了，当然删除之
							FileHelper.deleteFile(getTempImageFileLocation());
						}
					}
					catch (Exception e)
					{
						Log.e(TAG, "【ChangeAvatar】成功上传本地用户头像后，转换本地缓存文件时出错了，"+e.getMessage(), e);
					}
				}

				@Override
				protected void afterFaild()
				{
					WidgetUtils.showToast(parentActivity
//							, "头像更新失败！"
//							, "Sorry, your avatar update failed!"
							, parentActivity.getString(R.string.user_info_avatar_upload_faild4)
							, ToastType.WARN);
				}
			}
			.execute
			(
					// 上传头像的临时地址
					getTempImageFileLocation()
					// 上传头像图片的MD5码
					, fileNameUsedMd5
					// 上传服务的URL
					, MyApplication.AVATAR_UPLOAD_CONTROLLER_URL_ROOT
					// 本地用户的uid
					, localUser.getUser_id()
					// 传入该头像的bitmap对象（仅用于上传成功后在ui上显示哦）
					, bmOfTempAvatar
					
					// 将文件2进制数据也传过去（方便发往服务器）
					, fileData
					);
		}
	}

	/**
	 * 获得临时文件存放地址的Uri(此地址存在与否并不代表该文件一定存在哦).
	 * 
	 * @return 正常获得uri则返回，否则返回null
	 */
	private Uri getTempImageFileUri()
	{
		String tempImageFileLocation = getTempImageFileLocation();
		if(tempImageFileLocation != null)
		{
			return Uri.parse("file://"+tempImageFileLocation);
		}
		return null;
	}
	/**
	 * 获得临时文件存放地址(此地址存在与否并不代表该文件一定存在哦).
	 * 
	 * @return 正常获得则返回，否则返回null
	 */
	private String getTempImageFileLocation()
	{
		try
		{
			if(__tempImageFileLocation == null)
			{
				String avatarTempDirStr = AvatarHelper.getUserAvatarSavedDir(parentActivity);
				File avatarTempDir = new File(avatarTempDirStr);
				if(avatarTempDir != null)
				{
					// 目录不存在则新建之
					if(!avatarTempDir.exists())
						avatarTempDir.mkdirs();

					// 临时文件名
					__tempImageFileLocation = avatarTempDir.getAbsolutePath()+"/"+"local_avatar_temp.jpg";
				}
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "【ChangeAvatar】读取本地用户的头像临时存储路径时出错了，"+e.getMessage(), e);
		}

		Log.d(TAG, "【ChangeAvatar】正在获取本地用户的头像临时存储路径："+__tempImageFileLocation);

		return __tempImageFileLocation;
	}

	private String getTempFileMD5(byte[] fileData)
	{
		try
		{
			return FileHelper.getFileMD5(fileData);
		}
		catch (Exception e)
		{
			Log.w(TAG, "【ChangeAvatar】计算MD5码时出错了，"+e.getMessage(), e);
			return null;
		}
	}
//	/**
//	 * 获得用户图像临时文件的MD5码.
//	 * <p>
//	 * 此码将用于正式的存储命名，从而为后绪下载时判断是否要更新缓存提供依据.
//	 * 
//	 * @return 如果该文件存在且过程中无异常则返回md5码，否则返回null
//	 */
//	private String getTempFileMD5()
//	{
//		String pt = getTempImageFileLocation();
//		try
//		{
//			return FileHelper.getFileMD5(pt);
//		}
//		catch (Exception e)
//		{
//			Log.w(TAG, "【ChangeAvatar】计算文件"+pt+"的MD5码时出错了，"+e.getMessage(), e);
//			return null;
//		}
//	}
	
	//------------------------------------------------------------------------------------------
	private class ProfileAvatarChangePopWindow extends ChoiceItemPopWindow
	{
		private Button btn_take_photo, btn_pick_photo, btn_cancel;
		
		public ProfileAvatarChangePopWindow(Activity context, OnClickListener mItemsOnClick)
		{
			super(context, mItemsOnClick
					, R.layout.main_more_change_avatar_dialog
					, R.id.main_more_change_avatar_dialog_pop_layout);
		}

		protected void initContentViewComponents(View mMenuView)
		{
			btn_take_photo = (Button) mMenuView.findViewById(R.id.main_more_change_avatar_dialog_btn_take_photo);
			btn_pick_photo = (Button) mMenuView.findViewById(R.id.main_more_change_avatar_dialog_btn_pick_photo);
			btn_cancel = (Button) mMenuView.findViewById(R.id.main_more_change_avatar_dialog_btn_cancel);
			// 取消按钮
			btn_cancel.setOnClickListener(createCancelClickListener());
			// 设置按钮监听
			btn_pick_photo.setOnClickListener(mItemsOnClick);
			btn_take_photo.setOnClickListener(mItemsOnClick);
		}
	}
}