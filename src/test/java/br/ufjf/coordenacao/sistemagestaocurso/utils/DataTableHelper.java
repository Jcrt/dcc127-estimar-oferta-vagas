package br.ufjf.coordenacao.sistemagestaocurso.utils;

import org.primefaces.component.datatable.DataTable;

/**
 * Classe que oferece funções úteis para testes utilizando {@link DataTable}
 */
public class DataTableHelper {

	/**
	 * Cria uma instância de {@link DataTable}
	 *
	 * @param shouldMarkAsInitialState Informa se ela deve ser marcada como estado inicial
	 * @return uma instância de {@link DataTable}
	 */
	public static DataTable createDataTableForTest(boolean shouldMarkAsInitialState) {
		DataTable dt = new DataTable();
		if (shouldMarkAsInitialState)
			dt.markInitialState();
		return dt;
	}
}
