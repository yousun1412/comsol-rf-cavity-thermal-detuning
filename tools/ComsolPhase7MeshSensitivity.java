import com.comsol.model.Model;
import com.comsol.model.util.ModelUtil;

public class ComsolPhase7MeshSensitivity {
  private static final String BASE =
      "E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics";
  private static final String COLD_MODEL =
      BASE + "\\models\\comsol\\pillbox_cavity_baseline.mph";
  private static final double A0_M = 0.025;
  private static final double B0_M = 0.100;
  private static final double H0_M = 0.100;
  private static final double DA_UM = 1.339523101494558;
  private static final double DB_UM = 3.6213616121719276;
  private static final double DH_UM = 2.097210664859107;

  private static double firstPhysical(double[] freqs) {
    for (double f : freqs) {
      if (f > 1.0e-3) return f;
    }
    return Double.NaN;
  }

  private static double[] row(double[][] data) {
    if (data.length == 0) return new double[0];
    return data[0];
  }

  private static double runCase(String meshLabel, int meshSize, boolean hot) throws Exception {
    Model model = ModelUtil.load("model_" + meshLabel + "_" + (hot ? "hot" : "cold"), COLD_MODEL);
    double a = A0_M;
    double b = B0_M;
    double h = H0_M;
    if (hot) {
      a += DA_UM * 1e-6;
      b += DB_UM * 1e-6;
      h += DH_UM * 1e-6;
    }
    model.param().set("a", a + "[m]");
    model.param().set("b", b + "[m]");
    model.param().set("height", h + "[m]");
    model.component("comp1").geom("geom1").run();
    model.component("comp1").mesh("mesh1").autoMeshSize(meshSize);
    model.component("comp1").mesh("mesh1").run();
    int elems = model.component("comp1").mesh("mesh1").getNumElem();
    int verts = model.component("comp1").mesh("mesh1").getNumVertex();

    long start = System.nanoTime();
    model.study("std1").run();
    long elapsedMs = (System.nanoTime() - start) / 1000000L;

    model.result().numerical().create("freq_phase7", "EvalGlobal");
    model.result().numerical("freq_phase7").set("data", "dset2");
    model.result().numerical("freq_phase7").set("expr", new String[] {"freq"});
    model.result().numerical("freq_phase7").set("unit", new String[] {"GHz"});
    double[] freqs = row(model.result().numerical("freq_phase7").getReal());
    double physical = firstPhysical(freqs);
    System.out.println("case=" + meshLabel + "," + (hot ? "hot" : "cold"));
    System.out.println("mesh_elements=" + elems);
    System.out.println("mesh_vertices=" + verts);
    System.out.println("frequency_GHz=" + physical);
    System.out.println("solve_elapsed_s=" + (elapsedMs / 1000.0));
    System.out.println("case_row=" + meshLabel + "," + meshSize + "," + (hot ? "hot" : "cold") + "," + elems + "," + verts + "," + physical + "," + (elapsedMs / 1000.0));
    ModelUtil.remove(model.tag());
    return physical;
  }

  public static void main(String[] args) throws Exception {
    String[] labels = new String[] {"coarse", "normal", "fine"};
    int[] sizes = new int[] {4, 3, 2};
    for (int i = 0; i < labels.length; i++) {
      double cold = runCase(labels[i], sizes[i], false);
      double hot = runCase(labels[i], sizes[i], true);
      double delta = hot - cold;
      double rel = delta / cold;
      System.out.println("summary_row=" + labels[i] + "," + sizes[i] + "," + cold + "," + hot + "," + delta + "," + (delta * 1e6) + "," + rel);
    }
  }
}
