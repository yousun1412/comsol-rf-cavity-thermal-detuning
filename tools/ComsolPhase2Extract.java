import com.comsol.model.Model;
import com.comsol.model.util.ModelUtil;

public class ComsolPhase2Extract {
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

    model.result().numerical().create("pin", "IntLine");
    model.result().numerical("pin").set("data", "dset1");
    model.result().numerical("pin").selection().geom("geom1", 1);
    model.result().numerical("pin").selection().set(new int[] {1});
    model.result().numerical("pin").set("expr", "2*pi*r*q_flux");
    model.result().numerical("pin").set("unit", "W");
    printMatrix("input_power_W", model.result().numerical("pin").getReal());

    model.result().numerical().create("qconv", "IntLine");
    model.result().numerical("qconv").set("data", "dset1");
    model.result().numerical("qconv").selection().geom("geom1", 1);
    model.result().numerical("qconv").selection().set(new int[] {2, 3, 4});
    model.result().numerical("qconv").set("expr", "2*pi*r*h_conv*(T-T_amb)");
    model.result().numerical("qconv").set("unit", "W");
    printMatrix("conv_removed_W", model.result().numerical("qconv").getReal());

    ModelUtil.remove(model.tag());
  }
}
