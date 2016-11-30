package com.app.bean;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import com.app.server.Constants;
import com.app.server.createGroup;

@Entity
public class GroupBean
{

	private static final Logger logger = Logger.getLogger(createGroup.class
			.getCanonicalName());

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String groupAdmin;
	private String groupAdminPhoneNumber;
	private String groupName;
	private String type;
	private String description;

	public GroupBean()
	{

	}

	public String getGroupAdmin()
	{
		return groupAdmin;
	}

	public void setGroupAdmin(String groupAdmin)
	{
		this.groupAdmin = groupAdmin;
	}

	public String getGroupAdminPhoneNumber()
	{
		return groupAdminPhoneNumber;
	}

	public void setGroupAdminPhoneNumber(String groupAdminPhoneNumber)
	{
		this.groupAdminPhoneNumber = groupAdminPhoneNumber;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public static  GroupBean find(EntityManager em, String phone)
	{
		Query query = em.createQuery("select g from GroupBean g where g.groupAdminPhoneNumber=:groupAdminPhoneNumber");
		query.setParameter(Constants.GROUP_ADMIN_PHONE_NUMBER, phone);
		List<GroupBean> result = query.getResultList();
		
		if (!result.isEmpty())
		{
			logger.log(Level.WARNING, "GroupBean_groupAdminNAME = " + result.get(0).groupAdmin);
			return result.get(0);
		}
		return null;
	}


}