package br.ufjf.coordenacao.sistemagestaocurso.controller;

import br.ufjf.coordenacao.sistemagestaocurso.controller.util.Ordenar;
import br.ufjf.coordenacao.sistemagestaocurso.controller.util.UsuarioController;
import br.ufjf.coordenacao.sistemagestaocurso.model.*;
import br.ufjf.coordenacao.sistemagestaocurso.model.estrutura.DisciplinaGradeDisciplina;
import br.ufjf.coordenacao.sistemagestaocurso.repository.*;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.EstruturaArvore;
import br.ufjf.coordenacao.sistemagestaocurso.util.jpa.Transactional;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.primefaces.component.datatable.DataTable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



@Named
@ViewScoped
public class CadastroGradeController implements Serializable {

	static final String OBRIGATORIA = "Obrigatoria";
	static final String PRINCIPAL_FORM_GRID_OBRIGATORIAS = "principalForm:gridObrigatorias";
	static final String PRINCIPAL_FORM_GRID_ELETIVAS = "principalForm:gridEletivas";
	static final String PRINCIPAL_FORM_GRID_EQUIVALENCIAS = "principalForm:gridEquivalencias";
	static final String PRINCIPAL_FORM_GRID_IRA = "principalForm:gridIra";

	//========================================================= VARIABLES ==================================================================================//

	private static final long serialVersionUID = 1L;

	private boolean lgHorasAoe = true;
	private boolean lgMaxPeriodo = true;
	private boolean lgExcluirGrade = true;
	private boolean lgIncluirGrade = true;
	private boolean lgCodigoGrade = false;
	private boolean lgHorasEletivas = true;
	private boolean lgNomeDisciplina = true;	
	private boolean lgTipoDisciplina = true;
	private boolean lgHorasOpcionais  = true;		
	private boolean lgCodigoDisciplina = true;	
	private boolean lgLimparDisciplina = true;
	private boolean lgIncluirDisciplina = true;
	private boolean lgPeriodoDisciplina = true;
	private boolean lgLimparEquivalencia = true;
	private boolean lgIncluirEquivalencia = true;
	private boolean lgCargaHorariaDisciplina = true;
	private boolean lgNomeDisciplinaEquivalenciaUm = true;
	private boolean lgNomeDisciplinaEquivalenciaDois = true;
	private boolean lgCodigoDisciplinaEquivalenciaUm = true;
	private boolean lgCodigoDisciplinaEquivalenciaDois = true;
	private boolean lgDisciplinaIra = true;

	private List<Equivalencia> listaEquivalenciaSelecionada;
	private List<Equivalencia> listaEquivalencia = new ArrayList<>();
	private List<DisciplinaGradeDisciplina> listaEletivasSelecionada;
	private List<DisciplinaGradeDisciplina> listaObrigatoriasSelecionada;
	private List<DisciplinaGradeDisciplina> listaEletivas = new ArrayList<>();
	private List<DisciplinaGradeDisciplina> listaObrigatorias = new ArrayList<>();
	private List<GradeDisciplina> listaIra = new ArrayList<>();
	private List<GradeDisciplina> listaIraSelecionados;
	private List<PreRequisito> listaPreRequisitos = new ArrayList<>();

	private Grade grade = new Grade();
	private Curso curso = new Curso();
	private final Ordenar ordenar = new Ordenar();
	private Disciplina disciplina = new Disciplina();
	private Disciplina disciplinaPre = new Disciplina();
	private Disciplina disciplinaNova = new Disciplina();
	private Disciplina disciplinaIra = new Disciplina();
	private Disciplina disciplinaEquivalenciaUm = new Disciplina();
	private Disciplina disciplinaEquivalenciaDois = new Disciplina();
	private GradeDisciplina gradeDisciplina = new GradeDisciplina();	
	private PreRequisito linhaSelecionadaPreRequisto = new PreRequisito();
	private Equivalencia linhaSelecionadaEquivalencia = new Equivalencia();
	private DisciplinaGradeDisciplina linhaSelecionada = new DisciplinaGradeDisciplina();	
	private GradeDisciplina gradeDisciplinaIraSelecionada = new GradeDisciplina();

	@Inject
	private GradeRepository gradeDAO ;
	
	@Inject
	private DisciplinaRepository disciplinaDAO;

	@Inject
	private PreRequisitoRepository preRequisitoDAO;

	@Inject
	private EquivalenciaRepository equivalenciaDAO;

	@Inject
	private GradeDisciplinaRepository gradeDisciplinaDAO;

	@Inject
	private FacesContext facesContext;

	private String tipoPre;
	private EstruturaArvore estruturaArvore;

	private final Logger logger = Logger.getLogger(CadastroGradeController.class);

	//========================================================= METODOS ==================================================================================//

	@Inject
	private UsuarioController usuarioController;

	@PostConstruct
	public void init() {
		estruturaArvore = EstruturaArvore.getInstance();
		usuarioController.atualizarPessoaLogada();
		curso = usuarioController.getAutenticacao().getCursoSelecionado();
	}

	public List<String> gradeCodigos(String codigo) {
		codigo = codigo.toUpperCase();
		List<String> todos = new ArrayList<>();
		for (Grade gradeQuestao : this.gradeDAO.buscarTodosCodigosGradePorCurso(this.curso.getId())) {
			if (gradeQuestao.getCodigo().contains(codigo)) {
				todos.add(gradeQuestao.getCodigo());
			}
		}
		return todos;
	}

