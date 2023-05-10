package br.ufjf.coordenacao.sistemagestaocurso.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@SequenceGenerator(name="ira_sequencia", sequenceName="ira_seq", allocationSize=1)
public class IRA implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private Aluno aluno;
	private Grade grade;
	private Curso curso;
	private String semestre; 
	private float iraSemestre;
	private float iraAcumulado;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ID_ALUNO")
	public Aluno getAluno() {
		return aluno;
	}
	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}
	
	@Column(name="SEMESTRE")
	public String getSemestre() {
		return semestre;
	}
	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}
	
	@Column(name="IRA_SEMESTRE")
	public float getIraSemestre() {
		return iraSemestre;
	}
	public void setIraSemestre(float iraSemestre) {
		this.iraSemestre = iraSemestre;
	}
	
	@Column(name="IRA_ACUMULADO")
	public float getIraAcumulado() {
		return iraAcumulado;
	}
	public void setIraAcumulado(float iraAcumulado) {
		this.iraAcumulado = iraAcumulado;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY, generator="ira_sequencia")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ID_GRADE")
	public Grade getGrade() {
		return grade;
	}
	public void setGrade(Grade grade) {
		this.grade = grade;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ID_CURSO")
	public Curso getCurso() {
		return curso;
	}
	public void setCurso(Curso curso) {
		this.curso = curso;
	}
}
