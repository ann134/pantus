package ru.sigmadigital.pantus.ar.rendering;

import java.util.ArrayList;
import java.util.HashMap;

public class ObjModel {
	// из  obj/mtl
	public float[] vertices = null;
	public float[] texCoords = null;
	public float[] normals = null;
	public ArrayList<ObjFace> faces = null;
	public HashMap<String, ObjMaterial> materials = null;
	public float[] minmax = null;
}
