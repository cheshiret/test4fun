package com.active.qa.automation.web.test4fun;

import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.util.DateFunctions;
import com.active.qa.automation.web.testapi.util.TestProperty;

/**
 * Created by tchen on 1/28/2016.
 */
public interface TestConstants extends TestApiConstants {
  public static final String TIMESTAMP = DateFunctions.getLongTimeStamp();
  public static final String DATESTAMP = DateFunctions.getLongDateStamp();

  //Timing
  public static final int NO_MORE_SLEEP = 0;
  public static final int SHORT_SLEEP_BEFORE_CHECK = 1;
  public static final int DYNAMIC_SLEEP_BEFORE_CHECK = 5; //Mainly use for UWP page loading. We can update it based on the network speed
  public static final int SLEEP_TWENTY_SECOND_BEFORE_CHECK = 20;
  public static final int SLEEP_FIFTEN_SECOND_BEFORE_CHECK = 20;
  public static final int SLEEP_ONE_MINUTE_BEFORE_CHECK = 60; //Using befoer checking timer is changed in shopping cart page

  public static final int PAGE_LOADING_TRESHOLD = Integer.parseInt(TestProperty.getProperty("page.loading.treshold"));
  public static final int CHECK_REPORT_IN_MAILBOX_THRESHOLD = Integer.parseInt(TestProperty.getProperty("check.email.report.wait"));
  public static final int CHECK_NOTIFICATION_IN_MAILBOX_THRESHOLD = Integer.parseInt(TestProperty.getProperty("check.email.notification.wait"));
  public static final int CHECK_NOTIFICATION_IN_MAILBOX_TIMEDIFF = Integer.parseInt(TestProperty.getProperty("check.email.notification.timediff"));


  // Values for <projectID> in RecordTestRun2
  public static final int PROJECT_AUTOMATION = 7;

  public static final int PROJECT_LEGACY = 4;

  public static final int PROJECT_ORMS = 5;

  public static final int PROJECT_SANDBOX = 6;

  public static final int PROJECT_WEB = 8;

  // constants pointing to values in properties file where WEB contract URLs
  // reside
  public static final String WEB_URL_RA = ".web.ra.url";

  public static final String WEB_URL_RECGOV = ".web.recgov.url";

  public static final String WEB_URL_NY = ".web.ny.url";

  public static final String WEB_URL_NE = ".web.ne.url";

  public static final String WEB_URL_MS = ".web.ms.url";

  public static final String WEB_URL_CO = ".web.co.url";

  public static final String WEB_URL_KY = ".web.ky.url";

  public static final String WEB_URL_SC = ".web.sc.url";

  public static final String WEB_URL_WI = ".web.wi.url";

  public static final int TOTAL_CONTRACTS = 9;

  public static final int TOTAL_STATE_CONTRACTS = 7;

  // (web) contract constants
  public static final String CONTRACT_RA = "ra";

  public static final String CONTRACT_RECGOV = "recgov";

  public static final String CONTRACT_NY = "ny";

  public static final String CONTRACT_NE = "ne";

  public static final String CONTRACT_MS = "ms";

  public static final String CONTRACT_CO = "co";

  public static final String CONTRACT_KY = "ky";

  public static final String CONTRACT_SC = "sc";

  public static final String CONTRACT_WI = "wi";

  // QA database parameter indices or names (data_key)
  public static final int TD_PARAM_IDX_TESTSETID = 0;

  public static final int TD_PARAM_IDX_VARNAME1 = 1;

  public static final int TD_PARAM_IDX_VARNAME2 = 2;

  public static final int TD_PARAM_IDX_VARNAME3 = 3;

  public static final String DATAKEY_CHECKIN = "processCheckIn";

  public static final String DATAKEY_CHECKOUT = "processCheckOut";

  public static final String DATAKEY_STAYLENGTH_MODIFIER = "numNightsDiff";

  public static final String DATAKEY_ARRIVALDATE_MODIFIER = "arrivalDateShift";

  public static final String DATAKEY_INIT_SITENUM = "initSiteNum";

  public static final String DATAKEY_INIT_SITEPREFIX = "initSitePrefix";

  public static final String TESTID_GENERIC = "-1";

  //agent status
  public static final int AGENT_ACTIVE_STATUS = 1;

  public static final int AGENT_INACTIVE_STATUS = 2;

  public static final int AGENT_REVOKED_STATUS = 3;

  public static final int AGENT_CLOSED_STATUS = 4;


  // Reservation order status constants
  public static final int ORD_STATUS_ACTIVE = 1;

  public static final int ORD_STATUS_CANCELLED = 2;

  public static final int ORD_STATUS_VOIDED = 3;

  public static final int ORD_STATUS_NOSHOW = 4;

  public static final int ORD_STATUS_LOCKED = 5;

  //Order Item Status
  public static final int ORD_ITEM_STATUS_ACTIVE = 1;

  public static final int ORD_ITEM_STATUS_VOIDED = 2;

  public static final int ORD_ITEM_STATUS_INACTIVE = 3;

  public static final int ORD_ITEM_STATUS_REVERSED = 11;

  public static final int ORD_ITEM_STATUS_RETURNED = 12;

  public static final int ORD_ITEM_STATUS_CHARGED = 13;

  public static final int ORD_ITEM_STATUS_EXPIRED_CHARGED = 14;

  public static final int ORD_ITEM_STATUS_DOCKED = 21;

  public static final int ORD_ITEM_STATUS_UNDOCKED = 22;

  public static final int ORD_ITEM_STATUS_CHECK_OUT = 23;

  // privilege item status
  public static final int PRIV_STATUS_ACTIVE = 1;

  public static final int PRIV_STATUS_TRANSFERRED = 4;

  public static final int PRIV_STATUS_REVERSED = 5;

  public static final int PRIV_STATUS_VOIDED = 6;

  public static final int PRIV_STATUS_INVALID = 7;

  public static final int PRIV_STATUS_REVOKED = 9;

  public static final String RELEASE_STATUS = "Released";

  public static final String ACTIVE_STATUS = "Active";

  public static final String CANCELLED_STATUS = "Cancelled";

  public static final String NO_SHOW_STATUS = "No Show";

  public static final String INACTIVE_STATUS = "Inactive";

  public static final String VERIFIED_STATUS = "Verified";

  public static final String NOTAPPLICABLE_STATUS = "Not Applicable";

  public static final String FAILED_STATUS = "Failed";

  public static final String YES_STATUS = "Yes";

  public static final String NO_STATUS = "No";

  public static final String ALL_STATUS = "All";

  public static final String VOIDED_STATUS = "Voided";

  public static final String VOID_STATUS = "Void";

  public static final String INVALID_STATUS = "Invalid";

  public static final String REVERSED_STATUS = "Reversed";

  public static final String EXPIRED_STATUS = "Expired";

  public static final String CHARGED_STATUS = "Charged";

  public static final String EXPIRED_CHARGED_STATUS = "Expired Charged";

  public static final String PENDING_STATUS = "Pending";

  public static final String COMPLETED_STATUS = "Completed";

  public static final String RUNNING_STATUS = "RUNNING";

  public static final String REVOKED_STATUS = "Revoked";

  public static final String RETURNED_STATUS = "Returned";

  public static final String APPROVED_STATUS = "Approved";

  public static final String ISSUED_STATUS = "Issued";

  public static final String SURRENDERED_STATUS = "Surrendered";

  public static final String TRANSFERABLE_STATUS = "Transferable";

  public static final String TRANSFERRED_STATUS = "Transferred";

  public static final String PREARRIVAL_STATUS = "Pre Arrival";

  public static final String CHECKIN_STATUS = "Checked In";

  public static final String DOCKED_STATUS = "Docked";

  public static final String NOTSHARED_STATUS = "Not Shared";

  public static final String SHARED_STATUS = "Shared";

  public static final String UNDOCKED_STATUS = "Undocked";

  public static final String CHECKOUT_STATUS = "Checked Out";

  public static final String UNCONFIRMED_STATUS = "UnConfirmed";

  public static final String CONFIRMED_STATUS = "Confirmed";

  public static final String CLOSED_STATUS = "Closed";

  public static final String CANCEL_STATUS = "Cancelled";

  public static final String AWARDED_STATUS = "Awarded";

  public static final String ENTERED_STATUS = "Entered";

  public static final String NOTAWARDED_STATUS = "Not Awarded";

  public static final String AWARDEDACCEPTEDBYCUSTOMER_STATUS = "Award Accepted by Customer";

  public static final String AWARDEDDECLINEDBYCUSTOMER_STATUS = "Award Declined by Customer";

  public static final String REJECTED_STATUS = "Rejected";

  //privilege inventory status
  public static final String PRIV_INV_STATUS_AVAILABLE = "Available";

  public static final String PRIV_INV_STATUS_USED = "Used";

  public static final String PRIV_INV_STATUS_SOLD = "Sold";

  public static final String PRIV_INV_STATUS_RETURNED = "Returned";

  public static final String PRIV_INV_STATUS_WITHDRAWN = "Withdrawn";

  // privilege inventory status code
  public static final int PRIV_INV_STATUS_AVAILABLE_CODE = 3;

  public static final int PRIV_INV_STATUS_SOLD_CODE = 2;

  // public static final int PRIV_INV_STATUS_WITHDRAWN = -1;
  //
  // public static final int PRIV_INV_STATUS_RETURNED = -1;

  public static final int EVT_STATUS_ACTIVE = 1;

  public static final int EVT_STATUS_CLOSED = 8;

  public static final int EVT_STATUS_VOID = 3;

  // reservation status value in db
  public static final int PROC_STATUS_PREARRIVAL = 1;

  public static final int PROC_STATUS_CHECKEDIN = 2;

  public static final int PROC_STATUS_CHECKEDOUT = 3;

  public static final int PROC_STATUS_ENTERED = 4;

  public static final int PROC_STATUS_AWARDED = 5;

  public static final int PROC_STATUS_DENIED = 6;

  public static final int PROC_STATUS_REVOKED = 7;

