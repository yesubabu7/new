package com.example.insurence.daos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.insurence.contracts.DaoInterface;
import com.example.insurence.models.Claim;
import com.example.insurence.models.ClaimApplication;
import com.example.insurence.models.CustomerData;
import com.example.insurence.models.PolicyMembers;
import com.example.insurence.models.UserData;
import com.example.insurence.models.UserLoginValidation;
import com.example.insurence.rowmappers.ClaimMapper;
import com.example.insurence.rowmappers.CustomerDataRowMapper;
import com.example.insurence.rowmappers.PolicyMembersRowMapper;
import com.example.insurence.rowmappers.UserLoginValidationRowMapper;
import com.example.insurence.rowmappers.UsersDataRowMapper;

import jakarta.servlet.http.HttpSession;

@Repository

public class InsurenceDao implements DaoInterface {

	JdbcTemplate jdbcTemplate;
	HttpSession session;

	private String SQL_GET_CLAIMS = "select * from  _Claims";
	private String SQL_GET_FILTERED_CLAIMS = "select * from  _Claims where clam_status=?";
	private String SQL_GET_CLAIM_BY_ID = "select * from  _Claims where clam_id=?";
	private String SQL_INSERT_CLAIM = "insert into _Claims(clam_source,clam_type,clam_date,clam_amount_requested,clam_iplc_id) values(?,?,?,?,?)";
	// private String SQL_INSERT_CLAIM = "insert into
	// _Claims(clam_source,clam_type,clam_date,clam_amount_requested,clam_iplc_id) values(?,?,?,?,?)";
	private String SQL_INSERT_CLAIMBill = "insert into Claim_bills(clam_id,clbl_document_title,clbl_document_path,clbl_claim_amount) values(?,?,?,?)";

	@Autowired
	public InsurenceDao(DataSource datasource, HttpSession session) {
		this.jdbcTemplate = new JdbcTemplate(datasource);
		this.session = session;

	}

	public long saveUserData(String userName, String password) {

		Date currentDate = new Date();

		// Set the userStatus as "active"
		String userStatus = "active";

		// Set the user type as "customer"
		String userType = "customer";

		String insertSql = "INSERT INTO Users (userName, userCDate, userPwd, userType, userStatus) "
				+ "VALUES (?, ?, ?, ?, ?)";

		return jdbcTemplate.update(insertSql, userName, currentDate, password, userType, userStatus);

	}

	@Override
	public void saveCustomerData(CustomerData customerData) {
		String sql = "INSERT INTO Customers (cust_fname, cust_lname, cust_dob, cust_address, cust_gender, cust_cdate,cust_user_id, cust_aadhar, cust_status, cust_luudate, cust_luuser) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?)";

		jdbcTemplate.update(sql, customerData.getCust_fname(), customerData.getCust_lname(), customerData.getCust_dob(),
				customerData.getCust_address(), String.valueOf(customerData.getCust_gender()),
				customerData.getCust_cdate(), customerData.getCust_user_id(), customerData.getCust_aadhar(),
				customerData.getCust_status(), customerData.getCust_luudate(), customerData.getCust_luuser());
	}

	public List<CustomerData> getAllCustomersFromDao() {

		return jdbcTemplate.query("select * from Customers", new CustomerDataRowMapper());

	}

	public List<UserData> getAllUsersFromDao() {
		// TODO Auto-generated method stub
		return jdbcTemplate.query("select * from users", new UsersDataRowMapper());
	}

	public int resetpwd(String email, String pwd) {
		// int userId1 = (int) session.getAttribute("userId");

		String sql = "UPDATE updatePasswordTable SET username = ?, password = ? WHERE userId = ?";
		return jdbcTemplate.update(sql, email, pwd, 1);
	}

