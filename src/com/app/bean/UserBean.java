package com.app.bean;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Query;

import com.app.server.Constants;
import com.app.server.createGroup;

/**
 * @author Created by Aakash Singh
 *
 */
@Entity
public class UserBean
{
	private static final Logger logger = Logger.getLogger(UserBean.class
			.getCanonicalName());

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String regId;
	private String userName;
	private String phoneNumber;
	private String userImageURL;

	@Lob
	@Column(columnDefinition = "BLOB")
	private byte[] userImage;

	public UserBean()
	{
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getRegId()
	{
		return regId;
	}

	public void setRegId(String regId)
	{
		this.regId = regId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public byte[] getUserImage()
	{
		return userImage;
	}

	public void setUserImage(byte[] userImage)
	{
		this.userImage = userImage;
	}

	public String getUserImageURL()
	{
		return userImageURL;
	}

	public void setUserImageURL(String userImageURL)
	{
		this.userImageURL = userImageURL;
	}

	public static UserBean find(EntityManager em, String phone)
	{
		Query query = em
				.createQuery("select u from UserBean u where u.phoneNumber=:phoneNumberForRegistration");
		query.setParameter(Constants.USER_PHONE_NUMBER, phone);
		List<UserBean> result = query.getResultList();

		if (!result.isEmpty())
		{
			logger.log(Level.WARNING, "UserBean_redId = " + result.get(0).regId);
			return result.get(0);
		}
		return null;
	}

}