  public static final int PROC_STATUS_RESERVED = 8;

  public static final int PROC_STATUS_ISSUED = 9;

  public static final int CONF_STATUS_FULLCONFIRMED = 3;

  public static final int CONF_STATUS_CONFIRMED = 2;

  public static final int CONF_STATUS_UNCONFIRMED = 1;

  // Payment group, status, and type constants
  public static final int PMT_GROUP_CASH = 1;

  public static final int PMT_GROUP_NONCASH = 2;

  public static final int PMT_GROUP_CC = 3;

  public static final int PMT_GROUP_NONDEPOSIT = 4;

  public static final int PMT_GROUP_ACH = 5;

  public static final int PMT_GROUP_VOUCHER = 6;

  public static final int PMT_GROUP_DEF_CASH = 7;

  public static final int PMT_STATUS_RECEIVED = 2;

  public static final int PMT_STATUS_VOIDED = 5;

  public static final String PMT_TYPE_VISA = "VISA";

  public static final String PMT_TYPE_MASTERCARD = "MASTERCARD";

  public static final String PMT_TYPE_AMEX = "AMEX";

  public static final String PMT_TYPE_DISCOVER = "DISCOVER";

  public static final String PMT_TYPE_CASH = "CASH";

  public static final String PMT_TYPE_GIFTCARD = "GIFT CARD";

  // Refund data constants
  public static final int REFUND_GRP_CASH = 1;

  public static final int REFUND_GROUP_NONCASH = 2;

  public static final int REFUND_GROUP_CC = 3;

  public static final int REFUND_GROUP_NONDEPOSIT = 4;

  public static final int REFUND_GROUP_ACH = 5;

  public static final int REFUND_GROUP_VOUCHER = 6;

  // public static final String REFUND_STATUS_
  public static final String REFUND_TYPE_VCHR = "VCHR";

  // sleep time
  public static final int OCCAM_SYNC_TIME = Integer.parseInt(TestProperty
      .getProperty("occam.loading.sync"));
  public static final int CACHE_SYNC_TIME = Integer.parseInt(TestProperty
      .getProperty("cache.loading.sync"));
  public static final int SCHEDULER_WAIT_TIME = Integer.parseInt(TestProperty
      .getProperty("schedule.wait.sync")); // wait for report scheduler
  // running
  public static final int PAGELOADING_SYNC_TIME = Integer
      .parseInt(TestProperty.getProperty("page.loading.sync"));

  /**
   * the amount of time (in seconds) to wait between attempts to find the
   * object
   */
  public static final int FIND_OBJECT_WAIT_BETWEEN_RETRY = 1;

  /**
   * The extra number of tries for searching object after browser finished
   * loading
   */
  public static final int EXTRA_TRYS = 60;

  public static final int FREEZE_COUNT = 15;

  /**
   * date range to check when cleanup inventories
   */
  public static final int CLEANUP_PERIOD = 15;

  /**
   * Automation tool types
   */
  public static final int RFT8 = 1;
  public static final int SELENIUM = 2;

  /**
   * Browser types
   */
  public static final int IE = 0;
  public static final int IE_HTML_DIALOG = 1;
  public static final int IE_DIALOG = 2;
  public static final int FIREFOX = 3;
  // public static final int IE=0;

  /**
   * Product code
   */
  public static final int ORMS = 0;
  public static final int RA = 1;
  public static final int BW = 2;
  public static final int RECGOV = 3;
  public static final int PL = 4;
  public static final int WEB_APP = 5;
  public static final int HF = 6;
  public static final int CUI = 7;
  /**
   * Product Category ID
   */
  public static final String PRDCAT_SITE = "3"; // site
  public static final String PRDCAT_POS = "4"; // pos
  public static final String PRDCAT_PERMIT = "5"; // permit
  public static final String PRDCAT_TICKET = "6"; // ticket
  public static final String PRDCAT_GIFTCARD = "7"; // gift card
  public static final String PRDCAT_ASSET = "8";//asset
  public static final String PRDCAT_LOTTERY = "9";//lottery
  public static final String PRDCAT_PRIVILEGE = "10";//privilege
  public static final String PRDCAT_VEHICLERTI = "11";//vehicle RTI
  public static final String PRDCAT_SERVICE = "12";//service
  public static final String PRDCAT_FACILITY = "31";//Facility
  public static final String PRDCAT_SUPPLY = "15";//supply
  public static final String PRDCAT_LIST = "16";//list
  public static final String PRDCAT_SLIP = "20";//slip
  public static final String PRDCAT_ACTIVITY = "30";//Activity
  public static final String PRDCAT_OTHER = "50";//Activity

  /**
   * Product sub Category ID
   */
  public static final String PRDSUBCAT_PRDTYPE_FACILITY = "23"; //	Facility
  public static final String PRDSUBCAT_PRDTYPE_ACTIVITY = "20"; // Activity

  /**
   * Unit of stay type ID
   */
  public static final String UNITOFSTAY_DAY = "2"; // DAY USE
  public static final String UNITOFSTAY_OVERNIGHT = "1"; // OVERNIGHT

  /**
   * Agency ID
   */

  public static final String AGENCY_USFORESTSERVICE_ID = "70903"; // US Forest Service
  public static final String AGENCY_NATIONALPARKSERVICE_ID = "70904"; // National Park Service
  public static final String AGENCY_FISHANDWILDLIFESERVICE_ID = "70906"; // Fish and Wildlife Service
  public static final String AGENCY_USARMYCORPSOFENGINEERS_ID = "70902"; // US Army Corps of Engineers

  /**
   * Product Category Name
   */
  public static final String PRDCAT_NAME_POS = "POS";

  /**
   * Season ID
   */
  public static final String SEASON_PEAK = "1"; // Peak
  public static final String SEASON_NONPEAK = "2"; // Non Peak
  public static final String SEASON_WALKIN = "3"; // Walk In
  public static final String SEASON_CORE = "10"; // Core
  public static final String SEASON_OFFPEAK = "11"; // Off Peak

  /**
   * Out of state ID
   */
  public static final String STATE_IN = "0"; // In State
  public static final String STATE_OUT = "1"; // Out Of State
  public static final String STATE_ALL = "2"; // All

  /**
   * Fee type ID
   */
  public static final String FEETYPE_RATRANFEE = "1"; // RA Transaction Fee
  public static final String FEETYPE_USEFEE = "2"; // Use Fee
  public static final String FEETYPE_TRANFEE = "4"; // Transaction Fee
  public static final String FEETYPE_ATTRFEE = "12"; // Attribute Fee
  public static final String FEETYPE_POSFEE = "13"; // POS Fee
  public static final String FEETYPE_TICKETFEE = "18"; // Ticket Fee
  public static final String FEETYPE_PENALTY = "31"; // Penalty
  public static final String FEETYPE_TAX = "32"; // Tax
  public static final String FEETYPE_PRIVILEGEFEE = "34"; // Privilege fee
  public static final String FEETYPE_VENDORFEE = "36"; // Vendor fee
  public static final String FEETYPE_ACTIVITYFEE = "39"; // Activity fee
  public static final String FEETYPE_PRIVILEGELOTTERYFEE = "37"; // Privilege lottery fee

  /**
   * Fee Type Name
   */
  public static final String FEETYPE_NAME_POSFEE = "POS Fee";
  public static final String FEETYPE_NAME_TRANSACTIONFEE = "Transaction Fee";
  public static final String FEETYPE_NAME_PENALTY = "Penalty";
  public static final String FEETYPE_NAME_USEFEE = "Use Fee";
  public static final String FEETYPE_NAME_ATTRIBUTEFEE = "Attribute Fee";
  public static final String FEETYPE_NAME_TAX = "Tax";
  public static final String FEETYPE_NAME_DISCOUNT = "Discount";
  public static final String FEETYPE_NAME_RAFEE = "RA Fee";
  public static final String FEETYPE_NAME_REFUND = "Refund";
  public static final String FEETYPE_NAME_RATRANSACTIONFEE = "RA Transaction Fee";
  public static final String FEETYPE_NAME_ACTIVITYFEE = "Activity Fee";
  public static final String FEETYPE_NAME_VENDORFEE = "Vendor Fee";
  public static final String FEETYPE_NAME_FACILITYFEE = "Facility Fee";

  /**
   * Fee Schedule Type ID
   */
  public static final String FEESCHDTYPE_NORMALSCHD = "0"; // Normal fee
  // schedule
  public static final String FEESCHDTYPE_RAFEESCHD = "1"; // RA fee schedule
  public static final String FEESCHDTYPE_RAFEETHESHDSCHD = "2"; // RA fee Threshold
  public static final String FEESCHDTYPE_RAFEESCHD_SLIP = "17";//RA fee schedule of Slip
  public static final String FEESCHDTYPE_RAFEETHESHDSCHD_SLIP = "18"; // RA fee Threshold of Slip
  // threshold
  // schedule

  /**
   * Sales channel ID
   */
  public static final String SALESCHAN_ALL = "1"; // All
  public static final String SALESCHAN_WEB = "2"; // Web
  public static final String SALESCHAN_CALLCENTER = "3"; // Call Center
  public static final String SALESCHAN_FIELD = "4"; // Field
  public static final String SALESCHANL_NONE = "0";// recipient schedule id,
  // all

  /**
   * Sales channel Name
   */
  public static final String SALESCHAN_NAME_ALL = "All";
  public static final String SALESCHAN_NAME_WEB = "Web";
  public static final String SALESCHAN_NAME_CALLCENTER = "Call Center";
  public static final String SALESCHAN_NAME_FIELD = "Field";

  /**
   * Application ID
   */
  public static final int APPLICATION_ID_ADMIN_MANAGER = 1;
  public static final int APPLICATION_ID_FINANCE_MANAGER = 3;
  public static final int APPLICATION_ID_INVENTORY_MANAGER = 4;
  public static final int APPLICATION_ID_CALL_MANAGER = 5;
  public static final int APPLICATION_ID_FIELD_MANAGER = 6;
  public static final int APPLICATION_ID_OPERATIONS_MANAGER = 10;
  public static final int APPLICATION_ID_SYSTEM_MANAGER = 11;
  public static final int APPLICATION_ID_VENUE_MANGAER = 13;
  public static final int APPLICATION_ID_PERMIT_MANAGER = 16;
  public static final int APPLICATION_ID_MARINA_MANAGER = 17;
  public static final int APPLICATION_ID_LICENSE_MANAGER = 19;

