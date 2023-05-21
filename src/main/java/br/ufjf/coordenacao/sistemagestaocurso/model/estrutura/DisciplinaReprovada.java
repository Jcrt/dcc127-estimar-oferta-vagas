package br.ufjf.coordenacao.sistemagestaocurso.model.estrutura;

import br.ufjf.coordenacao.sistemagestaocurso.model.Aluno;
import br.ufjf.coordenacao.sistemagestaocurso.model.Disciplina;
import br.ufjf.coordenacao.sistemagestaocurso.model.Historico;

import java.util.*;
import java.util.function.Consumer;

enum TipoCalculoSituacaoAlunoEnum {
	APROVADOS,
	REPROVADOS_NOTA,
	REPROVADOS_FREQUENCIA,
	NENHUM
}

public class DisciplinaReprovada {
	private final Map<TipoCalculoSituacaoAlunoEnum, Consumer<AlunoSituacao>> tipoCalculoSituacaoStrategy;
	private List<Historico> listaReprovadosFreq = new ArrayList<>();
	private List<Historico> listaAprov = new ArrayList<>();
	private List<Historico> listaReprovadosNota = new ArrayList<>();
	private Disciplina disciplinaSelecionada;
	private List<Historico> listaMatriculados = new ArrayList<>();
	private int quantidadeAlunos;
	private int quantidadeAlunosAprovados;
	private int quantidadeAlunosReprovadosFreq;
	private int quantidadeAlunosReprovadosNota;
	private int quantidadeTentativas;
	private int quantidadeReprovacoesFreq;
	private int quantidadeAprovados;
	private int quantidadeReprovacoesNota;
	private boolean processado = false;
	private List<AlunoSituacao> listaAlunoSituacao = new ArrayList<>();
	private List<Historico> listaTotal = new ArrayList<>();

	public DisciplinaReprovada() {
		tipoCalculoSituacaoStrategy = calculoTipoSituacaoRegister();
	}

	public AlunoSituacao buscaAlunoSituacao(Aluno aluno) {
		for (AlunoSituacao alunoSituacaoQuestao : listaAlunoSituacao) {
			if (alunoSituacaoQuestao.getMatricula().equals(aluno.getMatricula())) {
				return alunoSituacaoQuestao;
			}
		}
		AlunoSituacao alunoSituacao = new AlunoSituacao();
		alunoSituacao.setMatricula(aluno.getMatricula());
		alunoSituacao.setNome(aluno.getNome());
		alunoSituacao.setQuantidadeMatriculas(0);
		alunoSituacao.setQuantidadeReprovacoesFreq(0);
		alunoSituacao.setQuantidadeReprovacoesNota(0);
		listaAlunoSituacao.add(alunoSituacao);

		return alunoSituacao;
	}

	private int calculaQuantidadePorHistorico(List<Historico> historico) {
		return calculaQuantidadePorHistorico(historico, TipoCalculoSituacaoAlunoEnum.NENHUM);
	}

	private int calculaQuantidadePorHistorico(List<Historico> historico, TipoCalculoSituacaoAlunoEnum tipoCalculo) {
		HashSet<String> matriculasInseridas = new HashSet<>();

		for (Historico historicoQuestao : historico) {
			AlunoSituacao alunoSituacaoQuestao = buscaAlunoSituacao(historicoQuestao.getAluno());
			Consumer<AlunoSituacao> tipoCalculoSituacao = tipoCalculoSituacaoStrategy.getOrDefault(tipoCalculo, null);
			if (tipoCalculoSituacao != null)
				tipoCalculoSituacao.accept(alunoSituacaoQuestao);
			alunoSituacaoQuestao.setQuantidadeMatriculas(alunoSituacaoQuestao.getQuantidadeMatriculas() + 1);
			matriculasInseridas.add(historicoQuestao.getAluno().getMatricula());
		}
		return matriculasInseridas.size();
	}

	private void calculaQuantidades() {
		if (!processado) {
			quantidadeTentativas = listaTotal.size();
			quantidadeReprovacoesFreq = listaReprovadosFreq.size();
			quantidadeReprovacoesNota = listaReprovadosNota.size();
			quantidadeAprovados = listaAprov.size();

			quantidadeAlunosReprovadosFreq = calculaQuantidadePorHistorico(listaReprovadosFreq, TipoCalculoSituacaoAlunoEnum.REPROVADOS_FREQUENCIA);
			quantidadeAlunosReprovadosNota = calculaQuantidadePorHistorico(listaReprovadosNota, TipoCalculoSituacaoAlunoEnum.REPROVADOS_NOTA);
			quantidadeAlunosAprovados = calculaQuantidadePorHistorico(listaAprov, TipoCalculoSituacaoAlunoEnum.APROVADOS);
			quantidadeAlunos = calculaQuantidadePorHistorico(listaMatriculados)
					+ quantidadeAlunosReprovadosNota
					+ quantidadeAlunosReprovadosFreq
					+ quantidadeAlunosAprovados;

			processado = true;
		}
	}

