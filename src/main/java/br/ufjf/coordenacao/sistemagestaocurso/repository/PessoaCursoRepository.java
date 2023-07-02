package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.model.PessoaCurso;

import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.List;

public class PessoaCursoRepository extends BaseRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	public PessoaCurso porid(long id) {
		return manager.find(PessoaCurso.class, id);
	}

	public void remover(PessoaCurso objeto) {
		EntityTransaction transaction = null;
		
		try {
			transaction = manager.getTransaction();
			transaction.begin();
			manager.remove(manager.contains(objeto) ? objeto : manager.merge(objeto));
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}
	}

	public List<PessoaCurso> listarTodos() {
		return manager.createQuery("FROM PessoaCurso", PessoaCurso.class).getResultList();
	}

	public List<PessoaCurso> buscarTodasPessoaCursoPorPessoa(long codigo){		
		return manager.createQuery("FROM PessoaCurso WHERE id_pessoa = :codigo", PessoaCurso.class)
				.setParameter("codigo",  codigo )
				.getResultList();
			
	}
}