  /**
   * Slip Contract Name ID
   */
  public static final int SLIP_CONTRACT_NAME_SEASONAL_CONTRACT = 1;
  public static final int SLIP_CONTRACT_NAME_LEASE_CONTRACT = 2;

  /**
   * Delivery Method ID
   */
  public static final String DELIVERYMETHOD_MAILOUT = "1";//Mail Out
  public static final String DELIVERYMETHOD_PRINTATHOME = "2";//Print@Home
  public static final String DELIVERYMETHOD_WILLCALL = "3";//Will Call

  /**
   * Active status
   */
  public static final String STATUS_INACTIVE = "0"; // DeActive
  public static final String STATUS_ACTIVE = "1"; // Active

  /**
   * Permit Type
   */
  public static final String PERMITTYPE_DAYUSEMOTOR = "273543442"; // Day Use
  // Motor
  public static final String PERMITTYPE_DAYUSEMOTORTOCAN = "273543443"; // Day
  // Use
  // Motor
  // to
  // Canada
  public static final String PERMITTYPE_OVERNIGHTHIKING = "273543441"; // Overnight
  // Hiking
  public static final String PERMITTYPE_OVERNIGHMOTOR = "273543440"; // Overnight
  // Motor
  public static final String PERMITTYPE_OVERNIGHPADDLE = "273543439"; // Overnight
  // Paddle

  public static interface AttrName {
    /**
     * Permit type name from DB
     */
    public static final String PERMIT_ACTUALENTRYDATE = "PT_ActualEntryDate";
    public static final String PERMIT_ALTERNATELEADERS = "PT_AlternateLeaders";
    public static final String PERMIT_BOATLEADER = "PT_BoatLeader";
    public static final String PERMIT_CAPTUREPERSONTYPES = "PT_CapturePersonTypes";
    public static final String PERMIT_COMMERCIALUSETYPE = "PT_CommercialUseType";
    public static final String PERMIT_COMMERCIALLYGUIDEDTRIPE = "PT_CommerciallyGuidedTrip";
    public static final String PERMIT_EMERGENCYCONTACT = "PT_EmergencyContact";
    public static final String PERMIT_TRAILHEAD = "PT_Trailhead";
    public static final String PERMIT_ENTRANCE = "PT_Entrance";
    public static final String PERMIT_ENTRYDATE = "PT_EntryDate";
    public static final String PERMIT_EXITDATE = "PT_ExitDate";
    public static final String PERMIT_EXITPOINT = "PT_ExitPoint";
    public static final String PERMIT_GROUPMEMBERINFO = "PT_GroupMemberInfo";
    public static final String PERMIT_GROUPSIZE = "PT_GroupSize";
    public static final String PERMIT_LAUNCHPOINT = "PT_LaunchPoint";
    public static final String PERMIT_lENGTHOFSTAY = "PT_LengthOfStay";
    public static final String PERMIT_METHODOFTRAVEL = "PT_MethodOfTravel";
    public static final String PERMIT_NONPROFITORG = "PT_NonProfitOrg";
    public static final String PERMIT_NUMOFGUIDES = "PT_NumOfGuides";
    public static final String PERMIT_NUMOFPETS = "PT_NumOfPets";
    public static final String PERMIT_NUMOFSTOCK = "PT_NumOfStock";
    public static final String PERMIT_NUMOFWATERCRAFT = "PT_NumOfWatercraft";
    public static final String PERMIT_PERMITDELIVERYMETHOD = "PT_PermitDeliveryMethod";
    public static final String PERMIT_PERMITISSUESTATION = "PT_PermitIssueStation";
    public static final String PERMIT_REENTRYDATE = "PT_ReentryDate";
    public static final String PERMIT_STOCKTYPE = "PT_StockType";
    public static final String PERMIT_TAKEOUTPOINT = "PT_TakeOutPoint";
    public static final String PERMIT_TRAVELPLAN = "PT_TravelPlan";
    public static final String PERMIT_TRIPITINERARY = "PT_TripItinerary";
    public static final String PERMIT_VEHICLEPLATENUM = "PT_VehiclePlateNum";
  }


  /**
   * Sales category
   */
  public static final String SALESCAT_ALL = "0"; // All
  public static final String SALESCAT_INDIVIDUAL = "1"; // Individual
  public static final String SALESCAT_ORGAN = "2"; // Organization
  public static final String SALESCAT_COMMER = "3"; // Commercial
  public static final String SALESCAT_NONCOMMER = "4"; // Non Commercial

  /**
   * Transaction Type
   */
  public static final String TRANTYPE_ADVPERMITPUR = "9133"; // Advanced
  public static final String TRANTYPE_WALKUPPERMITPUR = "9134"; // Walk up
  public static final String TRANTYPE_CANCELPERMITP = "9142"; // cancel
  public static final String TRANTYPE_NO_SHOW_PERMITP = "9140";
  public static final String TRANTYPE_UNDO_NO_SHOW_PERMITP = "9141";
  public static final String TRANTYPE_UNDO_NO_SHOW_MARINA = "14002";
  public static final String TRANTYPE_RE_ACTIVATE_SLIP_CONTRACT = "30024";
  public static final String TRANTYPE_UNDO_CANCEL_LIST_ENTRY = "9258";
  public static final String TRANTYPE_VOID_PERMIT = "9139";
  public static final String TRANTYPE_CHANGE_STAY_PERIOD = "9166";
  // Permit
  // Purchase
  public static final String TRANTYPE_ADVPTICKETPUR = "9003"; // Advanced
  // Ticket
  // Purchase
  public static final String TRANTYPE_RESERVATION = "920"; // Reservation
  public static final String TRANTYPE_PURCHASEPOS = "8001"; // Purchase POS
  public static final String TRANTYPE_CANCELLATION = "108056"; // Cancellation
  public static final String TRANTYPE_VOID = "108057"; // Void
  public static final String TRANTYPE_CANCELPERMIT_CUST = "9142";// Cancel
  public static final String TRANTYPE_EXTEND_STAY_LEAVE_LATER = "926";
  public static final String TRANTYPE_EXTEND_STAY_ARRIVE_EARLIER = "927";
  public static final String TRANTYPE_CHANGE_DATES = "923";
  public static final String TRANTYPE_WALK_IN = "5000";
  public static final String TRANTYPE_SHORTEN_STAY_ARRIVE_LATER = "925";
  public static final String TRANTYPE_SHORTEN_STAY_LEAVE_EARLIER = "924";
  public static final String TRANTYPE_CHECK_OUT = "5001";
  public static final String TRANTYPE_TRANSFER_SAME_FACILITY_SAME_VALUE = "921";
  public static final String TRANTYPE_TRANSFER_SAME_FACILITY_DIFF_VALUE = "5005";
  public static final String TRANTYPE_TRANSFER_DIFF_FACILITY = "922";
  public static final String TRANTYPE_MIDSTAYTRANSFER = "6013";//mid stay transfer
  public static final String TRANTYPE_SUBMIT_LOTTERY_ENTORY = "9147"; //Submit lottery entry
  // Permit -
  // Customer
  // Cancellation
  public static final String TRANTYPE_PURGIFTCARD = "9153"; // Purchase gift
  // HF Transaction Type
  public static final String TRANTYPE_PURCHASE_PRIVILEGE = "10000";
  public static final String TRANTYPE_TRANSFER_PRIVILEGE = "10001";
  public static final String TRANTYPE_REPRINT_DOCUMENT = "10014";
  public static final String TRANTYPE_PRINT_DOCUMENT = "10013";
  public static final String TRANTYPE_GENERATE_DOCUMENT = "10011";
  public static final String TRANTYPE_REGISTER_VEHICLE = "20000";
  public static final String TRANTYPE_RENEW_REGISTER = "20001";
  public static final String TRANTYPE_INSPECT_VEHICLE = "20019";
  public static final String TRANTYPE_SURRENDER_VEHICLE_TITLE = "20008";
  public static final String TRANTYPE_SET_VEHICLE_TITLE_TRANSFERABLE = "20018";
  public static final String TRANTYPE_REACTIVATE_VEHICLE_TITLE = "20009";
  public static final String TRANTYPE_DUPLICATE_VEHICLE_TITLE = "20005";
  public static final String TRANTYPE_CORRECT_VEHICLE_TITLE = "20006";
  public static final String TRANTYPE_APPLY_CONVENIENCE_FEE = "9167";
  public static final String TRANTYPE_APPLY_VOUCHER_PMT = "9111"; // Apply Voucher Payment
  public static final String TRANTYPE_CONVERT_VOUCHER_TO_REFUND = "9115"; // Convert Voucher to Refund
  public static final String TRANTYPE_NO_SHOW = "1400";
  public static final String TRANTYPE_UNDO_CHECKIN = "935";
  public static final String TRANTYPE_UNDO_CHECKOUT = "936";

