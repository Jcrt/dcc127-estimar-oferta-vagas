package br.ufjf.coordenacao.sistemagestaocurso.model.estrutura;

import br.ufjf.coordenacao.sistemagestaocurso.model.Aluno;

public class AlunoSelecionado {
	private String gradeIngresso;
	private Integer periodoCorrente;
	private String matricula;
	private String nomeAluno;
	private Aluno aluno;

	public String getGradeIngresso() {
		return gradeIngresso;
	}

	public void setGradeIngresso(String gradeIngresso) {
		this.gradeIngresso = gradeIngresso;
	}

	public Integer getPeriodoCorrente() {
		return periodoCorrente;
	}
	public void setPeriodoCorrente(Integer periodoCorrente) {
		this.periodoCorrente = periodoCorrente;
	}
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getNomeAluno() {
		return nomeAluno;
	}
	public void setNomeAluno(String nomeAluno) {
		this.nomeAluno = nomeAluno;
	}
	public Aluno getAluno() {
		return aluno;
	}
	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}
}
