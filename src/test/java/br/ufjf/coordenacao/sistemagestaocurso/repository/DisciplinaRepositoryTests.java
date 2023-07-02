package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.utils.RepositoryTestBase;
import org.mockito.InjectMocks;

public class DisciplinaRepositoryTests extends RepositoryTestBase {
	@InjectMocks
	DisciplinaRepository disciplinaRepositoryMock;

	@Override
	@SuppressWarnings("unchecked")
	protected <R extends BaseRepository> R GetRepository() {
		return (R) disciplinaRepositoryMock;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <E> E GetEntity() {
		return (E) mocks.getDisciplinas().get(0);
	}

	@Override
	public void specificSetup() {

	}
}
