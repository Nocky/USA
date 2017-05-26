package com.linkloving.rtring_c_watch.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.SysActionConst;
import com.google.gson.Gson;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.CommentReply;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserSignatureComment;

public class HttpSnsHelper
{
	/**
	 * 生成关注接口参数
	 * @return
	 */
	public static DataFromClient GenerateConcernParams(String user_id,String attention_user_id)
	{
		Map<String, String> newData = new HashMap<String, String>();
		newData.put("user_id", user_id);
		newData.put("attention_user_id", attention_user_id);
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
		                               .setJobDispatchId(JobDispatchConst.SNS_BASE)
		                               .setActionId(SysActionConst.ACTION_APPEND1)
		                               .setNewData(new Gson().toJson(newData));
		                               
	}
	
	/**
	 * 生成取消关注接口参数
	 * @return
	 */
	public static DataFromClient GenerateCancelConcernParams(String user_id,String attention_user_id)
	{
		Map<String, String> newData = new HashMap<String, String>();
		newData.put("user_id", user_id);
		newData.put("attention_user_id", attention_user_id);
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND2)
                .setNewData(new Gson().toJson(newData));
	}
	
	/**
	 * 生成顶/踩接口参数
	 * @param user_id
	 * @param user_time
	 * @param type
	 * @param praise_user_id
	 * @return
	 */
	public static DataFromClient GeneratePraiseParams(String user_id,String user_time,String type,String praise_user_id)
	{
		Map<String, String> newData = new HashMap<String, String>();
		newData.put("user_id", user_id);
		newData.put("user_time", user_time);
		newData.put("type", type);
		newData.put("praise_user_id", praise_user_id);
		
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND3)
                .setNewData(new Gson().toJson(newData));
	}
	
	/**
	 * 生成取消顶/踩接口参数
	 * @param user_id
	 * @param user_time
	 * @param type
	 * @param praise_user_id
	 * @return
	 */
	public static DataFromClient GenerateCancelPraiseParams(String user_id,String user_time,String praise_user_id)
	{
		Map<String, String> newData = new HashMap<String, String>();
		newData.put("user_id", user_id);
		newData.put("user_time", user_time);
		newData.put("praise_user_id", praise_user_id);
		
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND4)
                .setNewData(new Gson().toJson(newData));
	}
	
	/**
	 * 生成用户信息接口参数
	 * @param user_id
	 * @param user_time
	 * @param praise_user_id
	 * @return
	 */
	public static DataFromClient GenerateUserDetailParams(String my_id,String user_id,String user_time)
	{
		Map<String, String> newData = new HashMap<String, String>();
		newData.put("my_id", my_id);
		newData.put("user_id", user_id);
		newData.put("user_time", user_time);
		
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND5)
                .setNewData(new Gson().toJson(newData));
	}
	
	/**
	 * 生成查看我关注的人接口参数
	 * @param user_id
	 * @param user_time
	 * @return
	 */
	public static DataFromClient GenerateConcernAboutListParams(String user_id,String user_time)
	{
		Map<String, String> newData = new HashMap<String, String>();
		newData.put("user_id", user_id);
		newData.put("user_time", user_time);
		
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND6)
                .setNewData(new Gson().toJson(newData));
	}
	
	/**
	 * 生成关注我的人接口参数
	 * @param user_id
	 * @param user_time
	 * @return
	 */
	public static DataFromClient GenerateConcernMeListParams(String user_id,String user_time)
	{
		Map<String, String> newData = new HashMap<String, String>();
		newData.put("user_id", user_id);
		newData.put("user_time", user_time);
		
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND7)
                .setNewData(new Gson().toJson(newData));
	}
	
	/**
	 * 生成评论列表接口参数
	 * @param user_id
	 * @param user_time
	 * @return
	 */
	public static DataFromClient GenerateCommentListParams(String user_id,String user_time)
	{
		Map<String, String> newData = new HashMap<String, String>();
		newData.put("user_id", user_id);
		newData.put("user_time", user_time);
		
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND8)
                .setNewData(new Gson().toJson(newData));
	}
	
	
	/**
	 * 生成新增评论接口参数
	 * @param reply
	 * @return
	 */
	public static DataFromClient GenerateAddCommentParams(CommentReply reply)
	{
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND9)
                .setNewData(new Gson().toJson(reply));
	}
	
	/**
	 * 生成评论接口参数
	 * @param reply
	 * @return
	 */
	public static DataFromClient GenerateDelCommentParams(String comment_id)
	{
		Map<String, String> newData = new HashMap<String, String>();
		newData.put("comment_id", comment_id);
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND10)
                .setNewData(new Gson().toJson(newData));
	}
	
	/**
	 * 生成基本评论列表接口参数
	 * @param reply
	 * @return
	 */
	public static DataFromClient GenerateCommentRoughListParams(String user_id)
	{
		Map<String, String> newData = new HashMap<String, String>();
		newData.put("user_id", user_id);
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND11)
                .setNewData(new Gson().toJson(newData));
	}
	
	/**
	 * 生成搜索列表接口参数
	 * @param condition
	 * @param user_id
	 * @param page
	 * @return
	 */
	public static DataFromClient GenerateSearchListParams(String condition,String user_id,int page)
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("condition", condition);
		jsonObject.put("user_id", user_id);
		jsonObject.put("page", page);
		
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND12)
                .setNewData(jsonObject.toJSONString());
	}
	/**
	 * 生成搜索列表接口参数
	 * @param condition
	 * @param user_id
	 * @param page
	 * @return
	 */
	public static DataFromClient GenerateSearchListParams(String condition, String user_id, int page, int type)
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("condition", condition);
		jsonObject.put("user_id", user_id);
		jsonObject.put("page", page);
		jsonObject.put("type", type);
		