	@Transactional
	private void criarNovaGrade()
	{
		grade.setCurso(curso);
		grade.setHorasAce( 0);
		grade.setHorasEletivas( 0);
		grade.setHorasOpcionais( 0);
		gradeDAO.persistir(grade);
		
		usuarioController.setReseta(true);
		usuarioController.atualizarPessoaLogada();
		
		FacesMessage msg = new FacesMessage("Nova grade cadastrada!");
		facesContext.addMessage(null, msg);
	}
	
	//TODO: Refatorar isso aqui
	public void buscarGrade(){
		disciplina = new Disciplina();
		gradeDisciplina = new GradeDisciplina();

		grade.setCodigo(grade.getCodigo().trim().toUpperCase());

		Grade gradeAuxiliar = gradeDAO.buscarPorCodigoGrade(grade.getCodigo(), curso);
		
		if (gradeAuxiliar != null) {
			grade = gradeAuxiliar;
			FacesMessage msg = new FacesMessage("Grade encontrada!");
			facesContext.addMessage(null, msg);
			lgCodigoGrade = true;
			lgHorasEletivas = false;
			lgHorasOpcionais  = false;
			lgHorasAoe = false;
			lgMaxPeriodo = false;
			lgCodigoDisciplina = false;
			lgNomeDisciplina = false;	
			lgCargaHorariaDisciplina = false;
			lgTipoDisciplina = false;
			lgIncluirDisciplina = false;
			lgLimparDisciplina = false;
			lgCodigoDisciplinaEquivalenciaUm = false;
			lgCodigoDisciplinaEquivalenciaDois = false;
			lgNomeDisciplinaEquivalenciaUm = false;
			lgNomeDisciplinaEquivalenciaDois = false;
			lgIncluirEquivalencia = false;
			lgLimparEquivalencia = false;			
			lgDisciplinaIra = false;
			lgExcluirGrade = false;
			atualizaGrids();
		}

		else{	

			if(grade.getCodigo() != null && !grade.getCodigo().equals("")){
				criarNovaGrade();
			}
			else {
				FacesMessage msg = new FacesMessage("Preencha o campo Código Grade!");
				facesContext.addMessage(null, msg);
				return;
			}

			lgCodigoGrade = true;
			lgHorasEletivas = false;
			lgHorasOpcionais  = false;
			lgHorasAoe = false;
			lgMaxPeriodo = false;
			lgCodigoDisciplina = false;
			lgNomeDisciplina = false;	
			lgCargaHorariaDisciplina = false;
			lgTipoDisciplina = false;
			lgIncluirDisciplina = false;
			lgLimparDisciplina = false;
			lgCodigoDisciplinaEquivalenciaUm = false;
			lgCodigoDisciplinaEquivalenciaDois = false;
			lgNomeDisciplinaEquivalenciaUm = false;
			lgNomeDisciplinaEquivalenciaDois = false;
			lgIncluirEquivalencia = false;
			lgLimparEquivalencia = false;
			lgDisciplinaIra = false;
			lgExcluirGrade = false;
		}
	}

	@Transactional
	public void excluirGrade(){
		curso.getGrupoGrades().remove(grade);
		gradeDAO.remover(grade);
		estruturaArvore.removerEstrutura(grade);
		usuarioController.setReseta(true);
		usuarioController.atualizarPessoaLogada();

		limpaGrade();
	}

	public void limpaGrade() {

		grade = new Grade();
		disciplina = new Disciplina();
		gradeDisciplina = new GradeDisciplina();
		listaEquivalencia = new ArrayList<>();
		listaIra = new ArrayList<>();
		lgCodigoGrade = false;
		lgHorasEletivas = true;
		lgHorasOpcionais = true;
		lgHorasAoe = true;
		lgMaxPeriodo = true;
		lgIncluirGrade = true;
		lgCodigoDisciplina = true;
		lgNomeDisciplina = true;
		lgPeriodoDisciplina = true;
		lgCargaHorariaDisciplina = true;
		lgTipoDisciplina = true;
		lgIncluirDisciplina = true;
		lgLimparDisciplina = true;
		lgCodigoDisciplinaEquivalenciaUm = true;
		lgCodigoDisciplinaEquivalenciaDois = true;
		lgNomeDisciplinaEquivalenciaUm = true;
		lgNomeDisciplinaEquivalenciaDois = true;
		lgIncluirEquivalencia = true;
		lgLimparEquivalencia = true;
		lgDisciplinaIra = true;
		lgExcluirGrade = true;
		listaObrigatorias = new ArrayList<>();
		listaEletivas = new ArrayList<>();

		resetDataTables();

		init();

	}

	/**
	 * Reseta todas as {@link DataTable}s da tela
	 */
	private void resetDataTables() {
		resetDataTableByDataTableId(PRINCIPAL_FORM_GRID_OBRIGATORIAS);
		resetDataTableByDataTableId(PRINCIPAL_FORM_GRID_ELETIVAS);
		resetDataTableByDataTableId(PRINCIPAL_FORM_GRID_EQUIVALENCIAS);
		resetDataTableByDataTableId(PRINCIPAL_FORM_GRID_IRA);
	}

