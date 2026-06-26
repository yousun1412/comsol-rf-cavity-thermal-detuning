import com.comsol.model.Model;
import com.comsol.model.util.ModelUtil;

public class ComsolPhase6ThermalDetuning {
  private static final String BASE =
      "E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics";
  private static final String COLD_MODEL =
      BASE + "\\models\\comsol\\pillbox_cavity_baseline.mph";
  private static final String HOT_MODEL =
      BASE + "\\models\\comsol\\phase6_thermal_detuning.mph";
  private static final double COLD_FREQ_GHZ = 1.498961448338762;
  private static final double A0_M = 0.025;
  private static final double B0_M = 0.100;
  private static final double H0_M = 0.100;

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

  public static void main(String[] args) throws Exception {
    String[] ids = new String[] {"max_power2_h100", "medium_power1_h500"};
    double[] powerScale = new double[] {2.0, 1.0};
    double[] hConv = new double[] {100.0, 500.0};
    double[] maxDeltaT = new double[] {3.51964763520493, 0.443714518850641};
    double[] daUm = new double[] {1.339523101494558, 0.1374748434112485};
    double[] dbUm = new double[] {3.6213616121719276, 0.39556743643318265};
    double[] dhUm = new double[] {2.097210664859107, 0.26076512932768936};

    for (int c = 0; c < ids.length; c++) {
      Model model = ModelUtil.load("model" + c, COLD_MODEL);
      double aHot = A0_M + daUm[c] * 1e-6;
      double bHot = B0_M + dbUm[c] * 1e-6;
      double hHot = H0_M + dhUm[c] * 1e-6;
      model.param().set("a", aHot + "[m]");
      model.param().set("b", bHot + "[m]");
      model.param().set("height", hHot + "[m]");

      model.component("comp1").geom("geom1").run();
      model.component("comp1").mesh("mesh1").run();

      long start = System.nanoTime();
      model.study("std1").run();
      long elapsedMs = (System.nanoTime() - start) / 1000000L;

      model.result().numerical().create("freq_phase6", "EvalGlobal");
      model.result().numerical("freq_phase6").set("data", "dset2");
      model.result().numerical("freq_phase6").set("expr", new String[] {"freq"});
      model.result().numerical("freq_phase6").set("unit", new String[] {"GHz"});
      double[] freqs = row(model.result().numerical("freq_phase6").getReal());
      System.out.println("case=" + ids[c]);
      for (int i = 0; i < freqs.length; i++) {
        System.out.println("freq_GHz[" + i + "]=" + freqs[i]);
      }
      double hot = firstPhysical(freqs);
      double delta = hot - COLD_FREQ_GHZ;
      double rel = delta / COLD_FREQ_GHZ;
      int elems = model.component("comp1").mesh("mesh1").getNumElem();
      int verts = model.component("comp1").mesh("mesh1").getNumVertex();
      double elapsedS = elapsedMs / 1000.0;
      System.out.println("hot_first_physical_GHz=" + hot);
      System.out.println("delta_f_GHz=" + delta);
      System.out.println("relative_detuning=" + rel);
      System.out.println("mesh_elements=" + elems);
      System.out.println("mesh_vertices=" + verts);
      System.out.println("solve_elapsed_s=" + elapsedS);
      System.out.println("csv_row=" + ids[c] + "," +
          powerScale[c] + "," +
          hConv[c] + "," +
          maxDeltaT[c] + "," +
          "equivalent_parameterized_geometry_approximation," +
          aHot + "," +
          bHot + "," +
          hHot + "," +
          daUm[c] + "," +
          dbUm[c] + "," +
          dhUm[c] + "," +
          COLD_FREQ_GHZ + "," +
          hot + "," +
          delta + "," +
          (delta * 1.0e6) + "," +
          rel + "," +
          elems + "," +
          verts + "," +
          elapsedS);

      if (c == 0) {
        model.save(HOT_MODEL);
        System.out.println("saved_model=" + HOT_MODEL);
      }
      ModelUtil.remove(model.tag());
    }
  }
}
