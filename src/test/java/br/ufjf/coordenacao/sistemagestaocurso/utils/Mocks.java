package br.ufjf.coordenacao.sistemagestaocurso.utils;

import br.ufjf.coordenacao.OfertaVagas.model.*;
import br.ufjf.coordenacao.OfertaVagas.model.Class;
import br.ufjf.coordenacao.sistemagestaocurso.enums.DisciplinaStatus;
import br.ufjf.coordenacao.sistemagestaocurso.enums.DisciplinaTipo;
import br.ufjf.coordenacao.sistemagestaocurso.model.*;

import java.util.ArrayList;
import java.util.List;
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
	List<Historico> historicos = new ArrayList<>();

	public Curso getCurso() {
		return curso;
	}
	public Grade getGrade() { return grade; }
	public List<Historico> getHistoricos() { return historicos; }

	public Mocks(){
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
		this.setStudentsHistoryStudents();

		this.PopulateAndSetHistoricos();
	}

	private void PopulateAndSetHistoricos() {
		AtomicReference<Long> id = new AtomicReference<>(1L);
		List<String> statusDisciplina = new ArrayList<>();
		statusDisciplina.add(DisciplinaStatus.REPPROVED_BY_FREQUENCY.toString());
		statusDisciplina.add(DisciplinaStatus.REPPROVED_BY_SCORE.toString());
		statusDisciplina.add(DisciplinaStatus.LOCKED.toString());
		statusDisciplina.add(DisciplinaStatus.APPROVED.toString());
		statusDisciplina.add(DisciplinaStatus.CANCELLED.toString());
		statusDisciplina.add(DisciplinaStatus.DISPENSED.toString());
		statusDisciplina.add(DisciplinaStatus.ENROLLED.toString());

		for (Aluno aluno : alunos) {
			disciplinas.forEach(disciplina -> {
				Historico historico = new Historico();
				historico.setId(id.getAndSet(id.get() + 1));
				historico.setSemestreCursado(String.valueOf(curriculumSemester));
				historico.setNota(String.valueOf((10 * id.get())));
				historico.setAluno(aluno);
				historico.setDisciplina(disciplina);
				historico.setStatusDisciplina(
						statusDisciplina.get(id.get().intValue() % statusDisciplina.size())
				);

				historicos.add(historico);
			});
		}
	}

	private void setStudentsHistoryStudents() {
		students.forEach(student -> {
			ClassStatus[] classStatusList = ClassStatus.values();
			studentsHistory.add(
				student.getId(),
				student.getNome(),
				student.getCourse(),
				student.getCurriculum(),
				String.valueOf(curriculumSemester),
				student.getClass().getName(),
				classStatusList[Integer.parseInt(student.getId()) % classStatusList.length],
				codigoGrade,
				String.valueOf(4)
			);
		});
	}

	private void PopulateStudentsHistory() {
		studentsHistory = new StudentsHistory();
	}

	private void setCurriculumClasses() {
		classes.forEach(klass -> {
			int classId = Integer.parseInt(klass.getId());
			if(classId > 7)
				curriculum.addElectiveClass(klass);
			else
				curriculum.addMandatoryClass(curriculumSemester, klass);
		});
	}

	private void populateClasses() {
		for(int i = 0; i < 10; i++) {
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

	private void PopulateGradeDisciplina() {
		disciplinas.forEach(disciplina -> {
			long disciplinaId = disciplina.getId();
			GradeDisciplina gd = new GradeDisciplina();
			gd.setId(disciplinaId);
			gd.setGrade(grade);
			gd.setDisciplina(disciplina);
			gd.setPeriodo(periodoDisciplina);
			gd.setExcluirIra(disciplinaId % 2 == 0);
			gd.setTipoDisciplina(
				disciplinaId > 7
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