	/**
	 * Reseta {@link DataTable} usando o método {@link DataTable#reset()}
	 *
	 * @param dataTableId O id da {@link DataTable} que será resetada
	 */
	private void resetDataTableByDataTableId(String dataTableId) {
		DataTable dataTable = (DataTable) facesContext.getViewRoot().findComponent(dataTableId);
		dataTable.clearInitialState();
		dataTable.reset();
	}

	@Transactional
	public void alterarGrade() {
		gradeDAO.persistir(grade);
		estruturaArvore.removerEstrutura(grade);
		usuarioController.setReseta(true);

		usuarioController.atualizarPessoaLogada();
	}

	public void alteraCampoPeriodo() {

		lgPeriodoDisciplina = !gradeDisciplina.getTipoDisciplina().equals(OBRIGATORIA);
	}

	public List<String> disciplinaCodigos(String codigo) {
		return disciplinaDAO.buscarTodosCodigosDisciplina(codigo.toUpperCase());
	}

	public List<Disciplina> disciplinaNomes(String codigo) {
		return disciplinaDAO.buscarTodosNomesDisciplinaObjeto(codigo.toUpperCase());
	}

	public List<Disciplina> disciplinaNomesEquivalencia(String codigo) {

		List<Disciplina> listaNomesSelecionados = new ArrayList<>();
		List<DisciplinaGradeDisciplina> newList = new ArrayList<>(listaObrigatorias);
		newList.addAll(listaEletivas);
		for (DisciplinaGradeDisciplina disciplinaGradeDisciplina : newList) {
			if (disciplinaGradeDisciplina.getDisciplina().getNome().contains(codigo.toUpperCase())) {
				listaNomesSelecionados.add(disciplinaGradeDisciplina.getDisciplina());
			}
		}
		return listaNomesSelecionados;
	}

	public List<Disciplina> disciplinaCodigoEquivalencia(String codigo) {

		List<Disciplina> listaNomesSelecionados = new ArrayList<>();
		List<DisciplinaGradeDisciplina> newList = new ArrayList<>(listaObrigatorias);
		newList.addAll(listaEletivas);
		for (DisciplinaGradeDisciplina disciplinaGradeDisciplina : newList) {
			if (disciplinaGradeDisciplina.getDisciplina().getCodigo().contains(codigo.toUpperCase())) {
				listaNomesSelecionados.add(disciplinaGradeDisciplina.getDisciplina());
			}
		}
		return listaNomesSelecionados;
	}

	public void limparDisciplina(){

		disciplina = new Disciplina();
		gradeDisciplina = new GradeDisciplina();
		lgCodigoDisciplina = false;
		lgNomeDisciplina = false;	
		lgCargaHorariaDisciplina = false;

	}

	@Transactional
	public void incluirDisciplinaNova(){

		disciplinaNova.setCodigo(disciplinaNova.getCodigo().toUpperCase());
		disciplinaNova.setNome(disciplinaNova.getNome().toUpperCase());
		disciplinaNova.setCodigo(disciplinaNova.getCodigo().trim());
		disciplinaNova.setNome(disciplinaNova.getNome().trim());
		Disciplina disciplinaExiste = disciplinaDAO.buscarPorCodigoDisciplina(disciplinaNova.getCodigo());
		if (disciplinaNova.getCodigo().equals("")) {
			FacesMessage msg = new FacesMessage("Preencha o campo Código!");
			facesContext.addMessage(null, msg);
			return;
		}
		if (disciplinaNova.getNome().equals("")) {
			FacesMessage msg = new FacesMessage("Preencha o campo Nome!");
			facesContext.addMessage(null, msg);
			return;
		}
		if (disciplinaNova.getCargaHoraria() == null) {
			FacesMessage msg = new FacesMessage("Preencha o campo Carga Horária!");
			facesContext.addMessage(null, msg);
			return;
		}
		if (disciplinaExiste != null){
			FacesMessage msg = new FacesMessage("Disciplina já existe");
			facesContext.addMessage(null, msg);
		}
		else {
			disciplinaDAO.persistir(disciplinaNova);
			FacesMessage msg = new FacesMessage("Disciplina cadastrada!");
			facesContext.addMessage(null, msg);
			disciplinaNova =  new Disciplina();
			
			Hibernate.initialize(grade.getGrupoAlunos());

			for(Aluno a: grade.getGrupoAlunos())
			{
				a.dadosAlterados();
			}
		}
	}

	public void onItemSelectCodigoDisciplina() {
		disciplina = disciplinaDAO.buscarPorCodigoDisciplina(disciplina.getCodigo());
		lgCodigoDisciplina = true;
		lgNomeDisciplina = true;	
		lgCargaHorariaDisciplina = true;
	}

	public void onItemSelectCodigoNome() {
		Disciplina disciplinaAux = disciplinaDAO.buscarPorCodigoDisciplina(disciplina.getCodigo());
		if (disciplinaAux != null){
			disciplina = disciplinaAux;
		}
		lgCodigoDisciplina = true;
	}

	public void onItemSelectNomeDisciplina() {

		lgCodigoDisciplina = true;
		lgNomeDisciplina = true;	
		lgCargaHorariaDisciplina = true;	
	}

	public void onItemSelectCodigoDisciplinaEquivalenciaUm() {

		lgCodigoDisciplinaEquivalenciaUm = true;
		lgNomeDisciplinaEquivalenciaUm = true;	
	}

	public void onItemSelectNomeDisciplinaEquivalenciaUm() {
		onItemSelectCodigoDisciplinaEquivalenciaUm();
	}

