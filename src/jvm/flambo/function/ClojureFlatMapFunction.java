package flambo.function;

import flambo.Utils;
import org.apache.spark.api.java.function.FlatMapFunction;
import clojure.lang.RT;
import clojure.lang.IFn;

import java.util.List;
import java.util.ArrayList;

public class ClojureFlatMapFunction extends FlatMapFunction<Object, Object> {
  List<String> _fnSpec;
  List<Object> _params;
  FlatMapFunction _fn;
  boolean _booted = false;

  public ClojureFlatMapFunction(List fnSpec, List<Object> params) {
    _fnSpec = fnSpec;
    _params = params;
  }

  private void bootClojure() {
    if(!_booted) {
      try {
        IFn hof = Utils.loadClojureFn(_fnSpec.get(0), _fnSpec.get(1));
        _fn = (FlatMapFunction) hof.applyTo(RT.seq(_params));
        _booted = true;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public Iterable<Object> call(Object o) throws Exception {
    bootClojure();
    return (Iterable) _fn.call(o);
  }
}