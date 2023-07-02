package br.ufjf.coordenacao.sistemagestaocurso.model;

import br.ufjf.coordenacao.sistemagestaocurso.providers.EstruturaArvoreProvider;
import br.ufjf.coordenacao.sistemagestaocurso.providers.FacesContextProvider;
import br.ufjf.coordenacao.sistemagestaocurso.repository.DisciplinaRepository;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.EstruturaArvore;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.ImportarArvore;
import br.ufjf.coordenacao.sistemagestaocurso.utils.TestBaseClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.faces.context.FacesContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class AlunoTests extends TestBaseClass {
	@Mock
	FacesContextProvider facesContextProviderMock;

	@Mock
	EstruturaArvoreProvider estruturaArvoreProviderMock;

	@Mock
	DisciplinaRepository disciplinaRepositoryMock;

	@InjectMocks
	Aluno aluno = mocks.getAlunos().get(0);

	@Override
	public void specificSetup() {
		when(facesContextProviderMock.provide()).thenReturn(FacesContext.getCurrentInstance());

		EstruturaArvore estruturaArvore = EstruturaArvore.getInstance();
		ImportarArvore importarArvore = estruturaArvore.recuperarArvore(mocks.getGrade(), true);
		importarArvore.importarHistorico(mocks.getHistoricos());
		when(estruturaArvoreProviderMock.provide()).thenReturn(estruturaArvore);
	}

	@Test
	@DisplayName("GIVEN aluno WHEN all coursed subjects is are in grade THEN sum all hours correctly")
	void AlunoTests1() {
		when(disciplinaRepositoryMock.listarTodos()).thenReturn(mocks.getDisciplinas());
		aluno.setDisciplinaRepository(disciplinaRepositoryMock);
		aluno.calculaHorasCompletadas();

		assertEquals(7, aluno.getHorasObrigatoriasCompletadas());
		assertEquals(21, aluno.getSobraHorasEletivas());
		assertEquals(75, aluno.getSobraHorasOpcionais());
	}

	@Test
	@DisplayName("GIVEN aluno WHEN a subject isnt in grade THEN not throw exception and calculate hours correctly")
	void AlunoTests2() {
		List<Disciplina> disciplinaList = mocks.getDisciplinas()
				.stream()
				.filter(disciplina -> disciplina.getCodigo().equals("DIS08"))
				.collect(Collectors.toList());

		when(disciplinaRepositoryMock.listarTodos()).thenReturn(disciplinaList);
		aluno.setDisciplinaRepository(disciplinaRepositoryMock);

		assertEquals(7, aluno.getHorasObrigatoriasCompletadas());
		assertEquals(21, aluno.getSobraHorasEletivas());
		assertEquals(75, aluno.getSobraHorasOpcionais());
	}
}
