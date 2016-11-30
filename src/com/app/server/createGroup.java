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
import com.app.bean.MemberBean;
import com.app.bean.UserBean;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

@SuppressWarnings("serial")
public class createGroup extends HttpServlet
{

	private static final Logger logger = Logger.getLogger(createGroup.class
			.getCanonicalName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		
		//DEFINING SERVER MOOD
		final String serverMood_CreateGroup_Or_AddMember = "serverMood_CreateGroup_Or_AddMember";

		String fromPhoneNumber;
		String toPhoneNumber;

		String groupAdmin;
		String groupAdminPhoneNumber;

		String groupName;
		String groupType;
		String groupDescription;

		String memberGroupName;
		String memberName;
		String memberExpense;
		String memberPhoneNumber;
		
		BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
		BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
		BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);
		
		//will Hold all phoneNumbers to List for GCM to sendOver
		ArrayList<String> userPhoneNumbers = new ArrayList<String>();


		// Creating Message for sending to GCM
		Message message;

		int memberCount = 0;

		logger.log(Level.WARNING, "Aks = Working Fine");

		// // Fetching TO/FROM user phoneNumber
		// fromPhoneNumber = req.getParameter(Constants.FROM_USER_PHONE_NUMBER);
		// toPhoneNumber = req.getParameter(Constants.TO_USER_PHONE_NUMBER);

		// Fetching the GroupInfo from the request
		groupAdmin = req.getParameter(Constants.GROUP_ADMIN);
		groupAdminPhoneNumber = req
				.getParameter(Constants.GROUP_ADMIN_PHONE_NUMBER);

		groupName = req.getParameter(Constants.GROUP_NAME);
		groupType = req.getParameter(Constants.GROUP_TYPE);
		groupDescription = req.getParameter(Constants.GROUP_DESCRIPTION);

		// Fetching the UserInfo from the request
		// ****memberGroupName = to be used in
		// memberBean**************************************
		memberGroupName = groupName;
		memberName = req.getParameter(Constants.MEMBER_NAME);
		memberExpense = req.getParameter(Constants.MEMBER_EXPENSE);
		memberPhoneNumber = req.getParameter(Constants.MEMBER_PHONE_NUMBER);

		// ************************************************************************************
		logger.log(Level.WARNING, "groupAdmin = " + groupAdmin);
		logger.log(Level.WARNING, "groupAdminPhoneNumber = "
				+ groupAdminPhoneNumber);

		logger.log(Level.WARNING, "groupName = " + groupName);
		logger.log(Level.WARNING, "groupType = " + groupType);
		logger.log(Level.WARNING, "groupDescription = " + groupDescription);

		logger.log(Level.WARNING, "memberGroupName = " + memberGroupName);
		logger.log(Level.WARNING, "memberName = " + memberName);
		logger.log(Level.WARNING, "memberExpense = " + memberExpense);
		logger.log(Level.WARNING, "memberPhoneNumber = " + memberPhoneNumber);
		// ************************************************************************************

		EntityManager em = EMFService.get().createEntityManager();

		// Storing Group Details in DB using setters
		GroupBean groupBean = new GroupBean();

		groupBean.setGroupAdmin(groupAdmin);
		groupBean.setGroupAdminPhoneNumber(groupAdminPhoneNumber);
		groupBean.setGroupName(groupName);
		groupBean.setType(groupType);
		groupBean.setDescription(groupDescription);

		// Storing Member Details in DB using setters
		// required when restoring groups from server to device
		MemberBean memberBean = new MemberBean();

		memberBean.setMemberName(memberName);
		memberBean.setMemberGroupName(memberGroupName);
		memberBean.setMemberExpense(memberExpense);

		// persist changes

		// persisting groupBean
		em.persist(groupBean);

		// persisting memberBean
		em.persist(memberBean);

		em.close();

		// Fetching Other Members of the Group
		Map<BigDecimal, String> memberMap = new HashMap<BigDecimal, String>();

		// MAking as zero
		Constants.MEMBER_NUMBER = 0;

