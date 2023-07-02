package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.model.PreRequisito;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.List;

public class PreRequisitoRepository extends BaseRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	public PreRequisito porid(long id) {
		return manager.find(PreRequisito.class, id);
	}

	public void remover(PreRequisito objeto) {
		EntityTransaction transaction = null;
		
		int deletar;
		
		try {
			transaction = manager.getTransaction();
			transaction.begin();
			//manager.remove(manager.contains(objeto) ? objeto : manager.merge(objeto));
			
			deletar = manager.createQuery("DELETE FROM PreRequisito WHERE ID = :id")
					.setParameter("id", objeto.getId())
					.executeUpdate();
			
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}

	}

	public List<PreRequisito> listarTodos() {
		return manager.createQuery("FROM PreRequisito", PreRequisito.class).getResultList();
	}

	public List<PreRequisito> buscarPorTodosCodigoGradeDisc(Long codigo) {
		return manager.createQuery("FROM PreRequisito WHERE id_grade_disciplina = :codigo", PreRequisito.class)
				.setParameter("codigo", codigo).getResultList();
	}

	public List<PreRequisito> buscarPorTodosCodigoDisciplina(Long codigo) {
		return manager.createQuery("FROM PreRequisito WHERE id_disciplina = :codigo", PreRequisito.class)
				.setParameter("codigo", codigo).getResultList();
	}

	public Integer buscarPorDisciplanaGradeId(Long idGrade, Long idDisciplina) {
		try {
		return ((Long) manager .createQuery("Select id FROM PreRequisito WHERE id_grade_disciplina = :idGrade and id_disciplina = :idDisciplina")
				.setParameter("idGrade", idGrade)
				.setParameter("idDisciplina", idDisciplina)
				.getSingleResult()).intValue();
		}
		catch (NoResultException e)
		{
			return 0;
		}

	}

}