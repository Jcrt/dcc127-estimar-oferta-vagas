package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.utils.RepositoryTestBase;
import org.mockito.InjectMocks;

class AlunoRepositoryTests extends RepositoryTestBase {

	@InjectMocks
	AlunoRepository alunoRepository;

	@Override
	public void specificSetup() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R extends BaseRepository> R GetRepository() {
		return (R) alunoRepository;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E> E GetEntity() {
		return (E) mocks.getAlunos().get(0);
	}
}
