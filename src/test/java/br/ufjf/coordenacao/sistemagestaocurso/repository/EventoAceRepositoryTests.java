package br.ufjf.coordenacao.sistemagestaocurso.repository;

import br.ufjf.coordenacao.sistemagestaocurso.model.EventoAce;
import br.ufjf.coordenacao.sistemagestaocurso.utils.RepositoryTestBase;
import org.mockito.InjectMocks;

public class EventoAceRepositoryTests extends RepositoryTestBase {
	@InjectMocks
	EventoAceRepository eventoAceRepositoryMock;

	@Override
	@SuppressWarnings("unchecked")
	protected <R extends BaseRepository> R GetRepository() {
		return (R) eventoAceRepositoryMock;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <E> E GetEntity() {
		return (E) new EventoAce();
	}

	@Override
	public void specificSetup() {

	}
}
