package br.ufjf.coordenacao.sistemagestaocurso.controller;

import br.ufjf.coordenacao.sistemagestaocurso.controller.util.UsuarioController;
import br.ufjf.coordenacao.sistemagestaocurso.model.estrutura.Autenticacao;
import br.ufjf.coordenacao.sistemagestaocurso.providers.EstruturaArvoreProvider;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.EstruturaArvore;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.ImportarArvore;
import br.ufjf.coordenacao.sistemagestaocurso.utils.Mocks;
import br.ufjf.coordenacao.sistemagestaocurso.utils.TestBaseClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class VisaoGeralControllerTests extends TestBaseClass {

	@Mock
	UsuarioController usuarioControllerMock;

	@Mock
	Autenticacao autenticacaoMock;

	@Mock
	EstruturaArvoreProvider estruturaArvoreProviderMock;

	@InjectMocks
	VisaoGeralController visaoGeralController;

	@Override
	public void specificSetup() {
		EstruturaArvore estruturaArvore = EstruturaArvore.getInstance();
		ImportarArvore importarArvore = estruturaArvore.recuperarArvore(mocks.getGrade(), true);
		importarArvore.importarHistorico(mocks.getHistoricos());

		Mockito.when(usuarioControllerMock.getAutenticacao()).thenReturn(autenticacaoMock);
		Mockito.when(autenticacaoMock.getCursoSelecionado()).thenReturn(mocks.getCurso());
		Mockito.when(estruturaArvoreProviderMock.provide()).thenReturn(estruturaArvore);
	}

	@Test
	@DisplayName("GIVEN VisaoGeralController WHEN gerarDados is called THEN generate correct subject counts")
	void visaoGeralControllerTest1() {
		visaoGeralController.init();

		visaoGeralController.getListagrade().add(Mocks.codigoGrade);

		visaoGeralController.setCurso(mocks.getCurso());

		visaoGeralController.gerarDados();

		Assertions.assertEquals(10, visaoGeralController.getListaDisciplinasDisponiveis().size());
		Assertions.assertEquals(10, visaoGeralController.getListaTodasDisciplinasDisponiveis().size());
		Assertions.assertEquals(10, visaoGeralController.getListaEspectativaDisciplina().size());
	}
}