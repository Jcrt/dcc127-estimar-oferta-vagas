package br.ufjf.coordenacao.sistemagestaocurso.utils;

import br.ufjf.coordenacao.sistemagestaocurso.repository.BaseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public abstract class RepositoryTestBase extends TestBaseClass {

	@Mock
	protected EntityManager entityManagerMock;

	@Mock
	protected EntityTransaction transactionMock;

	protected abstract <R extends BaseRepository> R GetRepository();

	protected abstract <E> E GetEntity();

	@Test
	@DisplayName("GIVEN BaseRepository WHEN persistir is called with closed transaction THEN all methods are called once except rollback")
	<T> void baseRepositoryTest1() {
		when(entityManagerMock.getTransaction()).thenReturn(transactionMock);

		BaseRepository repository = GetRepository();
		T entity = GetEntity();
		T expectedEntity = repository.persistir(GetEntity());

		assertNotEquals(expectedEntity, entity);
		verify(transactionMock, times(1)).isActive();
		verify(transactionMock, times(1)).begin();
		verify(transactionMock, times(1)).commit();
		verify(transactionMock, times(0)).rollback();
	}

	@Test
	@DisplayName("GIVEN BaseRepository WHEN persistir is called with closed transaction and an exception occurs THEN all methods are called once")
	<T> void baseRepositoryTest2() {
		when(entityManagerMock.getTransaction()).thenReturn(transactionMock);
		doThrow(new RuntimeException()).when(transactionMock).commit();

		T entity = GetEntity();
		BaseRepository repository = GetRepository();

		assertThrows(RuntimeException.class, () -> repository.persistir(entity));
		verify(transactionMock, times(1)).isActive();
		verify(transactionMock, times(1)).begin();
		verify(transactionMock, times(1)).commit();
		verify(transactionMock, times(1)).rollback();
	}

	@Test
	@DisplayName("GIVEN BaseRepository WHEN persistir is called but transaction is null THEN do not call rollback")
	<T> void baseRepositoryTest3() {
		String exceptionMessage = "FirstMessage";
		NullPointerException nullPointer = new NullPointerException(exceptionMessage);
		doThrow(nullPointer).when(entityManagerMock).getTransaction();

		T entity = GetEntity();
		BaseRepository repository = GetRepository();

		assertThrows(NullPointerException.class, () -> repository.persistir(entity), exceptionMessage);
		verify(transactionMock, times(0)).rollback();
	}
}
