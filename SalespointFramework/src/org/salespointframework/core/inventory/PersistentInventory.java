package org.salespointframework.core.inventory;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.salespointframework.core.database.Database;
import org.salespointframework.core.product.PersistentProduct;
import org.salespointframework.core.product.PersistentProduct_;
import org.salespointframework.core.product.ProductFeature;
import org.salespointframework.core.product.ProductIdentifier;
import org.salespointframework.core.product.SerialNumber;
import org.salespointframework.util.Iterables;
import org.salespointframework.util.Objects;

/**
 * 
 * @author Paul Henke
 * 
 */
public final class PersistentInventory implements Inventory<PersistentProduct>
{

	private final EntityManagerFactory emf = Database.INSTANCE.getEntityManagerFactory();
	private final EntityManager entityManager;

	/**
	 * 
	 */
	public PersistentInventory()
	{
		this.entityManager = null;
	}

	/**
	 * 
	 * @param entityManager
	 */
	public PersistentInventory(EntityManager entityManager)
	{
		this.entityManager = Objects.requireNonNull(entityManager, "entityManager");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(PersistentProduct persistentProduct)
	{
		Objects.requireNonNull(persistentProduct, "persistentProduct");
		EntityManager em = getEntityManager();
		em.persist(persistentProduct);
		beginCommit(em);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(SerialNumber serialNumber)
	{
		Objects.requireNonNull(serialNumber, "serialNumber");
		EntityManager em = getEntityManager();
		Object product = em.find(PersistentProduct.class, serialNumber);
		if (product != null)
		{
			em.remove(product);
			beginCommit(em);
			return true;
		}
		{
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(SerialNumber serialNumber)
	{
		Objects.requireNonNull(serialNumber, "serialNumber");
		EntityManager em = getEntityManager();
		return em.find(PersistentProduct.class, serialNumber) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends PersistentProduct> E get(Class<E> clazz, SerialNumber serialNumber)
	{
		Objects.requireNonNull(serialNumber, "serialNumber");
		Objects.requireNonNull(clazz, "clazz");
		EntityManager em = getEntityManager();
		return em.find(clazz, serialNumber);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends PersistentProduct> Iterable<E> find(Class<E> clazz)
	{
		Objects.requireNonNull(clazz, "clazz");

		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<E> cq = cb.createQuery(clazz);
		Root<E> entry = cq.from(clazz);

		cq.where(entry.type().in(clazz));

		TypedQuery<E> tq = em.createQuery(cq);

		return Iterables.from(tq.getResultList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends PersistentProduct> Iterable<E> find(Class<E> clazz, ProductIdentifier productIdentifier)
	{
		Objects.requireNonNull(clazz, "clazz");
		Objects.requireNonNull(productIdentifier, "productIdentifier");

		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<E> cq = cb.createQuery(clazz);
		Root<E> entry = cq.from(clazz);

		Predicate p0 = entry.type().in(clazz);
		Predicate p1 = cb.equal(entry.get(PersistentProduct_.productIdentifier), productIdentifier);

		cq.where(p0, p1);

		TypedQuery<E> tq = em.createQuery(cq);

		return Iterables.from(tq.getResultList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends PersistentProduct> Iterable<E> find(Class<E> clazz, ProductIdentifier productIdentifier, Iterable<ProductFeature> productFeatures)
	{
		Objects.requireNonNull(clazz, "clazz");
		Objects.requireNonNull(productIdentifier, "productIdentifier");
		Objects.requireNonNull(productFeatures, "productFeatures");

		Set<ProductFeature> featureSet = Iterables.toSet(productFeatures);

		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<E> cq = cb.createQuery(clazz);
		Root<E> entry = cq.from(clazz);

		Predicate p0 = entry.type().in(clazz);
		Predicate p1 = cb.equal(entry.get(PersistentProduct_.productIdentifier), productIdentifier);
		Predicate p2 = cb.equal(entry.<Set<ProductFeature>> get("productFeatures"), featureSet);

		cq.where(p0, p1, p2);

		TypedQuery<E> tq = em.createQuery(cq);
		return Iterables.from(tq.getResultList());
	}

	public PersistentInventory createNew(EntityManager entityManager)
	{
		return new PersistentInventory(entityManager);
	}

	private final EntityManager getEntityManager()
	{
		return entityManager != null ? entityManager : emf.createEntityManager();
	}

	// TODO not pretty
	private final void beginCommit(EntityManager entityManager)
	{
		if (this.entityManager == null)
		{
			entityManager.getTransaction().begin();
			entityManager.getTransaction().commit();
		} else
		{
			this.entityManager.getTransaction().begin();
			this.entityManager.getTransaction().commit();
		}
	}
}
