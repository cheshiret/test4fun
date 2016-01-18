package com.active.qa.automation.web.testrunner;

/**
 * Created by tchen on 1/18/2016.
 */
public interface TestConstants {
    //test result code
    public static final int RESULT_FAILED  = 1;
    public static final int RESULT_PASSED  = 2;
    public static final int RESULT_NOT_RUN = 3;
    public static final int RESULT_NA      = 4;
    public static final int RESULT_BLOCKED = 5;
    public static final int RESULT_CAUTION = 6;
    public static final int RESULT_MEMERROR =7;
    public static final int RESULT_HIBERNATED =8;
    public static final int RESULT_JVMCRASH = 13;

    //execution status code
    public static final int EXECUTION_FINISH    = 0;
    public static final int EXECUTION_SUBMITTED = 1;
    public static final int EXECUTION_RUNNING   = 2;
    public static final int EXECUTION_STOP    = 3;
    public static final int EXECUTION_NA      = 4;
    public static final int EXECUTION_SUSPEND    = 5;
    public static final int EXECUTION_HIBERNATED = 8;

    //Test case status
    public static final int INACTIVE=0;
    public static final int ACTIVE=1;
    public static final int DRAFT=2;

    //node status
    public static final int IDLE=0;
    public static final int BUSY=1;
    public static final int SUSPEND=2;
    public static final int OCAM_RESTART=3;

    //test suite status
    public static final int TESTSUITE_PENDING=0;
    public static final int TESTSUITE_INPROGRESS=1;
    public static final int TESTSUITE_SUSPEND=2;
    public static final int TESTSUITE_FINISH=3;

    //Test case execution status
    public static final int TESTCASE_PENDING=0;
    public static final int TESTCASE_RUNNING=1;
    public static final int TESTCASE_FINISHED=2;
    public static final int TESTCASE_HIBERNATED=3;


    //tool code
    public static final int NONE=0;
    public static final int RFT=1;
    public static final int SELENIUM=2;
//	public static final int WATIJ=3;

    //app code
    public static final int WEB=0;
    public static final int SM=1;
    public static final int FLEX=2;
    public static final int EFT=3;
    public static final int REPORT=4;

    public static final int NORMAL_PRIORITY=3;
    public static final int SANITY_PRIORITY=6;
    public static final int PRODUCTION_PRIORITY=7;
    public static final int UNKNOWN_PRIORITY=-1;

    public static final boolean STATUS_TRUE = true;
    public static final boolean STATUS_FALSE = false;

    //begain: added by pzhu: AUTO-1494
    public static final int PRODUCTION_NONE = 0; 	//do not select for sanity testing.
    public static final int PRODUCTION_FULL = 1;	//select and run full test data for sanity testing.
    public static final int PRODUCTION_PARTIAL = 2; //select and run partial test data for sanity testing.(based on test data type(DATA_FETCH_RANDOM) of the test cases)

    public static final int DATA_FETCH_NONE = 0; //do not use this data
    public static final int DATA_FETCH_MANDATORY = 1; //must be used
    public static final int DATA_FETCH_RANDOM = 2; //randomly select one of them
    //end

}

