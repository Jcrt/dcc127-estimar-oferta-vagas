package br.ufjf.coordenacao.sistemagestaocurso.controller;

import br.ufjf.coordenacao.OfertaVagas.model.Class;
import br.ufjf.coordenacao.OfertaVagas.model.*;
import br.ufjf.coordenacao.sistemagestaocurso.controller.helpers.CursoHelper;
import br.ufjf.coordenacao.sistemagestaocurso.controller.helpers.IHorasCurricularesConsumerHelper;
import br.ufjf.coordenacao.sistemagestaocurso.controller.interfaces.IHorasCurricularesConsumer;
import br.ufjf.coordenacao.sistemagestaocurso.controller.util.CalculadorMateriasExcedentes;
import br.ufjf.coordenacao.sistemagestaocurso.controller.util.Ordenar;
import br.ufjf.coordenacao.sistemagestaocurso.controller.util.UsuarioController;
import br.ufjf.coordenacao.sistemagestaocurso.model.*;
import br.ufjf.coordenacao.sistemagestaocurso.model.estrutura.SituacaoDisciplina;
import br.ufjf.coordenacao.sistemagestaocurso.repository.AlunoRepository;
import br.ufjf.coordenacao.sistemagestaocurso.repository.DisciplinaRepository;
import br.ufjf.coordenacao.sistemagestaocurso.repository.EventoAceRepository;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.EstruturaArvore;
import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.ImportarArvore;
import br.ufjf.coordenacao.sistemagestaocurso.util.jpa.Transactional;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.*;

