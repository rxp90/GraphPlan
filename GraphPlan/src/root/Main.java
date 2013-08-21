package root;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

	public static void main(String args[]) {
/*
		a�adirHecho("tenerPastel");
		a�adirHecho("noComidoPastel");

		a�adirObjetivo("comidoPastel");
		a�adirObjetivo("tenerPastel");

		List<String> precondComer = new ArrayList<String>();
		List<String> posComer = new ArrayList<String>();
		List<String> negComer = new ArrayList<String>();

		precondComer.add("tenerPastel");

		posComer.add("noTenerPastel");
		posComer.add("comidoPastel");

		negComer.add("tenerPastel");

		a�adirAccion("comer", precondComer, posComer, negComer);

		List<String> precondCocinar = new ArrayList<String>();
		List<String> posCocinar = new ArrayList<String>();
		List<String> negCocinar = new ArrayList<String>();

		precondCocinar.add("noTenerPastel");
		posCocinar.add("tenerPastel");
		negCocinar.add("noTenerPastel");

		a�adirAccion("cocinar", precondCocinar, posCocinar, negCocinar);
*/
		
		a�adirHecho("atFlat,Axle");
		a�adirHecho("atSpare,Trunk");

		a�adirObjetivo("atSpare,Axle");

		List<String> precond = new ArrayList<>();
		List<String> pos = new ArrayList<>();
		List<String> neg = new ArrayList<>();

		precond.add("atSpare,Trunk");
		pos.add("atSpare,Ground");

		// Con un efecto negativo hay que a�adir la eliminaci�n del positivo y
		// la adici�n de la negaci�n
		pos.add("-atSpare,Trunk");
		neg.add("atSpare,Trunk");

		a�adirAccion("removeSpare, Trunk", precond, pos, neg);


		precond.clear();
		pos.clear();
		neg.clear();

		precond.add("atFlat,Axle");
		pos.add("atFlat,Ground");
		pos.add("-atFlat,Axle");
		neg.add("atFlat,Axle");

		a�adirAccion("removeFlat, Axle", precond, pos, neg);


		precond.clear();
		pos.clear();
		neg.clear();

		precond.add("atSpare,Ground");
		precond.add("-atFlat,Axle");

		pos.add("-atSpare,Ground");
		pos.add("atSpare,Axle");
		neg.add("atSpare,Ground");

		a�adirAccion("putOnSpare,Axle", precond, pos, neg);

		precond.clear();
		pos.clear();
		neg.clear();
		
		pos.add("-atSpare,Ground");
		pos.add("-atSpare,Axle");
		pos.add("-atSpare,Trunk");
		pos.add("-atFlat,Ground");
		pos.add("-atFlat,Axle");
		
		neg.add("atSpare,Ground");
		neg.add("atSpare,Axle");
		neg.add("atSpare,Trunk");
		neg.add("atFlat,Ground");
		neg.add("atFlat,Axle");
		
		AlgoritmoGraphPlan prueba = new AlgoritmoGraphPlan(acciones, hechos,
				objetivo);

		List<List<Accion>> resultado = prueba.ejecutar();

		System.out.println(prueba.getSalida());
		System.out.println(resultado);
		
	}

	private static Set<Accion> acciones = new HashSet<>();
	private static Set<Literal> hechos = new HashSet<>();
	private static List<Literal> objetivo = new ArrayList<>();

	public static void a�adirAccion(String nombre, List<String> precond,
			List<String> pos, List<String> neg) {
		Set<Literal> precondMap = new HashSet<Literal>();
		Set<Literal> negMap = new HashSet<Literal>();
		Set<Literal> posMap = new HashSet<Literal>();

		// Precondiciones
		for (String s : precond) {
			Literal l = new Literal();
			l.setNombre(s);
			precondMap.add(l);
		}
		// Efectos positivos
		for (String s : pos) {
			Literal l = new Literal();
			l.setNombre(s);
			posMap.add(l);
		}
		// Efectos negativos
		for (String s : neg) {
			Literal l = new Literal();
			l.setNombre(s);
			negMap.add(l);
		}

		// Creamos la acci�n
		Accion accion = new Accion();
		accion.setNombre(nombre);

		accion.setPrecondiciones(precondMap);
		accion.setEfectosPositivos(posMap);
		accion.setEfectosNegativos(negMap);

		// A�adimos al conjunto de acciones
		acciones.add(accion);

	}

	public static void a�adirHecho(String nombre) {
		Literal l = new Literal();
		l.setNombre(nombre);
		hechos.add(l);
	}

	public static void a�adirObjetivo(String nombre) {
		Literal l = new Literal();
		l.setNombre(nombre);
		objetivo.add(l);
	}
}
