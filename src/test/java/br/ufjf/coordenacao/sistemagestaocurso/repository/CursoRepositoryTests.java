package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.utils.RepositoryTestBase;
import org.mockito.InjectMocks;

class CursoRepositoryTests extends RepositoryTestBase {

	@InjectMocks
	CursoRepository cursoRepository;

	@Override
	public void specificSetup() {

	}

	@Override
	@SuppressWarnings("unchecked")
	protected <R extends BaseRepository> R GetRepository() {
		return (R) cursoRepository;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <E> E GetEntity() {
		return (E) mocks.getCurso();
	}
}
