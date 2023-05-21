package br.ufjf.coordenacao.sistemagestaocurso.controller.helpers;

import br.ufjf.coordenacao.sistemagestaocurso.controller.interfaces.IHorasCurricularesConsumer;
import br.ufjf.coordenacao.sistemagestaocurso.model.Aluno;
import br.ufjf.coordenacao.sistemagestaocurso.model.Grade;
import jakarta.validation.constraints.NotNull;

/**
 * Classe que oferece funções úteis para implementações de {@link IHorasCurricularesConsumer}
 */
public class IHorasCurricularesConsumerHelper {

	/**
	 * Define o valor para os campos de horas curriculares
	 *
	 * @param consumer O consumidor da informação de horas
	 * @param aluno    O objeto representando o aluno
	 */
	public static void setHorasCurriculares(@NotNull IHorasCurricularesConsumer consumer, @NotNull Aluno aluno) {
		Grade grade = aluno.getGrade();
		consumer.setHorasEletivas(grade.getHorasEletivas());
		consumer.setHorasOpcionais(grade.getHorasOpcionais());
		consumer.setHorasACE(grade.getHorasAce());
	}

	/**
	 * Define o valor para os campos de horas curriculares concluídas
	 *
	 * @param consumer O consumidor da informação de horas
	 * @param aluno    O objeto representando o aluno
	 */
	public static void setHorasCurricularesConcluidas(@NotNull IHorasCurricularesConsumer consumer, @NotNull Aluno aluno) {
		consumer.setHorasObrigatoriasConcluidas(aluno.getHorasObrigatoriasCompletadas());
		consumer.setHorasEletivasConcluidas(aluno.getHorasEletivasCompletadas());
		consumer.setHorasOpcionaisConcluidas(aluno.getHorasOpcionaisCompletadas());
	}
}
