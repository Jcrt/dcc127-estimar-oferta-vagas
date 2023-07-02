package br.ufjf.coordenacao.sistemagestaocurso.providers;

import org.springframework.stereotype.Service;

import javax.faces.context.FacesContext;

@Service
public class FacesContextProvider implements IFacesContextProvider {
	@Override
	public FacesContext provide() {
		return FacesContext.getCurrentInstance();
	}
}
