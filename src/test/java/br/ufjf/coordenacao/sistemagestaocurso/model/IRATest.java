package br.ufjf.coordenacao.sistemagestaocurso.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("IRA tests")
public class IRATest {
    @Test
    @DisplayName("Given IRA object WHEN clone is called THEN should create a different object")
    public void test1() throws CloneNotSupportedException{
        IRA originalIra = new IRA();
        IRA clonnedIra = (IRA) originalIra.clone();
        Assertions.assertFalse(originalIra.equals(clonnedIra));
    }
}