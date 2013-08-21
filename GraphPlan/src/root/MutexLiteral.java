package root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MutexLiteral {

	private Literal nodo;
	private List<Literal> enlacesMutex = new ArrayList<>();

	public Literal getNodo() {
		return nodo;
	}

	public void setNodo(Literal nodo) {
		this.nodo = nodo;
	}

	public List<Literal> getEnlacesMutex() {
		return enlacesMutex;
	}

	public void setEnlacesMutex(List<Literal> enlacesMutex) {
		this.enlacesMutex = enlacesMutex;
	}

	public boolean existeAlMenosUno(Iterator<Literal> iterator) {
		while (iterator.hasNext()) {
			if (enlacesMutex.contains(iterator.next())) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return "MUTEX[" + nodo.toString() + ": " + Arrays.toString(enlacesMutex.toArray()) + "]";
	}

}
