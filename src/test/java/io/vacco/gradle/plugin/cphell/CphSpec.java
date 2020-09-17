package io.vacco.gradle.plugin.cphell;

import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import org.slf4j.*;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

import static j8spec.J8Spec.*;

@RunWith(J8SpecRunner.class)
public class CphSpec {

  private static final Logger log = LoggerFactory.getLogger(CphSpec.class);

  static {
    describe("Artifact scanning", () -> it("can find duplicate resources inside two jars", () -> {
      File jar0 = new File("/home/jjzazuet/.gradle/caches/modules-2/files-2.1/org.hamcrest/hamcrest-all/1.3/63a21ebc981131004ad02e0434e799fd7f3a8d5a/hamcrest-all-1.3.jar");
      File jar1 = new File("/home/jjzazuet/.gradle/caches/modules-2/files-2.1/org.hamcrest/hamcrest-core/1.3/42a25dc3219429f0e5d060061f71acb49bf010a0/hamcrest-core-1.3.jar");

      CphResourceIdx idx = new CphResourceIdx();
      Predicate<String> pred = CphTaskUtil.defaultResourceInclude(new ArrayList<>());

      idx.add(jar0);
      idx.add(jar1);

      List<Map.Entry<String, List<File>>> allDupes = idx.getDuplicates(false, pred);
      List<Map.Entry<String, List<File>>> nonExactDupes = idx.getDuplicates(true, pred);

      log.warn("{}", allDupes.size());
      log.warn("{}", nonExactDupes.size());

      CphTask.reportDuplicates("test", allDupes, log);
      log.warn("===================");
      CphTask.reportDuplicates("test", nonExactDupes, log);
    }));
  }

}