package controller.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import model.Aluno;
import model.Curso;
import model.EventoAce;
import model.Grade;
import model.arvore.Curriculum;
import model.arvore.Student;
import model.arvore.StudentsHistory;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;

import controller.util.EstruturaArvore;
import controller.util.ImportarArvore;
import controller.util.UsuarioController;
import dao.Interface.CursoDAO;



@Named
@ViewScoped
public class GraficosIraAlunoController implements Serializable {

	//========================================================= VARIABLES ==================================================================================//

	private static final long serialVersionUID = 1L;
	private boolean lgNomeAluno  = false;
	private boolean lgMatriculaAluno = false;
	private boolean lgAce  = true;
	private boolean lgAluno = true;
	private Aluno aluno = new Aluno();
	private EventoAce eventosAce =  new EventoAce();
	private Curriculum curriculum;
	private ImportarArvore importador;
	private EstruturaArvore estruturaArvore;
	private int horasIncompletasOpcionais;
	private int horasIncompletasAce;
	private int horasIncompletasEletivas;
	private float ira;
	private int periodo;
	private int horasObrigatorias;
	private int horasAceConcluidas;	
	private int horasEletivasConcluidas;
	private int horasOpcionaisConcluidas;
	private int horasObrigatoriasConcluidas;
	private EventoAce eventoAceSelecionado;	
	private LineChartModel  lineChartModel;
	private Curso curso = new Curso();
	private CursoDAO cursoDAO ;

	//========================================================= METODOS ==================================================================================//

	@Inject
	private UsuarioController usuarioController;

