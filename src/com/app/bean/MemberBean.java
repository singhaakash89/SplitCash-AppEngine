package com.app.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MemberBean
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String memberGroupName;
	private String memberName;
	private String memberExpense;
	
	public MemberBean(){}
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getMemberGroupName()
	{
		return memberGroupName;
	}
	public void setMemberGroupName(String memberGroupName)
	{
		this.memberGroupName = memberGroupName;
	}
	public String getMemberName()
	{
		return memberName;
	}
	public void setMemberName(String memberName)
	{
		this.memberName = memberName;
	}
	public String getMemberExpense()
	{
		return memberExpense;
	}
	public void setMemberExpense(String memberExpense)
	{
		this.memberExpense = memberExpense;
	}
	
	
	
}