		logger.log(
				Level.WARNING,
				"req.getParameter(String.valueOf(Constants.MEMBER_NUMBER)) = "
						+ req.getParameter(String
								.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT)));

		
		try
		{
			while (req.getParameter(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT))
					.equals(null) == false)
			{
				logger.log(Level.WARNING, "WHILE_ITERATION = " + memberCount);

				memberMap.put(LOCAL_OTHER_MEMBER_NAME_COUNT, req.getParameter(String
						.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT)));

				memberMap.put(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT, req.getParameter(String
						.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT)));
				
				//Adding userPhone Numbers to ArrayList for GCM to send over
				userPhoneNumbers.add(req.getParameter(String
						.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT)));
				//**********************************************************
				
				memberMap.put(LOCAL_OTHER_MEMBER_EXPENSE_COUNT, req.getParameter(String
						.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT)));

				
				logger.log(
						Level.WARNING,
						LOCAL_OTHER_MEMBER_NAME_COUNT
								+ " - LOCAL_OTHER_MEMBER_NAME_COUNT(While) = "
								+ req.getParameter(String
										.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT)));

				//Incrementing BigDecimal
				LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1));
				
				LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1));
				
				LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1));


				memberCount++;
			}

		} catch (NullPointerException ne)
		{
			logger.log(Level.WARNING, "Catch =  Inside Catch");

			ne.printStackTrace();
		}

		finally
		{

			logger.log(Level.WARNING, "finally =  Inside finally");

			logger.log(Level.WARNING, "memberCount = " + memberCount);

			// Fetching Device GCM ID
			// String fromDeviceGCM = getDevice(fromPhoneNumber);
			ArrayList<String> selectedDevicesGCM = getSelectedDevices(userPhoneNumbers);

			logger.log(Level.WARNING, "toDeviceGCM = " + selectedDevicesGCM);

			switch (memberCount)
			{
			case 1:
				logger.log(Level.WARNING, "memberCount(Switch) = "
						+ memberCount);

				message = createMessageFromRequest_memberCount_1(req,
						groupAdmin, groupAdminPhoneNumber, groupName,
						groupType, groupDescription, memberName, memberExpense,
						memberPhoneNumber, memberMap, serverMood_CreateGroup_Or_AddMember);
				
				logger.log(Level.WARNING, "message = " + message);

				// sending to GCM
				sendToGCM(message, selectedDevicesGCM);
				
				break;

			case 2:

				logger.log(Level.WARNING, "memberCount(Switch) = "
						+ memberCount);

				message = createMessageFromRequest_memberCount_2(req,
						groupAdmin, groupAdminPhoneNumber, groupName,
						groupType, groupDescription, memberName, memberExpense,
						memberPhoneNumber, memberMap, serverMood_CreateGroup_Or_AddMember);

				logger.log(Level.WARNING, "message = " + message);
				
				// sending to GCM
				sendToGCM(message, selectedDevicesGCM);
				
				break;

			case 3:

				logger.log(Level.WARNING, "memberCount(Switch) = "
						+ memberCount);

				message = createMessageFromRequest_memberCount_3(req,
						groupAdmin, groupAdminPhoneNumber, groupName,
						groupType, groupDescription, memberName, memberExpense,
						memberPhoneNumber, memberMap, serverMood_CreateGroup_Or_AddMember);

				logger.log(Level.WARNING, "message = " + message);
			
				// sending to GCM
				sendToGCM(message, selectedDevicesGCM);
				
				break;

			case 4:

				logger.log(Level.WARNING, "memberCount(Switch) = "
						+ memberCount);

				message = createMessageFromRequest_memberCount_4(req,
						groupAdmin, groupAdminPhoneNumber, groupName,
						groupType, groupDescription, memberName, memberExpense,
						memberPhoneNumber, memberMap, serverMood_CreateGroup_Or_AddMember);

				logger.log(Level.WARNING, "message = " + message);
				
				// sending to GCM
				sendToGCM(message, selectedDevicesGCM);

				break;

			case 5:

				logger.log(Level.WARNING, "memberCount(Switch) = "
						+ memberCount);

				message = createMessageFromRequest_memberCount_5(req,
						groupAdmin, groupAdminPhoneNumber, groupName,
						groupType, groupDescription, memberName, memberExpense,
						memberPhoneNumber, memberMap, serverMood_CreateGroup_Or_AddMember);

				logger.log(Level.WARNING, "message = " + message);
				
				// sending to GCM
				sendToGCM(message, selectedDevicesGCM);

				break;

			case 6:

				logger.log(Level.WARNING, "memberCount(Switch) = "
						+ memberCount);

				message = createMessageFromRequest_memberCount_6(req,
						groupAdmin, groupAdminPhoneNumber, groupName,
						groupType, groupDescription, memberName, memberExpense,
						memberPhoneNumber, memberMap, serverMood_CreateGroup_Or_AddMember);

				logger.log(Level.WARNING, "message = " + message);
				
				// sending to GCM
				sendToGCM(message, selectedDevicesGCM);

				break;

			case 7:

				logger.log(Level.WARNING, "memberCount(Switch) = "
						+ memberCount);

				message = createMessageFromRequest_memberCount_7(req,
						groupAdmin, groupAdminPhoneNumber, groupName,
						groupType, groupDescription, memberName, memberExpense,
						memberPhoneNumber, memberMap, serverMood_CreateGroup_Or_AddMember);

				logger.log(Level.WARNING, "message = " + message);
				
				// sending to GCM
				sendToGCM(message, selectedDevicesGCM);

				break;

			case 8:

				logger.log(Level.WARNING, "memberCount(Switch) = "
						+ memberCount);

				message = createMessageFromRequest_memberCount_8(req,
						groupAdmin, groupAdminPhoneNumber, groupName,
						groupType, groupDescription, memberName, memberExpense,
						memberPhoneNumber, memberMap, serverMood_CreateGroup_Or_AddMember);

				logger.log(Level.WARNING, "message = " + message);
				
				// sending to GCM
				sendToGCM(message, selectedDevicesGCM);

				break;

			case 9:

				logger.log(Level.WARNING, "memberCount(Switch) = "
						+ memberCount);

				message = createMessageFromRequest_memberCount_9(req,
						groupAdmin, groupAdminPhoneNumber, groupName,
						groupType, groupDescription, memberName, memberExpense,
						memberPhoneNumber, memberMap, serverMood_CreateGroup_Or_AddMember);

				logger.log(Level.WARNING, "message = " + message);
				
				// sending to GCM
				sendToGCM(message, selectedDevicesGCM);

				break;

			case 10:

				logger.log(Level.WARNING, "memberCount(Switch) = "
						+ memberCount);

				message = createMessageFromRequest_memberCount_10(req,
						groupAdmin, groupAdminPhoneNumber, groupName,
						groupType, groupDescription, memberName, memberExpense,
						memberPhoneNumber, memberMap, serverMood_CreateGroup_Or_AddMember);

				logger.log(Level.WARNING, "message = " + message);

				// sending to GCM
				sendToGCM(message, selectedDevicesGCM);

				break;

			default:
				logger.log(Level.WARNING, "Switch Case =  Wrong Case");
				break;

			}

		}

	}

	private Message createMessageFromRequest_memberCount_1(
			HttpServletRequest req, String groupAdmin,
			String groupAdminPhoneNumber, String groupName, String groupType,
			String groupDescription, String memberName, String memberExpense,
			String memberPhoneNumber, Map<BigDecimal, String> memberMap, String serverMood_CreateGroup_Or_AddMember)
	{

		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
		BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
		BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

		Message message = new Message.Builder()
				.addData(Constants.GROUP_ADMIN, groupAdmin)
				.addData(Constants.GROUP_ADMIN_PHONE_NUMBER,
						groupAdminPhoneNumber)
				.addData(Constants.GROUP_NAME, groupName)
				.addData(Constants.GROUP_TYPE, groupType)
				.addData(Constants.GROUP_DESCRIPTION, groupDescription)
				.addData(Constants.MEMBER_NAME, memberName)
				.addData(Constants.MEMBER_EXPENSE, memberExpense)
				.addData(Constants.MEMBER_PHONE_NUMBER, memberPhoneNumber)
				.addData(Constants.SERVER_MOOD, serverMood_CreateGroup_Or_AddMember)
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT)).build();
		
		return message;
	}

	private Message createMessageFromRequest_memberCount_2(
			HttpServletRequest req, String groupAdmin,
			String groupAdminPhoneNumber, String groupName, String groupType,
			String groupDescription, String memberName, String memberExpense,
			String memberPhoneNumber, Map<BigDecimal, String> memberMap, String serverMood_CreateGroup_Or_AddMember)
	{

		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
		BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
		BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

		Message message = new Message.Builder()
				.addData(Constants.GROUP_ADMIN, groupAdmin)
				.addData(Constants.GROUP_ADMIN_PHONE_NUMBER,
						groupAdminPhoneNumber)
				.addData(Constants.GROUP_NAME, groupName)
				.addData(Constants.GROUP_TYPE, groupType)
				.addData(Constants.GROUP_DESCRIPTION, groupDescription)
				.addData(Constants.MEMBER_NAME, memberName)
				.addData(Constants.MEMBER_EXPENSE, memberExpense)
				.addData(Constants.MEMBER_PHONE_NUMBER, memberPhoneNumber)
				.addData(Constants.SERVER_MOOD, serverMood_CreateGroup_Or_AddMember)

				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				.build();
				
		return message;
	}

	private Message createMessageFromRequest_memberCount_3(
			HttpServletRequest req, String groupAdmin,
			String groupAdminPhoneNumber, String groupName, String groupType,
			String groupDescription, String memberName, String memberExpense,
			String memberPhoneNumber, Map<BigDecimal, String> memberMap, String serverMood_CreateGroup_Or_AddMember)
	{

		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
		BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
		BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

		Message message = new Message.Builder()
				.addData(Constants.GROUP_ADMIN, groupAdmin)
				.addData(Constants.GROUP_ADMIN_PHONE_NUMBER,
						groupAdminPhoneNumber)
				.addData(Constants.GROUP_NAME, groupName)
				.addData(Constants.GROUP_TYPE, groupType)
				.addData(Constants.GROUP_DESCRIPTION, groupDescription)
				.addData(Constants.MEMBER_NAME, memberName)
				.addData(Constants.MEMBER_EXPENSE, memberExpense)
				.addData(Constants.MEMBER_PHONE_NUMBER, memberPhoneNumber)
				.addData(Constants.SERVER_MOOD, serverMood_CreateGroup_Or_AddMember)

				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))

				.build();
				return message;
	}

	private Message createMessageFromRequest_memberCount_4(
			HttpServletRequest req, String groupAdmin,
			String groupAdminPhoneNumber, String groupName, String groupType,
			String groupDescription, String memberName, String memberExpense,
			String memberPhoneNumber, Map<BigDecimal, String> memberMap, String serverMood_CreateGroup_Or_AddMember)
	{

		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
		BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
		BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

		Message message = new Message.Builder()
				.addData(Constants.GROUP_ADMIN, groupAdmin)
				.addData(Constants.GROUP_ADMIN_PHONE_NUMBER,
						groupAdminPhoneNumber)
				.addData(Constants.GROUP_NAME, groupName)
				.addData(Constants.GROUP_TYPE, groupType)
				.addData(Constants.GROUP_DESCRIPTION, groupDescription)
				.addData(Constants.MEMBER_NAME, memberName)
				.addData(Constants.MEMBER_EXPENSE, memberExpense)
				.addData(Constants.MEMBER_PHONE_NUMBER, memberPhoneNumber)
				.addData(Constants.SERVER_MOOD, serverMood_CreateGroup_Or_AddMember)

				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))

				
				.build();
		return message;
	}

	private Message createMessageFromRequest_memberCount_5(
			HttpServletRequest req, String groupAdmin,
			String groupAdminPhoneNumber, String groupName, String groupType,
			String groupDescription, String memberName, String memberExpense,
			String memberPhoneNumber, Map<BigDecimal, String> memberMap, String serverMood_CreateGroup_Or_AddMember)
	{

		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
		BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
		BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

		Message message = new Message.Builder()
				.addData(Constants.GROUP_ADMIN, groupAdmin)
				.addData(Constants.GROUP_ADMIN_PHONE_NUMBER,
						groupAdminPhoneNumber)
				.addData(Constants.GROUP_NAME, groupName)
				.addData(Constants.GROUP_TYPE, groupType)
				.addData(Constants.GROUP_DESCRIPTION, groupDescription)
				.addData(Constants.MEMBER_NAME, memberName)
				.addData(Constants.MEMBER_EXPENSE, memberExpense)
				.addData(Constants.MEMBER_PHONE_NUMBER, memberPhoneNumber)
				.addData(Constants.SERVER_MOOD, serverMood_CreateGroup_Or_AddMember)	
				

				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.build();
				
				
				
				return message;
	}

	private Message createMessageFromRequest_memberCount_6(
			HttpServletRequest req, String groupAdmin,
			String groupAdminPhoneNumber, String groupName, String groupType,
			String groupDescription, String memberName, String memberExpense,
			String memberPhoneNumber, Map<BigDecimal, String> memberMap, String serverMood_CreateGroup_Or_AddMember)
	{

		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
		BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
		BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

		Message message = new Message.Builder()
				.addData(Constants.GROUP_ADMIN, groupAdmin)
				.addData(Constants.GROUP_ADMIN_PHONE_NUMBER,
						groupAdminPhoneNumber)
				.addData(Constants.GROUP_NAME, groupName)
				.addData(Constants.GROUP_TYPE, groupType)
				.addData(Constants.GROUP_DESCRIPTION, groupDescription)
				.addData(Constants.MEMBER_NAME, memberName)
				.addData(Constants.MEMBER_EXPENSE, memberExpense)
				.addData(Constants.MEMBER_PHONE_NUMBER, memberPhoneNumber)
				.addData(Constants.SERVER_MOOD, serverMood_CreateGroup_Or_AddMember)

				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.build();
				
				
				return message;
	}

	private Message createMessageFromRequest_memberCount_7(
			HttpServletRequest req, String groupAdmin,
			String groupAdminPhoneNumber, String groupName, String groupType,
			String groupDescription, String memberName, String memberExpense,
			String memberPhoneNumber, Map<BigDecimal, String> memberMap, String serverMood_CreateGroup_Or_AddMember)
	{

		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
		BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
		BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

		Message message = new Message.Builder()
				.addData(Constants.GROUP_ADMIN, groupAdmin)
				.addData(Constants.GROUP_ADMIN_PHONE_NUMBER,
						groupAdminPhoneNumber)
				.addData(Constants.GROUP_NAME, groupName)
				.addData(Constants.GROUP_TYPE, groupType)
				.addData(Constants.GROUP_DESCRIPTION, groupDescription)
				.addData(Constants.MEMBER_NAME, memberName)
				.addData(Constants.MEMBER_EXPENSE, memberExpense)
				.addData(Constants.MEMBER_PHONE_NUMBER, memberPhoneNumber)
				.addData(Constants.SERVER_MOOD, serverMood_CreateGroup_Or_AddMember)

				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))


				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.build();
				
				
				
				return message;
	}

	private Message createMessageFromRequest_memberCount_8(
			HttpServletRequest req, String groupAdmin,
			String groupAdminPhoneNumber, String groupName, String groupType,
			String groupDescription, String memberName, String memberExpense,
			String memberPhoneNumber, Map<BigDecimal, String> memberMap, String serverMood_CreateGroup_Or_AddMember)
	{

		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
		BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
		BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

		Message message = new Message.Builder()
				.addData(Constants.GROUP_ADMIN, groupAdmin)
				.addData(Constants.GROUP_ADMIN_PHONE_NUMBER,
						groupAdminPhoneNumber)
				.addData(Constants.GROUP_NAME, groupName)
				.addData(Constants.GROUP_TYPE, groupType)
				.addData(Constants.GROUP_DESCRIPTION, groupDescription)
				.addData(Constants.MEMBER_NAME, memberName)
				.addData(Constants.MEMBER_EXPENSE, memberExpense)
				.addData(Constants.MEMBER_PHONE_NUMBER, memberPhoneNumber)
				.addData(Constants.SERVER_MOOD, serverMood_CreateGroup_Or_AddMember)

				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.build();

				
				return message;
	}

	private Message createMessageFromRequest_memberCount_9(
			HttpServletRequest req, String groupAdmin,
			String groupAdminPhoneNumber, String groupName, String groupType,
			String groupDescription, String memberName, String memberExpense,
			String memberPhoneNumber, Map<BigDecimal, String> memberMap, String serverMood_CreateGroup_Or_AddMember)
	{

		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
		BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
		BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

		Message message = new Message.Builder()
				.addData(Constants.GROUP_ADMIN, groupAdmin)
				.addData(Constants.GROUP_ADMIN_PHONE_NUMBER,
						groupAdminPhoneNumber)
				.addData(Constants.GROUP_NAME, groupName)
				.addData(Constants.GROUP_TYPE, groupType)
				.addData(Constants.GROUP_DESCRIPTION, groupDescription)
				.addData(Constants.MEMBER_NAME, memberName)
				.addData(Constants.MEMBER_EXPENSE, memberExpense)
				.addData(Constants.MEMBER_PHONE_NUMBER, memberPhoneNumber)
				.addData(Constants.SERVER_MOOD, serverMood_CreateGroup_Or_AddMember)
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.build();

				
				
				return message;
	}

	private Message createMessageFromRequest_memberCount_10(
			HttpServletRequest req, String groupAdmin,
			String groupAdminPhoneNumber, String groupName, String groupType,
			String groupDescription, String memberName, String memberExpense,
			String memberPhoneNumber, Map<BigDecimal, String> memberMap, String serverMood_CreateGroup_Or_AddMember)
	{

		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		// MAKING Constants.MEMBER_NUMBER as "ZERO" again
		BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
		BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
		BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

		Message message = new Message.Builder()
				.addData(Constants.GROUP_ADMIN, groupAdmin)
				.addData(Constants.GROUP_ADMIN_PHONE_NUMBER,
						groupAdminPhoneNumber)
				.addData(Constants.GROUP_NAME, groupName)
				.addData(Constants.GROUP_TYPE, groupType)
				.addData(Constants.GROUP_DESCRIPTION, groupDescription)
				.addData(Constants.MEMBER_NAME, memberName)
				.addData(Constants.MEMBER_EXPENSE, memberExpense)
				.addData(Constants.MEMBER_PHONE_NUMBER, memberPhoneNumber)
				.addData(Constants.SERVER_MOOD, serverMood_CreateGroup_Or_AddMember)
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				

				.addData(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))
				.addData(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1))), memberMap.get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT))
				
				
				.build();
				
				
				
				return message;
	}
	
	
