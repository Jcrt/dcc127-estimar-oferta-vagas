package br.ufjf.coordenacao.sistemagestaocurso.utils;

import br.ufjf.coordenacao.OfertaVagas.model.Class;
import br.ufjf.coordenacao.OfertaVagas.model.*;
import br.ufjf.coordenacao.sistemagestaocurso.enums.DisciplinaStatus;
import br.ufjf.coordenacao.sistemagestaocurso.enums.DisciplinaTipo;
import br.ufjf.coordenacao.sistemagestaocurso.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Mocks {
	public static String aprovadoText = "APROVADO";
	public static String reprovadoText = "REPROVADO";

	public static String cursoCodigo = "76";
	public static String cursoNome = "Sistemas de informação";
	public static Long cursoId = 1L;

	public static String cursoDataAtualizacao = "2016-01-01 00:00:00";
	public static String alunoMatricula = "2013760";
	public static Long periodoDisciplina = 1L;
	public static String codigoGrade = "Grade1";
	public static String curriculumId = "2013";
	public static int curriculumSemester = 1;

	Curso curso;
	Grade grade;
	StudentsHistory studentsHistory;
	Curriculum curriculum;
	List<Student> students = new ArrayList<>();
	List<Class> classes = new ArrayList<>();
	List<Pessoa> pessoas = new ArrayList<>();
	List<PessoaCurso> pessoasCurso = new ArrayList<>();
	List<Aluno> alunos = new ArrayList<>();
	List<Disciplina> disciplinas = new ArrayList<>();
	List<GradeDisciplina> gradeDisciplinas = new ArrayList<>();
	HashMap<Long, List<Historico>> historicos = new HashMap<>();

	public Mocks() {
		this.PopulatePessoas();
		this.PopulateCurso();
		this.PopulatePessoaCurso();
		this.PopulateGrade();
		this.PopulateAlunos();
		this.PopulateDisciplinas();
		this.PopulateGradeDisciplina();
		this.populateClasses();
		this.populateCurriculums();
		this.PopulateStudent();
		this.PopulateStudentsHistory();


		this.setGrupoAlunos();
		this.setGrupoGrades();
		this.setGrupoGradesDisciplina();
		this.setPessoaCurso();
		this.setCurriculumClasses();
		this.setCurriculumStudentRelation();
		this.PopulateAndSetHistoricos();
		this.setStudentsHistoryStudents();
	}

	public Curso getCurso() {
		return curso;
	}

	public List<Aluno> getAlunos() {
		return this.alunos;
	}

	public Grade getGrade() {
		return grade;
	}

	public List<Historico> getHistoricos() {
		List<Historico> todosHistoricos = new ArrayList<>();
		Collection<List<Historico>> historicosList = historicos.values();
		historicosList.forEach(todosHistoricos::addAll);
		return todosHistoricos;
	}

	public List<Disciplina> getDisciplinas() {
		return disciplinas;
	}

	public List<GradeDisciplina> getGradeDisciplinas() {
		return gradeDisciplinas;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	private void addStatusToMap(long alunoId, DisciplinaStatus status, HashMap<Long, DisciplinaStatus[]> mapAlunoDisciplina) {
		DisciplinaStatus[] statusArray = new DisciplinaStatus[10];
		Arrays.fill(statusArray, status);
		mapAlunoDisciplina.put(alunoId, statusArray);
	}

	private HashMap<Long, DisciplinaStatus[]> createMapStatusDisciplinaPorAluno() {
		HashMap<Long, DisciplinaStatus[]> mapAlunoDisciplina = new HashMap<>();
		addStatusToMap(1L, DisciplinaStatus.APPROVED, mapAlunoDisciplina);
		addStatusToMap(2L, DisciplinaStatus.REPPROVED_BY_FREQUENCY, mapAlunoDisciplina);
		addStatusToMap(3L, DisciplinaStatus.REPPROVED_BY_SCORE, mapAlunoDisciplina);
		addStatusToMap(4L, DisciplinaStatus.LOCKED, mapAlunoDisciplina);
		addStatusToMap(5L, DisciplinaStatus.CANCELLED, mapAlunoDisciplina);
		addStatusToMap(6L, DisciplinaStatus.DISPENSED, mapAlunoDisciplina);
		addStatusToMap(7L, DisciplinaStatus.ENROLLED, mapAlunoDisciplina);

		mapAlunoDisciplina.put(8L, new DisciplinaStatus[]{
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.REPPROVED_BY_SCORE,
				DisciplinaStatus.REPPROVED_BY_SCORE,
				DisciplinaStatus.REPPROVED_BY_SCORE
		});

		mapAlunoDisciplina.put(9L, new DisciplinaStatus[]{
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.REPPROVED_BY_FREQUENCY,
				DisciplinaStatus.REPPROVED_BY_FREQUENCY,
				DisciplinaStatus.REPPROVED_BY_SCORE,
				DisciplinaStatus.REPPROVED_BY_SCORE,
				DisciplinaStatus.REPPROVED_BY_SCORE
		});

		mapAlunoDisciplina.put(10L, new DisciplinaStatus[]{
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.APPROVED,
				DisciplinaStatus.LOCKED,
				DisciplinaStatus.LOCKED,
				DisciplinaStatus.REPPROVED_BY_FREQUENCY,
				DisciplinaStatus.REPPROVED_BY_FREQUENCY,
				DisciplinaStatus.REPPROVED_BY_SCORE,
				DisciplinaStatus.REPPROVED_BY_SCORE,
				DisciplinaStatus.CANCELLED
		});

		return mapAlunoDisciplina;
	}

	private void PopulateAndSetHistoricos() {
		AtomicReference<Long> id = new AtomicReference<>(1L);
		HashMap<Long, DisciplinaStatus[]> mapAlunoDisciplina = createMapStatusDisciplinaPorAluno();

		for (Aluno aluno : alunos) {
			List<Historico> criacaoHistoricos = new ArrayList<>();

			disciplinas.forEach(disciplina -> {
				long nota = (10 * id.get()) % 100;
				DisciplinaStatus[] statusDisciplina = mapAlunoDisciplina.get(aluno.getId());

				Historico historico = new Historico();
				historico.setId(id.getAndSet(id.get() + 1));
				historico.setSemestreCursado(String.valueOf(curriculumSemester));
				historico.setNota(String.valueOf(nota));
				historico.setAluno(aluno);
				historico.setDisciplina(disciplina);
				historico.setStatusDisciplina(statusDisciplina[disciplina.getId().intValue()].toString());
				criacaoHistoricos.add(historico);

				historicos.put(aluno.getId(), criacaoHistoricos);
			});
		}
	}

	private void setStudentsHistoryStudents() {
		students.forEach(student -> {
			historicos.get(Long.parseLong(student.getId()))
					.forEach(historico -> {
						studentsHistory.add(
								student.getId(),
								student.getNome(),
								student.getCourse(),
								student.getCurriculum(),
								String.valueOf(curriculumSemester),
								student.getClass().getName(),
								classStatusMapping(historico),
								codigoGrade,
								String.valueOf(4)
						);
					});
		});
	}

	/**
	 * Faz mapeamento entre a descrição do histórico e o enum {@link ClassStatus}
	 *
	 * @param historico o {@link Historico informado}
	 * @return o valor correspondente do enum ou nulo
	 */
	private ClassStatus classStatusMapping(Historico historico) {
		String classStatus = historico.getStatusDisciplina();

		switch (classStatus) {
			case "Matriculado":
				return ClassStatus.ENROLLED;
			case "Aprovado":
			case "Dispensado":
				return ClassStatus.APPROVED;
			case "Rep Nota":
				return ClassStatus.REPROVED_GRADE;
			case "Rep Freq":
				return ClassStatus.REPROVED_FREQUENCY;
			case "Trancado":
			case "Cancelado":
				return ClassStatus.NOT_ENROLLED;
			default:
				return null;
		}
	}

	private void PopulateStudentsHistory() {
		studentsHistory = new StudentsHistory();
	}

	private void setCurriculumClasses() {
		classes.forEach(klass -> {
			int classId = Integer.parseInt(klass.getId());
			if (classId > 14)
				curriculum.addElectiveClass(klass);
			else
				curriculum.addMandatoryClass(curriculumSemester, klass);
		});
	}

	private void populateClasses() {
		disciplinas.forEach(disciplina -> {
			Class klass = new Class(String.valueOf(disciplina.getId()));
			classes.add(klass);
		});

		for (int i = 0; i < 10; i++) {
			Class klass = new Class(String.valueOf(i));
			classes.add(klass);
		}
	}

	private void setCurriculumStudentRelation() {
		students.forEach(student -> student.setCurriculum(curriculumId));
	}

	private void populateCurriculums() {
		curriculum = new Curriculum();
		curriculum.setCurriculumId(curriculumId);
	}

	private void PopulateStudent() {
		alunos.forEach(aluno -> {
			Student student = new Student(String.valueOf(aluno.getId()), aluno.getNome());
			student.setCourse(cursoNome);
			students.add(student);
		});
	}

	private void setPessoaCurso() {
		curso.setPessoaCurso(pessoasCurso);
		pessoas.forEach(pessoa -> pessoa.setPessoaCurso(pessoasCurso));
	}

	private void setGrupoGradesDisciplina() {
		grade.setGrupoGradeDisciplina(gradeDisciplinas);
		disciplinas.forEach(disciplina -> disciplina.setGrupoGradeDisciplina(gradeDisciplinas));
	}

	private void setGrupoGrades() {

		List<Grade> grupoGrade = new ArrayList<>();
		grupoGrade.add(grade);

		curso.setGrupoGrades(grupoGrade);
	}

	private void setGrupoAlunos() {
		grade.setGrupoAlunos(alunos);
		curso.setGrupoAlunos(alunos);
	}

	/**
	 * Popula a grade com as primeiras 12 disciplinas.
	 * Essa técnica é usada para que as outras 8 possam ser inclusas como eletivas/opcionais.
	 */
	private void PopulateGradeDisciplina() {
		disciplinas.stream()
				.limit(7)
				.forEach(disciplina -> {
					long disciplinaId = disciplina.getId();
					GradeDisciplina gd = new GradeDisciplina();
					gd.setId(disciplinaId);
					gd.setGrade(grade);
					gd.setDisciplina(disciplina);
					gd.setPeriodo(periodoDisciplina);
					gd.setExcluirIra(disciplinaId % 2 == 0);
					gd.setTipoDisciplina(
							disciplinaId <= 5
									? DisciplinaTipo.OPTIONAL.toString()
									: DisciplinaTipo.REQUIRED.toString()
					);
					gradeDisciplinas.add(gd);
				});
	}

	private void PopulateGrade() {
		grade = new Grade();
		grade.setId(1L);
		grade.setCurso(curso);
		grade.setCodigo(codigoGrade);
	}

	private void PopulatePessoaCurso() {
		pessoas.forEach(pessoa -> {
			PessoaCurso pessoaCurso = new PessoaCurso();
			pessoaCurso.setId(pessoa.getId());
			pessoaCurso.setCurso(curso);
			pessoaCurso.setPessoa(pessoa);
			pessoasCurso.add(pessoaCurso);
			pessoa.getPessoaCurso().add(pessoaCurso);
		});
	}

	private void PopulateAlunos(){
		pessoas.forEach(pessoa -> {
			Aluno aluno = new Aluno();
			aluno.setId(pessoa.getId());
			aluno.setNome("aluno" + pessoa.getId().intValue());
			aluno.setCurso(curso);
			aluno.setMatricula(
				alunoMatricula + String.format("%2s", pessoa.getId()).replace(' ', '0')
			);
			aluno.setGrade(grade);
			alunos.add(aluno);
		});
	}

	private void PopulateCurso() {
		curso = new Curso();
		curso.setCodigo(cursoCodigo);
		curso.setNome(cursoNome);
		curso.setId(cursoId);
		curso.setDataAtualizacao(cursoDataAtualizacao);
	}

	public void PopulatePessoas(){
		for(int i = 1; i < 11; i++) {
			Pessoa pessoa = new Pessoa();
			pessoa.setId((long) i);
			pessoa.setSiape("siape" + i);
			pessoa.setNome("Nome" + i);
			pessoa.setSenha("Senha" + i);
			pessoas.add(pessoa);
		}
	}

	public void PopulateDisciplinas(){
		for(int i = 0; i < 10; i++) {
			Disciplina disciplina = new Disciplina();
			disciplina.setId((long) i);
			disciplina.setNome("Disciplina" + i);
			disciplina.setGrupoGradeDisciplina(gradeDisciplinas);
			disciplina.setCargaHoraria((i + 1));
			disciplina.setCodigo("DIS0"+ i);
			disciplinas.add(disciplina);

			int finalI = i;
			alunos.forEach(aluno -> {
				int notaId = (int) (aluno.getId() * (finalI + 1));
				Historico historico = new Historico();
				historico.setAluno(aluno);
				historico.setId((long)notaId);
				historico.setDisciplina(disciplina);
				historico.setNota(String.valueOf(notaId));
				historico.setStatusDisciplina(notaId >= 60 ? aprovadoText : reprovadoText);

				aluno.setGrupoHistorico(new ArrayList<>());
				aluno.getGrupoHistorico().add(historico);
			});
		}
	}
}
