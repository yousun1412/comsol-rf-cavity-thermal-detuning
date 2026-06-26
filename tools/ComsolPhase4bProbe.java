import com.comsol.model.Model;
import com.comsol.model.util.ModelUtil;

public class ComsolPhase4bProbe {
  private static void tryExpr(Model model, String expr, String unit) {
    try {
      String tag = "n" + Math.abs(expr.hashCode());
      model.result().numerical().create(tag, "AvLine");
      model.result().numerical(tag).set("data", "dset2");
      model.result().numerical(tag).selection().geom("geom1", 1);
      model.result().numerical(tag).selection().set(new int[] {1});
      model.result().numerical(tag).set("expr", expr);
      if (unit != null && unit.length() > 0) {
        model.result().numerical(tag).set("unit", unit);
      }
      double[][] real = model.result().numerical(tag).getReal();
      System.out.println("OK expr=" + expr + " unit=" + unit + " value=" + real[0][0]);
      model.result().numerical().remove(tag);
    } catch (Throwable t) {
      System.out.println("FAIL expr=" + expr + " unit=" + unit + " error=" + t.getClass().getName() + ": " + t.getMessage());
    }
  }

  public static void main(String[] args) throws Exception {
    Model model = ModelUtil.load("model", args[0]);
    System.out.println("datasets=" + String.join(",", model.result().dataset().tags()));
    System.out.println("physics=" + String.join(",", model.component("comp1").physics().tags()));

    String[] exprs = new String[] {
      "emw.Qsh", "emw.Qrh", "emw.Qh", "emw.Qe", "emw.Qtot",
      "emw.Ploss", "emw.Plossd", "emw.Poav", "emw.Poavx", "emw.Poavy", "emw.Poavz",
      "emw.normH", "emw.Hr", "emw.Hphi", "emw.Hz", "emw.Hx", "emw.Hy",
      "sqrt(abs(emw.Hr)^2+abs(emw.Hphi)^2+abs(emw.Hz)^2)",
      "emw.normE", "emw.Er", "emw.Ephi", "emw.Ez"
    };
    for (String expr : exprs) {
      tryExpr(model, expr, "");
    }
    ModelUtil.remove(model.tag());
  }
}
