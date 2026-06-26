import com.comsol.model.Model;
import com.comsol.model.util.ModelUtil;

public class ComsolPhase4bSample {
  public static void main(String[] args) throws Exception {
    Model model = ModelUtil.load("model", args[0]);
    model.result().numerical().create("ip1", "Interp");
    model.result().numerical("ip1").set("data", "dset2");
    model.result().numerical("ip1").set("expr", new String[] {
      "emw.Hphi", "emw.Hz", "sqrt(abs(emw.Hphi)^2+abs(emw.Hz)^2)", "freq"
    });
    model.result().numerical("ip1").set("unit", new String[] {"A/m", "A/m", "A/m", "Hz"});
    try {
      model.result().numerical("ip1").set("solnum", "4");
    } catch (Throwable t) {
      System.out.println("set_solnum_error=" + t.getClass().getName() + ": " + t.getMessage());
    }
    int n = 41;
    double[][] coord = new double[2][n];
    for (int i = 0; i < n; i++) {
      coord[0][i] = 0.025;
      coord[1][i] = 0.1 * i / (n - 1);
    }
    model.result().numerical("ip1").set("coord", coord);
    double[][] real = model.result().numerical("ip1").getReal();
    System.out.println("rows=" + real.length);
    for (int i = 0; i < real.length; i++) {
      for (int j = 0; j < real[i].length; j++) {
        System.out.println("real[" + i + "][" + j + "]=" + real[i][j]);
      }
    }
    ModelUtil.remove(model.tag());
  }
}