	@PostConstruct
	public void init()  {
		try {
		lineChartModel = initBarModel();
		lineChartModel.setTitle("Gr�fico de Evolu��o do IRA por Per�odo");
		Axis yAxis = lineChartModel.getAxis(AxisType.Y);
		yAxis.setMin(0);
		yAxis.setMax(100);

		yAxis.setTickFormat("%d");
		estruturaArvore = EstruturaArvore.getInstance();
		usuarioController.atualizarPessoaLogada();
		Grade grade = new Grade();
		grade.setHorasEletivas(0);
		grade.setHorasOpcionais(0);
		grade.setHorasAce(0);
		aluno.setGrade(grade);
		cursoDAO = estruturaArvore.getCursoDAO();		
		if (usuarioController.getAutenticacao().getTipoAcesso().equals("aluno")){	
			List<Curso> listaCurso = (List<Curso>) cursoDAO.recuperarTodos();	
			for (Curso cursoQuestao : listaCurso){
				for (Aluno alunoQuestao : cursoQuestao.getGrupoAlunos()){
					if(alunoQuestao.getMatricula().contains(usuarioController.getAutenticacao().getSelecaoIdentificador())){
						aluno = alunoQuestao;
						break;
					}
				}	
			}		
			if (aluno.getMatricula() == null){
				FacesMessage msg = new FacesMessage("Matr�cula n�o cadastrada na base!");
				FacesContext.getCurrentInstance().addMessage(null, msg);
				lgMatriculaAluno = true;
				lgNomeAluno = true;	
				return;
			}			
			curso = aluno.getCurso();
			onItemSelectMatriculaAluno();
			lgAluno = false;
		}
		else{
			curso = usuarioController.getAutenticacao().getCursoSelecionado();
			if (curso.getGrupoAlunos().size() == 0){

				FacesMessage msg = new FacesMessage("Nenhum aluno cadastrado no curso!");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private LineChartModel initBarModel() {
		LineChartModel model = new LineChartModel();
		ChartSeries f = new ChartSeries();
		f.set("Semestre", 1);		
		model.addSeries(f);
		return model;
	}

	private LineChartModel gerarDados() {
		LineChartModel model = new LineChartModel();
		ChartSeries fPeriodo = new ChartSeries();
		ChartSeries fAcumulado = new ChartSeries();
		importador = estruturaArvore.recuperarArvore(aluno.getGrade(),false);
		curriculum = importador.get_cur();
		StudentsHistory sh = importador.getSh();		
		Student st = sh.getStudents().get(aluno.getMatricula());





		TreeSet<Integer> semestres = st.getCoursedSemesters();
		fPeriodo.setLabel( "IRA Per�odo");
		fAcumulado.setLabel( "IRA Acumulado");
		ira = st.getIRA();
		periodo = aluno.getPeriodoCorrente(usuarioController.getAutenticacao().getSemestreSelecionado());
		if(ira == -1) {
			ChartSeries f = new ChartSeries();
			ira = 0;
			f.setLabel( "IRA");
			f.set("Semestre", 1);		
			model.addSeries(f);
			return model;	

		}

		for(int i: semestres)
		{
			if (st.getSemesterIRA(i) != -1){
				fPeriodo.set( i, st.getSemesterIRA(i) );		
				fAcumulado.set(i,st.getIRA(i) );
			}		
		}


		model.addSeries(fPeriodo);
		model.addSeries(fAcumulado);
		return model;
	}

	public List<String> alunoMatricula(String codigo) {	
		codigo = codigo.toUpperCase();
		List<String> todos = new ArrayList<String>();
		for (Aluno alunoQuestao : curso.getGrupoAlunos()){
			if(alunoQuestao.getMatricula().contains(codigo)){
				todos.add(alunoQuestao.getMatricula()); 
			}
		}
		return todos;
	}

	public List<Aluno> alunoNome(String codigo) {
		codigo = codigo.toUpperCase();
		List<Aluno> todos = new ArrayList<Aluno>();
		for (Aluno alunoQuestao : curso.getGrupoAlunos()){
			if(alunoQuestao.getNome().contains(codigo)){
				todos.add(alunoQuestao); 
			}
		}
		return todos;
	}

	public void onItemSelectMatriculaAluno()  {
		for (Aluno alunoQuestao : curso.getGrupoAlunos()){
			if(alunoQuestao.getMatricula().contains(aluno.getMatricula())){
				aluno = alunoQuestao;
				break;
			}
		}

		if (aluno == null){
			FacesMessage msg = new FacesMessage("Matr�cula n�o cadastrada na base!");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			lgNomeAluno = true;	
			lgMatriculaAluno = true;
			return;
		}	


		lgAce = false;
		lgNomeAluno = true;	
		lgMatriculaAluno = true;

		if (aluno.getGrupoHistorico().size() == 0){

			FacesMessage msg = new FacesMessage("O aluno:" + aluno.getMatricula() + " n�o tem nenhum hist�rico de matricula cadastrado!");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return;


		}


		lineChartModel = gerarDados();
		lineChartModel.setTitle("Gr�fico de Evolu��o do IRA por Per�odo");
		lineChartModel.setLegendPosition("se");
		Axis yAxis = lineChartModel.getAxis(AxisType.Y);
		Axis xAxis = new CategoryAxis();
		lineChartModel.getAxes().put(AxisType.X, xAxis);
		lineChartModel.setDatatipFormat("%2$.2f");
		yAxis.setLabel("IRA");		
		yAxis.setTickFormat("%.2f");
		yAxis.setMin(0);
		yAxis.setMax(100);
	}

	public void limpaAluno(){		
		lgAce = true;
		lgNomeAluno  = false;
		lgMatriculaAluno = false;
		eventosAce =  new EventoAce();
		ira = 0;
		periodo = 0;
		horasObrigatorias = 0;
		horasAceConcluidas = 0;
		horasEletivasConcluidas = 0;
		horasOpcionaisConcluidas = 0;
		horasObrigatoriasConcluidas = 0;
		Grade grade = new Grade();
		grade.setHorasEletivas(0);
		grade.setHorasOpcionais(0);
		grade.setHorasAce(0);
		aluno = new Aluno();
		aluno.setGrade(grade);		
		init();
	}

	//========================================================= GET - SET ==================================================================================//


	public boolean isLgMatriculaAluno() {
		return lgMatriculaAluno;
	}

	public void setLgMatriculaAluno(boolean lgMatriculaAluno) {
		this.lgMatriculaAluno = lgMatriculaAluno;
	}

	public boolean isLgNomeAluno() {
		return lgNomeAluno;
	}

	public void setLgNomeAluno(boolean lgNomeAluno) {
		this.lgNomeAluno = lgNomeAluno;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public float getIra() {
		return ira;
	}

	public void setIra(float ira) {
		this.ira = ira;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public Curriculum getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(Curriculum curriculum) {
		this.curriculum = curriculum;
	}

	public ImportarArvore getImportador() {
		return importador;
	}

	public void setImportador(ImportarArvore importador) {
		this.importador = importador;
	}

	public int getHorasEletivasConcluidas() {
		return horasEletivasConcluidas;
	}

	public void setHorasEletivasConcluidas(int horasEletivasConcluidas) {
		this.horasEletivasConcluidas = horasEletivasConcluidas;
	}

	public int getHorasOpcionaisConcluidas() {
		return horasOpcionaisConcluidas;
	}

	public void setHorasOpcionaisConcluidas(int horasOpcionaisConcluidas) {
		this.horasOpcionaisConcluidas = horasOpcionaisConcluidas;
	}

	public int getHorasObrigatoriasConcluidas() {
		return horasObrigatoriasConcluidas;
	}

	public void setHorasObrigatoriasConcluidas(int horasObrigatoriasConcluidas) {
		this.horasObrigatoriasConcluidas = horasObrigatoriasConcluidas;
	}

	public int getHorasObrigatorias() {
		return horasObrigatorias;
	}

	public void setHorasObrigatorias(int horasObrigatorias) {
		this.horasObrigatorias = horasObrigatorias;
	}

	public EventoAce getEventosAce() {
		return eventosAce;
	}

	public void setEventosAce(EventoAce eventosAce) {
		this.eventosAce = eventosAce;
	}

	public int getHorasAceConcluidas() {
		return horasAceConcluidas;
	}

	public void setHorasAceConcluidas(int horasAceConcluidas) {
		this.horasAceConcluidas = horasAceConcluidas;
	}

	public boolean isLgAce() {
		return lgAce;
	}

	public void setLgAce(boolean lgAce) {
		this.lgAce = lgAce;
	}

	public EstruturaArvore getEstruturaArvore() {
		return estruturaArvore;
	}

	public void setEstruturaArvore(EstruturaArvore estruturaArvore) {
		this.estruturaArvore = estruturaArvore;
	}

	public EventoAce getEventoAceSelecionado() {
		return eventoAceSelecionado;
	}

	public void setEventoAceSelecionado(EventoAce eventoAceSelecionado) {
		this.eventoAceSelecionado = eventoAceSelecionado;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public UsuarioController getUsuarioController() {
		return usuarioController;
	}

	public void setUsuarioController(UsuarioController usuarioController) {
		this.usuarioController = usuarioController;
	}

	public boolean isLgAluno() {
		return lgAluno;
	}

	public void setLgAluno(boolean lgAluno) {
		this.lgAluno = lgAluno;
	}

	public int getHorasIncompletasOpcionais() {
		return horasIncompletasOpcionais;
	}

	public void setHorasIncompletasOpcionais(int horasIncompletasOpcionais) {
		this.horasIncompletasOpcionais = horasIncompletasOpcionais;
	}

	public int getHorasIncompletasAce() {
		return horasIncompletasAce;
	}

	public void setHorasIncompletasAce(int horasIncompletasAce) {
		this.horasIncompletasAce = horasIncompletasAce;
	}

	public int getHorasIncompletasEletivas() {
		return horasIncompletasEletivas;
	}

	public void setHorasIncompletasEletivas(int horasIncompletasEletivas) {
		this.horasIncompletasEletivas = horasIncompletasEletivas;
	}

	public LineChartModel getLineChartModel() {
		return lineChartModel;
	}

	public void setLineChartModel(LineChartModel lineChartModel) {
		this.lineChartModel = lineChartModel;
	}
}