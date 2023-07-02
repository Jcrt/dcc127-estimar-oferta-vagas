package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.utils.RepositoryTestBase;
import org.mockito.InjectMocks;

public class HistoricoRepositoryTests extends RepositoryTestBase {
	@InjectMocks
	HistoricoRepository historicoRepositoryMock;

	@Override
	@SuppressWarnings("unchecked")
	protected <R extends BaseRepository> R GetRepository() {
		return (R) historicoRepositoryMock;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <E> E GetEntity() {
		return (E) mocks.getHistoricos().get(0);
	}

	@Override
	public void specificSetup() {

	}
}