//	public String getDevice(String phoneNumber)
//	{
//		EntityManager em = EMFService.get().createEntityManager();
//		String deviceGCM = "";
//		UserBean userBean = UserBean.find(em, phoneNumber);
//		if (userBean != null)
//		{
//			deviceGCM = userBean.getRegId();
//		}
//		return deviceGCM;
//	}


	private void sendToGCM(Message message, List<String> selectedDevicesGCM)
	{
		Sender sender = new Sender(Constants.API_KEY);
		boolean success = false;
		try
		{
			//Result result = sender.send(message, toDevice, 5);
			MulticastResult multiresult = sender.send(message, selectedDevicesGCM, 5);
			logger.log(Level.WARNING, "Result: " + multiresult.toString());
			success = true;
		} catch (IOException e)
		{
			success = false;
			logger.log(Level.SEVERE, e.getMessage());
		}

	}

	 public ArrayList<String> getSelectedDevices(ArrayList<String> userPhoneNumbers)
	 {
		 EntityManager em = EMFService.get().createEntityManager();
		 ArrayList<String> selectedDevicesGCM = new ArrayList<String>();
		 for(String phone : userPhoneNumbers)
		 {
			 UserBean userBean = UserBean.find(em, phone);
			 if(userBean != null)
			 {
				 selectedDevicesGCM.add(userBean.getRegId());
			 }
		 }
		 return selectedDevicesGCM;
	 }

}
