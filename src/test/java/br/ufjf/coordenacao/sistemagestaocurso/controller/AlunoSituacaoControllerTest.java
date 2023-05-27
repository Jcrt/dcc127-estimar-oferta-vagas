package br.ufjf.coordenacao.sistemagestaocurso.controller;

import br.ufjf.coordenacao.OfertaVagas.model.Class;
import br.ufjf.coordenacao.OfertaVagas.model.Curriculum;
import br.ufjf.coordenacao.OfertaVagas.model.Student;
import br.ufjf.coordenacao.OfertaVagas.model.StudentsHistory;
import br.ufjf.coordenacao.sistemagestaocurso.controller.util.UsuarioController;
import br.ufjf.coordenacao.sistemagestaocurso.model.Aluno;
import br.ufjf.coordenacao.sistemagestaocurso.model.Curso;
import br.ufjf.coordenacao.sistemagestaocurso.model.Disciplina;
import br.ufjf.coordenacao.sistemagestaocurso.model.Grade;
import br.ufjf.coordenacao.sistemagestaocurso.model.estrutura.Autenticacao;
import br.ufjf.coordenacao.sistemagestaocurso.repository.DisciplinaRepository;
import br.ufjf.coordenacao.sistemagestaocurso.repository.EventoAceRepository;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.EstruturaArvore;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.ImportarArvore;
import br.ufjf.coordenacao.sistemagestaocurso.utils.DataTableHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.primefaces.component.datatable.DataTable;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import static org.mockito.ArgumentMatchers.*;

@DisplayName("AlunoSituacaoController tests")
class AlunoSituacaoControllerTest {
	private static final String gridObrigatoriasExpression = "form:gridObrigatorias";
	private static final String gridEletivasExpression = "form:gridEletivas";
	private static final String gridOpcionaisExpression = "form:gridOpcionais";
	private static final String gridAceExpression = "form:gridAce";
	private static final String codigoDisciplinaCalculo = "MAT155🆘";
	private static final String semestreSelecionado = "2023/1";
	private static final String codigoEstudante = "201376082";

	@Mock
	private FacesContext facesContextMock;

	@Mock
	private EstruturaArvore estruturaArvoreMock;

	@Mock
	private Aluno alunoMock;

	@Mock
	private Grade gradeMock;

	@Mock
	private Curso cursoMock;

	@Mock
	private EventoAceRepository eventoAceRepositoryMock;

	@Mock
	private UsuarioController usuarioControllerMock;

	@Mock
	private DisciplinaRepository disciplinaRepositoryMock;

