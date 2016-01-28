package com.active.qa.automation.web.test4fun.testcases.project;

import com.active.qa.automation.web.test4fun.util.project.TestDriverUtil;

/**
 * Created by tchen on 1/18/2016.
 */
public class TestCaseLoader {
//  private static String defaultScript =
//      com.activenetwork.qa.awo.testcases.sanity.orms.migrauto.
//
//
//          MA_LM_Login
//          .class.getName();
  public static void main(String[] args) {

    //Test non-static method

//		LM_PhantomjsLogin test = new LM_PhantomjsLogin();
//		test.setUp();

    //Test Static method
		SeleniumTest.Test();


//    TestDriverUtil.load(args, defaultScript);
  }
}
