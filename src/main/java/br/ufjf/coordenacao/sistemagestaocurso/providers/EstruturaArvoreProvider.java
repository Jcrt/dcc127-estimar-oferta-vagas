package br.ufjf.coordenacao.sistemagestaocurso.providers;

import br.ufjf.coordenacao.sistemagestaocurso.util.arvore.EstruturaArvore;
import org.springframework.stereotype.Service;

@Service
public class EstruturaArvoreProvider implements IEstruturaArvoreProvider {
	@Override
	public EstruturaArvore provide() {
		return EstruturaArvore.getInstance();
	}
}