	/**
	 * Registra os tipos de cálculo de situação
	 *
	 * @return um {@link Map} contendo os possíveis cálculos
	 */
	private Map<TipoCalculoSituacaoAlunoEnum, Consumer<AlunoSituacao>> calculoTipoSituacaoRegister() {

		Map<TipoCalculoSituacaoAlunoEnum, Consumer<AlunoSituacao>> mapInitiator = new EnumMap<>(TipoCalculoSituacaoAlunoEnum.class);

		mapInitiator.put(
				TipoCalculoSituacaoAlunoEnum.REPROVADOS_FREQUENCIA,
				alunoSituacao -> alunoSituacao.setQuantidadeReprovacoesFreq(alunoSituacao.getQuantidadeReprovacoesFreq() + 1)
		);

		mapInitiator.put(
				TipoCalculoSituacaoAlunoEnum.REPROVADOS_NOTA,
				alunoSituacao -> alunoSituacao.setQuantidadeReprovacoesNota(alunoSituacao.getQuantidadeReprovacoesNota() + 1)
		);

		mapInitiator.put(
				TipoCalculoSituacaoAlunoEnum.APROVADOS,
				alunoSituacao -> alunoSituacao.setQuantidadeAprovados(alunoSituacao.getQuantidadeAprovados() + 1)
		);

		return mapInitiator;
	}

	public int getQuantidadeAlunosAprovados() {
		calculaQuantidades();
		return quantidadeAlunosAprovados;
	}

	public int getQuantidadeAprovados() {
		calculaQuantidades();
		return quantidadeAprovados;
	}

	public int getQuantidadeAlunos() {
		calculaQuantidades();
		return quantidadeAlunos;
	}

	public int getQuantidadeAlunosReprovadosFreq() {
		calculaQuantidades();
		return quantidadeAlunosReprovadosFreq;
	}

	public int getQuantidadeAlunosReprovadosNota() {
		calculaQuantidades();
		return quantidadeAlunosReprovadosNota;
	}

	public int getQuantidadeTentativas() {
		calculaQuantidades();
		return quantidadeTentativas;
	}

	public int getQuantidadeReprovacoesFreq() {
		calculaQuantidades();
		return quantidadeReprovacoesFreq;
	}

	public int getQuantidadeReprovacoesNota() {
		calculaQuantidades();
		return quantidadeReprovacoesNota;
	}

	public List<Historico> getListaTotal() {
		return listaTotal;
	}

	public void setListaTotal(List<Historico> listaTotal) {
		this.listaTotal = listaTotal;
	}

	public List<AlunoSituacao> getListaAlunoSituacao() {
		return listaAlunoSituacao;
	}

	public void setListaAlunoSituacao(List<AlunoSituacao> listaAlunoSituacao) {
		this.listaAlunoSituacao = listaAlunoSituacao;
	}

	public List<Historico> getListaReprovadosFreq() {
		return listaReprovadosFreq;
	}

	public void setListaReprovadosFreq(List<Historico> listaReprovadosFreq) {
		this.listaReprovadosFreq = listaReprovadosFreq;
	}

	public List<Historico> getListaReprovadosNota() {
		return listaReprovadosNota;
	}

	public void setListaReprovadosNota(List<Historico> listaReprovadosNota) {
		this.listaReprovadosNota = listaReprovadosNota;
	}

	public List<Historico> getListaMatriculados() {
		return listaMatriculados;
	}

	public void setListaMatriculados(List<Historico> listaMatriculados) {
		this.listaMatriculados = listaMatriculados;
	}

	public Disciplina getDisciplinaSelecionada() {
		return disciplinaSelecionada;
	}

	public void setDisciplinaSelecionada(Disciplina disciplinaSelecionada) {
		this.disciplinaSelecionada = disciplinaSelecionada;
	}

	public List<Historico> getListaAprov() {
		return listaAprov;
	}

	public void setListaAprov(List<Historico> listaAprov) {
		this.listaAprov = listaAprov;
	}

	public boolean isProcessado() {
		return processado;
	}

	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

	public void setQuantidadeAlunos(int quantidadeAlunos) {
		this.quantidadeAlunos = quantidadeAlunos;
	}

	public void setQuantidadeAlunosAprovados(int quantidadeAlunosAprovados) {
		this.quantidadeAlunosAprovados = quantidadeAlunosAprovados;
	}

	public void setQuantidadeAlunosReprovadosFreq(int quantidadeAlunosReprovadosFreq) {
		this.quantidadeAlunosReprovadosFreq = quantidadeAlunosReprovadosFreq;
	}

	public void setQuantidadeAlunosReprovadosNota(int quantidadeAlunosReprovadosNota) {
		this.quantidadeAlunosReprovadosNota = quantidadeAlunosReprovadosNota;
	}

	public void setQuantidadeTentativas(int quantidadeTentativas) {
		this.quantidadeTentativas = quantidadeTentativas;
	}

	public void setQuantidadeReprovacoesFreq(int quantidadeReprovacoesFreq) {
		this.quantidadeReprovacoesFreq = quantidadeReprovacoesFreq;
	}

	public void setQuantidadeReprovacoesNota(int quantidadeReprovacoesNota) {
		this.quantidadeReprovacoesNota = quantidadeReprovacoesNota;
	}
}