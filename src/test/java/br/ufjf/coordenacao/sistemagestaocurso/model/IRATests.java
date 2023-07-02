package br.ufjf.coordenacao.sistemagestaocurso.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("IRA tests")
class IRATests {
	@Test
	@DisplayName("Given IRA object WHEN clone is called THEN should create a different object")
	void GIVEN_iraObject_WHEN_cloneIsCalled_THEN_shouldCreateADifferentObject() throws CloneNotSupportedException {
		IRA originalIra = new IRA();
		IRA clonnedIra = (IRA) originalIra.clone();
		Assertions.assertNotEquals(originalIra, clonnedIra);
	}
}