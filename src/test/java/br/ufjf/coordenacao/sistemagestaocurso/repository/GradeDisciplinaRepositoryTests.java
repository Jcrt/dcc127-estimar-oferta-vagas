package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.utils.RepositoryTestBase;
import org.mockito.InjectMocks;

public class GradeDisciplinaRepositoryTests extends RepositoryTestBase {
	@InjectMocks
	GradeDisciplinaRepository gradeDisciplinaRepositoryMock;

	@Override
	@SuppressWarnings("unchecked")
	protected <R extends BaseRepository> R GetRepository() {
		return (R) gradeDisciplinaRepositoryMock;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <E> E GetEntity() {
		return (E) mocks.getGradeDisciplinas().get(0);
	}

	@Override
	public void specificSetup() {

	}
}
