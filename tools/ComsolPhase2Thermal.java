import com.comsol.model.Model;
import com.comsol.model.util.ModelUtil;

public class ComsolPhase2Thermal {
  public static void main(String[] args) throws Exception {
    String out = args.length > 0 ? args[0] : "E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\models\\comsol\\phase2_standalone_thermal.mph";

    Model model = ModelUtil.create("Model");
    model.modelPath("E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\models\\comsol");
    model.label("phase2_standalone_thermal.mph");

    model.param().set("a", "2.5[cm]");
    model.param().set("b", "10[cm]");
    model.param().set("height", "10[cm]");
    model.param().set("q_flux", "1000[W/m^2]");
    model.param().set("h_conv", "100[W/(m^2*K)]");
    model.param().set("T_amb", "293.15[K]");

    model.component().create("comp1", true);
    model.component("comp1").geom().create("geom1", 2);
    model.component("comp1").geom("geom1").axisymmetric(true);
    model.component("comp1").geom("geom1").create("r1", "Rectangle");
    model.component("comp1").geom("geom1").feature("r1").set("pos", new String[] {"a", "0"});
    model.component("comp1").geom("geom1").feature("r1").set("size", new String[] {"b-a", "height"});
    model.component("comp1").geom("geom1").run();

    model.component("comp1").material().create("mat1", "Common");
    model.component("comp1").material("mat1").label("Copper baseline");
    model.component("comp1").material("mat1").propertyGroup("def").set("thermalconductivity", new String[] {"400[W/(m*K)]", "0", "0", "0", "400[W/(m*K)]", "0", "0", "0", "400[W/(m*K)]"});
    model.component("comp1").material("mat1").propertyGroup("def").set("density", "8960[kg/m^3]");
    model.component("comp1").material("mat1").propertyGroup("def").set("heatcapacity", "385[J/(kg*K)]");

    model.component("comp1").physics().create("ht", "HeatTransfer", "geom1");
    model.component("comp1").physics("ht").feature("init1").set("Tinit", "T_amb");

    model.component("comp1").physics("ht").create("hf_in", "HeatFluxBoundary", 1);
    model.component("comp1").physics("ht").feature("hf_in").selection().set(new int[] {1});
    model.component("comp1").physics("ht").feature("hf_in").set("q0", "q_flux");

    model.component("comp1").physics("ht").create("hf_out", "HeatFluxBoundary", 1);
    model.component("comp1").physics("ht").feature("hf_out").selection().set(new int[] {2, 3, 4});
    model.component("comp1").physics("ht").feature("hf_out").set("HeatFluxType", "ConvectiveHeatFlux");
    model.component("comp1").physics("ht").feature("hf_out").set("h", "h_conv");
    model.component("comp1").physics("ht").feature("hf_out").set("Text", "T_amb");

    model.component("comp1").mesh().create("mesh1");
    model.component("comp1").mesh("mesh1").autoMeshSize(3);
    model.component("comp1").mesh("mesh1").run();

    model.study().create("std1");
    model.study("std1").create("stat", "Stationary");
    model.study("std1").create("param", "Parametric");
    model.study("std1").feature("param").set("pname", new String[] {"q_flux", "h_conv"});
    model.study("std1").feature("param").set("plistarr", new String[] {"1000 1000 1000 2000 2000 2000", "100 500 1000 100 500 1000"});
    model.study("std1").feature("param").set("punit", new String[] {"W/m^2", "W/(m^2*K)"});

    model.result().create("pgT", "PlotGroup2D");
    model.result("pgT").label("Temperature Field");
    model.result("pgT").create("surf1", "Surface");
    model.result("pgT").feature("surf1").set("expr", "T");
    model.result("pgT").feature("surf1").set("unit", "K");

    long start = System.nanoTime();
    model.study("std1").run();
    long elapsedMs = (System.nanoTime() - start) / 1000000L;
    System.out.println("solve_elapsed_s=" + (elapsedMs / 1000.0));
    System.out.println("mesh_numElem=" + model.component("comp1").mesh("mesh1").getNumElem());
    System.out.println("mesh_numVertex=" + model.component("comp1").mesh("mesh1").getNumVertex());

    model.result().export().create("imgT", "Image2D");
    model.result("pgT").set("data", "dset1");
    model.result().export("imgT").set("plotgroup", "pgT");
    model.result().export("imgT").set("sizedesc", "manual");
    model.result().export("imgT").set("width", "1000");
    model.result().export("imgT").set("height", "700");
    model.result().export("imgT").set("pngfilename", "E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\results\\phase2\\temperature_field.png");
    model.result("pgT").run();
    model.result().export("imgT").run();

    model.save(out);
    System.out.println("saved_model=" + out);
  }
}
