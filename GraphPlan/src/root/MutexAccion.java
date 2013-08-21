package root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MutexAccion {

	private Accion nodo;
	private List<Accion> enlacesMutex = new ArrayList<>();

	public Accion getNodo() {
		return nodo;
	}

	public void setNodo(Accion nodo) {
		this.nodo = nodo;
	}

	public List<Accion> getEnlacesMutex() {
		return enlacesMutex;
	}

	public void setEnlacesMutex(List<Accion> enlacesMutex) {
		this.enlacesMutex = enlacesMutex;
	}

	public String toString() {
		return "MUTEX[" + nodo.toString() + ": "
				+ Arrays.toString(enlacesMutex.toArray()) + "]";
	}

}
