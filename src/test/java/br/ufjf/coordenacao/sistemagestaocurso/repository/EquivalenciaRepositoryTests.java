package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.model.Equivalencia;
import br.ufjf.coordenacao.sistemagestaocurso.utils.RepositoryTestBase;
import org.mockito.InjectMocks;

public class EquivalenciaRepositoryTests extends RepositoryTestBase {
	@InjectMocks
	EquivalenciaRepository equivalenciaRepositoryMock;

	@Override
	@SuppressWarnings("unchecked")
	protected <R extends BaseRepository> R GetRepository() {
		return (R) equivalenciaRepositoryMock;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <E> E GetEntity() {
		return (E) new Equivalencia();
	}

	@Override
	public void specificSetup() {

	}
}