	public void onItemSelectDisciplinaIra() {

		lgDisciplinaIra = true;
	}

	public void onItemSelectCodigoDisciplinaEquivalenciaDois() {

		disciplinaEquivalenciaDois = disciplinaDAO.buscarPorCodigoDisciplina(disciplinaEquivalenciaDois.getCodigo());
		lgCodigoDisciplinaEquivalenciaDois = true;
		lgNomeDisciplinaEquivalenciaDois = true;	
	}

	public void onItemSelectNomeDisciplinaEquivalenciaDois() {

		lgCodigoDisciplinaEquivalenciaDois = true;
		lgNomeDisciplinaEquivalenciaDois = true;	
	}

	@Transactional
	public void incluirGradeDisciplina(){

		if (disciplina.getNome() == null){

			disciplina.setNome("");

		}

		if (disciplina.getCodigo().equals("")) {

			FacesMessage msg = new FacesMessage("Preencha o campo Código Disciplina!");
			facesContext.addMessage(null, msg);
			return;
		}

		if (disciplina.getId() == null
				&& (disciplina.getNome() == null || disciplina.getNome().equals(""))) {
			FacesMessage msg = new FacesMessage("Preencha o campo Nome Disciplina!");
			facesContext.addMessage(null, msg);
			return;
		}

		if (gradeDisciplina.getTipoDisciplina().equals("")) {

			FacesMessage msg = new FacesMessage("Preencha o campo Tipo Disciplina!");
			facesContext.addMessage(null, msg);
			return;
		}

		if (gradeDisciplina.getTipoDisciplina().equals(OBRIGATORIA) && gradeDisciplina.getPeriodo() == 0) {

			FacesMessage msg = new FacesMessage("Preencha o campo Período!");
			facesContext.addMessage(null, msg);
			return;

		}

		gradeDisciplina.setDisciplina(disciplina);
		gradeDisciplina.setGrade(grade);

		GradeDisciplina gdDisciplinaAntiga = gradeDisciplinaDAO.buscarPorDisciplinaGrade(grade.getId(), disciplina.getId());
		if (gdDisciplinaAntiga != null) {
			FacesMessage msg = new FacesMessage("Disciplina já cadastrada nesta grade!");
			facesContext.addMessage(null, msg);
			return;
		}

		if (!gradeDisciplina.getTipoDisciplina().equals(OBRIGATORIA)) {
			gradeDisciplina.setPeriodo((long) 0);
		}

		gradeDisciplina.setExcluirIra(false);
		gradeDisciplina = gradeDisciplinaDAO.persistir(gradeDisciplina);
		DisciplinaGradeDisciplina disciplinaGradeDisciplina = new DisciplinaGradeDisciplina();
		disciplinaGradeDisciplina.setDisciplina(disciplina);
		disciplinaGradeDisciplina.setGradeDisciplina(gradeDisciplina);

		if (gradeDisciplina.getTipoDisciplina().equals(OBRIGATORIA)) {
			listaObrigatorias.add(disciplinaGradeDisciplina);
		} else {
			listaEletivas.add(disciplinaGradeDisciplina);
		}

		ordenar.DisciplinaGradeDisciplinaOrdenarCodigo(listaObrigatorias);
		ordenar.DisciplinaGradeDisciplinaOrdenarPeriodo(listaObrigatorias);
		ordenar.DisciplinaGradeDisciplinaOrdenarCodigo(listaEletivas);

		disciplina = new Disciplina();
		gradeDisciplina = new GradeDisciplina();
		
		for(Aluno a: grade.getGrupoAlunos())
		{
			a.dadosAlterados();
		}

		lgCodigoDisciplina = false;
		lgNomeDisciplina = false;	
		lgCargaHorariaDisciplina = false;	
		estruturaArvore.removerEstrutura(grade);
		usuarioController.setReseta(true);

		resetDataTables();
	}	

	@Transactional
	public void deletarGradeDisciplina(){

		gradeDisciplinaDAO.remover(linhaSelecionada.getGradeDisciplina());
		listaEletivas.remove(linhaSelecionada);
		listaObrigatorias.remove(linhaSelecionada);
		estruturaArvore.removerEstrutura(grade);
		usuarioController.setReseta(true);

		atualizaGrids();
	}

	public void atualizaGrids() {
		resetDataTables();

		listaEquivalencia = new ArrayList<>();
		listaEletivas = new ArrayList<>();
		listaObrigatorias = new ArrayList<>();
		listaEquivalencia = equivalenciaDAO.buscarTodasEquivalenciasPorGrade(grade.getId());
		listaIra = new ArrayList<>();

		List<GradeDisciplina> todos = gradeDisciplinaDAO.buscarTodasGradeDisciplinaPorGrade(grade.getId());
		for (GradeDisciplina gradeDisciplinaSelecionada : todos) {
			if (gradeDisciplinaSelecionada.getExcluirIra() != null && gradeDisciplinaSelecionada.getExcluirIra()) {
				listaIra.add(gradeDisciplinaSelecionada);
			}
		}
		while (!todos.isEmpty()) {
			DisciplinaGradeDisciplina disciplinaGradeDisciplina = new DisciplinaGradeDisciplina();
			disciplinaGradeDisciplina.setDisciplina(todos.get(0).getDisciplina());
			disciplinaGradeDisciplina.setGradeDisciplina(todos.get(0));
			if (todos.get(0).getTipoDisciplina().equals(OBRIGATORIA)) {
				listaObrigatorias.add(disciplinaGradeDisciplina);
			} else {
				listaEletivas.add(disciplinaGradeDisciplina);
			}
			ordenar.DisciplinaGradeDisciplinaOrdenarCodigo(listaObrigatorias);
			ordenar.DisciplinaGradeDisciplinaOrdenarPeriodo(listaObrigatorias);
			ordenar.DisciplinaGradeDisciplinaOrdenarCodigo(listaEletivas);
			todos.remove(0);
		}

		listaEletivasSelecionada =  null;
		listaObrigatoriasSelecionada  =  null;
		listaIraSelecionados = null;

	}

