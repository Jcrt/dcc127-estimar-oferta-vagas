package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.model.Equivalencia;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.List;

public class EquivalenciaRepository extends BaseRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	public Equivalencia porid(long id) {
		return manager.find(Equivalencia.class, id);
	}

	public void remover(Equivalencia equivalencia) {
		EntityTransaction transaction = null;
		
		try {
			transaction = manager.getTransaction();
			transaction.begin();
			manager.remove(manager.contains(equivalencia) ? equivalencia : manager.merge(equivalencia));
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}
	}

	public List<Equivalencia> listarTodos() {
		return manager.createQuery("FROM Equivalencia", Equivalencia.class).getResultList();
	}

	public List<Equivalencia> buscarTodasEquivalenciasPorGrade(long codigo) {
		return manager.createQuery("FROM Equivalencia WHERE id_grade = :codigo", Equivalencia.class)
				.setParameter("codigo", codigo).getResultList();
	}

	public Equivalencia buscarPorEquivalencia(long idDisciplinaEsquerda, long idDisciplinaDireita, long idGrade) {
		try {
			return manager
					.createQuery(
							"FROM Equivalencia WHERE id_disciplina = :iddisciplinaesquerda and id_disciplina_grade = :iddisciplinadireita and id_grade = :idgrade",
							Equivalencia.class)
					.setParameter("iddisciplinaesquerda", idDisciplinaEsquerda)
					.setParameter("iddisciplinadireita", idDisciplinaDireita).setParameter("idgrade", idGrade)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public boolean existeDisciplinaEquivalente(long idDisciplina, long idGrade) {
		return manager
				.createQuery(
						"FROM Equivalencia WHERE id_disciplina = :iddisciplina and id_grade = :idgrade",
						Equivalencia.class)
				.setParameter("iddisciplina", idDisciplina)
				.setParameter("idgrade", idGrade)
				.getResultList().size() > 0;
	}
}