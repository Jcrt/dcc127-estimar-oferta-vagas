package br.ufjf.coordenacao.sistemagestaocurso.controller;

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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

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
}