	@Transactional
	public void incluiPreRequisitos (){
		PreRequisito preRequisito  = new PreRequisito();

		disciplinaPre = disciplinaDAO.buscarPorCodigoDisciplina(disciplinaPre.getCodigo());

		preRequisito.setDisciplina(disciplinaPre);

		preRequisito.setGradeDisciplina(linhaSelecionada.getGradeDisciplina());

		preRequisito.setTipo(tipoPre);

		if (disciplinaPre == null || disciplinaPre.getCodigo().equals("")) {

			FacesMessage msg = new FacesMessage("Preencha o campo Código!");
			facesContext.addMessage(null, msg);
			return;
		}

		if (tipoPre.equals("")) {

			FacesMessage msg = new FacesMessage("Selecione o Tipo de Pré-Requsito!");
			facesContext.addMessage(null, msg);
			return;
		}

		long idPreprocurado = preRequisitoDAO.buscarPorDisciplanaGradeId(linhaSelecionada.getGradeDisciplina().getId(), disciplinaPre.getId());

		if(idPreprocurado != 0){

			FacesMessage msg = new FacesMessage("Pre-Requisito já cadastrado!");
			facesContext.addMessage(null, msg);
			disciplinaPre = new Disciplina();
			tipoPre = "";
			return;
		}


		if(disciplinaPre.getCodigo().equals(linhaSelecionada.getDisciplina().getCodigo())){

			FacesMessage msg = new FacesMessage("Não é possível incluir como pré-requisito a própria disciplina!!");
			facesContext.addMessage(null, msg);
			disciplinaPre = new Disciplina();
			tipoPre = "";
			return;
		}

		preRequisito = preRequisitoDAO.persistir(preRequisito);

		listaPreRequisitos.add(preRequisito);

		disciplinaPre = new Disciplina();
		
		carregaPreRequisitos();
		
		estruturaArvore.removerEstrutura(grade);
		usuarioController.setReseta(true);

	}

	public void carregaPreRequisitos() {

		listaPreRequisitos = new ArrayList<>();

		StringBuilder pre = new StringBuilder();

		List<PreRequisito> todos = preRequisitoDAO.buscarPorTodosCodigoGradeDisc(linhaSelecionada.getGradeDisciplina().getId());
		while (!todos.isEmpty()) {
			pre.append(todos.get(0).getDisciplina().getCodigo()).append(" : ");
			listaPreRequisitos.add(todos.remove(0));
		}
		linhaSelecionada.getGradeDisciplina().setPreRequisitos(pre.toString());


	}

	@Transactional
	public void deletarPreRequisito(){

		logger.info("Deletando Pré-requisito: " + linhaSelecionadaPreRequisto.getDisciplina().getNome());
		preRequisitoDAO.remover(linhaSelecionadaPreRequisto);

		listaPreRequisitos.clear();

		carregaPreRequisitos();
		estruturaArvore.removerEstrutura(grade);
		usuarioController.setReseta(true);

	}

	@Transactional
	public void incluirDisciplinaIra() {
		GradeDisciplina gradeDiscIra = null;
		boolean achouDisciplinaIra = false;

		if (disciplinaIra.getCodigo() == null || disciplinaIra.getCodigo().equals("")) {

			FacesMessage msg = new FacesMessage("Insira uma disciplina para ser ignorada no calculo do IRA!!");
			facesContext.addMessage(null, msg);
			return;

		}

		for (GradeDisciplina gradeDisciplinaSelecionada : grade.getGrupoGradeDisciplina()) {
			if (gradeDisciplinaSelecionada.getDisciplina().getCodigo().equals(disciplinaIra.getCodigo())) {
				if (Boolean.TRUE.equals(gradeDisciplinaSelecionada.getExcluirIra())) {
					achouDisciplinaIra = true;
				} else {
					gradeDiscIra = gradeDisciplinaSelecionada;
				}
			}
		}

		if (achouDisciplinaIra) {
			FacesMessage msg = new FacesMessage("Esta Disciplina já foi incluída!!");
			facesContext.addMessage(null, msg);
			return;
		}

		if (gradeDiscIra != null) {
			gradeDiscIra.setExcluirIra(true);
			gradeDisciplinaDAO.persistir(gradeDiscIra);
			listaIra.add(gradeDiscIra);
			disciplinaIra = new Disciplina();
			lgDisciplinaIra = false;
			estruturaArvore.removerEstrutura(grade);
			usuarioController.setReseta(true);
		}
	}

