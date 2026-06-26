import com.comsol.model.Model;
import com.comsol.model.util.ModelUtil;

public class ComsolPhase3Extract {
  private static void printMatrix(String name, double[][] values) {
    System.out.println(name + "_rows=" + values.length);
    for (int i = 0; i < values.length; i++) {
      for (int j = 0; j < values[i].length; j++) {
        System.out.println(name + "[" + i + "][" + j + "]=" + values[i][j]);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    Model model = ModelUtil.load("model", args[0]);
    System.out.println("model=" + args[0]);
    System.out.println("datasets=" + String.join(",", model.result().dataset().tags()));

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

    model.result().numerical().create("maxV", "MaxSurface");
    model.result().numerical("maxV").set("data", "dset1");
    model.result().numerical("maxV").selection().geom("geom1", 2);
    model.result().numerical("maxV").selection().all();
    model.result().numerical("maxV").set("expr", "w");
    model.result().numerical("maxV").set("unit", "um");
    printMatrix("max_axial_v_um", model.result().numerical("maxV").getReal());

    model.result().numerical().create("avgOuterU", "AvLine");
    model.result().numerical("avgOuterU").set("data", "dset1");
    model.result().numerical("avgOuterU").selection().geom("geom1", 1);
    model.result().numerical("avgOuterU").selection().set(new int[] {3});
    model.result().numerical("avgOuterU").set("expr", "u");
    model.result().numerical("avgOuterU").set("unit", "um");
    printMatrix("avg_outer_radial_u_um", model.result().numerical("avgOuterU").getReal());

    model.result().numerical().create("avgTopV", "AvLine");
    model.result().numerical("avgTopV").set("data", "dset1");
    model.result().numerical("avgTopV").selection().geom("geom1", 1);
    model.result().numerical("avgTopV").selection().set(new int[] {4});
    model.result().numerical("avgTopV").set("expr", "w");
    model.result().numerical("avgTopV").set("unit", "um");
    printMatrix("avg_top_axial_v_um", model.result().numerical("avgTopV").getReal());

    ModelUtil.remove(model.tag());
  }
}