@Named
@ViewScoped
public class AlunoSituacaoController
        implements IHorasCurricularesConsumer, Serializable {
    private static final String APROVADO = "APROVADO";
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AlunoSituacaoController.class);

    private boolean lgNomeAluno = false;
    private boolean lgMatriculaAluno = false;
    private boolean lgAce = true;
    private boolean lgAluno = true;
    private transient Aluno aluno = new Aluno();
    private EventoAce eventosAce;
    private transient Curriculum curriculum;
    private ImportarArvore importador;
    private EstruturaArvore estruturaArvore;
    private float ira;
    private String classeEscolhida;
    private int periodo;
    private int horasObrigatorias;
    private int horasAceConcluidas;
    private int horasEletivasConcluidas;
    private int horasOpcionaisConcluidas;
    private int horasObrigatoriasConcluidas;
    private int percentualObrigatorias;
    private int percentualEletivas;
    private int percentualOpcionais;
    private int percentualAce;
    private int horasEletivas;
    private int horasOpcionais;
    private int horasACE;
    private List<EventoAce> listaEventosAceSelecionadas;
    private transient List<SituacaoDisciplina> listaDisciplinaEletivasSelecionadas;
    private transient List<SituacaoDisciplina> listaDisciplinaOpcionaisSelecionadas;
    private transient List<SituacaoDisciplina> listaDisciplinaObrigatoriasSelecionadas;
    private transient List<Historico> listaHistorico;
    private List<EventoAce> listaEventosAce;
    private transient List<SituacaoDisciplina> listaDisciplinaObrigatorias;
    private transient List<SituacaoDisciplina> listaDisciplinaEletivas;
    private transient List<SituacaoDisciplina> listaDisciplinaOpcionais;
    private transient Curso curso = new Curso();

    @Inject
    private DisciplinaRepository disciplinas;
    @Inject
    private EventoAceRepository eventosAceRepository;
    @Inject
    private EventoAce eventoAceSelecionado;
    @Inject
    private AlunoRepository alunos;
    @Inject
    private UsuarioController usuarioController;
    @Inject
    private FacesContext facesContext;

    public AlunoSituacaoController() {
        eventosAce = new EventoAce();
        listaHistorico = new ArrayList<>();
        listaEventosAce = new ArrayList<>();
        listaDisciplinaObrigatorias = new ArrayList<>();
        listaDisciplinaEletivas = new ArrayList<>();
        listaDisciplinaOpcionais = new ArrayList<>();
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @PostConstruct
    public void init() {
        try {
            estruturaArvore = EstruturaArvore.getInstance();
            Grade grade = new Grade();
            grade.setHorasEletivas(0);
            grade.setHorasOpcionais(0);
            grade.setHorasAce(0);
            aluno.setGrade(grade);

            usuarioController.atualizarPessoaLogada();

            if (usuarioController.getAutenticacao().getTipoAcesso().equals("aluno")) {
                aluno = alunos.buscarPorMatricula(usuarioController.getAutenticacao().getSelecaoIdentificador());
                lgMatriculaAluno = true;
                lgNomeAluno = true;
                lgAluno = false;

                if (aluno == null || aluno.getMatricula() == null) {
                    FacesMessage msg = new FacesMessage("Matrícula não cadastrada na base!");
                    facesContext.addMessage(null, msg);
                    return;
                }
                curso = aluno.getCurso();
                onItemSelectMatriculaAluno();

            } else {
                curso = usuarioController.getAutenticacao().getCursoSelecionado();
                if (curso.getGrupoAlunos().isEmpty()) {
                    FacesMessage msg = new FacesMessage("Nenhum aluno cadastrado no curso!");
                    facesContext.addMessage(null, msg);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public List<String> alunoMatricula(String codigo) {
        codigo = codigo.toUpperCase();
        List<String> todos = new ArrayList<>();
        for (Aluno alunoQuestao : curso.getGrupoAlunos()) {
            if (alunoQuestao.getMatricula().contains(codigo)) {
                todos.add(alunoQuestao.getMatricula());
            }
        }
        return todos;
    }

    public List<Aluno> alunoNome(String codigo) {
        codigo = codigo.toUpperCase();
        List<Aluno> todos = new ArrayList<>();
        for (Aluno alunoQuestao : curso.getGrupoAlunos()) {

            //Desconsiderando acentuação
            String nome = alunoQuestao.getNome();
            String nomeNormalizado = Normalizer.normalize(nome, Normalizer.Form.NFD);
            String nomeAscii = nomeNormalizado.replaceAll("[^\\p{ASCII}]", "");
            String codigoNormalizado = Normalizer.normalize(codigo, Normalizer.Form.NFD);
            String codigoAscii = codigoNormalizado.replaceAll("[^\\p{ASCII}]", "");

            if (nomeAscii.contains(codigoAscii)) {
                todos.add(alunoQuestao);
            }
        }
        return todos;
    }

    public void onItemSelectMatriculaAluno() {
        lgAce = false;
        lgNomeAluno = true;
        lgMatriculaAluno = true;

        aluno = CursoHelper.getAlunoFromGrupoAlunos(aluno, curso);
        logger.info("Aluno: " + aluno.getMatricula());
        importador = estruturaArvore.recuperarArvore(aluno.getGrade(), true);

        aluno.setDisciplinaRepository(disciplinas);
        aluno.setEventoAceRepository(eventosAceRepository);

        IHorasCurricularesConsumerHelper.setHorasCurriculares(this, aluno);
        IHorasCurricularesConsumerHelper.setHorasCurricularesConcluidas(this, aluno);

        curriculum = importador.get_cur();
        StudentsHistory sh = importador.getSh();
        Student st = sh.getStudents().get(aluno.getMatricula());

        if (st == null) {
            FacesMessage msg = new FacesMessage("O aluno:" + aluno.getMatricula() + " não tem nenhum histórico de matrícula cadastrado!");
            facesContext.addMessage(null, msg);
            return;
        }

        listaEventosAce = new ArrayList<>(eventosAceRepository.buscarPorMatricula(aluno.getMatricula()));
        horasAceConcluidas = eventosAceRepository.recuperarHorasConcluidasPorMatricula(aluno.getMatricula());

        gerarDadosAluno(st, curriculum);

        if (this.aluno.getSobraHorasEletivas() > 0) {
            List<SituacaoDisciplina> disciplinaSituacao = CalculadorMateriasExcedentes.getExcedentesEletivas(this.aluno.getGrade().getHorasEletivas(), this.listaDisciplinaEletivas);
            for (SituacaoDisciplina eletivaExtra : disciplinaSituacao) {
                listaDisciplinaOpcionais.add(eletivaExtra);
                listaDisciplinaEletivas.remove(eletivaExtra);
            }
        }

        periodo = aluno.getPeriodoCorrente(usuarioController.getAutenticacao().getSemestreSelecionado());
        ira = aluno.getIra();
        aluno.calcularCet();

        percentualObrigatorias = (int) CalculaPercentual(horasObrigatoriasConcluidas, horasObrigatorias);
        percentualEletivas = (int) CalculaPercentual(horasEletivasConcluidas, aluno.getGrade().getHorasEletivas());
        percentualOpcionais = (int) CalculaPercentual(horasOpcionaisConcluidas, aluno.getGrade().getHorasOpcionais());

        if (this.aluno.getSobraHorasOpcionais() > 0) {
            List<EventoAce> excedentesOpcionais = CalculadorMateriasExcedentes.getExcedentesOpcionais(this.aluno.getGrade().getHorasOpcionais(), this.listaDisciplinaOpcionais);
            for (EventoAce eventoAceExtra : excedentesOpcionais) {
                listaEventosAce.add(eventoAceExtra);
                for (SituacaoDisciplina d : listaDisciplinaOpcionais) {
                    if (d.getCodigo().equals(eventoAceExtra.getMatricula())) {
                        listaDisciplinaOpcionais.remove(d);
                        break;
                    }
                }
            }
            horasAceConcluidas += this.aluno.getSobraHorasOpcionais();
        }

        percentualAce = (int) CalculaPercentual(horasAceConcluidas, aluno.getGrade().getHorasAce());

        this.resetaDataTables();
    }

    public double CalculaPercentual(double numerador, double denominador) {
        double resultado;

        if (denominador > 0) {
            resultado = (numerador * 100) / denominador;
        } else {
            resultado = 0;
        }

        return resultado;
    }

    public void limpaAluno() {

        lgAce = true;
        lgNomeAluno = false;
        lgMatriculaAluno = false;
        eventosAce = new EventoAce();
        listaEventosAce = new ArrayList<>();
        listaDisciplinaEletivas = new ArrayList<>();
        listaDisciplinaOpcionais = new ArrayList<>();
        listaDisciplinaObrigatorias = new ArrayList<>();
        ira = 0;
        periodo = 0;
        horasObrigatorias = 0;
        horasAceConcluidas = 0;
        horasEletivasConcluidas = 0;
        horasOpcionaisConcluidas = 0;
        horasObrigatoriasConcluidas = 0;
        percentualObrigatorias = 0;
        percentualEletivas = 0;
        percentualOpcionais = 0;
        percentualAce = 0;
        Grade grade = new Grade();
        grade.setHorasEletivas(0);
        grade.setHorasOpcionais(0);
        grade.setHorasAce(0);
        aluno = new Aluno();
        aluno.setGrade(grade);
        this.resetaDataTables();
    }

    /**
     * Reseta todas as {@link DataTable}
     */
    public void resetaDataTables() {
        this.resetDataTable("form:gridObrigatorias");
        this.resetDataTable("form:gridEletivas");
        this.resetDataTable("form:gridOpcionais");
        this.resetDataTable("form:gridAce");
    }

    /**
     * Reseta a {@link DataTable} baseada na expressão de busca
     *
     * @param expressao A expressão de busca da {@link DataTable}
     */
    private void resetDataTable(String expressao) {
        DataTable dataTable = (DataTable) facesContext.getViewRoot().findComponent(expressao);
        dataTable.clearInitialState();
        dataTable.reset();
    }

    public void gerarDadosAluno(Student st, Curriculum cur) {
        HashMap<Class, ArrayList<String[]>> aprovado;
        listaDisciplinaObrigatorias = new ArrayList<>();
        listaDisciplinaEletivas = new ArrayList<>();
        listaDisciplinaOpcionais = new ArrayList<>();
        horasObrigatorias = 0;
        aprovado = new HashMap<>(st.getClasses(ClassStatus.APPROVED));
        TreeSet<String> naocompletado = new TreeSet<>();
        boolean lgPeriodoAtual = false;
        for (int i : cur.getMandatories().keySet()) {
            for (Class c : cur.getMandatories().get(i)) {
                horasObrigatorias = horasObrigatorias + c.getWorkload();
                if (!aprovado.containsKey(c)) {
                    if (!lgPeriodoAtual) {
                        aluno.setPeriodoReal(i);
                        lgPeriodoAtual = true;
                    }
                    naocompletado.add(c.getId());
                    SituacaoDisciplina disciplinaSituacao = new SituacaoDisciplina();
                    disciplinaSituacao.setSituacao("NAO APROVADO");
                    disciplinaSituacao.setCodigo(c.getId());
                    disciplinaSituacao.setPeriodo(Integer.toString(i));
                    disciplinaSituacao.setCargaHoraria(Integer.toString(c.getWorkload()));
                    disciplinaSituacao.setNome(disciplinas.buscarPorCodigoDisciplina(c.getId()).getNome());
                    String preRequisito = "";

                    for (Class cl : c.getPrerequisite()) {
                        preRequisito = cl.getId() + " : " + preRequisito;
                    }
                    for (Class cl : c.getCorequisite()) {
                        preRequisito = cl.getId() + " : " + preRequisito;
                    }
                    disciplinaSituacao.setListaPreRequisitos(preRequisito);
                    listaDisciplinaObrigatorias.add(disciplinaSituacao);
                } else {
                    SituacaoDisciplina disciplinaSituacao = new SituacaoDisciplina();
                    disciplinaSituacao.setCodigo(c.getId());
                    disciplinaSituacao.setSituacao(APROVADO);
                    disciplinaSituacao.setPeriodo(Integer.toString(i));
                    disciplinaSituacao.setCargaHoraria(Integer.toString(c.getWorkload()));
                    disciplinaSituacao.setNome(disciplinas.buscarPorCodigoDisciplina(c.getId()).getNome());
                    String preRequisito = "";
                    for (Class cl : c.getPrerequisite()) {
                        if (!preRequisito.contains(cl.getId())) {
                            preRequisito = cl.getId() + " : " + preRequisito;
                        }
                    }
                    disciplinaSituacao.setListaPreRequisitos(preRequisito);
                    listaDisciplinaObrigatorias.add(disciplinaSituacao);
                    aprovado.remove(c);
                }
            }
        }
        for (Class c : cur.getElectives()) {
            if (aprovado.containsKey(c)) {
                SituacaoDisciplina disciplinaSituacao = new SituacaoDisciplina();
                disciplinaSituacao.setCodigo(c.getId());
                disciplinaSituacao.setSituacao(APROVADO);
                logger.info(c.getId() + " eletiva");
                disciplinaSituacao.setCargaHoraria(Integer.toString(c.getWorkload()));
                disciplinaSituacao.setNome(disciplinas.buscarPorCodigoDisciplina(c.getId()).getNome());
                listaDisciplinaEletivas.add(disciplinaSituacao);
                aprovado.remove(c);
            }
        }

        Set<Class> ap = aprovado.keySet();
        Iterator<Class> i = ap.iterator();
        while (i.hasNext()) {
            Class c = i.next();
            ArrayList<String[]> classdata = aprovado.get(c);
            for (String[] s2 : classdata) {
                if (s2[1].equals("APR") || s2[1].equals("A")) {
                    EventoAce evento = new EventoAce();
                    horasAceConcluidas = horasAceConcluidas + c.getWorkload();
                    evento.setDescricao(disciplinas.buscarPorCodigoDisciplina(c.getId()).getNome());

                    Disciplina d = disciplinas.buscarPorCodigoDisciplina(c.getId());

                    if (d != null)
                        c.setWorkload(d.getCargaHoraria());

                    evento.setHoras((long) c.getWorkload());
                    evento.setPeriodo(Integer.parseInt(s2[0]));
                    evento.setExcluir(false);
                    listaEventosAce.add(evento);
                } else {
                    Disciplina opcional = disciplinas.buscarPorCodigoDisciplina(c.getId());
                    SituacaoDisciplina disciplinaSituacao = new SituacaoDisciplina();
                    disciplinaSituacao.setCodigo(c.getId());
                    disciplinaSituacao.setSituacao(APROVADO);
                    disciplinaSituacao.setCargaHoraria(opcional.getCargaHoraria().toString());
                    disciplinaSituacao.setNome(opcional.getNome());
                    listaDisciplinaOpcionais.add(disciplinaSituacao);
                }
            }
        }
    }

    @Transactional
    public void adicionarAce() {

        if (eventosAce.getPeriodo() == 0) {
            FacesMessage msg = new FacesMessage("Preencha o campo \"Período\"");
            facesContext.addMessage(null, msg);
            return;
        }
        eventosAce.setDescricao(eventosAce.getDescricao().trim());
        if (eventosAce.getDescricao().isEmpty()) {
            FacesMessage msg = new FacesMessage("Preencha o campo Descrição!");
            facesContext.addMessage(null, msg);
            return;
        }
        if (eventosAce.getHoras() == 0) {
            FacesMessage msg = new FacesMessage("Preencha o campo Carga Horária!");
            facesContext.addMessage(null, msg);
            return;
        }
        eventosAce.setDescricao(eventosAce.getDescricao().toUpperCase());
        eventosAce.setMatricula(aluno.getMatricula());

        logger.info(eventosAce.getDescricao().toUpperCase() + ";" + eventosAce.getHoras() + ";" + eventosAce.getPeriodo());

        eventosAce.setExcluir(true);


        eventosAceRepository.persistir(eventosAce);
        listaEventosAce.add(eventosAce);
        Ordenar ordenar = new Ordenar();
        ordenar.EventoAceOrdenarPeriodo(listaEventosAce);
        horasAceConcluidas = (int) (horasAceConcluidas + eventosAce.getHoras());
        if (aluno.getGrade().getHorasAce() != 0) {
            percentualAce = (horasAceConcluidas * 100 / aluno.getGrade().getHorasAce());
        }
        eventosAce = new EventoAce();
    }

    public void limparAce() {
        eventosAce = new EventoAce();
    }

    //========================================================= GET - SET ==================================================================================//

    @Transactional
    public void deletarAce() {
        eventosAceRepository.remover(eventoAceSelecionado);
        horasAceConcluidas = (int) (horasAceConcluidas - eventoAceSelecionado.getHoras());
        if (aluno.getGrade().getHorasAce() != 0) {
            percentualAce = (horasAceConcluidas * 100 / aluno.getGrade().getHorasAce());
        }
        listaEventosAce.remove(eventoAceSelecionado);
    }

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

    public List<Historico> getListaHistorico() {
        return listaHistorico;
    }

    public void setListaHistorico(List<Historico> listaHistorico) {
        this.listaHistorico = listaHistorico;
    }

    public String getClasseEscolhida() {
        return classeEscolhida;
    }

    public void setClasseEscolhida(String classeEscolhida) {
        this.classeEscolhida = classeEscolhida;
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

    public List<SituacaoDisciplina> getListaDisciplinaObrigatorias() {
        return listaDisciplinaObrigatorias;
    }

    public void setListaDisciplinaObrigatorias(
            List<SituacaoDisciplina> listaDisciplinaObrigatorias) {
        this.listaDisciplinaObrigatorias = listaDisciplinaObrigatorias;
    }

    public List<SituacaoDisciplina> getListaDisciplinaEletivas() {
        return listaDisciplinaEletivas;
    }

    public void setListaDisciplinaEletivas(
            List<SituacaoDisciplina> listaDisciplinaEletivas) {
        this.listaDisciplinaEletivas = listaDisciplinaEletivas;
    }

    public List<SituacaoDisciplina> getListaDisciplinaOpcionais() {
        return listaDisciplinaOpcionais;
    }

    public void setListaDisciplinaOpcionais(
            List<SituacaoDisciplina> listaDisciplinaOpcionais) {
        this.listaDisciplinaOpcionais = listaDisciplinaOpcionais;
    }

    @Override
    public int getHorasEletivasConcluidas() {
        return horasEletivasConcluidas;
    }

    @Override
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

    public List<SituacaoDisciplina> getListaDisciplinaObrigatoriasSelecionadas() {
        return listaDisciplinaObrigatoriasSelecionadas;
    }

    public void setListaDisciplinaObrigatoriasSelecionadas(
            List<SituacaoDisciplina> listaDisciplinaObrigatoriasSelecionadas) {
        this.listaDisciplinaObrigatoriasSelecionadas = listaDisciplinaObrigatoriasSelecionadas;
    }

    public List<SituacaoDisciplina> getListaDisciplinaEletivasSelecionadas() {
        return listaDisciplinaEletivasSelecionadas;
    }

    public void setListaDisciplinaEletivasSelecionadas(
            List<SituacaoDisciplina> listaDisciplinaEletivasSelecionadas) {
        this.listaDisciplinaEletivasSelecionadas = listaDisciplinaEletivasSelecionadas;
    }

    public List<SituacaoDisciplina> getListaDisciplinaOpcionaisSelecionadas() {
        return listaDisciplinaOpcionaisSelecionadas;
    }

    public void setListaDisciplinaOpcionaisSelecionadas(
            List<SituacaoDisciplina> listaDisciplinaOpcionaisSelecionadas) {
        this.listaDisciplinaOpcionaisSelecionadas = listaDisciplinaOpcionaisSelecionadas;
    }

    public List<EventoAce> getListaEventosAce() {
        return this.listaEventosAce;
    }

    public void setListaEventosAce(List<EventoAce> listaEventosAce) {
        this.listaEventosAce = listaEventosAce;
    }

    public List<EventoAce> getListaEventosAceSelecionadas() {
        return listaEventosAceSelecionadas;
    }

    public void setListaEventosAceSelecionadas(
            List<EventoAce> listaEventosAceSelecionadas) {
        this.listaEventosAceSelecionadas = listaEventosAceSelecionadas;
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

    public float getPercentualObrigatorias() {
        return percentualObrigatorias;
    }

    public void setPercentualObrigatorias(int percentualObrigatorias) {
        this.percentualObrigatorias = percentualObrigatorias;
    }

    public int getPercentualEletivas() {
        return percentualEletivas;
    }

    public void setPercentualEletivas(int percentualEletivas) {
        this.percentualEletivas = percentualEletivas;
    }

    public int getPercentualOpcionais() {
        return percentualOpcionais;
    }

    public void setPercentualOpcionais(int percentualOpcionais) {
        this.percentualOpcionais = percentualOpcionais;
    }

    public int getPercentualAce() {
        return percentualAce;
    }

    public void setPercentualAce(int percentualAce) {
        this.percentualAce = percentualAce;
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

    @Override
    public int getHorasEletivas() {
        return horasEletivas;
    }

    @Override
    public void setHorasEletivas(int horasEletivas) {
        this.horasEletivas = horasEletivas;
    }

    public int getHorasOpcionais() {
        return horasOpcionais;
    }

    public void setHorasOpcionais(int horasOpcionais) {
        this.horasOpcionais = horasOpcionais;
    }

    public int getHorasACE() {
        return horasACE;
    }

    public void setHorasACE(int horasACE) {
        this.horasACE = horasACE;
    }
}
