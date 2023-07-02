package br.ufjf.coordenacao.sistemagestaocurso.utils;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

public abstract class TestBaseClass {
	protected static Mocks mocks = new Mocks();

	public abstract void specificSetup();

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		specificSetup();
	}
}

