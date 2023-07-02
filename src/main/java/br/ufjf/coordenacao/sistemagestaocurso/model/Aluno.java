package br.ufjf.coordenacao.sistemagestaocurso.model;

import br.ufjf.coordenacao.OfertaVagas.model.Class;
import br.ufjf.coordenacao.OfertaVagas.model.*;
import br.ufjf.coordenacao.sistemagestaocurso.providers.IEstruturaArvoreProvider;
import br.ufjf.coordenacao.sistemagestaocurso.providers.IFacesContextProvider;
import br.ufjf.coordenacao.sistemagestaocurso.repository.DisciplinaRepository;
import br.ufjf.coordenacao.sistemagestaocurso.repository.EventoAceRepository;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.EstruturaArvore;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.ImportarArvore;

import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.persistence.*;
import java.util.*;

@Entity
@SequenceGenerator(name="aluno_sequencia", sequenceName="aluno_seq", allocationSize=1)  
public class Aluno implements Cloneable {
	// ==========================VARI√ÉÔøΩVEIS=================================================================================================================//

	private Long id;
	private String matricula;
	private Curso curso;
	private Grade grade;
	private String nome;
	private String email;
	private Float ira;
	private Integer periodoReal;
	private List<Historico> grupoHistorico;
	private List<IRA> iras;
	private int horasObrigatoriasCompletadas;
	private int horasEletivasCompletadas;
	private List<Disciplina> disciplinasEletivasCompletadas;
	private int sobraHorasEletivas;
	private int horasOpcionaisCompletadas;
	private int horasAceConcluidas;
	private List<Disciplina> disciplinasOpcionaisCompletadas;
	private int sobraHorasOpcionais;
	private boolean horasCalculadas;
	private List<String> ultimosTresSemestres = new ArrayList<String>();
	private int cet;
	private boolean emAcompanhamentoAcademico;
	private List<Float> iraUltimosTresSemestres = new ArrayList<Float>();

	private DisciplinaRepository disciplinaRepository;
	private EventoAceRepository eventoAceRepository;

	@Inject
	private IFacesContextProvider facesContextProvider;

	@Inject
	private IEstruturaArvoreProvider estruturaArvoreProvider;

