package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.model.GradeDisciplina;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.List;

public class GradeDisciplinaRepository extends BaseRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	public GradeDisciplina porid(long id) {
		return manager.find(GradeDisciplina.class, id);
	}

	public void remover(GradeDisciplina objeto) {
		EntityTransaction transaction = null;
		
		try {
			transaction = manager.getTransaction();
			transaction.begin();
			manager.remove(manager.contains(objeto) ? objeto : manager.merge(objeto));
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		};

	}

	public List<GradeDisciplina> listarTodos() {
		return manager.createQuery("FROM GradeDisciplina", GradeDisciplina.class).getResultList();
	}

	public List<GradeDisciplina> buscarTodasGradeDisciplinaPorGrade(long codigo) {
		return manager
				.createQuery("FROM GradeDisciplina WHERE id_grade = :codigo order by periodo", GradeDisciplina.class)
				.setParameter("codigo", codigo).getResultList();
	}

	public List<GradeDisciplina> buscarTodasGradeDisciplinaPorGradeId(long idGrade) {
		return manager
				.createQuery("FROM GradeDisciplina WHERE id_grade = :idGrade order by periodo", GradeDisciplina.class)
				.setParameter("idGrade", idGrade).getResultList();
	}

	public GradeDisciplina buscarPorPK(String tipo, Long periodo, Long idGrade, Long idDisciplina) {
		try {
			return manager
					.createQuery("FROM GradeDisciplina WHERE tipo = :tipo and periodo = :periodo",
							GradeDisciplina.class)
					.setParameter("tipo", tipo).setParameter("periodo", periodo).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	public GradeDisciplina buscarPorDisciplinaGrade(Long idGrade, Long idDisciplina) {
		try {
			return manager
					.createQuery("FROM GradeDisciplina WHERE id_disciplina = :idDisciplina and id_grade = :idGrade",
							GradeDisciplina.class)
					.setParameter("idGrade", idGrade).setParameter("idDisciplina", idDisciplina).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public GradeDisciplina buscarPorDisciplinaGradeIra(Long idGrade, Long idDisciplina) {
		try {
			return manager
					.createQuery(
							"FROM GradeDisciplina WHERE id_disciplina = :idDisciplina and id_grade = :idGrade and excluir_ira = true",
							GradeDisciplina.class)
					.setParameter("idGrade", idGrade).setParameter("idDisciplina", idDisciplina).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<GradeDisciplina> buscarPorTipoGrade(Long idGrade, String tipo) {
		return manager.createQuery("FROM GradeDisciplina WHERE id_grade = :idGrade and tipo_disciplina = :tipo")
				.setParameter("idGrade", idGrade).setParameter("tipo", tipo).getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<GradeDisciplina> buscarPorIra(Long idGrade, boolean ira) {
		EntityTransaction transaction = null;
		List<GradeDisciplina> gDisciplina = null;
		try {
			transaction = manager.getTransaction();
			if (!transaction.isActive())
				transaction.begin();
			gDisciplina = manager.createQuery("FROM GradeDisciplina WHERE id_grade = :idGrade and excluir_ira = :ira")
				.setParameter("idGrade", idGrade).setParameter("ira", ira).getResultList();
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}
		
		return gDisciplina;
	}
	
	@SuppressWarnings("unchecked")
	public List<GradeDisciplina> buscarPorIraSemCommit(Long idGrade, boolean ira) {
		EntityTransaction transaction = null;
		List<GradeDisciplina> gDisciplina = null;
		try {
			transaction = manager.getTransaction();
			if (!transaction.isActive())
				transaction.begin();
			gDisciplina = manager.createQuery("FROM GradeDisciplina WHERE id_grade = :idGrade and excluir_ira = :ira")
				.setParameter("idGrade", idGrade).setParameter("ira", ira).getResultList();
			//transaction.commit();
		} catch (Exception e) {
			//transaction.rollback();
			throw e;
		}
		
		return gDisciplina;
	}
}