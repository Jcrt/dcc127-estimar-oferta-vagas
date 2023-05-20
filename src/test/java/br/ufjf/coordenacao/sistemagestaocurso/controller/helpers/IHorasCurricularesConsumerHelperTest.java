package br.ufjf.coordenacao.sistemagestaocurso.controller.helpers;

import br.ufjf.coordenacao.sistemagestaocurso.controller.AlunoSituacaoController;
import br.ufjf.coordenacao.sistemagestaocurso.controller.interfaces.IHorasCurricularesConsumer;
import br.ufjf.coordenacao.sistemagestaocurso.model.Aluno;
import br.ufjf.coordenacao.sistemagestaocurso.model.Grade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class IHorasCurricularesConsumerHelperTest {

	@Mock
	Aluno alunoMock;

	@Mock
	Grade gradeMock;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("GIVEN IHorasCurricularesConsumerHelper WHEN setHorasCurricularesConcluidas is called THEN consumer should be" +
			"hours correcly filled")
	void setHorasCurricularesConcluidasTest1() {
		int horasObrigatorias = 10;
		int horasEletivas = 20;
		int horasOpcionais = 30;

		IHorasCurricularesConsumer consumer = new AlunoSituacaoController();
		Mockito.when(alunoMock.getHorasObrigatoriasCompletadas()).thenReturn(horasObrigatorias);
		Mockito.when(alunoMock.getHorasOpcionaisCompletadas()).thenReturn(horasOpcionais);
		Mockito.when(alunoMock.getHorasEletivasCompletadas()).thenReturn(horasEletivas);

		IHorasCurricularesConsumerHelper.setHorasCurricularesConcluidas(consumer, alunoMock);

		Assertions.assertEquals(horasObrigatorias, consumer.getHorasObrigatoriasConcluidas());
		Assertions.assertEquals(horasEletivas, consumer.getHorasEletivasConcluidas());
		Assertions.assertEquals(horasOpcionais, consumer.getHorasOpcionaisConcluidas());
	}

	@Test
	@DisplayName("GIVEN IHorasCurricularesConsumerHelper WHEN setHorasCurriculares is called THEN consumer should be" +
			"hours correcly filled")
	void setHorasCurricularesTest1() {
		int horasACE = 10;
		int horasEletivas = 20;
		int horasOpcionais = 30;

		IHorasCurricularesConsumer consumer = new AlunoSituacaoController();
		Mockito.when(alunoMock.getGrade()).thenReturn(gradeMock);

		Mockito.when(gradeMock.getHorasAce()).thenReturn(horasACE);
		Mockito.when(gradeMock.getHorasOpcionais()).thenReturn(horasOpcionais);
		Mockito.when(gradeMock.getHorasEletivas()).thenReturn(horasEletivas);

		IHorasCurricularesConsumerHelper.setHorasCurriculares(consumer, alunoMock);

		Assertions.assertEquals(horasACE, consumer.getHorasACE());
		Assertions.assertEquals(horasEletivas, consumer.getHorasEletivas());
		Assertions.assertEquals(horasOpcionais, consumer.getHorasOpcionais());
	}

	@Test
	@DisplayName("GIVEN IHorasCurricularesConsumerHelper WHEN setHorasCurriculares is called and no Grade is found in Aluno THEN exception should be thrown")
	void setHorasCurricularesTest2() {
		IHorasCurricularesConsumer consumer = new AlunoSituacaoController();
		Assertions.assertThrows(NullPointerException.class, () -> {
			IHorasCurricularesConsumerHelper.setHorasCurriculares(consumer, alunoMock);
		});
	}
}