	@Transactional
	public void incluiEquivalencia(){
		Equivalencia equivalencia = new Equivalencia();
		equivalencia.setDisciplinaGrade(disciplinaEquivalenciaUm);
		equivalencia.setDisciplinaEquivalente(disciplinaEquivalenciaDois);
		equivalencia.setGrade(grade);
		Equivalencia equivalenciaAuxiliar;
		equivalenciaAuxiliar = null;
		
		if (disciplinaEquivalenciaUm.getCodigo() == null || disciplinaEquivalenciaUm.getCodigo().equals("") || disciplinaEquivalenciaDois.getCodigo() == null || disciplinaEquivalenciaDois.getCodigo().equals("")){
			FacesMessage msg = new FacesMessage("Complete os dados da equivalência!!");
			facesContext.addMessage(null, msg);
			return;
		}

		for (Equivalencia equivalenciaQuestao : grade.getGrupoEquivalencia()){
			if (equivalenciaQuestao.getDisciplinaEquivalente().getCodigo().equals( disciplinaEquivalenciaUm.getCodigo())  && equivalenciaQuestao.getDisciplinaGrade().getCodigo().equals(disciplinaEquivalenciaDois.getCodigo())){
				equivalenciaAuxiliar = equivalenciaQuestao;
				break;
			}
		}
				
		if (equivalenciaAuxiliar != null){
			FacesMessage msg = new FacesMessage("Equivalncia já existe!!");
			facesContext.addMessage(null, msg);
			disciplinaEquivalenciaUm = new Disciplina();
			disciplinaEquivalenciaDois = new Disciplina();
			lgCodigoDisciplinaEquivalenciaDois = false;
			lgNomeDisciplinaEquivalenciaDois = false;	
			lgCodigoDisciplinaEquivalenciaUm = false;
			lgNomeDisciplinaEquivalenciaUm = false;	
			return;
		}

		if(disciplinaEquivalenciaDois.getCodigo().equals(disciplinaEquivalenciaUm.getCodigo()) ){
			FacesMessage msg = new FacesMessage("Não possível incluir equivalência da própria disciplina!!");
			facesContext.addMessage(null, msg);
			disciplinaEquivalenciaUm = new Disciplina();
			disciplinaEquivalenciaDois = new Disciplina();
			lgCodigoDisciplinaEquivalenciaDois = false;
			lgNomeDisciplinaEquivalenciaDois = false;	
			lgCodigoDisciplinaEquivalenciaUm = false;
			lgNomeDisciplinaEquivalenciaUm = false;	
			return;
		}

		
		equivalenciaDAO.persistir(equivalencia);	
		listaEquivalencia.add(equivalencia);
		disciplinaEquivalenciaUm = new Disciplina();
		disciplinaEquivalenciaDois = new Disciplina();
		lgCodigoDisciplinaEquivalenciaDois = false;
		lgNomeDisciplinaEquivalenciaDois = false;	
		lgCodigoDisciplinaEquivalenciaUm = false;
		lgNomeDisciplinaEquivalenciaUm = false;	
		estruturaArvore.removerEstrutura(grade);
		usuarioController.setReseta(true);
	}

	public void limpaDisciplinaIra(){
		disciplinaIra = new Disciplina();
		lgDisciplinaIra = false;
	}

	public void limpaEquivalencia(){
		disciplinaEquivalenciaUm = new Disciplina();
		disciplinaEquivalenciaDois = new Disciplina();
		lgCodigoDisciplinaEquivalenciaDois = false;
		lgNomeDisciplinaEquivalenciaDois = false;	
		lgCodigoDisciplinaEquivalenciaUm = false;
		lgNomeDisciplinaEquivalenciaUm = false;	

	}

	@Transactional
	public void deletarEquivalencia(){		
		equivalenciaDAO.remover(linhaSelecionadaEquivalencia);
		estruturaArvore.removerEstrutura(grade);
		usuarioController.setReseta(true);
		listaEquivalencia.remove(linhaSelecionadaEquivalencia);

		atualizaGrids();
	}
	
	@Transactional
	public void deletarDisciplinaIra(){		
		listaIra.remove(gradeDisciplinaIraSelecionada);
		gradeDisciplinaIraSelecionada.setExcluirIra(false);
		gradeDisciplinaDAO.persistir(gradeDisciplinaIraSelecionada);
		estruturaArvore.removerEstrutura(grade);
		usuarioController.setReseta(true);
	}

	//========================================================= GET - SET ==================================================================================//

	public Grade getGrade() {
		return grade;
	}

	public void setGrade(Grade grade) {
		this.grade = grade;
	}

	public boolean isLgHorasEletivas() {
		return lgHorasEletivas;
	}

	public void setLgHorasEletivas(boolean lgHorasEletivas) {
		this.lgHorasEletivas = lgHorasEletivas;
	}

	public boolean isLgHorasOpcionais() {
		return lgHorasOpcionais;
	}

	public void setLgHorasOpcionais(boolean lgHorasOpcionais) {
		this.lgHorasOpcionais = lgHorasOpcionais;
	}

	public boolean isLgHorasAoe() {
		return lgHorasAoe;
	}

	public void setLgHorasAoe(boolean lgHorasAoe) {
		this.lgHorasAoe = lgHorasAoe;
	}

	public boolean isLgIncluirGrade() {
		return lgIncluirGrade;
	}

	public void setLgIncluirGrade(boolean lgIncluirGrade) {
		this.lgIncluirGrade = lgIncluirGrade;
	}

	public boolean isLgCodigoGrade() {
		return lgCodigoGrade;
	}

