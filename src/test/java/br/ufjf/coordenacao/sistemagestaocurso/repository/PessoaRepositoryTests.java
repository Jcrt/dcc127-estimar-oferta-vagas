package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.utils.RepositoryTestBase;
import org.mockito.InjectMocks;

public class PessoaRepositoryTests extends RepositoryTestBase {
	@InjectMocks
	PessoaRepository pessoaRepositoryMock;

	@Override
	@SuppressWarnings("unchecked")
	protected <R extends BaseRepository> R GetRepository() {
		return (R) pessoaRepositoryMock;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <E> E GetEntity() {
		return (E) mocks.getPessoas().get(0);
	}

	@Override
	public void specificSetup() {

	}
}
