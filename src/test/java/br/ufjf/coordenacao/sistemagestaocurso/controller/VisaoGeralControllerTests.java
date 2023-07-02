package br.ufjf.coordenacao.sistemagestaocurso.controller;

import br.ufjf.coordenacao.sistemagestaocurso.controller.util.UsuarioController;
import br.ufjf.coordenacao.sistemagestaocurso.model.estrutura.Autenticacao;
import br.ufjf.coordenacao.sistemagestaocurso.model.estrutura.EspectativaDisciplina;
import br.ufjf.coordenacao.sistemagestaocurso.model.estrutura.GradeHistorico;
import br.ufjf.coordenacao.sistemagestaocurso.providers.EstruturaArvoreProvider;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.EstruturaArvore;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.ImportarArvore;
import br.ufjf.coordenacao.sistemagestaocurso.utils.Mocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisaoGeralControllerTests {

	Mocks _mocks;

	@Mock
	UsuarioController usuarioControllerMock;

	@Mock
	Autenticacao autenticacaoMock;

	@Mock
	EstruturaArvoreProvider estruturaArvoreProviderMock;

	@InjectMocks
	VisaoGeralController visaoGeralController;

	@BeforeEach
	void setup(){
		MockitoAnnotations.openMocks(this);
	}

	public VisaoGeralControllerTests(){
		_mocks = new Mocks();
	}

	@Test
	void testGerarDados() {
		EstruturaArvore estruturaArvore = EstruturaArvore.getInstance();
		ImportarArvore importarArvore = estruturaArvore.recuperarArvore(_mocks.getGrade(), true);
		importarArvore.importarHistorico(_mocks.getHistoricos());

		Mockito.when(usuarioControllerMock.getAutenticacao()).thenReturn(autenticacaoMock);
		Mockito.when(autenticacaoMock.getCursoSelecionado()).thenReturn(_mocks.getCurso());
		Mockito.when(estruturaArvoreProviderMock.provide()).thenReturn(estruturaArvore);

		visaoGeralController.init();

		visaoGeralController.getListagrade().add(Mocks.codigoGrade);

		visaoGeralController.setCurso(_mocks.getCurso());

		visaoGeralController.gerarDados();
	}
}