package br.ufjf.coordenacao.sistemagestaocurso.model.estrutura;

import java.util.ArrayList;
import java.util.List;

public class TurmaAlunos {
	private int quantidadeTotal;
	private final List<AprovacoesQuantidade> listaAprovacoesQuantidade = new ArrayList<>();
	private String ingressoAlunos;

	public int getQuantidadeTotal() {
		return quantidadeTotal;
	}

	public void setQuantidadeTotal(int quantidadeTotal) {
		this.quantidadeTotal = quantidadeTotal;
	}

	public String getIngressoAlunos() {
		return ingressoAlunos;
	}
	public void setIngressoAlunos(String ingressoAlunos) {
		this.ingressoAlunos = ingressoAlunos;
	}
	public List<AprovacoesQuantidade> getListaAprovacoesQuantidade() {
		return listaAprovacoesQuantidade;
	}
}