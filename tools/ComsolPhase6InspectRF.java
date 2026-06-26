import com.comsol.model.Model;
import com.comsol.model.util.ModelUtil;

public class ComsolPhase6InspectRF {
  public static void main(String[] args) throws Exception {
    String path = "E:\\RND_Project_Portfolio\\08_rf_cavity_cae_multiphysics\\models\\comsol\\pillbox_cavity_baseline.mph";
    Model model = ModelUtil.load("model", path);
    System.out.println("model=" + path);
    System.out.println("params=" + String.join(",", model.param().varnames()));
    for (String p : model.param().varnames()) {
      try {
        System.out.println("param " + p + "=" + model.param().get(p) + " descr=" + model.param().descr(p));
      } catch (Throwable t) {
        System.out.println("param " + p + " read_error=" + t.getMessage());
      }
    }
    System.out.println("components=" + String.join(",", model.component().tags()));
    if (model.component().tags().length > 0) {
      String comp = model.component().tags()[0];
      System.out.println("geom_tags=" + String.join(",", model.component(comp).geom().tags()));
      for (String g : model.component(comp).geom().tags()) {
        System.out.println("geom " + g + " dim=" + model.component(comp).geom(g).getSDim());
        System.out.println("geom_features=" + String.join(",", model.component(comp).geom(g).feature().tags()));
        for (String f : model.component(comp).geom(g).feature().tags()) {
          try {
            System.out.println("feature " + f + " type=" + model.component(comp).geom(g).feature(f).getType());
          } catch (Throwable t) {
            System.out.println("feature " + f + " type_error=" + t.getMessage());
          }
        }
      }
      System.out.println("physics=" + String.join(",", model.component(comp).physics().tags()));
      System.out.println("mesh=" + String.join(",", model.component(comp).mesh().tags()));
    }
    System.out.println("studies=" + String.join(",", model.study().tags()));
    System.out.println("datasets=" + String.join(",", model.result().dataset().tags()));
    ModelUtil.remove(model.tag());
  }
}