  //transaction type id stored in O_ORD_ITEM_TRANS
  public static final String TRANTYPE_REVERSE_FEE = "4507";
  public static final String TRANTYPE_ISSUE_REFUND = "4508";
  public static final String TRANTYPE_REALLOCATE_PAYMENT = "7000";
  public static final String TRANTYPE_CHANGE_CUSTOMER_TYPE_SLIP = "7010";
  public static final String TRANTYPE_CHANGE_CUSTOMER_PASS_SLIP = "7011";
  public static final String TRANTYPE_CHANGE_PROMO_CODE = "9002";
  public static final String TRANTYPE_CHANGE_NUMBER_OF_PEOPLE = "5006";
  public static final String TRANTYPE_CHANGE_NUMBER_OF_VEHICLES = "5008";
  public static final String TRANTYPE_CHANGE_MISCELLAEOUS = "6009";
  public static final String TRANTYPE_CHANGE_BOAT_OWNER_DETAILS = "30030";
  public static final String TRANTYPE_CHANGE_BOAT = "30010";
  public static final String TRANTYPE_CHANGE_BOAT_CATEGORY = "30011";
  public static final String TRANTYPE_CHANGE_BOAT_LENGTH = "30012";
  public static final String TRANTYPE_CHANGE_BOAT_WIDTH = "30013";
  public static final String TRANTYPE_CHANGE_BOAT_DEPTH = "30014";
  public static final String TRANTYPE_ADD_BOAT_PERMIT_NUMBER = "30015";
  public static final String TRANTYPE_CHANGE_BOAT_PERMIT_NUMBER = "30016";
  public static final String TRANTYPE_REMOVE_BOAT_PERMIT_NUMBER = "30020";
  public static final String TRANTYPE_DOCK = "30027";
  public static final String TRANTYPE_UNDOCK = "30022";
  public static final String TRANTYPE_UNDO_DOCK = "30029";
  public static final String TRANTYPE_UNDO_UNDOCK = "30028";
  public static final String TRANTYPE_APPLY_DISCOUNT = "6002";
  public static final String TRANTYPE_REMOVE_DISCOUNT = "6003";

  public static final String ORDERTYPE_PRIVILEGE_SALE = "Privilege Sale";
  public static final String ORDERTYPE_VEHICLE_SALE = "Vehicle Sale";
  public static final String ORDERTYPE_POS_SALE = "POS Sale";

  public static final String TRANTYPE_CREATE_SLIP_CONTRACT = "30017";
  public static final String TRANTYPE_CHANGE_SLIP_CONTRACT = "30018";
  public static final String TRANTYPE_ADD_SLIP_RESERVATION_TO_SLIP_CONTRACT = "30019";
  public static final String TRANTYPE_INACTIVATE_SLIP_CONTRACT = "30023";
  public static final String TRANTYPE_ADD_WAITING_LIST = "9250";
  public static final String TRANTYPE_ADD_TRANSFER_LIST = "9249";
  public static final String TRANTYPE_CANCEL_LIST_ENTRY = "9257";
  public static final String TRANTYPE_MID_TRANSFER = "6013";
  public static final String TRANTYPE_MID_TRANSFER_OUT = "6014";
  public static final String TRANTYPE_FULFILL_LIST_ENTRY = "9253";
  /**
   * Transaction Name
   */
  public static final String TRANNAME_RESERVATION = "Reservation";

  public static final String TRANNAME_CANCELLATION = "Cancellation";

  public static final String TRANNAME_SUBMIT_LOTTERY_ENTRY = "Submit Lottery Entry";

  public static final String TRANNAME_PURCHASE_PRIVILEGE = "Purchase Privilege";

  public static final String TRANNAME_DUPLICATE_PRIVILEGE = "Duplicate Privilege";

  public static final String TRANNAME_TRANSFER_PRIVILEGE = "Transfer Privilege";

  public static final String TRANNAME_EXPIRE_PRIVILEGE = "Expire Privilege";

  public static final String TRANNAME_REPLACE_PRIVILEGE_INVENTORY = "Replace Privilege Inventory";

  public static final String TRANNAME_VOID_PURCHASE_PRIVILEGE = "Void Purchase Privilege";

  public static final String TRANNAME_VOID_DUPLICATE_PRIVILEGE = "Void Duplicate Privilege";

  public static final String TRANNAME_UNDO_VOID_PURCHASE_PRIVILEGE = "Undo Void Purchase Privilege";

  public static final String TRANNAME_UNDO_VOID_DUPLICATE_PRIVILEGE = "Undo Void Duplicate Privilege";

  public static final String TRANNAME_RETURN_DOCUMENT = "Return Document";

  public static final String TRANNAME_CHARGE_FOR_UNRETURNED_PRIVILEGE_DOCUMENTS = "Charge for Unreturned Documents";

  public static final String TRANNAME_PRINT_DOCUMENT = "Print Document";

  public static final String TRANNAME_GENERATE_DOCUMENT = "Generate Document";

  public static final String TRANNAME_REPRINT_DOCUMENT = "Reprint Document";

  public static final String TRANNAME_REGISTER_VEHICLE = "Registration";

  public static final String TRANNAME_TRANSFER_REGISTRATION = "Transfer Registration";

  public static final String TRANNAME_REVERSE_TRANSFER_REGISTRATION = "Reverse (Transfer) Registration";

  public static final String TRANNAME_INSPECT_VEHICLE = "Inspection";

  public static final String TRANNAME_TITLE_VEHICLE = "Title";

  public static final String TRANNAME_CORRECT_VEHICLE_TITLE = "Correct Title";

  public static final String TRANNAME_DUPLICATE_VEHICLE_REGISTRATION = "Duplicate Registration";

  public static final String TRANNAME_DUPLICATE_VEHICLE_TITLE = "Duplicate Title";

  public static final String TRANNAME_SUBMIT_SUPPLIES_ORDER = "Submit Supplies Order";

  public static final String TRANNAME_ADV_PERMIT_PURCHASE = "Advanced Permit Purchase";

  public static final String TRANNAME_CANCEL_PERMIT = "Cancel Permit - Customer Cancellation";

  public static final String TRANNAME_NO_SHOW_PERMIT = "No Show Permit";

  public static final String TRANNAME_UNDO_NO_SHOW_PERMIT = "Undo Permit No Show";

  public static final String TRANNAME_VOID_PERMIT = "Void Permit Order";

  public static final String TRANNAME_CHANGE_STAY_PERIOD = "Change Stay Period";

  public static final String TRANNAME_REVERSE_FEE = "Reverse Fee";

  public static final String TRANNAME_REALLOCATE_PAYMENT = "Reallocate Payment";

  public static final String TRANNAME_MAKE_DONATION = "Make Donation";

  public static final String TRANNAME_VOID_DONATION = "Void Donation";

  public static final String TRANNAME_PURCHASE_CONSUMABLE = "Purchase Consumable";

  public static final String TRANNAME_PURCHASE_SUBSCRIPTION = "Purchase Subscription";

  public static final String TRANNAME_VOID_CONSUMABLE = "Void Consumable";

  public static final String TRANNAME_NO_SHOW_SLIP = "No Show Slip Reservation";

  public static final String TRANNAME_MAKE_PAYMENT = "Make Payment";

  /**
   * For Activity transaction type
   */
  public static final String TRANNAME_REGISTER_FOR_ACTIVITY = "Register for Activity";

  /**
   * For ticket transaction type
   */
  public static final String TRANNAME_ADV_TICKET_PURCHASE = "Advanced Ticket Purchase";

  /**
   * Transaction name for Slip
   */
  public static final String TRANNAME_ADVANCED_SLIP_RESERVATION = "Advanced Slip Reservation";

  public static final String TRANNAME_ADD_TO_WAITINGLIST = "Add to Waiting List";

  public static final String TRANNAME_FLOAT_IN_REGISTRATION = "Float-in Registration";

  public static final String TRANNAME_TRANSFER_DIFFERENT_FACILITY = "Transfer Different Facility";

  public static final String TRANNAME_TRANSFER_SAME_FACILITY_DIFF_VALUE = "Transfer Same Facility - Diff Value";

  public static final String TRANNAME_TRANSFER_SAME_FACILITY_SAME_VALUE = "Transfer Same Facility - Same Value";

  public static final String TRANNAME_EXTEND_STAY_LEAVE_LATER = "Extend Stay Leave Later";

  public static final String TRANNAME_EXTEND_STAY_ARRIVE_EARLIER = "Extend Stay Arrive Earlier";

  public static final String TRANNAME_SHORTEN_STAY_LEAVE_EARLIER = "Shorten Stay Leave Earlier";

  public static final String TRANNAME_SHORTEN_STAY_ARRIVE_LATER = "Shorten Stay Arrive Later";

  public static final String TRANNAME_CHANGE_DATE = "Change Dates";

  public static final String TRANNAME_CHECKIN = "Checkin";

  public static final String TRANNAME_NO_SHOW = "No Show";

  public static final String TRANNAME_CHECKOUT_SLIP_RESERVATION = "Check out Slip Reservation";

  public static final String TRANNAME_CHANGE_BOAT = "Change Boat";

  public static final String TRANNAME_CHANGE_BOAT_CATEGORY = "Change Boat Category";

  public static final String TRANNAME_CHANGE_BOAT_LENGTH = "Change Boat Length";

  public static final String TRANNAME_CHANGE_BOAT_OWNER_DETAILS = "Change Boat Owner Details";

  public static final String TRANNAME_NO_SHOW_SLIP_RESERVATION = "No Show Slip Reservation";

  public static final String TRANNAME_UNDO_NO_SHOW_SLIP_RESERVATION = "Undo No Show Slip Reservation";

  public static final String TRANNAME_UNDO_CANCEL_LIST_ENTRY = "Undo Cancel List Entry";

  public static final String TRANNAME_MID_STAY_TRANSFER = "Mid Stay Transfer";

  public static final String TRANNAME_VOID_ORDER = "Void Order";

  public static final String TRANNAME_CHARGE_POS = "Charge POS";

  public static final String TRANNAME_CANCEL_SLIP_RESERVATION = "Cancel Slip Reservation";

  public static final String TRANNAME_ADD_BOAT_PERMIT_NUMBER = "Add Boat Permit Number";

  public static final String TRANNAME_CHANGE_CUSTOMER_TYPE = "Change Customer Type";

  public static final String TRANNAME_CHANGE_CUSTOMER_PASS = "Change Customer Pass";

  public static final String TRANNAME_CHANGE_PROMO_CODE = "Change Promo Code";

  public static final String TRANNAME_CANCEL_LIST_ENTRY = "Cancel List Entry";

  public static final String TRANNAME_VOID_LIST_ENTRY = "Void List Entry";

  public static final String TRANNAME_PURCHARSE_POS = "Purchase POS";

  public static final String TRANNAME_PURCHASE_GIFT_CARD = "Purchase Gift Card";

