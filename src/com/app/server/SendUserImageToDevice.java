package com.app.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import com.app.bean.EMFService;
import com.app.bean.UserBean;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

@SuppressWarnings("serial")
public class SendUserImageToDevice extends HttpServlet
{

	private static final Logger logger = Logger.getLogger(SendUserImageToDevice.class
			.getCanonicalName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{

		// Sending userImageURL to Device
		// DEFINING SERVER MOOD
		final String serverMood_userImage = "serverMood_userImageURL";

		String phoneNumber = req.getParameter(Constants.USER_PHONE_NUMBER);
		logger.log(Level.WARNING, "phoneNumber = " + phoneNumber);

		// Fetch Contact GCM id
		String toDeviceGCM = getDevice(phoneNumber);
		logger.log(Level.WARNING, "toDeviceGCM = " + toDeviceGCM);

		// Fetch userImage
		// byte[] userImage = getUserImage(phoneNew);
		// String encodedImage = DatatypeConverter.printBase64Binary(userImage);
		// logger.log(Level.WARNING, "encodedImage = " +
		// encodedImage.substring(0, 10));

		String userImageURL = getUserImageURL(phoneNumber);
		logger.log(Level.WARNING, "userImageURL = " + userImageURL);

		// Create Message from data
		Message message = createMessageFromRequest(userImageURL,
				serverMood_userImage);

		// sEND to GCM server
		sendToGCM(message, toDeviceGCM);

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

	public String getUserImageURL(String phoneNumber)
	{
		EntityManager em = EMFService.get().createEntityManager();
		String userImageURL = null;
		UserBean userBean = UserBean.find(em, phoneNumber);
		if (userBean != null)
		{
			userImageURL = userBean.getUserImageURL();
		}
		return userImageURL;

	}

//	public byte[] getUserImage(String phoneNumber)
//	{
//		EntityManager em = EMFService.get().createEntityManager();
//		byte[] userImage = null;
//		UserBean userBean = UserBean.find(em, phoneNumber);
//		if (userBean != null)
//		{
//			userImage = userBean.getUserImage();
//		}
//		return userImage;
//
//	}

	private Message createMessageFromRequest(String userImageURL,
			String serverMood_userImage)
	{

		Message message = new Message.Builder()
				.addData(Constants.USER_IMAGE_URL, userImageURL)
				.addData(Constants.SERVER_MOOD, serverMood_userImage).build();

		return message;
	}

	private void sendToGCM(Message message, String toDeviceGCM)
	{
		Sender sender = new Sender(Constants.API_KEY);
		boolean success = false;
		try
		{
			// send("message", "GCM_ID", "NO. OF RETRY ATTEMPTS")
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