	public void updateCustomersData(List<CustomerData> updatedCustomerData) {
		for (CustomerData customer : updatedCustomerData) {
			// Step 1: Delete existing record with the same cust_id
			String deleteSql = "DELETE FROM Customers WHERE cust_id = ?";
			jdbcTemplate.update(deleteSql, customer.getCust_id());

			// Step 2: Insert the updated customer data
			String insertSql = "INSERT INTO Customers (cust_id, cust_fname, cust_lname, cust_dob, "
					+ "cust_address, cust_gender, cust_cdate,cust_user_id, cust_aadhar, cust_status, "
					+ "cust_luudate, cust_luuser) VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)";

			jdbcTemplate.update(insertSql, customer.getCust_id(), customer.getCust_fname(), customer.getCust_lname(),
					customer.getCust_dob(), customer.getCust_address(), customer.getCust_gender(),
					customer.getCust_cdate(), customer.getCust_user_id(), customer.getCust_aadhar(),
					customer.getCust_status(), customer.getCust_luudate(), customer.getCust_luuser());
		}
	}

	public UserLoginValidation getLoginTimeRange(Long userId) {
		String sql = "SELECT * FROM user_login_validation WHERE user_id = ?";

		return jdbcTemplate.queryForObject(sql, new Object[] { userId }, new UserLoginValidationRowMapper());
	}

	public Claim getClaimByid(int clamIplcId) {
		System.out.println(clamIplcId + "InGetClaimById");
		return jdbcTemplate.queryForObject(SQL_GET_CLAIM_BY_ID, new Object[] { 68 }, new ClaimMapper());
	}

	public void addClaimBills(String originalFilename, String filePath, int cid, int i) {
		System.out.println("brooo");
		jdbcTemplate.update(SQL_INSERT_CLAIMBill, cid, originalFilename, filePath, i);
		// private String SQL_INSERT_CLAIMBill = "insert into
		// Claim_bills(clam_id,clbl_document_title,clbl_document_path,clbl_claim_amount) values(?,?,?,?)";

	}

	public void addClaimApplication(ClaimApplication application) {
		System.out.println(application.getMemberIndex() + 1);
		String query = "insert into insurance_claim(policy_id,member_index,relation,joined_date,patient_name,date_of_birth,gender,contact_number,address,disease,diagnosis,treatment,claimAmount,hosp_name) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] values = { application.getClamIplcId(), application.getMemberIndex(), application.getRelation(),
				application.getJoinedDate(), application.getPatientName(), application.getDateOfBirth(),
				application.getGender(), application.getContactNumber(), application.getAddress(),
				application.getDisease(), application.getDiagnosis(), application.getTreatment(),
				application.getClaimAmountRequested(), "Service" };
		jdbcTemplate.update(query, values);

	}

	public void addClaim(int clamIplcId, double claimAmountRequested) {
		LocalDate currentDate = LocalDate.now();
		java.sql.Date sqlDate = java.sql.Date.valueOf(currentDate);

		jdbcTemplate.update(SQL_INSERT_CLAIM, "INDI", "IND", sqlDate, claimAmountRequested, clamIplcId);

	}

	public ArrayList<Claim> getAllClaims() {

		return (ArrayList<Claim>) jdbcTemplate.query(SQL_GET_CLAIMS, new ClaimMapper());
	}

	public ArrayList<Claim> getFilteredClaims(String status) {

		return (ArrayList<Claim>) jdbcTemplate.query(SQL_GET_FILTERED_CLAIMS, new Object[] { status },
				new ClaimMapper());
	}

	public Claim getClaimById1(int clamId) {
		// TODO Auto-generated method stub
		return jdbcTemplate.queryForObject(SQL_GET_CLAIM_BY_ID, new Object[] { clamId }, new ClaimMapper());
	}

	public List<PolicyMembers> getPoliMem() {
		// TODO Auto-generated method stub
		return jdbcTemplate.query(
				"select ipcm_mindex,iplc_id, ipcm_membername, ipcm_relation from insurancepolicycoveragemembers",
				new PolicyMembersRowMapper());
	}

}