	public void setLgCodigoGrade(boolean lgCodigoGrade) {
		this.lgCodigoGrade = lgCodigoGrade;
	}

	public Disciplina getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(Disciplina disciplina) {
		this.disciplina = disciplina;
	}

	public boolean isLgCodigoDisciplina() {
		return lgCodigoDisciplina;
	}

	public void setLgCodigoDisciplina(boolean lgCodigoDisciplina) {
		this.lgCodigoDisciplina = lgCodigoDisciplina;
	}

	public boolean isLgNomeDisciplina() {
		return lgNomeDisciplina;
	}

	public void setLgNomeDisciplina(boolean lgNomeDisciplina) {
		this.lgNomeDisciplina = lgNomeDisciplina;
	}

	public boolean isLgPeriodoDisciplina() {
		return lgPeriodoDisciplina;
	}

	public void setLgPeriodoDisciplina(boolean lgPeriodoDisciplina) {
		this.lgPeriodoDisciplina = lgPeriodoDisciplina;
	}

	public boolean isLgCargaHorariaDisciplina() {
		return lgCargaHorariaDisciplina;
	}

	public void setLgCargaHorariaDisciplina(boolean lgCargaHorariaDisciplina) {
		this.lgCargaHorariaDisciplina = lgCargaHorariaDisciplina;
	}

	public boolean isLgTipoDisciplina() {
		return lgTipoDisciplina;
	}

	public void setLgTipoDisciplina(boolean lgTipoDisciplina) {
		this.lgTipoDisciplina = lgTipoDisciplina;
	}

	public GradeDisciplina getGradeDisciplina() {
		return gradeDisciplina;
	}

