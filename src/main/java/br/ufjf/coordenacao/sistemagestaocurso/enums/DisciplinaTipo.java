package br.ufjf.coordenacao.sistemagestaocurso.enums;

public enum DisciplinaTipo {
	REQUIRED("Obrigatória"),
	OPTIONAL("Opcional");

	private final String description;

	DisciplinaTipo(String description){
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}
}