	// ==========================GETTERS_AND_SETTERS======================================================================================================//

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "aluno_sequencia")
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	@Column (name="MATRICULA", unique = true, nullable = false)
	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}


	@Column(name="NOME")
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}	
	
	@Column(name="EMAIL")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}	


	@Transient
	public String getPeriodoIngresso() {

		String periodoInicioLocal = "";
		if (matricula != null){
			periodoInicioLocal =  matricula.substring(0,4) ;
		}		
		return periodoInicioLocal;
	}

	@ManyToOne
	@JoinColumn(name="ID_CURSO" , referencedColumnName="ID")
	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	@ManyToOne
	@JoinColumn(name="ID_GRADE" , referencedColumnName="ID",nullable = false)
	public Grade getGrade() {
		return grade;
	}

	public void setGrade(Grade grade) {
		this.grade = grade;
	}
	
	@OneToMany(mappedBy = "aluno", targetEntity = IRA.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public List<IRA> getIras(){
		return iras;
	}

	@OneToMany(mappedBy = "aluno", targetEntity = Historico.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public List<Historico> getGrupoHistorico() {
		return grupoHistorico;
	}
	

	@Transient
	public List<Historico> getGrupoHistorico(String statusDisciplina) {
		List<Historico> historicos = new ArrayList<Historico>();
		for(Historico h : this.getGrupoHistorico()) {
			if(statusDisciplina.equals(h.getStatusDisciplina()))
				historicos.add(h);
		}
		return historicos;
	}

	public void setGrupoHistorico(List<Historico> grupoHistorico) {
		this.grupoHistorico = grupoHistorico;
	}

	@Transient
	public Integer getPeriodoCorrente(String periodoAtual) {

		String periodoInicioLocal;
		if (grade.getPeriodoInicio() == 0){
			periodoInicioLocal = matricula.substring(0,4) + "1";
		}
		else {
			periodoInicioLocal =  matricula.substring(0,4)  + grade.getPeriodoInicio();
		}
		return periodoCorrente( periodoInicioLocal,periodoAtual);
	}

	@Transient
	public Integer getPeriodoReal() {
		return periodoReal;
	}

	public void setPeriodoReal(Integer periodoReal) {
		this.periodoReal = periodoReal;
	}

	@Column(name="IRA")
	public Float getIra() {
		return ira;
	}


	public void setIra(Float ira) {
		this.ira = ira;
	}

	public int periodoCorrente(String ingresso,String semestreAtual){
		
		int i = 1;
		
		int anoAtual = Integer.parseInt(semestreAtual.substring(0, 4));
		int periodoAtual = Integer.parseInt(semestreAtual.substring(4, 5));
		
		int anoIngresso = Integer.parseInt(ingresso.substring(0, 4));
		int periodoIngresso = Integer.parseInt(ingresso.substring(4, 5));
		while( anoIngresso != anoAtual || periodoAtual != periodoIngresso  ){
			i++;
			if (periodoIngresso == 3){
				anoIngresso++;
				periodoIngresso = 1;
			}
			else{
				periodoIngresso = 3;
			}
		}
		return i;
	}

	@Transient
	public String getTurma() {

		return matricula.substring(0,4);
	}
	
	@Transient
	public void calculaHorasCompletadas()
	{
		this.disciplinasOpcionaisCompletadas = new ArrayList<Disciplina>();
		this.disciplinasEletivasCompletadas = new ArrayList<Disciplina>();
		
		this.horasObrigatoriasCompletadas = 0;
		this.horasEletivasCompletadas = 0;
		this.horasOpcionaisCompletadas = 0;
		this.sobraHorasEletivas = 0;
		this.horasCalculadas = false;

		ImportarArvore importador;
		EstruturaArvore estruturaArvore = estruturaArvoreProvider.provide();
		importador = estruturaArvore.recuperarArvore(this.grade,false);
		
		Curriculum cur = importador.get_cur();
		StudentsHistory sh = importador.getSh();		
		Student st = sh.getStudents().get(this.getMatricula());
		
		if (st == null){
			FacesMessage msg = new FacesMessage("O aluno:" + this.getMatricula() + " não tem nenhum histórico de matricula cadastrado!");
			facesContextProvider.provide().addMessage(null, msg);
			return;
		}
		
		HashMap<Class, ArrayList<String[]>> aprovado = new HashMap<Class, ArrayList<String[]>>(st.getClasses(ClassStatus.APPROVED));
		
		//++
		List<Disciplina> disciplinas = disciplinaRepository.listarTodos();
				
		for(int i: cur.getMandatories().keySet())
		{
			for(Class c: cur.getMandatories().get(i))
			{
				if(aprovado.containsKey(c)){
					Disciplina d = null;
					for(Disciplina disciplina: disciplinas) {
						if(disciplina.getCodigo().equals(c.getId())) {
							d = disciplina;
						}
					}
						
					if(d != null)
					{
						c.setWorkload(d.getCargaHoraria());
					}
					
					this.horasObrigatoriasCompletadas += c.getWorkload();
					aprovado.remove(c);
				}				
			}
		}
		
		for(Class c: cur.getElectives()) {
			if(aprovado.containsKey(c))	{
				this.horasEletivasCompletadas += c.getWorkload();
				Disciplina d = null;
				for(Disciplina disciplina: disciplinas) {
					if(disciplina.getCodigo().equals(c.getId())) {
						d = disciplina;
						this.disciplinasEletivasCompletadas.add(d);
					}
				}
				
				aprovado.remove(c);
			}
		}
		
		Set<Class> ap = aprovado.keySet();
		Iterator<Class> i = ap.iterator();
		while(i.hasNext()){
			Class c = i.next();
			for(String[] s2: aprovado.get(c))	{
				if (s2[1].equals("APR") || s2[1].equals("A")){
					Disciplina d = null;
					for(Disciplina disciplina: disciplinas) {
						if(disciplina.getCodigo().equals(c.getId())) {
							d = disciplina;
						}
					}
					
					if(d != null)
						c.setWorkload(d.getCargaHoraria());
					horasAceConcluidas += c.getWorkload();
				}
				else
				{
					for(Disciplina disciplina: disciplinas) {
						if(disciplina.getCodigo().equals(c.getId())) {
							horasOpcionaisCompletadas += disciplina.getCargaHoraria();
							this.disciplinasOpcionaisCompletadas.add(disciplina);
						}
					}
				}
			}
		}

		
		if(this.horasEletivasCompletadas > this.grade.getHorasEletivas())
		{
			this.sobraHorasEletivas = this.horasEletivasCompletadas - this.grade.getHorasEletivas();
			this.horasEletivasCompletadas -= this.sobraHorasEletivas;
			this.horasOpcionaisCompletadas += this.sobraHorasEletivas;
			
			//--
		}
		
		if(this.horasOpcionaisCompletadas > this.grade.getHorasOpcionais())
		{
			this.sobraHorasOpcionais = this.horasOpcionaisCompletadas - this.grade.getHorasOpcionais();
			this.horasOpcionaisCompletadas -= this.sobraHorasOpcionais;
			this.horasAceConcluidas += this.sobraHorasOpcionais;
			
			//--
		}
		
		//-----------------------------
		this.horasCalculadas = true;
	}
	

	@Transient
	public int getHorasObrigatoriasCompletadas() {
		if(!this.horasCalculadas)
			this.calculaHorasCompletadas();
			
		return this.horasObrigatoriasCompletadas;
	}

	@Transient
	public int getHorasEletivasCompletadas() {
		if(!this.horasCalculadas)
			this.calculaHorasCompletadas();
		
		return this.horasEletivasCompletadas;
	}

	@Transient
	public int getHorasOpcionaisCompletadas() {
		if(!this.horasCalculadas)
			this.calculaHorasCompletadas();
		
		return this.horasOpcionaisCompletadas;
	}
	
	@Transient
	public int getSobraHorasEletivas()
	{
		if(!this.horasCalculadas)
			this.calculaHorasCompletadas();
		
		return this.sobraHorasEletivas;
	}
	
	@Transient
	public int getHorasAceConcluidas() {		
		if(!this.horasCalculadas)
			this.calculaHorasCompletadas();
		
		int aceCadastradas = 0;
		
		List<EventoAce> eventos = this.eventoAceRepository.buscarPorMatricula(this.getMatricula());
		
		for(EventoAce evento: eventos)
		{
			aceCadastradas += evento.getHoras();
		}
				
		this.horasAceConcluidas =  Math.min((horasAceConcluidas + aceCadastradas), this.grade.getHorasAce());
		
		return this.horasAceConcluidas;
	}
	
	@Transient
	public int getSobraHorasOpcionais()
	{
		if(!this.horasCalculadas)
			this.calculaHorasCompletadas();
		
		return this.sobraHorasOpcionais;
	}

	@Transient
	public void dadosAlterados()
	{
		this.horasCalculadas = false;
	}


	public void setIras(List<IRA> iras) {
		this.iras = iras;
	}
	
	@Transient
	public void setEventoAceRepository(EventoAceRepository eventoAceRepository) {
		this.eventoAceRepository = eventoAceRepository;
	}
	
	@Transient
	public void setDisciplinaRepository(DisciplinaRepository disciplinaRepository) {
		this.disciplinaRepository = disciplinaRepository;
	}
	
	@Transient
	public List<String> getUltimosTresSemestres() {
		return ultimosTresSemestres;
	}


	public void setUltimosTresSemestres(List<String> ultimosTresSemestres) {
		this.ultimosTresSemestres = ultimosTresSemestres;
	}
	
	@Transient
	public int getCet() {
		return cet;
	}
	
	public void setCet(int cet) {
		this.cet = cet;
	}

	@Transient
	public boolean isEmAcompanhamentoAcademico() {
		return emAcompanhamentoAcademico;
	}


	public void setEmAcompanhamentoAcademico(boolean emAcompanhamentoAcademico) {
		this.emAcompanhamentoAcademico = emAcompanhamentoAcademico;
	}
	
	@Transient
	public List<Float> getIraUltimosTresSemestres() {
		return iraUltimosTresSemestres;
	}
	
	public void setIraUltimosTresSemestres(List<Float> iraUltimosTresSemestres) {
		this.iraUltimosTresSemestres = iraUltimosTresSemestres;
	}
	
	public void buscarUltimosTresSemestres() {		
		for (ListIterator<Historico> it = this.getGrupoHistorico().listIterator(this.getGrupoHistorico().size()); it.hasPrevious() && this.ultimosTresSemestres.size() < 3;) {
			Historico historico = it.previous();
			if (!historico.getStatusDisciplina().equals("Matriculado") && !historico.getStatusDisciplina().equals("Trancado") && !historico.getStatusDisciplina().equals("Cancelado") && !this.ultimosTresSemestres.contains(historico.getSemestreCursado())) {
					this.ultimosTresSemestres.add(historico.getSemestreCursado());
			}
		}
	}
	
	public void calcularCet() {
		this.cet = 0;
		
		if (this.ultimosTresSemestres.isEmpty()) {
			this.buscarUltimosTresSemestres();
		}
				
		if (ultimosTresSemestres.size() == 3) {
			List<EventoAce> eventosAce = this.eventoAceRepository.buscarPorMatricula(this.getMatricula());
			
			for (Historico historico : this.getGrupoHistorico()) {
				if (ultimosTresSemestres.contains(historico.getSemestreCursado()) && historico.getStatusDisciplina().equals("Aprovado")) {
					this.cet = this.cet + historico.getDisciplina().getCargaHoraria();
				}					
			}
			
			for (EventoAce eventoAce : eventosAce) {
				if (ultimosTresSemestres.contains(eventoAce.getPeriodo().toString())) {
					this.cet = (int) (this.cet + eventoAce.getHoras());
				}
			}
		}
	}
	
	public void verificarAcompanhamentoAcademico() {
		int cargaHorariaTotal = this.getGrade().getHorasObrigatorias() + this.getGrade().getHorasEletivas() + this.getGrade().getHorasOpcionais() + this.getGrade().getHorasAce();
		
		int numeroMedioPeriodos = 1;
		for (GradeDisciplina disciplina : this.getGrade().getGrupoGradeDisciplina()) {
			if (disciplina.getPeriodo() > numeroMedioPeriodos) {
				numeroMedioPeriodos = disciplina.getPeriodo().intValue();
			}
		}
		
		int chm = cargaHorariaTotal / numeroMedioPeriodos;
		
		this.calcularCet();

		this.emAcompanhamentoAcademico = this.cet < 1.5 * chm;
	}
	
	public void calcularIraUltimosTresSemestres() {
		if (this.ultimosTresSemestres.isEmpty()) {
			this.buscarUltimosTresSemestres();
		}
		
		if (this.ultimosTresSemestres.size() == 3) {
			for (IRA ira : this.getIras()) {
				if (this.ultimosTresSemestres.contains(ira.getSemestre())) {
					this.iraUltimosTresSemestres.add(ira.getIraSemestre());
				}
			}
		}
	}
}