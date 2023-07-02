package br.ufjf.coordenacao.sistemagestaocurso.enums;

public enum DisciplinaStatus {
	APPROVED("Aprovado"),
	REPPROVED_BY_SCORE("Rep Nota"),
	REPPROVED_BY_FREQUENCY("Rep Freq"),
	LOCKED("Trancado"),
	DISPENSED("Dispensado"),
	ENROLLED("Matriculado"),
	CANCELLED("Cancelado");

	private final String description;

	DisciplinaStatus(String description){
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}
}
