package com.app.server;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.bean.EMFService;
import com.app.bean.GroupBean;
import com.app.bean.UserBean;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

@SuppressWarnings("serial")
public class deleteMember extends HttpServlet
{
	private static final Logger logger = Logger.getLogger(createGroup.class
			.getCanonicalName());
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		//DEFINING SERVER MOOD
		final String serverMood_deleteMember = "serverMood_deleteMember";
		
		String groupName;
		String groupMemberPhoneNumber;
		String groupAdmin;
		String groupAdminPhoneNumber;
		
		// Creating Message for sending to GCM
		Message message;

		// will Hold MemberCount for proper Message to be generated out of it
		int memberCount = 0;

		
		BigDecimal LOCAL_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.4);

		// will Hold all phoneNumbers to List for GCM to sendOver
		ArrayList<String> userPhoneNumbers = new ArrayList<String>();
		
//		//will hold all members phoneNumbers using key/value PAIR
//		Map<String, String> memberMap = new HashMap<String, String>();
		
		groupName = req.getParameter(Constants.GROUP_NAME);
		groupMemberPhoneNumber = req.getParameter(Constants.MEMBER_TO_UPDATE);
		groupAdminPhoneNumber = req.getParameter(Constants.GROUP_ADMIN_PHONE_NUMBER);
		
		//****************************************************************************		
		logger.log(Level.WARNING, "groupName(BEFORE) = " + groupName);
		logger.log(Level.WARNING, "groupMemberPhoneNumber(BEFORE) = " + groupMemberPhoneNumber);
		logger.log(Level.WARNING, "groupAdminPhoneNumber(BEFORE) = " + groupAdminPhoneNumber);
		//****************************************************************************

		
		//Finding Group Admin
		EntityManager em = EMFService.get().createEntityManager();
		
		GroupBean groupBean = GroupBean.find(em, groupAdminPhoneNumber);
		
		groupAdmin = groupBean.getGroupAdmin();

		//****************************************************************************		
		logger.log(Level.WARNING, "groupName = " + groupName);
		logger.log(Level.WARNING, "groupMemberPhoneNumber = " + groupMemberPhoneNumber);
		logger.log(Level.WARNING, "groupAdmin = " + groupAdmin);
		//****************************************************************************
		
		try
		{
			while (req.getParameter(
					String.valueOf(LOCAL_MEMBER_PHONE_NUMBER_COUNT)).equals(
					null) == false)
			{
				
//				//Adding phoneNumbers to Map
//				memberMap.put(String.valueOf(LOCAL_MEMBER_PHONE_NUMBER_COUNT), req.getParameter(String
//						.valueOf(LOCAL_MEMBER_PHONE_NUMBER_COUNT)));
				
				// Adding userPhone Numbers to ArrayList for GCM to send over
				userPhoneNumbers.add(req.getParameter(String
						.valueOf(LOCAL_MEMBER_PHONE_NUMBER_COUNT)));
				// **********************************************************

				logger.log(Level.WARNING, "BigDecimal : " +LOCAL_MEMBER_PHONE_NUMBER_COUNT);
				
				logger.log(Level.WARNING, "req.getParameter(String.valueOf(LOCAL_MEMBER_PHONE_NUMBER_COUNT) : "
				+req.getParameter(String.valueOf(LOCAL_MEMBER_PHONE_NUMBER_COUNT)));
				
				// Incrementing BigDecimal
				LOCAL_MEMBER_PHONE_NUMBER_COUNT = LOCAL_MEMBER_PHONE_NUMBER_COUNT
						.add(new BigDecimal(1));

				// Incrementing Member Count
				memberCount++;

			}

		} catch (NullPointerException ne)
		{
			ne.printStackTrace();
		} finally
		{
			logger.log(Level.WARNING, "finally =  Inside finally");

			logger.log(Level.WARNING, "memberCount = " + memberCount);
			
			//For avoiding userPhoneNumbers = NULL in case of No other Members apart from the BEING eDITED mEMBER
			if(memberCount == 0)
			{
				userPhoneNumbers.add(groupMemberPhoneNumber);
			}

			// Fetching Device GCM ID
			// String fromDeviceGCM = getDevice(fromPhoneNumber);
			ArrayList<String> selectedDevicesGCM = getSelectedDevices(userPhoneNumbers);

			logger.log(Level.WARNING, "toDeviceGCM = " + selectedDevicesGCM);
			
			message = createMessageFromRequest(req,
					groupName, groupMemberPhoneNumber, groupAdmin, serverMood_deleteMember);
			
			// sending to GCM
			sendToGCM(message, selectedDevicesGCM);
		}
			
	}
	
	
	
	
	private Message createMessageFromRequest(
			HttpServletRequest req, String groupName,
			String groupMemberPhoneNumber, String groupAdmin, String serverMood_deleteMember)
	{

		// 
		BigDecimal LOCAL_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.4);
		

		Message message = new Message.Builder()
				.addData(Constants.GROUP_NAME, groupName)
				.addData(Constants.MEMBER_TO_UPDATE, groupMemberPhoneNumber)
				.addData(Constants.GROUP_ADMIN, groupAdmin)
				.addData(Constants.SERVER_MOOD, serverMood_deleteMember)
				
				
				.build();
		
		return message;
	}


	private void sendToGCM(Message message, List<String> selectedDevicesGCM)
	{
		Sender sender = new Sender(Constants.API_KEY);
		boolean success = false;
		try
		{
			// Result result = sender.send(message, toDevice, 5);
			MulticastResult multiresult = sender.send(message,
					selectedDevicesGCM, 5);
			logger.log(Level.WARNING, "Result: " + multiresult.toString());
			success = true;
		} catch (IOException e)
		{
			success = false;
			logger.log(Level.SEVERE, e.getMessage());
		}

	}

	public ArrayList<String> getSelectedDevices(
			ArrayList<String> userPhoneNumbers)
	{
		EntityManager em = EMFService.get().createEntityManager();
		ArrayList<String> selectedDevicesGCM = new ArrayList<String>();
		for (String phone : userPhoneNumbers)
		{
			UserBean userBean = UserBean.find(em, phone);
			if (userBean != null)
			{
				selectedDevicesGCM.add(userBean.getRegId());
			}
		}
		return selectedDevicesGCM;
	}
	
	
}
