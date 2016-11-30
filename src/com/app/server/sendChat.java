package com.app.server;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.bean.EMFService;
import com.app.bean.UserBean;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;


@SuppressWarnings("serial")
public class sendChat extends HttpServlet
{
	private static final Logger logger = Logger.getLogger(createGroup.class
			.getCanonicalName());
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		
		//DEFINING SERVER MOOD
		final String serverMood_chat = "serverMood_chat";

		
		String chatWithNoSpace = req.getParameter(Constants.CHAT_NAME_WITH_NO_SPACE);
	    String toUserPhoneNumber = req.getParameter(Constants.CHAT_TO_PHONE_NUMBER);
	    String fromUserPhoneNumber = req.getParameter(Constants.CHAT_FROM_PHONE_NUMBER);
	    String toUserName = req.getParameter(Constants.CHAT_TO_NAME);
	    String fromUserName = req.getParameter(Constants.CHAT_FROM_NAME);
	    String chatText = req.getParameter(Constants.CHAT_TEXT);
	    
	    //Create Message from data
	    Message message = createMessageFromRequest(req, chatWithNoSpace, toUserPhoneNumber, fromUserPhoneNumber, toUserName, fromUserName, chatText, serverMood_chat);
	    
	    //Fetch Contact GCM id
	    String toDeviceGCM = getDevice(toUserPhoneNumber);
 
	    logger.log(Level.WARNING, "chatWithNoSpace = " +chatWithNoSpace );
	    logger.log(Level.WARNING, "toUserPhoneNumber = " +toUserPhoneNumber );
	    logger.log(Level.WARNING, "fromUserPhoneNumber = " +fromUserPhoneNumber );
	    logger.log(Level.WARNING, "toUserName = " +toUserName );
	    logger.log(Level.WARNING, "fromUserName = " +fromUserName );
	    logger.log(Level.WARNING, "chatText = " +chatText );
	    
	    //sEND to GCM server
	    sendToGCM(message, toDeviceGCM);
	    
	    
	}
	
	private Message createMessageFromRequest(
			HttpServletRequest req, String chatWithNoSpace, String toUserPhoneNumber, String fromUserPhoneNumber, 
			String toUserName, String fromUserName, String chatText, String serverMood_chat)
	{

		Message message = new Message.Builder()
				.addData(Constants.CHAT_NAME_WITH_NO_SPACE, chatWithNoSpace)
				.addData(Constants.CHAT_TO_PHONE_NUMBER, toUserPhoneNumber)
				.addData(Constants.CHAT_FROM_PHONE_NUMBER, fromUserPhoneNumber)
				.addData(Constants.CHAT_TO_NAME, toUserName)
				.addData(Constants.CHAT_FROM_NAME, fromUserName)
				.addData(Constants.CHAT_TEXT, chatText)
				.addData(Constants.SERVER_MOOD, serverMood_chat)
				
				
				.build();
		
		return message;
	}
	
	
	public String getDevice(String phoneNumber)
	{
		EntityManager em = EMFService.get().createEntityManager();
		String deviceGCM = "";
		UserBean userBean = UserBean.find(em, phoneNumber);
		if (userBean != null)
		{
			deviceGCM = userBean.getRegId();
		}
		return deviceGCM;
	}

	private void sendToGCM(Message message, String toDeviceGCM)
	{
		Sender sender = new Sender(Constants.API_KEY);
		boolean success = false;
		try
		{
			Result result = sender.send(message, toDeviceGCM, 5);
			logger.log(Level.WARNING, "Result: " + result.toString());
			success = true;
		} catch (IOException e)
		{
			success = false;
			logger.log(Level.SEVERE, e.getMessage());
		}

	}




}
