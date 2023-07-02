package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.model.Curso;
import br.ufjf.coordenacao.sistemagestaocurso.model.Grade;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.List;

public class GradeRepository extends BaseRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	public Grade porid(long id) {
		return manager.find(Grade.class, id);
	}

	public Grade persistirSemCommit(Grade objeto) {
		EntityTransaction transaction = null;
		try {
			transaction = manager.getTransaction();
			if (!transaction.isActive())
				transaction.begin();
			objeto = manager.merge(objeto);
			manager.flush();
			//transaction.commit();
		} catch (Exception e) {
			//transaction.rollback();
			throw e;
		}
		
		return objeto;
	}
	
	public void persistir(List<Grade> objetos) {
		EntityTransaction transaction = null;
		try {
			transaction = manager.getTransaction();
			transaction.begin();
			//objetos = manager.merge(objetos);
			for(Grade grade : objetos) {
				grade = manager.merge(grade);
				//manager.flush();
				//manager.clear();
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}
		
	}
	
	public void persistirSemCommit(List<Grade> objetos) {
		EntityTransaction transaction = null;
		try {
			transaction = manager.getTransaction();
			if (!transaction.isActive())
				transaction.begin();
			//objetos = manager.merge(objetos);
			for(Grade grade : objetos) {
				grade = manager.merge(grade);
				manager.flush();
				//manager.clear();
			}
			//transaction.commit();
		} catch (Exception e) {
			//transaction.rollback();
			throw e;
		}
		
	}

	public void remover(Grade objeto) {
		EntityTransaction transaction = null;
		
		try {
			transaction = manager.getTransaction();
			transaction.begin();
			manager.createQuery("DELETE FROM Grade g WHERE g.id = :grade").setParameter("grade", objeto.getId()).executeUpdate();
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}
	}

	public List<Grade> listarTodos() {
		return manager.createQuery("FROM Grade", Grade.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> buscarTodosCodigosGrade(String variavel, long idCurso) {
		return manager.createQuery("Select codigo FROM Grade WHERE codigo like :codigo and id_curso = :idcurso")
				.setParameter("codigo", "%" + variavel + "%").setParameter("idcurso", idCurso).getResultList();

	}
	
	@SuppressWarnings("unchecked")
	public List<String> buscarTodosCodigosGrade(long idCurso) {
		return manager.createQuery("Select codigo FROM Grade WHERE id_curso = :idcurso")
				.setParameter("idcurso", idCurso).getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<Grade> buscarTodosCodigosGradePorCurso(long idCurso) {

		return manager.createQuery("FROM Grade WHERE id_curso = :idcurso").setParameter("idcurso", idCurso)
				.getResultList();
	}

	public Grade buscarPorCodigoGrade(String grade, Curso curso) {
		try {
			return manager.createQuery("FROM Grade WHERE codigo = :codigo AND id_curso = :curso", Grade.class)
					.setParameter("codigo", grade)
					.setParameter("curso", curso.getId())
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}