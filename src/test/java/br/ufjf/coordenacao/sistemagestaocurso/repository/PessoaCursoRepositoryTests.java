package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.model.PessoaCurso;
import br.ufjf.coordenacao.sistemagestaocurso.utils.RepositoryTestBase;
import org.mockito.InjectMocks;

public class PessoaCursoRepositoryTests extends RepositoryTestBase {
	@InjectMocks
	PessoaCursoRepository pessoaCursoRepositoryMock;

	@Override
	@SuppressWarnings("unchecked")
	protected <R extends BaseRepository> R GetRepository() {
		return (R) pessoaCursoRepositoryMock;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <E> E GetEntity() {
		return (E) new PessoaCurso();
	}

	@Override
	public void specificSetup() {

	}
}