//		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_NEW_LOGIC)
//                .setJobDispatchId(JobDispatchConst.NEW_LOGIC_BASE)
//                .setActionId(SysActionConst.ACTION_APPEND12)
//                .setNewData(jsonObject.toJSONString());
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
                .setJobDispatchId(JobDispatchConst.SNS_BASE)
                .setActionId(SysActionConst.ACTION_APPEND12)
                .setNewData(jsonObject.toJSONString());
		
	}
	
	/**
	 * 生成标记未读数接口参数
	 * @param user_id
	 * @param user_time
	 * @return
	 */
	public static DataFromClient GenerateMarkUnreadParams(String user_id,String user_time)
	{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("user_id", user_id);
			jsonObject.put("user_time", user_time);
			return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
	                .setJobDispatchId(JobDispatchConst.SNS_BASE)
	                .setActionId(SysActionConst.ACTION_REMOVE)
	                .setNewData(jsonObject.toJSONString());
	}
	
	/**
	 * 生成添加签名接口参数
	 * @param user_id
	 * @param sign_content
	 * @return
	 */
    public static DataFromClient GenerateAddWhatsUpParams(String user_id,String sign_content)
    {
    	JSONObject jsonObject = new JSONObject();
		jsonObject.put("user_id", user_id);
		jsonObject.put("sign_content", sign_content);
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USER_SIGNATURE)
                .setJobDispatchId(JobDispatchConst.USER_SIGNATURE)
                .setActionId(SysActionConst.ACTION_APPEND1)
                .setNewData(jsonObject.toJSONString());
    }
    
    /**
     * 生成获取签名列表接口参数
     * @param user_id
     * @param page
     * @return
     */
    public static DataFromClient GenerateWhatsUpListParams(String user_id,int page)
    {
    	JSONObject jsonObject = new JSONObject();
		jsonObject.put("user_id", user_id);
		jsonObject.put("page", page);
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USER_SIGNATURE)
                .setJobDispatchId(JobDispatchConst.USER_SIGNATURE)
                .setActionId(SysActionConst.ACTION_APPEND6)
                .setNewData(jsonObject.toJSONString());
    }
    
    public static DataFromClient GenerateWhatsUpDetailList(String sign_id)
    {
    	JSONObject jsonObject = new JSONObject();
		jsonObject.put("sign_id", sign_id);
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USER_SIGNATURE)
                .setJobDispatchId(JobDispatchConst.USER_SIGNATURE)
                .setActionId(SysActionConst.ACTION_APPEND7)
                .setNewData(jsonObject.toJSONString());
    }
    
    /**
     * 生成获取“历史签名条数”、“总评论数”接口参数
     * @param user_id
     * @return
     */
    public static DataFromClient GenerateWhatsUpCountParams(String user_id)
    {
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USER_SIGNATURE)
                .setJobDispatchId(JobDispatchConst.USER_SIGNATURE)
                .setActionId(SysActionConst.ACTION_VERIFY)
                .setNewData(user_id);
    }
    
    /**
     * 生成修改签名接口参数
     * @param user_id
     * @param sign_content
     * @return
     */
    public static DataFromClient GenerateModifyWhatsUpParams(String user_id,String sign_content)
    {
    	JSONObject jsonObject = new JSONObject();
		jsonObject.put("sign_id", user_id);
		jsonObject.put("sign_content", sign_content);
		return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USER_SIGNATURE)
                .setJobDispatchId(JobDispatchConst.USER_SIGNATURE)
                .setActionId(SysActionConst.ACTION_APPEND2)
                .setNewData(jsonObject.toJSONString());
    }
    
    /**
     * 生成删除签名接口参数
     * @param user_id
     * @return
     */
    public static DataFromClient GenerateDeleteWhatsUpParams(String sign_id)
    {
    	return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USER_SIGNATURE)
                .setJobDispatchId(JobDispatchConst.USER_SIGNATURE)
                .setActionId(SysActionConst.ACTION_APPEND3)
                .setNewData(sign_id);
    }
    
    /**
     * 生成新增评论接口参数
     * @param comment
     * @return
     */
    public static DataFromClient GenerateWhatsUpAddCommentsParams(UserSignatureComment comment)
    {
    	return DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USER_SIGNATURE)
                .setJobDispatchId(JobDispatchConst.USER_SIGNATURE)
                .setActionId(SysActionConst.ACTION_APPEND4)
                .setNewData(new Gson().toJson(comment));
    }
    
    

}
