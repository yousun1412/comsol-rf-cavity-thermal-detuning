import com.comsol.model.Model;
import com.comsol.model.util.ModelUtil;

public class ComsolPhase3Structural {
  public static void main(String[] args) throws Exception {
    String out = args.length > 0 ? args[0] : "E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\models\\comsol\\phase3_structural_thermal_expansion.mph";

    Model model = ModelUtil.create("Model");
    model.modelPath("E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\models\\comsol");
    model.label("phase3_structural_thermal_expansion.mph");

    model.param().set("a", "2.5[cm]");
    model.param().set("b", "10[cm]");
    model.param().set("height", "10[cm]");
    model.param().set("deltaT", "1[K]");
    model.param().set("Tref", "293.15[K]");
    model.param().set("alphaCu", "17e-6[1/K]");
    model.param().set("Ecu", "110e9[Pa]");
    model.param().set("nuCu", "0.34");

    model.component().create("comp1", true);
    model.component("comp1").geom().create("geom1", 2);
    model.component("comp1").geom("geom1").axisymmetric(true);
    model.component("comp1").geom("geom1").create("r1", "Rectangle");
    model.component("comp1").geom("geom1").feature("r1").set("pos", new String[] {"a", "0"});
    model.component("comp1").geom("geom1").feature("r1").set("size", new String[] {"b-a", "height"});
    model.component("comp1").geom("geom1").run();

    model.component("comp1").material().create("mat1", "Common");
    model.component("comp1").material("mat1").label("Copper structural baseline");
    model.component("comp1").material("mat1").propertyGroup("def").set("youngsmodulus", "Ecu");
    model.component("comp1").material("mat1").propertyGroup("def").set("poissonsratio", "nuCu");
    model.component("comp1").material("mat1").propertyGroup("def").set("thermalexpansioncoefficient", new String[] {"alphaCu", "0", "0", "0", "alphaCu", "0", "0", "0", "alphaCu"});

    model.component("comp1").physics().create("solid", "SolidMechanics", "geom1");
    model.component("comp1").physics("solid").create("fix1", "Fixed", 0);
    model.component("comp1").physics("solid").feature("fix1").selection().set(new int[] {1});
    try {
      model.component("comp1").physics("solid").feature("lemm1").create("te1", "ThermalExpansion", 2);
      model.component("comp1").physics("solid").feature("lemm1").feature("te1").selection().all();
      model.component("comp1").physics("solid").feature("lemm1").feature("te1").set("minput_temperature", "userdef");
      model.component("comp1").physics("solid").feature("lemm1").feature("te1").set("T", "Tref+deltaT");
      model.component("comp1").physics("solid").feature("lemm1").feature("te1").set("Tref", "Tref");
      System.out.println("thermal_expansion_feature=lemm1/te1");
    } catch (Throwable t) {
      System.out.println("thermal_expansion_create_error=" + t.getClass().getName() + ": " + t.getMessage());
      throw t;
    }

    model.component("comp1").mesh().create("mesh1");
    model.component("comp1").mesh("mesh1").autoMeshSize(3);
    model.component("comp1").mesh("mesh1").run();

    model.study().create("std1");
    model.study("std1").create("stat", "Stationary");
    model.study("std1").create("param", "Parametric");
    model.study("std1").feature("param").set("pname", new String[] {"deltaT"});
    model.study("std1").feature("param").set("plistarr", new String[] {"1 5 10"});
    model.study("std1").feature("param").set("punit", new String[] {"K"});

    model.result().create("pgU", "PlotGroup2D");
    model.result("pgU").label("Displacement magnitude");
    model.result("pgU").create("surf1", "Surface");
    model.result("pgU").feature("surf1").set("expr", "solid.disp");
    model.result("pgU").feature("surf1").set("unit", "um");

    long start = System.nanoTime();
    model.study("std1").run();
    long elapsedMs = (System.nanoTime() - start) / 1000000L;
    System.out.println("solve_elapsed_s=" + (elapsedMs / 1000.0));
    System.out.println("mesh_numElem=" + model.component("comp1").mesh("mesh1").getNumElem());
    System.out.println("mesh_numVertex=" + model.component("comp1").mesh("mesh1").getNumVertex());

    model.result("pgU").set("data", "dset1");
    model.result().export().create("imgU", "Image2D");
    model.result().export("imgU").set("plotgroup", "pgU");
    model.result().export("imgU").set("sizedesc", "manual");
    model.result().export("imgU").set("width", "1000");
    model.result().export("imgU").set("height", "700");
    model.result().export("imgU").set("pngfilename", "E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\results\\phase3\\displacement_field.png");
    model.result("pgU").run();
    model.result().export("imgU").run();

    model.save(out);
    System.out.println("saved_model=" + out);
  }
}
