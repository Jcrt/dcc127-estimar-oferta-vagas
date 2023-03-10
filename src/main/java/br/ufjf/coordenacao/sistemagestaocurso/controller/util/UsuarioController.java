package br.ufjf.coordenacao.sistemagestaocurso.controller.util;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import br.ufjf.coordenacao.sistemagestaocurso.model.Aluno;
import br.ufjf.coordenacao.sistemagestaocurso.model.Curso;
import br.ufjf.coordenacao.sistemagestaocurso.model.PessoaCurso;
import br.ufjf.coordenacao.sistemagestaocurso.model.estrutura.Autenticacao;
import br.ufjf.coordenacao.sistemagestaocurso.repository.AlunoRepository;
import br.ufjf.coordenacao.sistemagestaocurso.repository.CursoRepository;
import br.ufjf.coordenacao.sistemagestaocurso.repository.PessoaRepository;
import br.ufjf.coordenacao.sistemagestaocurso.util.jpa.Transactional;

@Named
@SessionScoped
public class UsuarioController implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<PessoaCurso> listaPessoaCurso = new ArrayList<PessoaCurso>();
	private String senhaNova;
	private String senhaConf;
	private String senha;

	private Logger logger = Logger.getLogger(UsuarioController.class);
	
	@Inject
	private PessoaRepository pessoaDAO;

	@Inject
	private CursoRepository cursoDAO;

	private Autenticacao autenticacao = new Autenticacao();
	private List<String> periodos = new ArrayList<String>();
	// private EstruturaArvore estruturaArvore;
	private Boolean reseta = false;
	private Boolean matar = false;

	@Inject
	private FacesContext facesContext;
	@Inject
	private HttpServletRequest request;
	@Inject
	private HttpServletResponse response;
	@Inject
	private PerfilController perfilController;
	@Inject
	private EntityManager manager;
	@Inject
	private AlunoRepository alunos;
	@Inject
	private AutenticacaoController autenticacaoController;

	@PostConstruct
	public void init() {

	}

	@SuppressWarnings("unchecked")
	public void periodoIniciar() { // ok
		try {
			logger.info("Selecionando periodos baseado no hist??rico.");
			
			String query = "SELECT SEMESTRE_CURSADO FROM (SELECT SEMESTRE_CURSADO, COUNT( SEMESTRE_CURSADO ) AS matriculado FROM (SELECT SEMESTRE_CURSADO FROM Historico INNER JOIN Aluno AS a ON Historico.ID_MATRICULA = a.id WHERE ID_CURSO ="
					+ autenticacao.getCursoSelecionado().getId()
					+ " AND STATUS_DISCIPLINA = 'Matriculado' ) AS historico_curso GROUP BY SEMESTRE_CURSADO order by matriculado DESC, SEMESTRE_CURSADO DESC LIMIT 0,3) AS t";

			if(autenticacao.getTipoAcesso().equals("aluno")) {
				periodos.add(manager.createNativeQuery(query).getResultList().get(0).toString());
			} else {
				periodos = manager.createNativeQuery(query).getResultList();
			}
			autenticacao.setSemestreSelecionado(periodos.get(0));
		} catch (Exception e) {
			logger.warn("N??o foi poss??vel selecionar os periodos baseado nos dados de hist??rico. Ser?? usado um m??todo alternativo. Motivo: " + e.getMessage());
		} finally {
			if (periodos.size() < 1) {
				logger.info("Carregando peridos pelo m??todo do calendario.");
				Calendar now = Calendar.getInstance();
				int anoAtual = now.get(Calendar.YEAR);
				int mes = now.get(Calendar.MONTH);
				int periodoAtual = 0;
				if(mes >= 1 && mes <= 6){
					periodoAtual = 1;
				}
				else {
					periodoAtual = 3;
				}
				if (periodoAtual == 1){
					autenticacao.setSemestreSelecionado(Integer.toString(anoAtual) + "1") ;
					periodos.add(Integer.toString(anoAtual) + "1");
					if(!autenticacao.getTipoAcesso().equals("aluno"))
						periodos.add(Integer.toString(anoAtual-1) + "3");
				}
				else {
					autenticacao.setSemestreSelecionado(Integer.toString(anoAtual) + "3") ;			
					periodos.add(Integer.toString(anoAtual) + "3");
					if(!autenticacao.getTipoAcesso().equals("aluno"))
						periodos.add(Integer.toString(anoAtual) + "1");
				}
			}
			else if(periodos.size() == 1 && !autenticacao.getTipoAcesso().equals("aluno")) {
				String periodoAtual = periodos.get(0);
				int anoAtual = Integer.parseInt(periodoAtual.substring(0, 4));
				int periodo = Integer.parseInt(periodoAtual.substring(4));
				if (periodo == 1){
					periodos.add(Integer.toString(anoAtual-1) + "3");
				} else {
					periodos.add(Integer.toString(anoAtual) + "1");
				}
			}
		}
		
	}

	public void login() throws ServletException, IOException { // ok

		// autenticacao = new
		// Autenticacao(autenticacao.getLogin(),autenticacao.getSenha());

		if (validarLogin()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("/j_spring_security_check");
			dispatcher.forward(request, response);
			facesContext.responseComplete();
		}
	}

	public boolean validarLogin() throws IOException { // ok

		perfilController.setPerfil(null);
		autenticacao.setSenha(md5(autenticacao.getSenha()));
		if (autenticacao.getLogin() == null || autenticacao.getSenha() == null) {
			return false;
		}
		autenticacao = autenticacaoController.logar(autenticacao);
		if (autenticacao.getTipoAcesso().equals("acessoNegado")) {
			FacesMessage msg = new FacesMessage("Dados incorretos!");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return false;
		} else {
			carregarPerfis();
			periodoIniciar();
			return true;
		}
	}
	
	public boolean validarLogin(Autenticacao a) throws IOException { // ok;
		perfilController.setPerfil(null);
		a.setSenha(md5(a.getSenha()));
		if (a.getLogin() == null || a.getSenha() == null) {
			return false;
		}
		a = autenticacaoController.logar(a);
		if (a.getTipoAcesso().equals("acessoNegado")) {
			logger.info("acessoNegado");
			FacesMessage msg = new FacesMessage("Dados incorretos!");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return false;
		} else {
			facesContext.responseComplete();
			carregarPerfis();
			periodoIniciar();
			return true;
		}
	}


	public void logout() {
		/*
		 * System.out.println("Tentando...");
		 * SecurityContextHolder.getContext().setAuthentication(null);
		 * System.out.println("OK?" +
		 * SecurityContextHolder.getContext().getAuthentication());
		 */
	}
	/*
	public static String md5(String input) { // ok

		String md5 = null;
		if (null == input)
			return null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(input.getBytes(), 0, input.length());
			md5 = new BigInteger(1, digest.digest()).toString(16);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5;
	}
	*/
	
	public static String md5(String password) {
		HashCode md5 = Hashing.md5().hashString(password, Charsets.UTF_8);
		return md5.toString();
	}

	public void GerarConfirmacao() { // OK
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Usu??rio/Senha inv??lidos",
				"Mensagem de erro completa");
		context.addMessage("destinoErro", msg);
	}

	public void GerarErroConexao() { // OK
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Falha ao conectar com SIGA",
				"Mensagem de erro completa");
		context.addMessage("destinoErro", msg);
	}

	@Transactional
	public void mudarSenha() { // ok
		if (md5(senha).equals(autenticacao.getSenha())) {
			if (senhaNova.equals(senhaConf)) {
				if (autenticacao.getPessoa() != null) {
					autenticacao.getPessoa().setSenha(md5(senhaNova));
					pessoaDAO.alterarSenha(autenticacao.getPessoa());
					senha = "";
					senhaConf = "";
					senhaNova = "";
					FacesMessage msg = new FacesMessage("Senha alterada");
					FacesContext.getCurrentInstance().addMessage(null, msg);
					return;
				}
			} else {
				FacesMessage msg = new FacesMessage("A confirma????o de senha deve ser a mesma que a nova senha!");
				FacesContext.getCurrentInstance().addMessage(null, msg);
				return;
			}
		} else {
			FacesMessage msg = new FacesMessage("Senha atual incorreta");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return;
		}
	}

	public void carregarPerfis() { // ok

		if (autenticacao.getTipoAcesso().equals("coordenador")) {
			listaPessoaCurso = autenticacao.getPessoa().getPessoaCurso();
			for (PessoaCurso pessoaCurso : listaPessoaCurso) {
				autenticacao.getPerfisCursos().add(pessoaCurso.getCurso().getCodigo());
				if (autenticacao.getSelecaoIdentificador() == null) {
					autenticacao.setSelecaoIdentificador(autenticacao.getPessoa().getSiape());
					autenticacao.setSelecaoCurso(pessoaCurso.getCurso().getCodigo());
					autenticacao.setCursoSelecionado(pessoaCurso.getCurso());
				}
			}
		} else if (autenticacao.getTipoAcesso().equals("aluno")) {

			if (autenticacao.getSelecaoIdentificador() == null) {
				autenticacao.setSelecaoIdentificador(autenticacao.getPerfis().get(0));
				Aluno aluno = alunos.buscarPorMatricula(autenticacao.getSelecaoIdentificador());
				if (aluno != null && aluno.getCurso() != null) {
					autenticacao.setCursoSelecionado(aluno.getCurso());
				}
			}
		} else if (autenticacao.getTipoAcesso().equals("admin") || autenticacao.getTipoAcesso().equals("externo")) {

			listaPessoaCurso = autenticacao.getPessoa().getPessoaCurso();
			for (PessoaCurso pessoaCurso : listaPessoaCurso) {
				autenticacao.getPerfisCursos().add(pessoaCurso.getCurso().getCodigo());
				if (autenticacao.getSelecaoCurso() == null) {
					autenticacao.setSelecaoCurso(pessoaCurso.getCurso().getCodigo());
					autenticacao.setCursoSelecionado(pessoaCurso.getCurso());
				}
			}

		}

	}

	public void mudarCurso() throws Exception, IOException {
		if (autenticacao.getPessoa() != null) {
			for (PessoaCurso pessoaCurso : autenticacao.getPessoa().getPessoaCurso()) {
				if (pessoaCurso.getPessoa().getSiape().equals(autenticacao.getSelecaoIdentificador())) {

					if (!autenticacao.getTipoAcesso().equals("coordenador")) {

						autenticacao.setTipoAcesso("coordenador");

						Authentication auth = SecurityContextHolder.getContext().getAuthentication();
						List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
						authorities.add(new SimpleGrantedAuthority("coordenador"));
						Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(),
								auth.getCredentials(), authorities);
						SecurityContextHolder.getContext().setAuthentication(newAuth);
					}
				} else {

					if (!autenticacao.getTipoAcesso().equals("aluno")) {

						autenticacao.setTipoAcesso("aluno");
						Authentication auth = SecurityContextHolder.getContext().getAuthentication();
						List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
						authorities.remove(new SimpleGrantedAuthority("coordenador"));
						Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(),
								auth.getCredentials(), authorities);
						SecurityContextHolder.getContext().setAuthentication(newAuth);
					}

				}

			}
		}
		
		periodoIniciar();
	}

	public void mudarPeriodo() {
	}

	public void atualizarPessoaLogada() {

		if (reseta == true) {

			Curso cursoAtualizado = cursoDAO.porid(autenticacao.getCursoSelecionado().getId());
			autenticacao.setCursoSelecionado(cursoAtualizado);
			reseta = false;

			for (Aluno a : cursoAtualizado.getGrupoAlunos()) {
				a.dadosAlterados();
			}
		}
	}

	public void mudarCursoSelecionado() {

		for (PessoaCurso pessoaCursoQuestao : autenticacao.getPessoa().getPessoaCurso()) {

			if (pessoaCursoQuestao.getCurso().getCodigo().equals(autenticacao.getSelecaoCurso())) {

				Curso cursoAtualizado = cursoDAO.buscarPorCodigo(autenticacao.getSelecaoCurso());
				autenticacao.setCursoSelecionado(cursoAtualizado);

				return;

			}
		}
	}

	public List<PessoaCurso> getListaPessoaCurso() {
		return listaPessoaCurso;
	}

	public void setListaPessoaCurso(List<PessoaCurso> listaPessoaCurso) {
		this.listaPessoaCurso = listaPessoaCurso;
	}

	public String getSenhaNova() {
		return senhaNova;
	}

	public void setSenhaNova(String senhaNova) {
		this.senhaNova = senhaNova;
	}

	public String getSenhaConf() {
		return senhaConf;
	}

	public void setSenhaConf(String senhaConf) {
		this.senhaConf = senhaConf;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Autenticacao getAutenticacao() {
		return autenticacao;
	}

	public void setAutenticacao(Autenticacao autenticacao) {
		this.autenticacao = autenticacao;
	}

	public List<String> getPeriodos() {
		return periodos;
	}

	public void setPeriodos(List<String> periodos) {
		this.periodos = periodos;
	}

	public FacesContext getFacesContext() {
		return facesContext;
	}

	public void setFacesContext(FacesContext facesContext) {
		this.facesContext = facesContext;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public PerfilController getPerfilController() {
		return perfilController;
	}

	public void setPerfilController(PerfilController perfilController) {
		this.perfilController = perfilController;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Boolean getReseta() {
		return reseta;
	}

	public void setReseta(Boolean reseta) {
		this.reseta = reseta;
	}

	public Boolean getMatar() {

		SecurityContextHolder.getContext().setAuthentication(null);
		SecurityContextHolder.clearContext();

		return matar;
	}

	public void setMatar(Boolean matar) {
		this.matar = matar;
	}

}