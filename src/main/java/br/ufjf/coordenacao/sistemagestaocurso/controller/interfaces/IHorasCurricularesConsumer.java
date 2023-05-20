package br.ufjf.coordenacao.sistemagestaocurso.controller.interfaces;

/**
 * Interface que define o contrato para classes que consomem horas curriculares.
 * Basicamente só possui getters e setters por enquanto 😉
 */
public interface IHorasCurricularesConsumer {

	int getHorasEletivas();

	void setHorasEletivas(int horasEletivas);

	int getHorasEletivasConcluidas();

	void setHorasEletivasConcluidas(int horasEletivasConcluidas);

	int getHorasOpcionais();

	void setHorasOpcionais(int horasOpcionais);

	int getHorasOpcionaisConcluidas();

	void setHorasOpcionaisConcluidas(int horasOpcionaisConcluidas);

	int getHorasACE();

	void setHorasACE(int horasAce);

	int getHorasObrigatoriasConcluidas();

	void setHorasObrigatoriasConcluidas(int horasOpcionaisConcluidas);
}
