package br.ufjf.coordenacao.sistemagestaocurso.controller.helpers;

import br.ufjf.coordenacao.sistemagestaocurso.model.Aluno;
import br.ufjf.coordenacao.sistemagestaocurso.model.Curso;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class CursoHelperTest {
	@Test
	@DisplayName("GIVEN CursoHelper THEN call getAlunoFromGrupoAlunos THEN should find Aluno correctly")
	void getAlunoFromGrupoAlunosTest1() {
		Curso curso = new Curso();
		Aluno aluno = new Aluno();
		aluno.setMatricula("Matricula bizurada");
		List<Aluno> grupoAlunos = new ArrayList<>();
		grupoAlunos.add(aluno);

		curso.setGrupoAlunos(grupoAlunos);

		Aluno foundAluno = CursoHelper.getAlunoFromGrupoAlunos(aluno, curso);

		Assertions.assertEquals(aluno, foundAluno);
	}

	@Test
	@DisplayName("GIVEN CursoHelper THEN call getAlunoFromGrupoAlunos with divergent matricula THEN should throw an exception")
	void getAlunoFromGrupoAlunosTest2() {
		Curso curso = new Curso();
		Aluno aluno = new Aluno();
		Aluno alunoOutOfList = new Aluno();
		aluno.setMatricula("Matricula bizurada");
		aluno.setMatricula("To invisivel, desliga o freio motor");
		List<Aluno> grupoAlunos = new ArrayList<>();
		grupoAlunos.add(aluno);

		curso.setGrupoAlunos(grupoAlunos);

		Assertions.assertThrows(NullPointerException.class, () -> {
			CursoHelper.getAlunoFromGrupoAlunos(alunoOutOfList, curso);
		});
	}
}