  public static final String TRANNAME_CHANGE_SLIP_CONTRACT = "Change Slip Contract";

  /**
   * Transaction name for Redeem Points
   */
  public static final String TRANNAME_REMOVE_REDEEM_POINTS = "Remove Redeem Points";

  /**
   * Sales Flow Type
   */
  public static final int SALES_FLOW_TYPE_REGULAR_ID = 1;
  public static final int SALES_FLOW_TYPE_QUICK_RENEWAL_ID = 2;
  public static final int SALES_FLOW_TYPE_MS_SPORTSMAN_RENEWAL_ID = 3;

  /**
   * Slip reservation order item status
   */
  public static final String SLIP_ORDER_ITEM_STATUS_ACTIVE = "Active";

  public static final String SLIP_ORDER_ITEM_STATUS_DOCKED = "Docked";

  public static final String SLIP_ORDER_ITEM_STATUS_UNDOCKED = "Undocked";

  public static final String SLIP_ORDER_ITEM_STATUS_CHECKED_IN = "Checked In";

  public static final String SLIP_ORDER_ITEM_STATUS_CHECKED_OUT = "Checked Out";

  /**
   * list entry status
   */
  public static final String LIST_ENTRY_STATUS_WAITING = "Waiting";
  public static final String LIST_ENTRY_STATUS_CANCELLED = "Cancelled";
  public static final String LIST_ENTRY_STATUS_VOIDED = "Voided";
  public static final String LIST_ENTRY_STATUS_FULFILLED = "Fulfilled";
  public static final String LIST_ENTRY_STATUS_CLOSED = "Closed";

  /**
   * Customer class
   */
  public static final String BUSINESS_CUSTOMER_CLASS = "Business";

  public static final String BUSINESS_CUST_CLASS_ID = "2";

  public static final String INDIVIDUAL_CUSTOMER_CLASS = "Individual";

  public static final String INDIVIDUAL_CUST_CLASS_ID = "1";

  public static final String OUTFITTER_CUSTOMER_CLASS = "Outfitter";
  /**
   * product line of business
   */
  public static final String HUNTING_FISHING = "H&F";

  /**
   * Fee journal type
   */
  public static final String JOURNALTYPE_DEBIT = "DEBIT";
  public static final String JOURNALTYPE_CREDIT = "CREDIT";
  public static final String JOURNALTYPE_DEBIT_CODE = "1";
  public static final String JOURNALTYPE_CREDIT_CODE = "2";

  /**
   * Fee Status
   */
  public static final String FEESTATUS_REVERSED = "Reversed";
  public static final String FEESTATUS_PRICED = "Priced";
  public static final String FEESTATUS_PENDING = "Pending";
  public static final String FEESTATUS_VOID = "Void";
  public static final String FEESTATUS_REVERSED_CODE = "6";
  public static final String FEESTATUS_PRICED_CODE = "2";
  public static final String FEESTATUS_PENDING_CODE = "1";
  public static final String FEESTATUS_VOID_CODE = "3";

  /**
   * Purchase type description
   */
  public static final String ORIGINAL_PURCHASE_TYPE = "Original";

  public static final String TRANSFER_PURCHASE_TYPE = "Transfer";

  public static final String RENEWAL_PURCHASE_TYPE = "Renewal";

  public static final String CORRECTED_PURCHASE_TYPE = "Corrected";

  public static final String DUPLICATE_PURCHASE_TYPE = "Duplicate";

  public static final String PRIVILEGE_INVENTORY_REPLACEMENT_PURCHASE_TYPE = "Privilege Inventory Replacement";

  /**
   * Purchase type id
   */
  public static final String ORIGINAL_PURCHASE_TYPE_ID = "1";

  public static final String REPLACEMENT_PURCHASE_TYPE_ID = "2";

  public static final String TRANSFER_PURCHASE_TYPE_ID = "3";

  public static final String RENEWAL_PURCHASE_TYPE_ID = "4";

  public static final String CORRECTED_PURCHASE_TYPE_ID = "6";

  public static final String REPLACE_PRIVILEGE_INVENTORY_ID = "7";

  /**
   * Transaction Occurrence
   */
  public static final String TRANOCCU_PRIORTOMINWINDOW = "1";//Prior to Min Window
  public static final String TRANOCCU_PRIORTOMINWIN = "82"; // Prior to Minimum Window
  public static final String TRANOCCU_WITHINMINWINBEFARR = "2"; // Within Min Window before Arrival Date
  public static final String TRANOCCU_DAYOFARRONORBEF6PMLOCTIME = "3"; // Day of Arrival on or before 6:00pm Local Time
  public static final String TRANOCCU_AFTDAYOFARRONORBEFDEPART = "6"; // After Day of Arrival on or before Departure Date
  public static final String TRANOCCU_AFTDEPARTDATE = "7"; // After Departure Date
  public static final String TRANOCCU_10ORMOREDAYBEFTOURDATE = "39"; // 10 or more days before tour date
  public static final String TRANOCCU_14ORMOREDAYBEFTOURDATE = "42"; // 14 or more days before tour date
  public static final String TRANOCCU_1DAYSBEFOREARRIVALDATE = "50"; // 0-1 days before arrival date
  public static final String TRANOCCU_2DAYSBEFOREARRIVALDATE = "60"; // 0-2 days before arrival date
  public static final String TRANOCCU_3DAYSBEFOREARRIVALDATE = "30"; // 0-3 days before arrival date
  public static final String TRANOCCU_WITHMinWin = "38"; // Within Minimum Window
  public static final String TRANOCCU_WithIn9Days = "40"; // 	Within 9 days before tour date and prior to the minimum window
  public static final String TRANOCCU_5DAYSBEFOREARRIVALDATE = "43"; // 0-5 days before arrival date
  public static final String TRANOCCU_WITHIN15DAYSWINTOTHEENTRYDATE = "89"; // Within 15 Days window to the entry date
  public static final String TRANOCCU_WITHIN7DAYSBEFOREARRDATE = "21"; // Within 7 Days before arrival date
  /**
   * Apply Fee
   */
  public static final String APPLYFEE_ORDERLEVEL = "1"; // Order Level
  public static final String APPLYFEE_ORDERITEMLEVEL = "2"; // Order Item
  public static final String APPLYRATE_NEWUNIT = "New Unit(s)"; // apply rate to new unit
  public static final String APPLYRATE_NEWCHANGEDUNIT = "New/Changed Unit(s)"; // apply rate to new or changed unit
  public static final String APPLYRATE_MAXFEE_RESTRICTCOND_NONE_CODE = "0";
  public static final String APPLYRATE_MAXFEE_RESTRICTCOND_FLAT_CODE = "1";
  public static final String APPLYRATE_MAXFEE_RESTRICTCOND_BASED_ON_PENALTY_CHARGES = "2";
  public static final String APPLYRATE_MAXFEE_RESTRICTCOND_COMBINATION_OF_FLAT_AND_PENALTY_CHARGES = "3";
  // Level

  /**
   * Fee Penalty Unit
   */
  public static final String FEEPENALTYUNIT_NIGHTS = "1";
  public static final String FEEPENALTYUNIT_DAYS = "2";
  public static final String FEEPENALTYUNIT_PERCENT = "3";
  public static final String FEEPENALTYUNIT_FLAT = "4";

  public static final String FEEPENALTY_UNIT_PERCENT = "Percent";
  public static final String FEEPENALTY_UNIT_FLAT = "Flat";

  /**
   * Product Fee Class
   */
  public static final String PRUDFEECLASS_FEE = "2"; // Fee
  public static final String PRUDFEECLASS_NONFEE = "3"; // Non Fee

  /**
   * Distribution type
   */
  public static final String DISTRITYPE_ALL = "0";
  public static final String DISTRITYPE_USEFEE = "1";
  public static final String DISTRITYPE_USEFEEDISCUNT = "2";
  public static final String DISTRITYPE_USEFEEPENALTY = "3";
  public static final String DISTRITYPE_OVERPAYMENT = "8";
  public static final String DISTRITYPE_RAFEE = "10";
  public static final String DISTRITYPE_TRANSACTIONFEE = "16";
  public static final String DISTRITYPE_POSFEE = "21";
  public static final String DISTRITYPE_TICKETFEE = "30";
  public static final String DISTRITYPE_VOUCHER = "1001";

  /**
   * recipient type
   */
  public static final String RECIPTYPE_LOCATION = "1"; // location
  public static final String RECIPTYPE_CONCESS = "2"; // Concessionaire

  /**
   * recipient schedule unit
   */
  public static final String RECIPUNIT_FLAT = "1"; // Flat
  public static final String RECIPUNIT_PERCENT = "2"; // Percent

  public static final String KEY = "CA67C6C94D75A9CF08CC7E7697E32298";

  /**
   * site attribute id
   */
  public static final String MINIMUM_NUMBER_OF_VEHICLES = "209";
  public static final String MAXIMUM_NUMBER_OF_VEHICLES = "9";
  public static final String MINIMUM_NUMBER_OF_PEOPLE = "111";
  public static final String MAXIMUM_NUMBER_OF_PEOPLE = "12";
  public static final String BASE_NUMBER_OF_PEOPLE = "212";
  public static final String BASE_NUMBER_OF_VEHICLES = "210";
  public static final String ADA_ACCESSIBLE = "104";
  public static final String ADA_OCCUPANT_REQUIRED = "2402";
  public static final String SITE_ACCESS = "9137";
  public static final String WATER_HOOKUP = "32";
  public static final String SEWER_HOOKUP = "33";
  public static final String ELECTRICITY_HOOKUPS = "218";
  public static final String PROXIMITY_TO_WATER = "24";
  public static final String DEFAULT_AVAILABLE_QUANTITY = "10024";
  public static final String TOTAL_QUANTITY = "10028";
  public static final String MAP_X_COORDINATE = "100";
  public static final String MAP_Y_COORDINATE = "101";
  public static final String PETS_ALLOWED = "220";
  public static final String MVEF = "3020";
  public static final String FRTRIGGER = "500000";

