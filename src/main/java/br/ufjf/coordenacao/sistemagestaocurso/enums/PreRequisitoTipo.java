package br.ufjf.coordenacao.sistemagestaocurso.enums;

public enum PreRequisitoTipo {
	PREREQUISITO("Pré-Requisito"),
	COREQUISITO("Co-Requisito");

	private final String description;

	PreRequisitoTipo(String description){
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}
}
