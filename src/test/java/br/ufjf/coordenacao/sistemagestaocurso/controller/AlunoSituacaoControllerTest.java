package br.ufjf.coordenacao.sistemagestaocurso.controller;

import br.ufjf.coordenacao.OfertaVagas.model.Class;
import br.ufjf.coordenacao.OfertaVagas.model.*;
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
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.*;

@DisplayName("AlunoSituacaoController tests")
class AlunoSituacaoControllerTest {
	private static final String gridObrigatoriasExpression = "form:gridObrigatorias";
	private static final String gridEletivasExpression = "form:gridEletivas";
	private static final String gridOpcionaisExpression = "form:gridOpcionais";
	private static final String gridAceExpression = "form:gridAce";
	private static final String codigoDisciplinaCalculoI = "MAT154🆘";
	private static final String codigoDisciplinaCalculoII = "MAT156🆘";
	private static final String codigoDisciplinaGA = "MAT155🆘";
	private static final String codigoDisciplinaEletiva1 = "ELE001";
	private static final String codigoDisciplinaEletiva2 = "ELE002";
	private static final String codigoDisciplinaOpcional1 = "OPT001";
	private static final String codigoDisciplinaOpcional2 = "OPT002";
	private static final String selectedSemester = "2023/1";
	private static final String codigoEstudante = "201376082";
	private static final String codigoCurso = "76";
	private static final String codigoCurriculo = "2013-1";
	private static final String descricaoDisciplinaOpcional1 = "Disciplina Opcional 1 👿";

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

	@Mock
	private Student studentMock;

	@Mock
	private Curriculum curriculumMock;

	@Mock
	private Class mandatoryClassMock1;
	@Mock
	private Class mandatoryClassMock2;
	@Mock
	private Class mandatoryClassMock3;
	@Mock
	private Class electiveClassMock1;
	@Mock
	private Class electiveClassMock2;
	@Mock
	private Class optionalClassMock1;
	@Mock
	private Class optionalClassMock2;