  public static final String BASE_NUMBER_OF_PEOPLE_NAME = "Base Number of People";
  public static final String BASE_NUMBER_OF_VEHICLES_NAME = "Base Number of Vehicles";
  public static final String MINIMUM_NUMBER_OF_PEOPLE_NAME = "Minimum Number of People";
  public static final String MINIMUM_NUMBER_OF_VEHICLES_NAME = "Minimum Number of Vehicles";
  public static final String MAXIMUM_NUMBER_OF_PEOPLE_NAME = "Maximum Number of People";
  public static final String MAXIMUM_NUMBER_OF_VEHICLES_NAME = "Maximum Number of Vehicles";
  public static final String ADA_ACCESSIBLE_NAME = "ADA Accessible";
  public static final String ADA_OCCUPANT_REQUIRED_NAME = "ADA Occupant Required";
  public static final String WATER_HOOKUP_NAME = "Water Hookup";
  public static final String SEWER_HOOKUP_NAME = "Sewer Hookup";
  public static final String ELECTRICITY_HOOKUPS_NAME = "Electricity Hookup";
  public static final String PROXIMITY_TO_WATER_NAME = "Proximity to Water";
  public static final String DEFAULT_AVAILABLE_QUANTITY_NAME = "Default Available Quantity";
  public static final String TOTAL_QUANTITY_NAME = "Total Quantity";
  public static final String MVEF_NAME = "MVEF (Motor Vehicle Entrance Fee)";
  public static final String FRTRIGGER_NAME = "FRTrigger";
  /**
   * site information in database
   */
  public static final String RATE_TYPE = "PRD_RATE_TYPE_ID";
  public static final String RATE_TYPE_FAMILY = "1";
  public static final String RATE_TYPE_GROUP = "2";
  public static final String RATE_TYPE_BOTH = "3";
  public static final String CALL_RESERVABLE = "IMPORT_RESERVABLE";
  public static final String WEB_RESERVABLE = "IMPORT_WEB_RESERVABLE";

  /**
   * invalid Date
   */
  public static final String[] INVALID_DATES = new String[]{"VerifyDate",
      "1/0/2015", "1/32/2015", "2/29/2015", "2/30/2016", "4/00/2015",
      "4/31/2015", "12/32/2015"};

  //Test case status
  public static final int TESTCASE_PENDING = 0;
  public static final int TESTCASE_RUNNING = 1;
  public static final int TESTCASE_FINISHED = 2;
  public static final int TESTCASE_HIBERNATED = 3;

  public static final String[] residencyProofsString = new String[]{
      "Homestead Exempt Certification",
      "MDWFP Approved",
      "Military Orders",
      "State Income Tax Document",
      "Student ID",
      "Youth < 19 must have Parents Drivers License"};//Vivian [20140616]: Missed "Utility Bill/Lease",

  public static final String[] postedToString = new String[]{
      "Valid From Date",
      "Valid From Time",
      "Valid To Date",
      "Valid To Time",
      "Privilege Number",
      "Inventory Number"};

  /**
   * Allocation type id in DB
   *
   * @author Lesley Wang
   */
  public static final String ALLOCATION_TYPE_NONE = "0";
  public static final String ALLOCATION_TYPE_APPLIED = "1";
  public static final String ALLOCATION_TYPE_REVERSED = "2";

  /**
   * Payment/Refund Status id in DB
   *
   * @author Lesley Wang
   */
  public static final String PMT_RFD_STATUS_PENDING = "1";
  public static final String PMT_RFD_STATUS_RECEIVED = "2";
  public static final String PMT_RFD_STATUS_APPROVED = "3";
  public static final String PMT_RFD_STATUS_ISSUED = "4";
  public static final String PMT_RFD_STATUS_VOIDED = "5";
  public static final String PMT_RFD_STATUS_ISSUED_VOUCHER = "19";

  /**
   * Payment/Refund Type id in DB
   *
   * @author Lesley Wang
   */
  public static final String PMT_TYPE = "1";
  public static final String RFD_TYPE = "2";

  //EFT Type
  public static final String EFT_TYPE_NOEFT = "No EFT";
  public static final String EFT_TYPE_EFT = "EFT";
  public static final String EFT_TYPE_EDI = "EDI";

  //EFT Type Code
  public static final String EFT_TYPE_NOEFT_CODE = "1";
  public static final String EFT_TYPE_EFT_CODE = "2";
  public static final String EFT_TYPE_EDI_CODE = "3";

  //EFT Invoice Status
  public static final String EFT_INVOICE_STATUS_PENDING = "Pending";
  public static final String EFT_INVOICE_STATUS_SENT = "Sent";
  public static final String EFT_INVOICE_STATUS_CLEARED = "Cleared";
  public static final String EFT_INVOICE_STATUS_FAILED = "Failed";
  public static final String EFT_INVOICE_STATUS_GENERATED = "Generated";
  public static final String EFT_INVOICE_STATUS_PAID = "Paid";

  //EFT Invoice Status Code
  public static final String EFT_INVOICE_STATUS_PENDING_CODE = "1";
  public static final String EFT_INVOICE_STATUS_SENT_CODE = "2";
  public static final String EFT_INVOICE_STATUS_CLEARED_CODE = "3";
  public static final String EFT_INVOICE_STATUS_FAILED_CODE = "4";
  public static final String EFT_INVOICE_STATUS_GENERATED_CODE = "5";
  public static final String EFT_INVOICE_STATUS_PAID_CODE = "6";

  //Invoice Transaction type
  public static final String EFT_TRANS_TYPE_NAME_CREATE = "Create EFT Invoice";
  public static final String EFT_TRANS_TYPE_NAME_CLEAR = "Clear EFT Invoice";
  public static final String EFT_TRANS_TYPE_NAME_TRANSMIT = "Transmit EFT Invoice";
  public static final String EFT_TRANS_TYPE_NAME_APPLY = "Apply payment to EFT invoice";
  public static final String EFT_TRANS_TYPE_NAME_MARK_AS_PAID = "Mark EFT invoice as paid";
  public static final String EFT_TRANS_TYPE_NAME_PROCESS_RETURNED = "Process returned EFT Invoice";

  //Invoice Transaction type code
  public static final String EFT_TRANS_TYPE_NAME_CREATE_CODE = "1";
  public static final String EFT_TRANS_TYPE_NAME_CLEAR_CODE = "";
  public static final String EFT_TRANS_TYPE_NAME_TRANSMIT_CODE = "2";
  public static final String EFT_TRANS_TYPE_NAME_APPLY_CODE = "4";
  public static final String EFT_TRANS_TYPE_NAME_MARK_AS_PAID_CODE = "5";
  public static final String EFT_TRANS_TYPE_NAME_PROCESS_RETURNED_CODE = "";


  //EFT Frequency
  public static final String EFT_FREQUENCY_DAILY = "Daily";
  public static final String EFT_FREQUENCY_WEEKLY = "Weekly";
  public static final String EFT_FREQUENCY_MONTHLY = "Monthly";
  public static final String EFT_FREQUENCY_QUARTERLY = "Quarterly";
  public static final String EFT_FREQUENCY_ANNUALLY = "Annually";

  // EFT Frequency ID
  public static final String EFT_FREQ_DAILY_ID = "1";
  public static final String EFT_FREQ_WEEKLY_ID = "2";
  public static final String EFT_FREQ_MONTHLY_ID = "3";
  public static final String EFT_FREQ_QUARTERLY_ID = "4";
  public static final String EFT_FREQ_ANNUALLY_ID = "5";
  /**
   * Voucher Status Code in DB
   *
   * @author Lesley Wang
   * @Date May 28, 2012
   */
  public static final String VOUCHER_STATUS_ACTIVE = "1";
  public static final String VOUCHER_STATUS_REFUNDED = "2";
  public static final String VOUCHER_STATUS_VOIDED = "3";

  // Voucher Status - Refunded
  public static final String REFUNDED_STATUS = "Refunded";

  // Refund status - Issued to Voucher
  public static final String ISSUED_To_VOUCHER_STATUS = "Issued to Voucher";

  /**
   * Journal Type Code in DB
   *
   * @author Lesley Wang
   * @Date May 30, 2012
   */
  public static final String JOURNAL_TYPE_VOUCHER = "6"; // Voucher

  /**
   * EFT Remittance status
   */
  public static final String REMITTANCE_PENDING = "Pending";
  public static final String REMITTANCE_SENT = "Sent";
  public static final String REMITTANCE_CLEAR = "Cleared";
  public static final String REMITTANCE_FAIL = "Failed";

  /**
   * EFT Remittance Transmission status
   */
  public static final String REMITTANCE_TRANSMISSION_PENDING = "Pending";
  public static final String REMITTANCE_TRANSMISSION_SENT = "Sent";
  public static final String REMITTANCE_TRANSMISSION_CLEAR = "Cleared";
  public static final String REMITTANCE_TRANSMISSION_FAIL = "Failed";

  /**
   * EFT Remittance status code
   */
  public static final String REMITTANCE_PENDING_CODE = "1";
  public static final String REMITTANCE_SENT_CODE = "2";
  public static final String REMITTANCE_CLEAR_CODE = "3";
  public static final String REMITTANCE_FAIL_CODE = "4";


  /**
   * EFT Remittance Transmission status code
   */
  public static final String REMITTANCE_TRANSMISSION_PENDING_CODE = "1";
  public static final String REMITTANCE_TRANSMISSION_SENT_CODE = "2";
  public static final String REMITTANCE_TRANSMISSION_CLEAR_CODE = "3";
  public static final String REMITTANCE_TRANSMISSION_FAIL_CODE = "4";

  /**
   * EFT Invoice Transmission status code
   */
  public static final String INVOICE_TRANSMISSION_PENDING_CODE = "1";

  public static final String DAILY_EFT_TYPE_FOR_INVOICEING = "1";

