package com.github.nikolajr93.studenttestingintellijplugin.api;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.runner.parallel.ZeroCodeLoadRunner;
import org.junit.runner.RunWith;

@LoadWith("load_generation_config.properties")
@TestMapping(testClass = JunitExistingRestTest.class, testMethod = "testRafStudentsGetApi")
@RunWith(ZeroCodeLoadRunner.class)
public class LoadExistingJUnitRestTest {
}
