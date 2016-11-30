package com.app.bean;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author
 * Created by Aakash Singh
 *
 */
public class EMFService {
	private static final EntityManagerFactory emfInstance = Persistence.createEntityManagerFactory("transactions-optional");

	private EMFService() {
	}

	public static EntityManagerFactory get() {
		return emfInstance;
	}
}
