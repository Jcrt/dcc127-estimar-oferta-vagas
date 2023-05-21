package br.ufjf.coordenacao.sistemagestaocurso.model.estrutura;

import java.util.ArrayList;
import java.util.List;

public class GrupoElementoGrafico {
	
	private Integer grade;
	private List<ElementoGrafico> listaElementoGrafico = new ArrayList<>();
	
	public Integer getGrade() {
		return grade;
	}
	public void setGrade(Integer grade) {
		this.grade = grade;
	}
	public List<ElementoGrafico> getListaElementoGrafico() {
		return listaElementoGrafico;
	}
	public void setListaElementoGrafico(List<ElementoGrafico> listaElementoGrafico) {
		this.listaElementoGrafico = listaElementoGrafico;
	}
	
	
	

}
