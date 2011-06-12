package org.salespointframework.core.database;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


// TODO Name �ndern
// Singleton ist IMO n�tig, da alles EntityManager von EINER Factory kommen sollten
public class Database {

	private EntityManagerFactory emf;
	
	private static final Database INSTANCE = new Database();
	
	public static Database getInstance() {
		return INSTANCE;
	}
	
	// TODO 
	// laut Doku wird keine Exception geworfen, genauer checken
	// http://download.oracle.com/javaee/6/api/javax/persistence/Persistence.html#createEntityManagerFactory(java.lang.String)
	// also gehe ich davon aus, dass da einfach null bei einem Fehler zur�ck kommt
	
	public boolean initializeEntityManagerFactory(String persistenceUnitName) {
		emf = Persistence.createEntityManagerFactory(persistenceUnitName);
		return emf != null ? true : false; 
	}
	
	public boolean initializeEntityManagerFactory(String persistenceUnitName, @SuppressWarnings("rawtypes") Map properties)
	{
		emf = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
		return emf != null ? true : false;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}
	
}
