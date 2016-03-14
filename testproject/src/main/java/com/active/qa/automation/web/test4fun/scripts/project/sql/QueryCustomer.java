package com.active.qa.automation.web.test4fun.scripts.project.sql;

import com.active.qa.automation.web.test4fun.util.project.DatabaseInst;
import com.active.qa.automation.web.testapi.exception.ErrorOnDataException;

import java.util.List;

/**
 * Load customer from database to find proper test data
 * Created by tchen on 1/18/2016.
 */
public class QueryCustomer {
  public DatabaseInst db;

  public QueryCustomer() {
    db = DatabaseInst.getInstance();
  }

  public QueryCustomer(DatabaseInst omConn) {
    this.db = omConn;
  }

  public List<String[]> searchCustWithCustId(String schema, String custId) {
    List<String[]> result = null;
    String[] colNames = { "custFName", "custLName", "businessName", "dateOfBirth", "hPhone", "email"};
    String query = "select F_NAME as custFName, L_NAME as custLName, ORG_NAME as businessName, birthday as dateOfBirth, \"'HOME'\" as hPhone, \"'EMAIL'\" as email from ("
        +"SELECT c.F_NAME, c.L_NAME, c.ORG_NAME, cp.BIRTHDAY,ccp.typ, ccp.val FROM c_cust c "
        +"LEFT JOIN c_cust_hfprofile cp ON cp.cust_id=c.cust_id "
        +"LEFT JOIN C_CUST_PHONE ccp ON ccp.cust_id=c.cust_id "
        +"WHERE cp.cust_number="+custId
        +" AND ccp.UNUSABLE=0) "
        +"pivot (min(val) for typ in('HOME','EMAIL'))";
    db.resetSchema(schema);
    result = db.executeQuery(query, colNames);
    if (null == result || result.size() < 1) {
      throw new ErrorOnDataException("Can't find customer info with customer number:"+custId+" under schema:"+schema);
    }
    db.disconnect();
    return result;
  }
}
