package com.app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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

/**
 * @author appsrox.com
 *
 */
@SuppressWarnings("serial")
public class RegisterUser extends HttpServlet
{

	private static final Logger logger = Logger.getLogger(RegisterUser.class
			.getCanonicalName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{

		// Fetching the userInfo from the request
		String regId = req.getParameter(Constants.USER_GCM_ID);
		logger.log(Level.WARNING, "GCM_ID = " + regId);

		String user = req.getParameter(Constants.USER_NAME);
		logger.log(Level.WARNING, "User = " + user);

		String phone = req.getParameter(Constants.USER_PHONE_NUMBER);
		logger.log(Level.WARNING, "Phone = " + phone);
		
//		//fetching image in lowest resolution i.e. <4KB (not using as of now - will use for thumbnail)
//		String encodeImage = req.getParameter(Constants.USER_IMAGE_DATA);
//		logger.log(Level.WARNING, "userImage = " + encodeImage.substring(0, 10));

//		byte[] imageInBytes = DatatypeConverter.parseBase64Binary(encodeImage);
//		logger.log(Level.WARNING, "imageInBytes = " + imageInBytes);

		//fETCHING IMAGE url
		String userImageURL = req.getParameter(Constants.USER_IMAGE_URL);

		EntityManager em = EMFService.get().createEntityManager();

		// Storing in DB using setters
		UserBean userBean = new UserBean();

		userBean.setRegId(regId);
		userBean.setUserName(user);
		userBean.setPhoneNumber(phone);
//		userBean.setUserImage(imageInBytes);
		userBean.setUserImageURL(userImageURL);

		// persist changes
		em.persist(userBean);

		em.close();

	}

}
