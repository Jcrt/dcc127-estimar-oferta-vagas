package br.ufjf.coordenacao.sistemagestaocurso.controller;

import br.ufjf.coordenacao.sistemagestaocurso.model.Aluno;
import br.ufjf.coordenacao.sistemagestaocurso.model.Curso;
import br.ufjf.coordenacao.sistemagestaocurso.model.Grade;
import br.ufjf.coordenacao.sistemagestaocurso.utils.DataTableHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.primefaces.component.datatable.DataTable;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import java.util.ArrayList;

@DisplayName("AlunoSituacaoController tests")
class AlunoSituacaoControllerTest {
    private static final String gridObrigatoriasExpression = "form:gridObrigatorias";
    private static final String gridEletivasExpression = "form:gridEletivas";
    private static final String gridOpcionaisExpression = "form:gridOpcionais";
    private static final String gridAceExpression = "form:gridAce";

    @Mock
    private FacesContext facesContextMock;

    @InjectMocks
    private AlunoSituacaoController alunoSituacaoController;

    @Mock
    private Aluno alunoMock;

    @Mock
    private Grade gradeMock;

    @Mock
    private Curso cursoMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        alunoSituacaoController.init();
    }

    /*****
     * resetaDataTable tests
     */

    @Test
    @DisplayName("GIVEN AlunoSituacaoController WHEN resetDataTable is called THEN resets all data tables")
    void resetaDataTablesTest1() {
        UIViewRoot viewRoot = Mockito.mock(UIViewRoot.class);
        ArrayList<DataTable> dataTables = new ArrayList<>();

        dataTables.add(DataTableHelper.createDataTableForTest(true));
        dataTables.add(DataTableHelper.createDataTableForTest(true));
        dataTables.add(DataTableHelper.createDataTableForTest(true));
        dataTables.add(DataTableHelper.createDataTableForTest(true));

        Mockito.when(facesContextMock.getViewRoot()).thenReturn(viewRoot);
        Mockito.when(viewRoot.findComponent(gridObrigatoriasExpression)).thenReturn(dataTables.get(0));
        Mockito.when(viewRoot.findComponent(gridEletivasExpression)).thenReturn(dataTables.get(1));
        Mockito.when(viewRoot.findComponent(gridOpcionaisExpression)).thenReturn(dataTables.get(2));
        Mockito.when(viewRoot.findComponent(gridAceExpression)).thenReturn(dataTables.get(3));

        alunoSituacaoController.resetaDataTables();

        dataTables.forEach(dataTable -> {
            Assertions.assertTrue(dataTable.isReset());
            Assertions.assertFalse(dataTable.initialStateMarked());
        });
    }

    /*****
     * onItemSelectMatriculaAluno tests
     */


    @Test
    @DisplayName("GIVEN AlunoSituacaoController WHEN onItemSelectMatriculaAluno is called with no grade information THEN all hours should be 0")
    void onItemSelectMatriculaAlunoTest1() {
        initMocks();

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
        initMocks();
        int horasEletivas = 10;
        int horasOpcionais = 2;
        int horasACE = 3;
        int horasObrigatoriasConcluidas = 2;
        int horasEletivasConcluidas = 9;
        int horasOpcionaisConcluidas = 1;

        Mockito.when(gradeMock.getHorasEletivas()).thenReturn(horasEletivas);
        Mockito.when(gradeMock.getHorasOpcionais()).thenReturn(horasOpcionais);
        Mockito.when(gradeMock.getHorasAce()).thenReturn(horasACE);
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

    /**
     * Method that initializes the main mocks
     */
    private void initMocks() {
        ArrayList<Aluno> alunoListStub = new ArrayList<>();
        alunoListStub.add(alunoMock);

        Mockito.when(cursoMock.getGrupoAlunos()).thenReturn(alunoListStub);
        Mockito.when(gradeMock.getCurso()).thenReturn(cursoMock);
        Mockito.when(gradeMock.getId()).thenReturn(1L);
        Mockito.when(gradeMock.getCodigo()).thenReturn("Codigo");
        Mockito.when(alunoMock.getGrade()).thenReturn(gradeMock);
        Mockito.when(alunoMock.getMatricula()).thenReturn("201376082");

        alunoSituacaoController.setCurso(cursoMock);
        alunoSituacaoController.setAluno(alunoMock);
    }
}