  /**
   * Fin Session - Opening Float Status ID - table: F_FLOAT
   */
  public static final String OPENING_FLOAT_STATUS_ID_ACTIVE = "1";
  public static final String OPENING_FLOAT_STATUS_ID_INACTIVE = "2";
  public static final String OPENING_FLOAT_STATUS_ID_REVERSED = "3";

  /**
   * Applications
   */
  public static final String FIELD_MANAGER = "Field Manager";
  public static final String LICENSE_MANAGER = "LicenseManager";
  public static final String FINANCE_MANAGER = "FinanceManager";
  public static final String MARINA_MANAGER = "MarinaManager";
  public static final String INVENTORY_MANAGER = "InventoryManager";


  /**
   * Attribute name
   */
  public static final String ATTRIBUT_ENFORCEMINIMUMTOCONFIRMRULEINFIELD_NAME = "Enforce Minimum to Confirm Rule in Field";
  public static final String ATTRIBUT_ENFORCEMINIMUMTOCONFIRMRULEINFIELD_CODE = "4131";

  /*
   * Unit Type
   */
  public static final String UNITTYPE_PER_UNIT_CODE = "1";
  public static final String UNITTYPE_PER_TRANSACTION_CODE = "2";
  public static final String UNITTYPE_FLAT_BY_RANGE_OF_TICKET_QUANTITY_CODE = "3";
  public static final String UNITTYPE_PER_PERSON_PER_DAY_CODE = "4";
  public static final String UNITTYPE_PER_PERSON_PER_PERIOD_CODE = "5";
  public static final String UNITTYPE_PERCENTAGE_CODE = "6";

  public static final String UNITTYPE_PER_UNIT = "Per Unit";
  public static final String UNITTYPE_PER_TRANSACTION = "Per Transaction";
  public static final String UNITTYPE_FLAT_BY_RANGE_OF_TICKET_QUANTITY = "Flat by Range of Ticket Quantity";
  public static final String UNITTYPE_PER_PERSON_PER_DAY = "Per Person Per Day";
  public static final String UNITTYPE_PER_PERSON_PER_PERIOD = "Per Person Per Period";
  public static final String UNITTYPE_PERCENTAGE = "Per Percentage";

  /**
   * POS Inventory Type
   **/
  public static final String SERIALIZED_INVENTORY_TYPE = "Serialized Inventory";
  public static final String NO_INVENTORY_TYPE = "No Inventory";
  public static final String NON_RESTRICTIVE_INVENTORY_TYPE = "Non-Restrictive Inventory";
  public static final String RESTRICTIVE_INVENTORY_TYPE = "Restrictive Inventory";
  public static final String POS_ALLOCATED = "Allocated";
  public static final String POS_DEALLOCATED = "Not Allocated";


  /**
   * Display Category Type ID
   */
  public static final String PRODUCT_CLASS_TYPE_ID = "4";
  public static final String PRODUCT_SUB_CLASS_TYPE_ID = "5";

  public static final String INV_USED_STATUS_BOOKED = "Booked";
  public static final String INV_USED_STATUS_CLOSED = "Closed";
  public static final String INV_USED_STATUS_DEPARTED = "Departed";
  public static final String INV_USED_STATUS_OCCUPIED = "Occupied";
  public static final String INV_USED_STATUS_HOLD = "Hold";
  public static final String INV_USED_STATUS_AVAILABLE = "Available";

  /**
   * Marina Manager related constants
   */
  public static final String SLIP_RESERVATION_TYPE_TRANSIENT = "Transient";
  public static final String SLIP_RESERVATION_TYPE_LEASE = "Lease";
  public static final String SLIP_RESERVATION_TYPE_SEASONAL = "Seasonal";
  public static final String SLIP_RESERVATION_TYPE_ALL = "All";

  public static final String SLIP_RESERVATION_TYPE_TRANSIENT_ID = "3";
  public static final String SLIP_RESERVATION_TYPE_LEASE_ID = "2";
  public static final String SLIP_RESERVATION_TYPE_SEASONAL_ID = "1";
  public static final String SLIP_RESERVATION_TYPE_ALL_ID = "4";

  /**
   * Marina Slip Availability status
   */
  public static final String SLIP_AVAILABILITY_AVAILABLE = "Available";
  public static final String SLIP_AVAILABILITY_RESERVED = "Reserved";
  public static final String SLIP_AVAILABILITY_NO_AVAILABLE = "Not Available";
  public static final String SLIP_AVAILABILITY_CLOSED = "Closed";
  public static final String SLIP_AVAILABILITY_OVERRIDE_CLOSURE = "Override Closure";
  public static final String SLIP_AVAILABILITY_FLOANINONLY = "Float-in Only";

  /**
   * MS Internet transaction code
   */
  public static final String MS_INTERNET_CODE_PURCHASE = "1121";
  public static final String MS_INTERNET_CODE_VEHICLE_RENEWAL = "1124";
  public static final String MS_INTERNET_CODE_COMPLETION_TRANSACTION = "1122";
  public static final String MS_INTERNET_CODE_CUSTOMER_INQUIRY = "1120";
  public static final String MS_INTERNET_CODE_VEHICLE_INQUIRY = "1123";
  public static final String MS_INTERNET_CODE_CUSTOMER_UPDATE = "1029";
  /**
   * VeriFone transaction code
   */
  public static final String MS_VERIFONE_CODE_ORIGINAL_SALE = "2001";
  public static final String MS_VERIFONE_CODE_VOID_TRANSACTION = "1007";

  // For add new customer
  public static final String MS_LICENSE_TYPE_MDWFP = "MDWFP #";
  public static final String MS_INVALID_MDWFP_NUM = "invalid123";

  //Product group name
  public static final String PRD_GRP_NAME_CABIN = "Cabin";
  public static final String PRD_GRP_NAME_STANDARD_SITE = "Standard Site";

  /**
   * Sales Status for POS
   */
  public static final String PENDING = "Pending";
  public static final String INPROGRESS = "In Progress";
  public static final String SOLDOUT = "Sold Out";

  /**
   * TPA Labels
   */
  public final static String TPA_LABEL_ARRIVAL = "Arrival";
  public final static String TPA_LABEL_ARRIVAL_HH = "Arrival_Hh";
  public final static String TPA_LABEL_ARRIVAL_MM = "Arrival_Mm";
  public final static String TPA_LABEL_ARRIVAL_AP = "Arrival_Ap";

  /**
   * P_MIN_PMT_ENTRY_CFM rule type id
   */
  public final static String RULE_TYPE_PERCENT = "1";
  public final static String RULE_TYPE_FLAT = "2";
  public final static String RULE_TYPE_UNIFOFSTAY = "3";

  /**
   * HF tax schedule rate type
   */
  public final static String TAX_RATE_TYPE_FLAT = "2";
  public final static String TAX_RATE_TYPE_PERCENT = "1";

  /**
   * Tour Attribute Name
   */
  public final static String IMPORTANT_INFO = "Important Information";

  /**
   * Verification Status for HF customer Identifier/Education
   */
  public final static String VERIFICATION_STATUS_FAILED = "Failed";
  public final static String VERIFICATION_STATUS_PENDING = "Pending";
  public final static String VERIFICATION_STATUS_VERIFIED = "Verified";
  public final static String VERIFICATION_STATUS_NOTAPPLICABLE = "Not Applicable";

  public final static String VERIFICATION_STATUS_FAILED_ID = "1";
  public final static String VERIFICATION_STATUS_PENDING_ID = "2";
  public final static String VERIFICATION_STATUS_VERIFIED_ID = "3";
  public final static String VERIFICATION_STATUS_NOTAPPLICABLE_ID = "4";

  /**
   * Identifier type id
   */
  public final static String IDEN_HAL_ID = "1";
  public final static String IDEN_MS_DRIVERS_LICENSE_ID = "2";
  public final static String IDEN_CONSERVATION_ID = "1";
  public final static String IDEN_PASSPORT_NUM_ID = "3";
  public final static String IDEN_SOCIALSECURITY_NUM_ID = "4";
  public final static String IDEN_USDRIVERSLICENSE_NUM_ID = "6";
  public final static String IDEN_NONUSDL_NUM_ID = "7";
  public final static String IDEN_GREENCARD_NUM_ID = "9";
  public final static String IDEN_OTHER_NUM_ID = "14";
  public final static String IDEN_CAF_ID = "19";
  public final static String IDEN_RCMP_ID = "18";
  public final static String IDEN_CANDL_ID = "17";
  public final static String IDEN_SKDL_ID = "16";
  public final static String IDEN_FL_ID = "21";
  public final static String IDEN_VISA_ID = "10";

  public final static String AB_IDEN_OTHER_ID = "14";
  public final static String AB_IDEN_DRIVERSLICENCE_NUM_ID = "17";
  public final static String AB_IDEN_CANADIANARMEDFORCES_NUM_ID = "19";
  public final static String AB_IDEN_WIN_NUM_ID = "1";
  public final static String AB_PASSPORT_NUM_ID = "3";
  public final static String AB_IDEN_HEALTHCARE_ID = "22";

  /**
   * SK Identifier Type Short Name for SK contract
   */
  public final static String IDENT_TYPE_HAL = "HAL ID #";
  public final static String IDENT_TYPE_RCMP = "RCMP #";
  public final static String IDENT_TYPE_CAF = "CAF #";
  public final static String IDENT_TYPE_CANDL = "CAN DL #";
  public final static String IDENT_TYPE_FL = "FL #";
  public final static String IDENT_TYPE_PASSPORT = "Passport #"; // for SK contract
  public final static String IDENT_TYPE_OTHER = "Other #"; // for SK contract
  public final static String IDENT_TYPE_SKDL = "SK DL #";

  /**
   * SK Identifier Type Full Name for SK contract
   */
  public final static String IDENT_TYPE_NAME_CANDL = "Canadian Driver's Licence #";
  public final static String IDENT_TYPE_NAME_CAF = "Canadian Armed Forces #";
  public final static String IDENT_TYPE_NAME_RCMP = "RCMP #";
  public final static String IDENT_TYPE_NAME_PASSPORT = "Passport #";
  public final static String IDENT_TYPE_NAME_OTHER = "Other #";
  public final static String IDENT_TYPE_NAME_SKDL = "Saskatchewan Driver's Licence #";
  public final static String IDENT_TYPE_NAME_FL = "Canadian Firearms Licence #";

