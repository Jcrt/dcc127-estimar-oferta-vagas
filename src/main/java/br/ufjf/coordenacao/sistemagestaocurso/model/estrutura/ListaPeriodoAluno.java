package br.ufjf.coordenacao.sistemagestaocurso.model.estrutura;

import java.util.ArrayList;
import java.util.List;

public class ListaPeriodoAluno {

	private String ingressoAlunos;
	private Integer maximoEncontrado;
	private List<PeriodoAluno> listaPeriodo = new ArrayList<>();

	public String getIngressoAlunos() {
		return ingressoAlunos;
	}

	public void setIngressoAlunos(String ingressoAlunos) {
		this.ingressoAlunos = ingressoAlunos;
	}

	public List<PeriodoAluno> getListaPeriodo() {
		return listaPeriodo;
	}

	public void setListaPeriodo(List<PeriodoAluno> listaPeriodo) {
		this.listaPeriodo = listaPeriodo;
	}

	public Integer getMaximoEncontrado() {
		return maximoEncontrado;
	}

	public void setMaximoEncontrado(Integer maximoEncontrado) {
		this.maximoEncontrado = maximoEncontrado;
	}
}