	@InjectMocks
	private AlunoSituacaoController alunoSituacaoController;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		alunoSituacaoController.init();
	}

	@Test
	@DisplayName("GIVEN AlunoSituacaoController WHEN resetDataTable is called THEN resets all data tables")
	void resetaDataTablesTest1() {
		ArrayList<DataTable> dataTables = initDataTableMocks(true);
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
		initDataTableFacesMock(initDataTableMocks(false));

		HashMap studentList = Mockito.mock(HashMap.class);

		Mockito.when(studentList.get(anyString())).thenReturn(studentMock);

		StudentsHistory sh = Mockito.mock(StudentsHistory.class);
		Mockito.when(sh.getStudents()).thenReturn(studentList);

		initCurriculumMock(horasObrigatorias, 0);
		initOptionalClassMock(10);

		ImportarArvore importaArvore = Mockito.mock(ImportarArvore.class);
		Mockito.when(importaArvore.getSh()).thenReturn(sh);
		Mockito.when(importaArvore.get_cur()).thenReturn(curriculumMock);

		Autenticacao autenticacao = Mockito.mock(Autenticacao.class);
		Mockito.when(autenticacao.getSemestreSelecionado()).thenReturn(selectedSemester);

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

	@Test
	@DisplayName("GIVEN AlunoSituacaoController WHEN gerarDadosAluno is called THEN all disciplines should be in the correct list")
	void gerarDadosAlunoTest1() {
		initBaseMocks();
		initCurriculumMock(10, 5);
		initOptionalClassMock(20);

		alunoSituacaoController.gerarDadosAluno(studentMock, curriculumMock);

		Assertions.assertTrue(checkIfPredicateIsTrue(
				alunoSituacaoController.getListaDisciplinaObrigatorias(),
				x -> x.getSituacao().equals(AlunoSituacaoController.APROVADO)
						&& x.getCodigo().equals(codigoDisciplinaCalculoI)
		));
		Assertions.assertTrue(checkIfPredicateIsTrue(
				alunoSituacaoController.getListaDisciplinaObrigatorias(),
				x -> x.getSituacao().equals(AlunoSituacaoController.NAO_APROVADO)
						&& x.getCodigo().equals(codigoDisciplinaCalculoII)
						&& x.getListaPreRequisitos().equals(codigoDisciplinaGA + " : " + codigoDisciplinaCalculoI + " : ")
		));
		Assertions.assertTrue(checkIfPredicateIsTrue(
				alunoSituacaoController.getListaDisciplinaObrigatorias(),
				x -> x.getSituacao().equals(AlunoSituacaoController.APROVADO)
						&& x.getCodigo().equals(codigoDisciplinaGA)
		));
		Assertions.assertTrue(checkIfPredicateIsTrue(
				alunoSituacaoController.getListaDisciplinaOpcionais(),
				x -> x.getSituacao().equals(AlunoSituacaoController.APROVADO)
						&& x.getCodigo().equals(codigoDisciplinaOpcional2)
		));
		Assertions.assertTrue(checkIfPredicateIsTrue(
				alunoSituacaoController.getListaDisciplinaEletivas(),
				x -> x.getSituacao().equals(AlunoSituacaoController.APROVADO)
						&& x.getCodigo().equals(codigoDisciplinaEletiva1)
		));
		Assertions.assertTrue(checkIfPredicateIsTrue(
				alunoSituacaoController.getListaEventosAce(),
				x -> x.getDescricao().equals(descricaoDisciplinaOpcional1)
		));
		Assertions.assertEquals(30, alunoSituacaoController.getHorasObrigatorias());
		Assertions.assertEquals(20, alunoSituacaoController.getHorasAceConcluidas());
	}

	/**
	 * Dada uma lista de objetos, verifica se o predicado passado retorna verdadeiro
	 *
	 * @param list        A lista data
	 * @param predicate   O predicado para ser testado
	 * @param <TListType> O tipo de lista que será manipulada
	 * @return Verdadeiro se existe algum ítem dentro da lista com o predicado dado, senão falso
	 */
	<TListType> boolean checkIfPredicateIsTrue(List<TListType> list, Predicate<TListType> predicate) {
		return list.stream().anyMatch(predicate);
	}

	/**
	 * Constrói um {@link ArrayList<DataTable>} contendo as {@link DataTable} necessárias para teste
	 *
	 * @param shouldMarkAsInitialState True se for necessário marcar a {@link DataTable} como estado inicial
	 * @return Um {@link ArrayList<DataTable>} contendo as {@link DataTable} necessárias para teste
	 */
	ArrayList<DataTable> initDataTableMocks(boolean shouldMarkAsInitialState) {
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

	/**
	 * Constrói os mocks para as {@link Class} obrigatórias
	 *
	 * @param mandatoryHours Numero de horas obrigatórias que serão atribuidas para cada uma das classes
	 */
	void initMandatoryClassesMock(int mandatoryHours) {
		Mockito.when(mandatoryClassMock1.getId()).thenReturn(codigoDisciplinaCalculoI);
		Mockito.when(mandatoryClassMock1.getWorkload()).thenReturn(mandatoryHours);

		Mockito.when(mandatoryClassMock2.getId()).thenReturn(codigoDisciplinaCalculoII);
		Mockito.when(mandatoryClassMock2.getWorkload()).thenReturn(mandatoryHours);

		Mockito.when(mandatoryClassMock3.getId()).thenReturn(codigoDisciplinaGA);
		Mockito.when(mandatoryClassMock3.getWorkload()).thenReturn(mandatoryHours);

		ArrayList<Class> prerequisites = new ArrayList<>();
		prerequisites.add(mandatoryClassMock1);
		Mockito.when(mandatoryClassMock2.getPrerequisite()).thenReturn(new ArrayList<>(prerequisites));

		ArrayList<Class> corequisite = new ArrayList<>();
		corequisite.add(mandatoryClassMock3);
		Mockito.when(mandatoryClassMock2.getCorequisite()).thenReturn(new ArrayList<>(corequisite));
	}

	/**
	 * Constrói os mocks para as {@link Class} eletivas
	 *
	 * @param electiveHours Numero de horas eletivas que serão atribuidas para cada uma das classes
	 */
	void initElectiveClassMock(int electiveHours) {
		Mockito.when(electiveClassMock1.getId()).thenReturn(codigoDisciplinaEletiva1);
		Mockito.when(electiveClassMock1.getWorkload()).thenReturn(electiveHours);

		Mockito.when(electiveClassMock2.getId()).thenReturn(codigoDisciplinaEletiva2);
		Mockito.when(electiveClassMock2.getWorkload()).thenReturn(electiveHours);
	}

	/**
	 * Constrói os mocks para as {@link Class} opcionais
	 *
	 * @param optionalHours Numero de horas opcionais que serão atribuidas para cada uma das classes
	 */
	void initOptionalClassMock(int optionalHours) {
		Mockito.when(optionalClassMock1.getId()).thenReturn(codigoDisciplinaOpcional1);
		Mockito.when(optionalClassMock1.getWorkload()).thenReturn(optionalHours);

		Mockito.when(optionalClassMock2.getId()).thenReturn(codigoDisciplinaOpcional2);
		Mockito.when(optionalClassMock2.getWorkload()).thenReturn(optionalHours);
	}

	/**
	 * Define quantidade de horas para cada uma das modalidades de ensino para a classe {@link Grade}
	 *
	 * @param horasEletivas  Numero de horas eletivas que serão atribuidas para cada uma das classes
	 * @param horasOpcionais Numero de horas opcionais que serão atribuidas para cada uma das classes
	 * @param horasACE       Numero de horas ACE que serão atribuidas para cada uma das classes
	 */
	private void initGradeTimesMock(int horasEletivas, int horasOpcionais, int horasACE) {
		Mockito.when(gradeMock.getHorasEletivas()).thenReturn(horasEletivas);
		Mockito.when(gradeMock.getHorasOpcionais()).thenReturn(horasOpcionais);
		Mockito.when(gradeMock.getHorasAce()).thenReturn(horasACE);
	}

	/**
	 * Method that initializes the main mocks
	 */
	private void initBaseMocks() {

		InitDisciplinaStubAndMock(codigoDisciplinaCalculoI, "Calculo I 👿", 40);
		InitDisciplinaStubAndMock(codigoDisciplinaCalculoII, "Calculo II 👿", 40);
		InitDisciplinaStubAndMock(codigoDisciplinaGA, "Geometria Analítica 👿", 40);
		InitDisciplinaStubAndMock(codigoDisciplinaEletiva1, "Disciplina Eletiva 1 👿", 20);
		InitDisciplinaStubAndMock(codigoDisciplinaEletiva2, "Disciplina Eletiva 2 👿", 40);
		InitDisciplinaStubAndMock(codigoDisciplinaOpcional1, descricaoDisciplinaOpcional1, 20);
		InitDisciplinaStubAndMock(codigoDisciplinaOpcional2, "Disciplina Opcional 2 👿", 40);

		InitStudentMock();

		ArrayList<Aluno> alunoListStub = new ArrayList<>();
		alunoListStub.add(alunoMock);

		Mockito.when(cursoMock.getGrupoAlunos()).thenReturn(alunoListStub);
		Mockito.when(cursoMock.getCodigo()).thenReturn(codigoCurso);

		Mockito.when(gradeMock.getCurso()).thenReturn(cursoMock);
		Mockito.when(gradeMock.getId()).thenReturn(1L);
		Mockito.when(gradeMock.getCodigo()).thenReturn("Codigo");

		Mockito.when(alunoMock.getGrade()).thenReturn(gradeMock);
		Mockito.when(alunoMock.getMatricula()).thenReturn(selectedSemester);

		alunoSituacaoController.setCurso(cursoMock);
		alunoSituacaoController.setAluno(alunoMock);
	}

	/**
	 * Method that initializes the mocks for the {@link Curriculum}
	 *
	 * @param mandatoryHours Number of mandatory hours that will be assigned to each of the classes
	 * @param electiveHours  Number of elective hours that will be assigned to each of the classes
	 */
	public void initCurriculumMock(int mandatoryHours, int electiveHours) {
		Mockito.when(curriculumMock.getCurriculumId()).thenReturn(codigoCurriculo);

		HashMap<Integer, TreeSet<Class>> mandatories = new HashMap<>();
		TreeSet<Class> mandatoryClasses = new TreeSet<>();
		mandatoryClasses.add(mandatoryClassMock1);
		mandatoryClasses.add(mandatoryClassMock2);
		mandatoryClasses.add(mandatoryClassMock3);
		mandatories.put(1, mandatoryClasses);
		initMandatoryClassesMock(mandatoryHours);
		Mockito.when(curriculumMock.getMandatories()).thenReturn(mandatories);

		TreeSet<Class> electiveClasses = new TreeSet<>();
		electiveClasses.add(electiveClassMock1);
		electiveClasses.add(electiveClassMock2);
		initElectiveClassMock(electiveHours);
		Mockito.when(curriculumMock.getElectives()).thenReturn(electiveClasses);
	}

	/**
	 * Method that initializes the mocks for the {@link Student}
	 */
	public void InitStudentMock() {
		Mockito.when(studentMock.getId()).thenReturn(codigoEstudante);
		Mockito.when(studentMock.getCourse()).thenReturn(codigoCurso);
		Mockito.when(studentMock.getCurriculum()).thenReturn(codigoCurriculo);

		HashMap<Class, ArrayList<String[]>> approvedClasses = new HashMap<>();
		approvedClasses.put(mandatoryClassMock1, new ArrayList<>());
		approvedClasses.put(mandatoryClassMock3, new ArrayList<>());
		approvedClasses.put(electiveClassMock1, new ArrayList<>());

		ArrayList<String[]> optionalClassData1 = new ArrayList<>();
		optionalClassData1.add("1,APR,A".split(","));
		approvedClasses.put(optionalClassMock1, optionalClassData1);

		ArrayList<String[]> optionalClassData2 = new ArrayList<>();
		optionalClassData2.add("1,HATE,UNIT,TESTS".split(","));
		approvedClasses.put(optionalClassMock2, optionalClassData2);

		HashMap<Class, ArrayList<String[]>> reprovedClasses = new HashMap<>();
		reprovedClasses.put(mandatoryClassMock2, new ArrayList<>());
		reprovedClasses.put(electiveClassMock2, new ArrayList<>());

		Mockito.when(studentMock.getClasses(ClassStatus.APPROVED)).thenReturn(approvedClasses);
		Mockito.when(studentMock.getClasses(ClassStatus.REPROVED_GRADE)).thenReturn(reprovedClasses);
	}

	/**
	 * Inicializa uma disciplina e constrói o mock para o método da classe {@link DisciplinaRepository}
	 * para a busca ser realizada corretamente
	 *
	 * @param codigoDisciplina O código da disciplina a ser mockada
	 * @param nomeDisciplina   O nome da disciplina a ser mockada
	 * @param cargaHoraria     A carga horária da disciplina a ser mockada
	 */
	public void InitDisciplinaStubAndMock(String codigoDisciplina, String nomeDisciplina, int cargaHoraria) {
		Disciplina disciplina1Stub = new Disciplina();
		disciplina1Stub.setCodigo(codigoDisciplina);
		disciplina1Stub.setNome(nomeDisciplina);
		disciplina1Stub.setCargaHoraria(cargaHoraria);
		Mockito.when(disciplinaRepositoryMock.buscarPorCodigoDisciplina(codigoDisciplina)).thenReturn(disciplina1Stub);
	}
}
