package br.ufjf.coordenacao.sistemagestaocurso.model.estrutura;

import br.ufjf.coordenacao.sistemagestaocurso.model.Aluno;

import java.util.ArrayList;
import java.util.List;

public class PeriodoAluno {

	private Integer periodoReal;
	private List<Aluno> listaAlunosPeriodo = new ArrayList<>();

	public List<Aluno> getListaAlunosPeriodo() {
		return listaAlunosPeriodo;
	}

	public void setListaAlunosPeriodo(List<Aluno> listaAlunosPeriodo) {
		this.listaAlunosPeriodo = listaAlunosPeriodo;
	}

	public Integer getPeriodoReal() {
		return periodoReal;
	}

	public void setPeriodoReal(Integer periodoReal) {
		this.periodoReal = periodoReal;
	}
}
