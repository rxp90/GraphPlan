package root;

import java.util.HashSet;
import java.util.Set;

public class Literal {

	private String nombre;
	private Set<Accion> negIn = new HashSet<>();
	private Set<Accion> posIn = new HashSet<>();
	private Set<Accion> salida = new HashSet<>();

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Set<Accion> getNegIn() {
		return negIn;
	}

	public void setNegIn(Set<Accion> negIn) {
		this.negIn = negIn;
	}

	public Set<Accion> getPosIn() {
		return posIn;
	}

	public void setPosIn(Set<Accion> posIn) {
		this.posIn = posIn;
	}

	public Set<Accion> getSalida() {
		return salida;
	}

	public void setSalida(Set<Accion> salida) {
		this.salida = salida;
	}

	public String toString() {
		return nombre;
	}

	/*
	 * MUY IMPORTANTE QUE SÓLO MIRE EL NOMBRE (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return ((Literal) o).getNombre() == nombre;
	}

	public int hashCode() {
		return nombre.hashCode();
	}

}
