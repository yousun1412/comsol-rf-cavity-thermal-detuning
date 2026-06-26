import com.comsol.model.Model;
import com.comsol.model.util.ModelUtil;

public class ComsolPhase5ThermalStructural {
  public static void main(String[] args) throws Exception {
    String out = (args != null && args.length > 0) ? args[0] : "E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\models\\comsol\\phase5_thermal_structural_coupling.mph";

    Model model = ModelUtil.create("Model");
    model.modelPath("E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\models\\comsol");
    model.label("phase5_thermal_structural_coupling.mph");

    model.param().set("a", "2.5[cm]");
    model.param().set("b", "10[cm]");
    model.param().set("height", "10[cm]");
    model.param().set("T_amb", "293.15[K]");
    model.param().set("Tref", "293.15[K]");
    model.param().set("P0_rf", "20[W]");
    model.param().set("power_scale", "1");
    model.param().set("h_conv", "100[W/(m^2*K)]");
    model.param().set("field_wall_shape", "2*cos(pi*z/height)^2");
    model.param().set("wall_loss_flux", "P0_rf*power_scale*field_wall_shape/(2*pi*a*height)");
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
    model.component("comp1").material("mat1").label("Copper thermal-structural baseline");
    model.component("comp1").material("mat1").propertyGroup("def").set("thermalconductivity", new String[] {"400[W/(m*K)]", "0", "0", "0", "400[W/(m*K)]", "0", "0", "0", "400[W/(m*K)]"});
    model.component("comp1").material("mat1").propertyGroup("def").set("density", "8960[kg/m^3]");
    model.component("comp1").material("mat1").propertyGroup("def").set("heatcapacity", "385[J/(kg*K)]");
    model.component("comp1").material("mat1").propertyGroup("def").set("youngsmodulus", "Ecu");
    model.component("comp1").material("mat1").propertyGroup("def").set("poissonsratio", "nuCu");
    model.component("comp1").material("mat1").propertyGroup("def").set("thermalexpansioncoefficient", new String[] {"alphaCu", "0", "0", "0", "alphaCu", "0", "0", "0", "alphaCu"});

    model.component("comp1").physics().create("ht", "HeatTransfer", "geom1");
    model.component("comp1").physics("ht").feature("init1").set("Tinit", "T_amb");
    model.component("comp1").physics("ht").create("rf_loss", "HeatFluxBoundary", 1);
    model.component("comp1").physics("ht").feature("rf_loss").selection().set(new int[] {1});
    model.component("comp1").physics("ht").feature("rf_loss").set("q0", "wall_loss_flux");
    model.component("comp1").physics("ht").create("conv", "HeatFluxBoundary", 1);
    model.component("comp1").physics("ht").feature("conv").selection().set(new int[] {2, 3, 4});
    model.component("comp1").physics("ht").feature("conv").set("HeatFluxType", "ConvectiveHeatFlux");
    model.component("comp1").physics("ht").feature("conv").set("h", "h_conv");
    model.component("comp1").physics("ht").feature("conv").set("Text", "T_amb");

    model.component("comp1").physics().create("solid", "SolidMechanics", "geom1");
    model.component("comp1").physics("solid").create("fix1", "Fixed", 0);
    model.component("comp1").physics("solid").feature("fix1").selection().set(new int[] {1});
    model.component("comp1").physics("solid").feature("lemm1").create("te1", "ThermalExpansion", 2);
    model.component("comp1").physics("solid").feature("lemm1").feature("te1").selection().all();
    model.component("comp1").physics("solid").feature("lemm1").feature("te1").set("minput_temperature", "userdef");
    model.component("comp1").physics("solid").feature("lemm1").feature("te1").set("T", "T");
    model.component("comp1").physics("solid").feature("lemm1").feature("te1").set("Tref", "Tref");

    model.component("comp1").mesh().create("mesh1");
    model.component("comp1").mesh("mesh1").autoMeshSize(3);
    model.component("comp1").mesh("mesh1").run();

    model.study().create("std1");
    model.study("std1").create("stat", "Stationary");
    model.study("std1").create("param", "Parametric");
    model.study("std1").feature("param").set("pname", new String[] {"power_scale", "h_conv"});
    model.study("std1").feature("param").set("plistarr", new String[] {"0.5 0.5 0.5 1 1 1 2 2 2", "100 500 1000 100 500 1000 100 500 1000"});
    model.study("std1").feature("param").set("punit", new String[] {"", "W/(m^2*K)"});

    model.result().create("pgT", "PlotGroup2D");
    model.result("pgT").label("Phase 5 temperature field");
    model.result("pgT").create("surf1", "Surface");
    model.result("pgT").feature("surf1").set("expr", "T");
    model.result("pgT").feature("surf1").set("unit", "K");

    model.result().create("pgU", "PlotGroup2D");
    model.result("pgU").label("Phase 5 displacement from RF heating");
    model.result("pgU").create("surf1", "Surface");
    model.result("pgU").feature("surf1").set("expr", "solid.disp");
    model.result("pgU").feature("surf1").set("unit", "um");

    long start = System.nanoTime();
    model.study("std1").run();
    long elapsedMs = (System.nanoTime() - start) / 1000000L;
    System.out.println("solve_elapsed_s=" + (elapsedMs / 1000.0));
    System.out.println("mesh_numElem=" + model.component("comp1").mesh("mesh1").getNumElem());
    System.out.println("mesh_numVertex=" + model.component("comp1").mesh("mesh1").getNumVertex());

    model.result("pgT").set("data", "dset1");
    model.result().export().create("imgT", "Image2D");
    model.result().export("imgT").set("plotgroup", "pgT");
    model.result().export("imgT").set("sizedesc", "manual");
    model.result().export("imgT").set("width", "1000");
    model.result().export("imgT").set("height", "700");
    model.result().export("imgT").set("pngfilename", "E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\results\\phase5\\temperature_field_from_rf_heating.png");
    model.result("pgT").run();
    model.result().export("imgT").run();

    model.result("pgU").set("data", "dset1");
    model.result().export().create("imgU", "Image2D");
    model.result().export("imgU").set("plotgroup", "pgU");
    model.result().export("imgU").set("sizedesc", "manual");
    model.result().export("imgU").set("width", "1000");
    model.result().export("imgU").set("height", "700");
    model.result().export("imgU").set("pngfilename", "E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\results\\phase5\\displacement_field_from_rf_heating.png");
    model.result("pgU").run();
    model.result().export("imgU").run();

    model.save(out);
    System.out.println("saved_model=" + out);
  }
}