	@InjectMocks
	private AlunoSituacaoController alunoSituacaoController;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		alunoSituacaoController.init();
	}

	/**
	 * Constrói um {@link ArrayList<DataTable>} contendo as {@link DataTable} necessárias para teste
	 *
	 * @param shouldMarkAsInitialState True se for necessário marcar a {@link DataTable} como estado inicial
	 * @return Um {@link ArrayList<DataTable>} contendo as {@link DataTable} necessárias para teste
	 */
	ArrayList<DataTable> getDataTablesForTest(boolean shouldMarkAsInitialState) {
		ArrayList<DataTable> dataTables = new ArrayList<>();

		dataTables.add(DataTableHelper.createDataTableForTest(shouldMarkAsInitialState));
		dataTables.add(DataTableHelper.createDataTableForTest(shouldMarkAsInitialState));
		dataTables.add(DataTableHelper.createDataTableForTest(shouldMarkAsInitialState));
		dataTables.add(DataTableHelper.createDataTableForTest(shouldMarkAsInitialState));

		return dataTables;
	}

	/**
	 * Constrói os mocks para testes que usam as {@link DataTable} do PrimeFace
	 *
	 * @param dataTables As {@link DataTable} passadas para gerar os mocks
	 */
	void initDataTableFacesMock(ArrayList<DataTable> dataTables) {
		UIViewRoot viewRoot = Mockito.mock(UIViewRoot.class);

		Mockito.when(facesContextMock.getViewRoot()).thenReturn(viewRoot);
		Mockito.when(viewRoot.findComponent(gridObrigatoriasExpression)).thenReturn(dataTables.get(0));
		Mockito.when(viewRoot.findComponent(gridEletivasExpression)).thenReturn(dataTables.get(1));
		Mockito.when(viewRoot.findComponent(gridOpcionaisExpression)).thenReturn(dataTables.get(2));
		Mockito.when(viewRoot.findComponent(gridAceExpression)).thenReturn(dataTables.get(3));
	}

	/*****
	 * resetaDataTable tests
	 */

	@Test
	@DisplayName("GIVEN AlunoSituacaoController WHEN resetDataTable is called THEN resets all data tables")
	void resetaDataTablesTest1() {
		ArrayList<DataTable> dataTables = getDataTablesForTest(true);
		initDataTableFacesMock(dataTables);

		alunoSituacaoController.resetaDataTables();

		dataTables.forEach(dataTable -> {
			Assertions.assertTrue(dataTable.isReset());
			Assertions.assertFalse(dataTable.initialStateMarked());
		});
	}

	@ParameterizedTest
	@CsvSource({
			"0, 0, 0, 0, 0, 0, 0, 0",
			"2, 2, 2, 2, 20, 100, 66, 20"
	})
	@DisplayName("GIVEN AlunoSituacaoController WHEN onItemSelectMatriculaAluno is called with no concluded hours by aluno THEN should return empty conclusion percents")
	void onItemSelectMatriculaAlunoTest3(
			int horasEletivasCompletadas, int horasOpcionaisCompletadas,
			int horasAceCompletadas, int horasObrigatoriasCompletadas,
			double percentualHorasEletivasCompletadas, double percentualHorasOpcionaisCompletas,
			double percentualHorasACECompletadas, double percentualHorasObrigatoriasCompletadas
	) {
		int horasEletivas = 10;
		int horasOpcionais = 2;
		int horasACE = 3;
		int horasObrigatorias = 10;

		initBaseMocks();
		initGradeTimesMock(horasEletivas, horasOpcionais, horasACE);
		initDataTableFacesMock(getDataTablesForTest(false));

		Class classeSI = new Class(codigoDisciplinaCalculo);
		classeSI.setWorkload(horasObrigatorias);

		TreeSet<Class> classTree = new TreeSet<>();
		classTree.add(classeSI);

		HashMap studentList = Mockito.mock(HashMap.class);
		Mockito.when(studentList.get(anyString())).thenReturn(new Student(codigoEstudante));

		StudentsHistory sh = Mockito.mock(StudentsHistory.class);
		Mockito.when(sh.getStudents()).thenReturn(studentList);

		HashMap<Integer, TreeSet<Class>> mandatories = new HashMap<>();
		mandatories.put(1, classTree);
		Curriculum curriculum = Mockito.mock(Curriculum.class);
		Mockito.when(curriculum.getMandatories()).thenReturn(mandatories);

		ImportarArvore importaArvore = Mockito.mock(ImportarArvore.class);
		Mockito.when(importaArvore.getSh()).thenReturn(sh);
		Mockito.when(importaArvore.get_cur()).thenReturn(curriculum);

		Autenticacao autenticacao = Mockito.mock(Autenticacao.class);
		Mockito.when(autenticacao.getSemestreSelecionado()).thenReturn(semestreSelecionado);

		Mockito.when(estruturaArvoreMock.recuperarArvore(any(Grade.class), anyBoolean())).thenReturn(importaArvore);
		Mockito.when(eventoAceRepositoryMock.buscarPorMatricula(anyString())).thenReturn(new ArrayList<>());
		Mockito.when(eventoAceRepositoryMock.recuperarHorasConcluidasPorMatricula(alunoMock.getMatricula())).thenReturn(horasAceCompletadas);
		Mockito.when(usuarioControllerMock.getAutenticacao()).thenReturn(autenticacao);
		Mockito.when(alunoMock.getHorasAceConcluidas()).thenReturn(horasAceCompletadas);
		Mockito.when(alunoMock.getHorasEletivasCompletadas()).thenReturn(horasEletivasCompletadas);
		Mockito.when(alunoMock.getHorasOpcionaisCompletadas()).thenReturn(horasOpcionaisCompletadas);
		Mockito.when(alunoMock.getHorasObrigatoriasCompletadas()).thenReturn(horasObrigatoriasCompletadas);

		alunoSituacaoController.setEstruturaArvore(estruturaArvoreMock);
		alunoSituacaoController.onItemSelectMatriculaAluno();

		Assertions.assertEquals(percentualHorasOpcionaisCompletas, alunoSituacaoController.getPercentualOpcionais());
		Assertions.assertEquals(percentualHorasACECompletadas, alunoSituacaoController.getPercentualAce());
		Assertions.assertEquals(percentualHorasEletivasCompletadas, alunoSituacaoController.getPercentualEletivas());
		Assertions.assertEquals(percentualHorasObrigatoriasCompletadas, alunoSituacaoController.getPercentualObrigatorias());
	}

	/*****
	 * onItemSelectMatriculaAluno tests
	 */
	@Test
	@DisplayName("GIVEN AlunoSituacaoController WHEN onItemSelectMatriculaAluno is called with no grade information THEN all hours should be 0")
	void onItemSelectMatriculaAlunoTest1() {
		initBaseMocks();

		alunoSituacaoController.onItemSelectMatriculaAluno();

        Assertions.assertEquals(0, alunoSituacaoController.getHorasEletivas());
        Assertions.assertEquals(0, alunoSituacaoController.getHorasEletivasConcluidas());
        Assertions.assertEquals(0, alunoSituacaoController.getHorasOpcionais());
        Assertions.assertEquals(0, alunoSituacaoController.getHorasOpcionaisConcluidas());
        Assertions.assertEquals(0, alunoSituacaoController.getHorasACE());
        Assertions.assertEquals(0, alunoSituacaoController.getHorasObrigatoriasConcluidas());
    }

    @Test
    @DisplayName("GIVEN AlunoSituacaoController WHEN onItemSelectMatriculaAluno is called with curricular hours defined THEN all hours should correspond definitions")
    void onItemSelectMatriculaAlunoTest2() {
	    initBaseMocks();

	    int horasEletivas = 10;
	    int horasOpcionais = 2;
	    int horasACE = 3;
	    int horasObrigatoriasConcluidas = 2;
	    int horasEletivasConcluidas = 9;
	    int horasOpcionaisConcluidas = 1;

	    initGradeTimesMock(horasEletivas, horasOpcionais, horasACE);

	    Mockito.when(alunoMock.getHorasObrigatoriasCompletadas()).thenReturn(horasObrigatoriasConcluidas);
	    Mockito.when(alunoMock.getHorasEletivasCompletadas()).thenReturn(horasEletivasConcluidas);
	    Mockito.when(alunoMock.getHorasOpcionaisCompletadas()).thenReturn(horasOpcionaisConcluidas);

	    alunoSituacaoController.onItemSelectMatriculaAluno();

	    Assertions.assertEquals(horasEletivas, alunoSituacaoController.getHorasEletivas());
	    Assertions.assertEquals(horasEletivasConcluidas, alunoSituacaoController.getHorasEletivasConcluidas());
	    Assertions.assertEquals(horasOpcionais, alunoSituacaoController.getHorasOpcionais());
	    Assertions.assertEquals(horasOpcionaisConcluidas, alunoSituacaoController.getHorasOpcionaisConcluidas());
	    Assertions.assertEquals(horasACE, alunoSituacaoController.getHorasACE());
	    Assertions.assertEquals(horasObrigatoriasConcluidas, alunoSituacaoController.getHorasObrigatoriasConcluidas());
    }

	private void initGradeTimesMock(int horasEletivas, int horasOpcionais, int horasACE) {
		Mockito.when(gradeMock.getHorasEletivas()).thenReturn(horasEletivas);
		Mockito.when(gradeMock.getHorasOpcionais()).thenReturn(horasOpcionais);
		Mockito.when(gradeMock.getHorasAce()).thenReturn(horasACE);
	}

	/**
	 * Method that initializes the main mocks
	 */
	private void initBaseMocks() {
		ArrayList<Aluno> alunoListStub = new ArrayList<>();
		alunoListStub.add(alunoMock);

		Disciplina disciplinaStub = new Disciplina();
		disciplinaStub.setCodigo(codigoDisciplinaCalculo);
		disciplinaStub.setNome("Calculo I 👿");

		Mockito.when(cursoMock.getGrupoAlunos()).thenReturn(alunoListStub);
		Mockito.when(gradeMock.getCurso()).thenReturn(cursoMock);
		Mockito.when(gradeMock.getId()).thenReturn(1L);
		Mockito.when(gradeMock.getCodigo()).thenReturn("Codigo");
		Mockito.when(alunoMock.getGrade()).thenReturn(gradeMock);
		Mockito.when(alunoMock.getMatricula()).thenReturn(semestreSelecionado);
		Mockito.when(disciplinaRepositoryMock.buscarPorCodigoDisciplina(codigoDisciplinaCalculo)).thenReturn(disciplinaStub);

		alunoSituacaoController.setCurso(cursoMock);
		alunoSituacaoController.setAluno(alunoMock);
    }
}