  public final static String CONVENIENCE_ORDER_TYPE = "Convenience Order";

  public final static String IDENT_TYPE_MDL = "MS Drivers License";

  /**
   * residency Override
   */
  public final static String RESIDENCY_OVERRIDE_COUNTRYRESIDENT = "Country Resident";
  public final static String RESIDENCY_OVERRIDE_RESIDENT = "Resident";
  public final static String RESIDENCY_OVERRIDE_NONRESIDENT = "Non Resident";

  /**
   * residency status
   */
  public final static String RESIDENCY_STATUS_RESIDENT = "Resident";
  public final static String RESIDENCY_STATUS_NON_RESIDENT = "Non Resident";

  /**
   * List Entry status
   */
  public final static String LIST_ENTRY_WAITING = "1";
  public final static String LIST_ENTRY_FULFILLED = "2";
  public final static String LIST_ENTRY_CANCELLED = "3";
  public final static String LIST_ENTRY_VOIDED = "3";
  public final static String LIST_ENTRY_CLOSED = "5";

  /**
   * List Entry status name
   */
  public final static String ENTRY_STATUS_WAITING = "Waiting";
  public final static String ENTRY_STATUS_FULFILLED = "Fulfilled";

  /**
   * Transaction reason code id
   */
  public final static String TRAN_REASON_CD_CANCEL = "3";//Cancellation
  public final static String TRAN_REASON_CD_VOID = "4";//Void

  /**
   * Product relation type: 1- site specific, 2 - Non site specific parent, 3 - Non site specific child.
   * prd_rel_type col from table "p_prd"
   */
  public final static String SITE_SPECIFIC = "1";
  public final static String NON_SITE_SPECIFIC_PARENT = "2";
  public final static String NON_SITE_SPECIFIC_CHILD = "3";

  /**
   * Inventory Allocation Type
   */
  public final static String INV_ALLOC_TYPE_ALL = "0";
  public final static String INV_ALLOC_TYPE_ADVANCED = "1";
  public final static String INV_ALLOC_TYPE_WALKIN = "2";

  /**
   * Order type id
   */
  public final static String ORD_TYPE_FLOAT_IN_RES = "4401";
  public final static String ORD_TYPE_ADV_SLIP_RES = "4400";

  /**
   * SK Resident Status Name
   */
  public final static String RESID_STATUS_SK = "Saskatchewan Resident";
  public final static String RESID_STATUS_CAN = "Canadian Resident";
  public final static String RESID_STATUS_NON = "Non Resident";
  public final static String RESID_STATUS_ALBERTA = "Alberta";
  public final static String RESID_STATUS_CANADA = "Canada";
  public final static String RESID_STATUS_OTHER = "Other";

  public final static String GENDER_ID = "5031";

  public final static String EMERGENCY_CANCELLATION = "Emergency Cancellation";

  /**
   * Transaction fee detail:Maximum Fee Restriction
   */
  public final static String RESTRICTION_NONE = "None";
  public final static String RESTRICTION_FLAT = "Flat";
  public final static String RESTRICTION_BASED_ON_PENALTY_CHARGES = "Based On Penalty Charges";
  public final static String RESTRICTION_COMBINATION_OF_FLAT_AND_PENALTY_CHARGES = "Combination of Flat and Penalty Charges";

  /**
   * Transaction fee detail:Rate Applies To
   */
  public final static String RATE_APPLIES_TO_NEW_UNITS = "New Unit(s)";
  public final static String RATE_APPLIES_TO_NEW_CHANGED_UNITS = "New/Changed Unit(s)";

  /**
   * Transaction Attribute
   */
  public final static String TRANSATTR_SLIP_CONTRACT = "1023";


  /**
   * File Import Type
   */
  public final static String FILE_IMPORT_TYPE_ULPRI = "UNLOCKED_PRIVILEGE";
  public final static String FILE_IMPORT_TYPE_POINT = "POINT_BALANCES";

  /**
   * Unlocked Privilege Record Status (Purchase Status)
   */
  public final static String UL_PRI_AWARDED_ID = "1"; // Awarded status ID
  public final static String UL_PRI_RESERVED_ID = "2"; // Reserved status ID

  /**
   * Fulfillment method id
   */
  public final static String FULFILLED_BY_MAIL = "1";
  public final static String INVENTORY_ON_HAND = "2";
  public final static String SELECT_IMMED = "3";

  public final static String PACKAGE_PREFIX = "com.activenetwork.qa.awo";

  public static enum SalesChannel {
    Admin_Manager, Field_Manager, Call_Manager, Resource_Manager, Operations_Manager, Inventory_Manager, Finance_Manager, Venue_Manager, Permit_Manager, License_Manager, Marina_Manager, Communities;

    public String getName() {
      return this.toString().replaceAll("_", " ");
    }
  }

  /**
   * Ticket category id
   */
  public final static String TICKET_CAT_ALL = "0";
  public final static String TICKET_CAT_INDV = "1";
  public final static String TICKET_CAT_ORG = "2";

  /**
   * License Manager -> Lottery Pricing Rate Type
   */
  public static String PER_APPLICATION_TYPE = "Per Application";
  public static String PER_CHOICE_TYPE = "Per Choice";
  public static String CHOICE_RANGE_TYPE = "Choice Range";

  //Contact status for entry list
  public final static String CONTACT_STATUS_PENDING_RESPONSE = "Pending Response";
  public final static String CONTACT_STATUS_REJECTED = "Rejected";
  public final static String CONTACT_STATUS_CONFIRMED = "Confirmed";
  public final static String CONTACT_STATUS_OTHER = "Other";

  //Fee type name for (lottery) privilege pricing
  public final static String FEE_TYPE_NAME_STATE_FEE = "State Fee";
  public final static String FEE_TYPE_NAME_PRIVILEGE_LOTTERY_FEE = "Privilege Lottery Fee";
  public final static String FEE_TYPE_NAME_STATE_AMOUNT = "State Amount";
  public final static String FEE_TYPE_NAME_TRANSACIONT_FEE = "Transaction Fee";
  public final static String FEE_TYPE_NAME_PARENT_FEE = "Parent Fee";
  public final static String FEE_TYPE_NAME_VENDOR_FEE = "Vendor Fee";
  public final static String FEE_TYPE_NAME_HOLDING_FEE = "Holding Fee";
  public final static String FEE_TYPE_NAME_STATE_VENDOR_FEE = "State Vendor Fee";
  public final static String FEE_TYPE_NAME_AQUATIC_SURCHARGE_FEE = "Aquatic Surcharge Fee";
  public final static String FEE_TYPE_NAME_ELS_FILING_FEE = "ELS Filing Fee";

  public final static String CHECKIN_TIME_OVERRIDE_CONFIG_TYPE = "1";
  public final static String CHECKOUT_TIME_OVERRIDE_CONFIG_TYPE = "2";
  public final static String CHECKIN_CHECKOUT_TIME_OVERRIDE_CONFIG_TYPE = "3";

  public final static String PET_TYPE_ID_CAT = "601";
  public final static String PET_TYPE_ID_DOG = "600";
  public final static String PET_TYPE_ID_OTHER = "603";
  public final static String PET_TYPE_ID_ALL = "0";

  public final static String EQUIPMENT_TYPE_ID_RVMOTORHOME = "108063";
  public final static String EQUIPMENT_TYPE_ID_TENT = "108060";
  public final static String EQUIPMENT_TYPE_ID_FIFTHWHEEL = "108067";
  public final static String EQUIPMENT_TYPE_ID_ALL = "0";

  public final static String OCCUPANT_TYPE_ID_ADULT = "1";
  public final static String OCCUPANT_TYPE_ID_YOUTH = "2";
  public final static String OCCUPANT_TYPE_ID_SENIOR = "14";
  public final static String OCCUPANT_TYPE_ID_ALL = "13";

  /**
   * Dynamic Pricing - PCR 4715
   */
  public final static String THRESHOLD_DATE_TYPE_ALL = "All";
  public final static String THRESHOLD_DATE_TYPE_WEEKDAY = "Weekday";
  public final static String THRESHOLD_DATE_TYPE_WEEKEND = "Weekend";
  public final static int THRESHOLD_DATE_TYPE_ALL_ID = 1;
  public final static int THRESHOLD_DATE_TYPE_WEEKDAY_ID = 2;
  public final static int THRESHOLD_DATE_TYPE_WEEKEND_ID = 3;

  //Customer Residence status
  public final static String NON_RESIDENT_STATUS = "Non Resident";
  public final static String RESIDENT_STATUS = "Resident";

  // Organization Type
  public final static String ORGTYPE_NONPROFIT = "Non Profit Organization";

  //Batch harvest email schedule status
  public final static String BATCH_HARVEST_EMAIL_SCHEDULE_PENDING = "Pending";
  public final static String BATCH_HARVEST_EMAIL_SCHEDULE_COMPLETED = "Completed";
  public final static String BATCH_HARVEST_EMAIL_SCHEDULE_INACTIVED = "Inactivated";
  public final static String BATCH_HARVEST_EMAIL_SCHEDULE_SKIPPED = "Skipped";

  //Gift card type
  public final static String GIFT_CARD_TYPE_GIFT_CARD = "Gift Card";
  public final static String GIFT_CARD_TYPE_GIFT_CERTIFICATE = "Gift Certificate";

  //SNAPSHOT STATUS
  public final static String SNAPSHOT_ACTIVE_STATUS = "1";
  public final static String SNAPSHOT_INACTIVE_STATUS = "2";
  public final static String SNAPSHOT_INITIAL_IND = "0";
  public final static String SNAPSHOT_DELETED_IND = "1";

  //Status id
  public final static String ACTIVE_ID = "1";


  public static final String LAUNCH_BATCH_TEST_WITH_UI = "1";
  public static final String LAUNCH_BATCH_TEST_WITHOUT_UI = "0";
}


