package br.ufjf.coordenacao.sistemagestaocurso.repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public abstract class BaseRepository {
	@Inject
	protected EntityManager manager;

	public <T> T persistir(T entity) {
		EntityTransaction transaction = null;

		try {
			transaction = manager.getTransaction();
			if (!transaction.isActive())
				transaction.begin();
			entity = manager.merge(entity);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		}

		return entity;
	}
}
