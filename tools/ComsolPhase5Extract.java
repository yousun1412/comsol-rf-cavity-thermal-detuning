import com.comsol.model.Model;
import com.comsol.model.util.ModelUtil;

public class ComsolPhase5Extract {
  private static void printMatrix(String name, double[][] values) {
    System.out.println(name + "_rows=" + values.length);
    for (int i = 0; i < values.length; i++) {
      for (int j = 0; j < values[i].length; j++) {
        System.out.println(name + "[" + i + "][" + j + "]=" + values[i][j]);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    String modelPath = (args != null && args.length > 0) ? args[0] : "E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\models\\comsol\\phase5_thermal_structural_coupling.mph";
    Model model = ModelUtil.load("model", modelPath);
    System.out.println("model=" + modelPath);
    System.out.println("datasets=" + String.join(",", model.result().dataset().tags()));

    model.result().numerical().create("ps", "EvalGlobal");
    model.result().numerical("ps").set("data", "dset1");
    model.result().numerical("ps").set("expr", "power_scale");
    printMatrix("power_scale", model.result().numerical("ps").getReal());

    model.result().numerical().create("hc", "EvalGlobal");
    model.result().numerical("hc").set("data", "dset1");
    model.result().numerical("hc").set("expr", "h_conv");
    model.result().numerical("hc").set("unit", "W/(m^2*K)");
    printMatrix("h_conv", model.result().numerical("hc").getReal());

    model.result().numerical().create("maxT", "MaxSurface");
    model.result().numerical("maxT").set("data", "dset1");
    model.result().numerical("maxT").selection().geom("geom1", 2);
    model.result().numerical("maxT").selection().all();
    model.result().numerical("maxT").set("expr", "T");
    model.result().numerical("maxT").set("unit", "K");
    printMatrix("maxT_K", model.result().numerical("maxT").getReal());

    model.result().numerical().create("avgT", "AvSurface");
    model.result().numerical("avgT").set("data", "dset1");
    model.result().numerical("avgT").selection().geom("geom1", 2);
    model.result().numerical("avgT").selection().all();
    model.result().numerical("avgT").set("expr", "T");
    model.result().numerical("avgT").set("unit", "K");
    printMatrix("avgT_K", model.result().numerical("avgT").getReal());

    model.result().numerical().create("maxDisp", "MaxSurface");
    model.result().numerical("maxDisp").set("data", "dset1");
    model.result().numerical("maxDisp").selection().geom("geom1", 2);
    model.result().numerical("maxDisp").selection().all();
    model.result().numerical("maxDisp").set("expr", "solid.disp");
    model.result().numerical("maxDisp").set("unit", "um");
    printMatrix("max_disp_um", model.result().numerical("maxDisp").getReal());

    model.result().numerical().create("maxU", "MaxSurface");
    model.result().numerical("maxU").set("data", "dset1");
    model.result().numerical("maxU").selection().geom("geom1", 2);
    model.result().numerical("maxU").selection().all();
    model.result().numerical("maxU").set("expr", "u");
    model.result().numerical("maxU").set("unit", "um");
    printMatrix("max_radial_u_um", model.result().numerical("maxU").getReal());

    model.result().numerical().create("maxW", "MaxSurface");
    model.result().numerical("maxW").set("data", "dset1");
    model.result().numerical("maxW").selection().geom("geom1", 2);
    model.result().numerical("maxW").selection().all();
    model.result().numerical("maxW").set("expr", "w");
    model.result().numerical("maxW").set("unit", "um");
    printMatrix("max_axial_w_um", model.result().numerical("maxW").getReal());

    model.result().numerical().create("avgInnerU", "AvLine");
    model.result().numerical("avgInnerU").set("data", "dset1");
    model.result().numerical("avgInnerU").selection().geom("geom1", 1);
    model.result().numerical("avgInnerU").selection().set(new int[] {1});
    model.result().numerical("avgInnerU").set("expr", "u");
    model.result().numerical("avgInnerU").set("unit", "um");
    printMatrix("avg_inner_radius_change_um", model.result().numerical("avgInnerU").getReal());

    model.result().numerical().create("avgOuterU", "AvLine");
    model.result().numerical("avgOuterU").set("data", "dset1");
    model.result().numerical("avgOuterU").selection().geom("geom1", 1);
    model.result().numerical("avgOuterU").selection().set(new int[] {3});
    model.result().numerical("avgOuterU").set("expr", "u");
    model.result().numerical("avgOuterU").set("unit", "um");
    printMatrix("avg_outer_radius_change_um", model.result().numerical("avgOuterU").getReal());

    model.result().numerical().create("avgTopW", "AvLine");
    model.result().numerical("avgTopW").set("data", "dset1");
    model.result().numerical("avgTopW").selection().geom("geom1", 1);
    model.result().numerical("avgTopW").selection().set(new int[] {4});
    model.result().numerical("avgTopW").set("expr", "w");
    model.result().numerical("avgTopW").set("unit", "um");
    printMatrix("avg_length_change_um", model.result().numerical("avgTopW").getReal());

    ModelUtil.remove(model.tag());
  }
}
