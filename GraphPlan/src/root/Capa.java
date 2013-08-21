package root;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Cada capa tiene: Un conjunto*** de literales Un conjunto de acciones Un
 * conjunto de acciones NO persistencia Mutex entre literales Mutex entre
 * acciones Tabla noGood para eficiencia en BackTracking
 * 
 * @author Raul
 * 
 */
public class Capa {

	private Set<Literal> literales;
	private Map<Literal, MutexLiteral> mutexLiterales;

	private Set<Accion> acciones;
	private Set<Accion> accionesSinPersistencia;
	private Map<Accion, MutexAccion> mutexAcciones;

	private Map<List<Literal>, List<Literal>> noGoodTable;

	public Capa() {

		literales = new HashSet<>();
		mutexLiterales = new HashMap<>();

		acciones = new HashSet<>();
		accionesSinPersistencia = new HashSet<>();
		mutexAcciones = new HashMap<>();

		noGoodTable = new HashMap<>();

	}

	/**
	 * Añade una nueva relación mutex entre los parámetros dados
	 * 
	 * @param muMap
	 *            Mutex de la capa
	 * @param lA
	 *            Literal A
	 * @param lB
	 *            Literal B
	 */
	public void añadirNodoMutex(Map<Literal, MutexLiteral> muMap, Literal lA,
			Literal lB) {

		// Recogemos Mutex asociados a cada literal

		MutexLiteral muA = muMap.get(lA);
		MutexLiteral muB = muMap.get(lB);

		// Si no hay ningún mutex asociado a A, creamos uno nuevo y establecemos
		// la relación

		if (muA == null) {

			muA = new MutexLiteral();
			muA.setNodo(lA);
			muA.getEnlacesMutex().add(lB);
			
			muMap.put(lA, muA);

			// En el caso de que sí tenga Mutex pero NO tenga relación con B, lo
			// añadimos

		} else if (!muA.getEnlacesMutex().contains(lB)) {
			muA.getEnlacesMutex().add(lB);
		}

		// IGUAL CON B

		if (muB == null) {

			muB = new MutexLiteral();
			muB.setNodo(lB);
			muB.getEnlacesMutex().add(lA);

			muMap.put(lB, muB);

		} else if (!muB.getEnlacesMutex().contains(lA)) {
			muB.getEnlacesMutex().add(lA);
		}
	}

	/**
	 * Añade una nueva relación mutex entre los parámetros dados
	 * 
	 * @param muMap
	 *            Mutex de la capa
	 * @param lA
	 *            Accion A
	 * @param lB
	 *            Accion B
	 */
	public void añadirNodoMutex(Map<Accion, MutexAccion> muMap, Accion accionA,
			Accion accionB) {

		// Recogemos Mutex asociados a cada acción

		MutexAccion muA = muMap.get(accionA);
		MutexAccion muB = muMap.get(accionB);

		// Si no hay ningún mutex asociado a A, creamos uno nuevo y establecemos
		// la relación

		if (muA == null) {

			muA = new MutexAccion();
			muA.setNodo(accionA);
			muA.getEnlacesMutex().add(accionB);
			muMap.put(accionA, muA);

			// En el caso de que sí tenga Mutex pero NO tenga relación con B, lo
			// añadimos

		} else if (!muA.getEnlacesMutex().contains(accionB)) {
			muA.getEnlacesMutex().add(accionB);
		}

		// IGUAL CON B

		if (muB == null) {

			muB = new MutexAccion();
			muB.setNodo(accionB);
			muB.getEnlacesMutex().add(accionA);
			muMap.put(accionB, muB);

		} else if (!muB.getEnlacesMutex().contains(accionA)) {
			muB.getEnlacesMutex().add(accionA);
		}
	}

	/* ***************************************************** */
	/* **************** Getters and setters **************** */
	/* ***************************************************** */

	public Map<Literal, MutexLiteral> getMutexLiterales() {
		return mutexLiterales;
	}

	public Set<Literal> getLiterales() {
		return literales;
	}

	public void setLiterales(Set<Literal> literales) {
		this.literales = literales;
	}

	public void setMutexLiterales(Map<Literal, MutexLiteral> mutexLiterales) {
		this.mutexLiterales = mutexLiterales;
	}

	public Set<Accion> getAcciones() {
		return acciones;
	}

	public void setAcciones(Set<Accion> acciones) {
		this.acciones = acciones;
	}

	public Set<Accion> getAccionesSinPersistencia() {
		return accionesSinPersistencia;
	}

	public void setAccionesSinPersistencia(Set<Accion> accionesSinPersistencia) {
		this.accionesSinPersistencia = accionesSinPersistencia;
	}

	public Map<Accion, MutexAccion> getMutexAcciones() {
		return mutexAcciones;
	}

	public void setMutexAcciones(Map<Accion, MutexAccion> mutexAcciones) {
		this.mutexAcciones = mutexAcciones;
	}

	public Map<List<Literal>, List<Literal>> getNoGoodTable() {
		return noGoodTable;
	}

	public void setNoGoodTable(Map<List<Literal>, List<Literal>> noGoodTable) {
		this.noGoodTable = noGoodTable;
	}
	
	public String toString(){
		String temp = "[Capa : Acciones : ";
		Iterator<Accion> iterActs = getAcciones().iterator();
		Iterator<Literal> iterProps = getLiterales().iterator();
		Iterator<MutexAccion> iterMuActs = getMutexAcciones().values().iterator();
		Iterator<MutexLiteral> iterMuProps = getMutexLiterales().values().iterator();
	    while (iterActs.hasNext()) {
			temp = temp + iterActs.next().getNombre() + " ";
		}
	    temp = temp + "\n         Literales   : ";
	    while (iterProps.hasNext()) {
			temp = temp + iterProps.next().getNombre() + " ";
		}
	    temp = temp + "\n         muLit : ";
	    while (iterMuProps.hasNext()) {
			temp = temp + iterMuProps.next() + " ";
		}
	    temp = temp + "\n         muAccs : ";
	    while (iterMuActs.hasNext()) {
			temp = temp + iterMuActs.next() + " ";
		}
	    temp = temp + "\n";
		return temp;
	}

}
