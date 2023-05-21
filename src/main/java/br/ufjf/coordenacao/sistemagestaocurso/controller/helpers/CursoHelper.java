package br.ufjf.coordenacao.sistemagestaocurso.controller.helpers;

import br.ufjf.coordenacao.sistemagestaocurso.model.Aluno;
import br.ufjf.coordenacao.sistemagestaocurso.model.Curso;
import jakarta.validation.constraints.NotNull;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Classe que oferece funções úteis para a classe {@link Curso}
 */
public class CursoHelper {

	/**
	 * Pesquisa, dentro do grupo de alunos do {@link Curso} informado, se o {@link Aluno} existe
	 * @param searchedAluno O aluno informado
	 * @param currentCurso  O curso informado
	 * @return O aluno pertencente ao curso
	 */
	public static Aluno getAlunoFromGrupoAlunos(@NotNull Aluno searchedAluno, @NotNull Curso currentCurso) {

		Optional<Aluno> found = currentCurso
				.getGrupoAlunos()
				.stream()
				.filter(x -> x.getMatricula().contains(searchedAluno.getMatricula()))
				.findFirst();

		if (found.isPresent())
			return found.get();

		throw new NoSuchElementException("Nenhum aluno encontrado no grupo");
	}
}