	public void setGradeDisciplina(GradeDisciplina gradeDisciplina) {
		this.gradeDisciplina = gradeDisciplina;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isLgIncluirDisciplina() {
		return lgIncluirDisciplina;
	}

	public void setLgIncluirDisciplina(boolean lgIncluirDisciplina) {
		this.lgIncluirDisciplina = lgIncluirDisciplina;
	}

	public boolean isLgLimparDisciplina() {
		return lgLimparDisciplina;
	}

	public void setLgLimparDisciplina(boolean lgLimparDisciplina) {
		this.lgLimparDisciplina = lgLimparDisciplina;
	}

	public List<DisciplinaGradeDisciplina> getListaObrigatorias() {
		return listaObrigatorias;
	}

	public void setListaObrigatorias(
			List<DisciplinaGradeDisciplina> listaObrigatorias) {
		this.listaObrigatorias = listaObrigatorias;
	}

	public List<DisciplinaGradeDisciplina> getListaEletivas() {
		return listaEletivas;
	}

	public void setListaEletivas(List<DisciplinaGradeDisciplina> listaEletivas) {
		this.listaEletivas = listaEletivas;
	}

	public DisciplinaGradeDisciplina getLinhaSelecionada() {
		return linhaSelecionada;
	}

	public void setLinhaSelecionada(DisciplinaGradeDisciplina linhaSelecionada) {
		this.linhaSelecionada = linhaSelecionada;
	}

	public Disciplina getDisciplinaPre() {
		return disciplinaPre;
	}

	public void setDisciplinaPre(Disciplina disciplinaPre) {
		this.disciplinaPre = disciplinaPre;
	}

	public List<PreRequisito> getListaPreRequisitos() {
		return listaPreRequisitos;
	}

	public void setListaPreRequisitos(List<PreRequisito> listaPreRequisitos) {
		this.listaPreRequisitos = listaPreRequisitos;
	}

	public List<Equivalencia> getListaEquivalencia() {
		return listaEquivalencia;
	}

	public void setListaEquivalencia(List<Equivalencia> listaEquivalencia) {
		this.listaEquivalencia = listaEquivalencia;
	}

	public Disciplina getDisciplinaEquivalenciaUm() {
		return disciplinaEquivalenciaUm;
	}

	public void setDisciplinaEquivalenciaUm(Disciplina disciplinaEquivalenciaUm) {
		this.disciplinaEquivalenciaUm = disciplinaEquivalenciaUm;
	}

	public Disciplina getDisciplinaEquivalenciaDois() {
		return disciplinaEquivalenciaDois;
	}

	public void setDisciplinaEquivalenciaDois(Disciplina disciplinaEquivalenciaDois) {
		this.disciplinaEquivalenciaDois = disciplinaEquivalenciaDois;
	}

	public boolean isLgCodigoDisciplinaEquivalenciaUm() {
		return lgCodigoDisciplinaEquivalenciaUm;
	}

	public void setLgCodigoDisciplinaEquivalenciaUm(
			boolean lgCodigoDisciplinaEquivalenciaUm) {
		this.lgCodigoDisciplinaEquivalenciaUm = lgCodigoDisciplinaEquivalenciaUm;
	}

	public boolean isLgCodigoDisciplinaEquivalenciaDois() {
		return lgCodigoDisciplinaEquivalenciaDois;
	}

	public void setLgCodigoDisciplinaEquivalenciaDois(
			boolean lgCodigoDisciplinaEquivalenciaDois) {
		this.lgCodigoDisciplinaEquivalenciaDois = lgCodigoDisciplinaEquivalenciaDois;
	}

	public boolean isLgNomeDisciplinaEquivalenciaUm() {
		return lgNomeDisciplinaEquivalenciaUm;
	}

	public void setLgNomeDisciplinaEquivalenciaUm(
			boolean lgNomeDisciplinaEquivalenciaUm) {
		this.lgNomeDisciplinaEquivalenciaUm = lgNomeDisciplinaEquivalenciaUm;
	}

	public boolean isLgNomeDisciplinaEquivalenciaDois() {
		return lgNomeDisciplinaEquivalenciaDois;
	}

	public void setLgNomeDisciplinaEquivalenciaDois(
			boolean lgNomeDisciplinaEquivalenciaDois) {
		this.lgNomeDisciplinaEquivalenciaDois = lgNomeDisciplinaEquivalenciaDois;
	}

	public boolean isLgIncluirEquivalencia() {
		return lgIncluirEquivalencia;
	}

	public void setLgIncluirEquivalencia(boolean lgIncluirEquivalencia) {
		this.lgIncluirEquivalencia = lgIncluirEquivalencia;
	}

	public boolean isLgLimparEquivalencia() {
		return lgLimparEquivalencia;
	}

	public void setLgLimparEquivalencia(boolean lgLimparEquivalencia) {
		this.lgLimparEquivalencia = lgLimparEquivalencia;
	}

	public PreRequisito getLinhaSelecionadaPreRequisto() {
		return linhaSelecionadaPreRequisto;
	}

	public void setLinhaSelecionadaPreRequisto(
			PreRequisito linhaSelecionadaPreRequisto) {
		this.linhaSelecionadaPreRequisto = linhaSelecionadaPreRequisto;
	}

	public Equivalencia getLinhaSelecionadaEquivalencia() {
		return linhaSelecionadaEquivalencia;
	}

	public void setLinhaSelecionadaEquivalencia(
			Equivalencia linhaSelecionadaEquivalencia) {
		this.linhaSelecionadaEquivalencia = linhaSelecionadaEquivalencia;
	}

	public UsuarioController getUsuarioController() {
		return usuarioController;
	}

	public void setUsuarioController(UsuarioController usuarioController) {
		this.usuarioController = usuarioController;
	}


	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public String getTipoPre() {
		return tipoPre;
	}

	public void setTipoPre(String tipoPre) {
		this.tipoPre = tipoPre;
	}

	public boolean isLgExcluirGrade() {
		return lgExcluirGrade;
	}

	public void setLgExcluirGrade(boolean lgExcluirGrade) {
		this.lgExcluirGrade = lgExcluirGrade;
	}

	public boolean isLgMaxPeriodo() {
		return lgMaxPeriodo;
	}

	public void setLgMaxPeriodo(boolean lgMaxPeriodo) {
		this.lgMaxPeriodo = lgMaxPeriodo;
	}

	public EstruturaArvore getEstruturaArvore() {
		return estruturaArvore;
	}

	public void setEstruturaArvore(EstruturaArvore estruturaArvore) {
		this.estruturaArvore = estruturaArvore;
	}

	public Disciplina getDisciplinaNova() {
		return disciplinaNova;
	}

	public void setDisciplinaNova(Disciplina disciplinaNova) {
		this.disciplinaNova = disciplinaNova;
	}

	public List<Equivalencia> getListaEquivalenciaSelecionada() {
		return listaEquivalenciaSelecionada;
	}

	public void setListaEquivalenciaSelecionada(
			List<Equivalencia> listaEquivalenciaSelecionada) {
		this.listaEquivalenciaSelecionada = listaEquivalenciaSelecionada;
	}

	public List<DisciplinaGradeDisciplina> getListaObrigatoriasSelecionada() {
		return listaObrigatoriasSelecionada;
	}

	public void setListaObrigatoriasSelecionada(
			List<DisciplinaGradeDisciplina> listaObrigatoriasSelecionada) {
		this.listaObrigatoriasSelecionada = listaObrigatoriasSelecionada;
	}

	public List<DisciplinaGradeDisciplina> getListaEletivasSelecionada() {
		return listaEletivasSelecionada;
	}

	public void setListaEletivasSelecionada(
			List<DisciplinaGradeDisciplina> listaEletivasSelecionada) {
		this.listaEletivasSelecionada = listaEletivasSelecionada;
	}
	public Disciplina getDisciplinaIra() {
		return disciplinaIra;
	}
	public void setDisciplinaIra(Disciplina disciplinaIra) {
		this.disciplinaIra = disciplinaIra;
	}
	public boolean isLgDisciplinaIra() {
		return lgDisciplinaIra;
	}
	public void setLgDisciplinaIra(boolean lgDisciplinaIra) {
		this.lgDisciplinaIra = lgDisciplinaIra;
	}
	public List<GradeDisciplina> getListaIra() {
		return listaIra;
	}
	public void setListaIra(List<GradeDisciplina> listaIra) {
		this.listaIra = listaIra;
	}
	public List<GradeDisciplina> getListaIraSelecionados() {
		return listaIraSelecionados;
	}
	public void setListaIraSelecionados(List<GradeDisciplina> listaIraSelecionados) {
		this.listaIraSelecionados = listaIraSelecionados;
	}
	public GradeDisciplina getGradeDisciplinaIraSelecionada() {
		return gradeDisciplinaIraSelecionada;
	}
	public void setGradeDisciplinaIraSelecionada(
			GradeDisciplina gradeDisciplinaIraSelecionada) {
		this.gradeDisciplinaIraSelecionada = gradeDisciplinaIraSelecionada;
	}
}
