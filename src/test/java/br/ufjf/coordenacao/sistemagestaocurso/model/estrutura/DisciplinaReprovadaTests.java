package br.ufjf.coordenacao.sistemagestaocurso.model.estrutura;

import br.ufjf.coordenacao.sistemagestaocurso.model.Aluno;
import br.ufjf.coordenacao.sistemagestaocurso.model.Historico;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

class DisciplinaReprovadaTests {

	final int[] seeds = {1, 2, 3, 4, 5, 6};
	Map<String, Aluno> alunos;
	Map<String, AlunoSituacao> alunoSituacoes;

	DisciplinaReprovadaTests() {
		alunos = new Hashtable<>();
		alunoSituacoes = new Hashtable<>();
		initQueues();
	}

	private String getMatricula(int seedEntry) {
		return "201" + seedEntry + "76082";
	}

	@Test
	@DisplayName("GIVEN DiscipinaReprovada WHEN calculaQuantidadades is called with correct data THEN quantities should be calculed correctly")
	void calculaQuantidadesTest1() {
		DisciplinaReprovada dr = new DisciplinaReprovada();
		dr.setProcessado(false);

		dr.setListaAlunoSituacao(this.CastDictionaryValuesAsList(alunoSituacoes));

		List<Historico> historicos = Arrays.stream(seeds).mapToObj(x -> {
			Historico hist = new Historico();
			hist.setNota(String.valueOf(x * 15));
			hist.setId((x / 10L));
			hist.setAluno(alunos.get(getMatricula(x)));
			return hist;
		}).collect(Collectors.toList());

		List<Historico> listaAprovados = historicos
				.stream()
				.filter(x -> Integer.parseInt(x.getNota()) >= 60)
				.collect(Collectors.toList());

		List<Historico> listaReprovados = historicos
				.stream()
				.filter(x -> Integer.parseInt(x.getNota()) < 60)
				.collect(Collectors.toList());

		dr.setListaAprov(listaAprovados);
		dr.setListaReprovadosNota(listaReprovados);
		dr.setListaReprovadosFreq(new ArrayList<>());
		dr.setListaTotal(historicos);

		Assertions.assertEquals(listaAprovados.size(), dr.getQuantidadeAlunosAprovados());
		Assertions.assertEquals(listaReprovados.size(), dr.getQuantidadeReprovacoesNota());
		Assertions.assertEquals(0, dr.getQuantidadeAlunosReprovadosFreq());
		Assertions.assertEquals(6, dr.getQuantidadeAlunos());
		Assertions.assertEquals(historicos.size(), dr.getQuantidadeTentativas());
		Assertions.assertTrue(dr.isProcessado());
	}

	/**
	 * Faz a conversão de um {@link Map} para uma {@link List<T>}
	 *
	 * @param givenMap O {@link Map} informado
	 * @param <T>      O tipo genérico desejado
	 * @return Uma {@link List<T>} com os valores contidos no {@link Map}
	 */
	private <T> List<T> CastDictionaryValuesAsList(Map<String, T> givenMap) {
		return new ArrayList<>(givenMap.values());
	}

	/**
	 * Inicializa as filas de objetos usados no teste
	 */
	private void initQueues() {

		Arrays.stream(seeds).forEach(x -> {
			long idAluno = Integer.toUnsignedLong(x);
			String matriculaAluno = getMatricula(x);
			String nomeAluno = "Fulanilho " + x;

			Aluno aluno = new Aluno();
			aluno.setId(idAluno);
			aluno.setMatricula(matriculaAluno);
			aluno.setNome(nomeAluno);

			AlunoSituacao alunoSituacao = new AlunoSituacao();
			alunoSituacao.setNome(nomeAluno);
			alunoSituacao.setMatricula(matriculaAluno);

			alunos.put(matriculaAluno, aluno);
			alunoSituacoes.put(matriculaAluno, alunoSituacao);
		});
	}
}
