package root;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Accion {

	private Set<Literal> precondiciones = new HashSet<Literal>();
	private Set<Literal> efectosPositivos = new HashSet<Literal>();
	private Set<Literal> efectosNegativos = new HashSet<Literal>();
	private String nombre;
	private Boolean esPersistencia = false;

	/**
	 * Comprueba si todas las precondiciones de la acción están presentes en el
	 * conjunto de literales dado
	 * 
	 * @param literales
	 *            Literales de la capa actual
	 * @return boolean
	 */
	public boolean esAplicable(Set<Literal> literales) {
		boolean stop = false;
		boolean res = true;
		System.out.println("Literales: " + literales);
		for (Literal precondicion : precondiciones) {

			if (!literales.contains(precondicion) && !stop) {
				stop = true;
				res = false;
			}
		}
		if (!res) {
			System.out.println("NO ES APLICABLE");
		} else {
			System.out.println("ES APLICABLE");
		}

		return res;
	}

	/**
	 * Comprueba si hay necesidades que compiten
	 * 
	 * @param mapLiteralesMutex
	 * @return
	 */
	public boolean tienePrecondicionesMutex(
			Map<Literal, MutexLiteral> mapLiteralesMutex) {
		for (Literal l : precondiciones) {
			// Mutex asociados a la precondición l
			MutexLiteral mutex = mapLiteralesMutex.get(l);
			// ¿Hay en el mutex al menos una de las precondiciones?
			// if (mutex != null && mutex.getEnlacesMutex().contains(l)) { No
			// vale, porque miraría si hay mutex con ella misma
			if (mutex != null
					&& mutex.existeAlMenosUno(precondiciones.iterator())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Comprueba interferencias y efectos inconsistentes
	 * 
	 * @param accionB
	 * @return
	 */

	public boolean esIndependienteDe(Accion accionB) {

		for (Literal efectoA : this.efectosNegativos) {
			if (accionB.precondiciones.contains(efectoA)
					|| accionB.efectosPositivos.contains(efectoA)) {
				return false;
			}
		}

		for (Literal efectoB : accionB.efectosNegativos) {
			if (this.precondiciones.contains(efectoB)
					|| this.efectosPositivos.contains(efectoB)) {
				return false;
			}
		}

		return true;
	}

	public Set<Literal> getPrecondiciones() {
		return precondiciones;
	}

	public void setPrecondiciones(Set<Literal> precondiciones) {
		this.precondiciones = precondiciones;
	}

	public Set<Literal> getEfectosPositivos() {
		return efectosPositivos;
	}

	public void setEfectosPositivos(Set<Literal> efectosPositivos) {
		this.efectosPositivos = efectosPositivos;
	}

	public Set<Literal> getEfectosNegativos() {
		return efectosNegativos;
	}

	public void setEfectosNegativos(Set<Literal> efectosNegativos) {
		this.efectosNegativos = efectosNegativos;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Boolean getEsPersistencia() {
		return esPersistencia;
	}

	public void setEsPersistencia(Boolean esPersistencia) {
		this.esPersistencia = esPersistencia;
	}

	public String toString() {
		return nombre;
	}

	public boolean equals(Object o) {
		return ((Accion) o).getNombre() == nombre;
	}

	public int hashCode() {
		return nombre.hashCode();
	}

}
