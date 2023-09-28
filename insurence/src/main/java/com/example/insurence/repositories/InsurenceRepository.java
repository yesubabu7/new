package com.example.insurence.repositories;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.insurence.contracts.RepositoryInterface;
import com.example.insurence.daos.InsurenceDao;
import com.example.insurence.models.AuthUtils;
import com.example.insurence.models.Claim;
import com.example.insurence.models.ClaimApplication;
import com.example.insurence.models.CustomerData;
import com.example.insurence.models.PolicyMembers;
import com.example.insurence.models.UserData;
import com.example.insurence.models.UserLoginValidation;

import jakarta.servlet.http.HttpSession;

@Repository
public class InsurenceRepository implements RepositoryInterface {

	private InsurenceDao insurenceDao;
	HttpSession session;

	@Autowired
	public InsurenceRepository(InsurenceDao insurenceDao, HttpSession session) {
		this.insurenceDao = insurenceDao;
		this.session = session;
	}

	public Long saveUserData(String userName, String password) {

		long id = insurenceDao.saveUserData(userName, password);
		return id;
	}

	public void saveCustomerData(CustomerData customerData) {

		insurenceDao.saveCustomerData(customerData);

	}

	public List<CustomerData> getAllCustomers() {

		return insurenceDao.getAllCustomersFromDao();

	}

	public List<UserData> getAllUsers() {

		return insurenceDao.getAllUsersFromDao();
	}

	public int sendmail(String to_mail) {
		String to = to_mail;
		String subject = "Password Reset";

		int OTP = generateOTP();
		String body = "The OTP for the Password Reset: " + OTP;
		sendEmail(to, subject, body);

		return OTP;
	}

	private static void sendEmail(String to, String subject, String body) {
		String host = "smtp.gmail.com";
		int port = 587;
		String username = "avengersbtrs@gmail.com";
		String password = "urpr twig ffeb uqlx";

		// Set properties
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		// Create session
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// Create message
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(body);
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	private static int generateOTP() {
		Random random = new Random();
		int randomNumber = 100000 + random.nextInt(900000);

		return randomNumber;
	}

	public int resetpwd(String email, String pwd, String cnfpwd) {
		if (pwd.equals(cnfpwd))
			return insurenceDao.resetpwd(email, pwd);

		else
			return 0;

	}

	public boolean userChecking(String userName, String password, List<UserData> userDataList) {
		for (UserData userData : userDataList) {

			// checking username and password
			if (userName.equals(userData.getUserName()) && password.equals(userData.getUserPwd())) {
				System.out.println(userData.getUserPwd());
				System.out.println(userData.getUserName());

				session.setAttribute("userId", userData.getUserId());

				// generate randome key for security and store in session
				String key = AuthUtils.generateKey();
				session.setAttribute("key", key);

				Long userId = (Long) session.getAttribute("userId");

				System.out.println(userId + "userId Curent from sesion");

				UserLoginValidation user = insurenceDao.getLoginTimeRange(userId);

				Date currentDate = new Date();

				// Check if the current date and time falls within the login time range
				// Format the current date to match the login date format
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String formattedCurrentDate = dateFormat.format(currentDate);

				// Get the login time range as strings
				String loginTimeFrom = dateFormat.format(user.getLoginTimeFrom());
				String loginTimeTo = dateFormat.format(user.getLoginTimeTo());

				System.out.println("current Date" + formattedCurrentDate);

				System.out.println("loginDate to Date" + loginTimeTo);

				// checking login time is expired or not
				if (formattedCurrentDate.compareTo(loginTimeFrom) >= 0
						&& formattedCurrentDate.compareTo(loginTimeTo) <= 0) {
					// Your code here

					System.out.println("iam inside time checking");

					System.out.println("Time checking");
					return true;

				}
			}

		}
		return false;
	} // No matching user found

	public String updateCustomersData(List<CustomerData> updatedCustomerData) {

		insurenceDao.updateCustomersData(updatedCustomerData);

		return "updated Succesfully";
	}

	public Claim getClaimByid(int clamIplcId) {

		return insurenceDao.getClaimByid(clamIplcId);
	}

	public void addClaimBills(String originalFilename, String fn, int cid, int i) {
		insurenceDao.addClaimBills(originalFilename, fn, cid, i);

	}

	public void addClaimApplication(ClaimApplication application) {
		insurenceDao.addClaimApplication(application);

	}

	public void addClaim(int clamIplcId, double claimAmountRequested) {
		insurenceDao.addClaim(clamIplcId, claimAmountRequested);

	}

	public ArrayList<Claim> getAllClaims() {
		// TODO Auto-generated method stub
		System.out.println("hem");
		return (ArrayList<Claim>) insurenceDao.getAllClaims();
	}

	public ArrayList<Claim> getFilteredClaims(String status) {
		// TODO Auto-generated method stub
		return (ArrayList<Claim>) insurenceDao.getFilteredClaims(status);
	}

	public Claim getClaimById(int clamId) {
		// TODO Auto-generated method stub
		return insurenceDao.getClaimById1(clamId);
	}

	public List<String> getFamilyByPolicy(int id) {
		List<PolicyMembers> members = insurenceDao.getPoliMem();
		List<String> names = new ArrayList<>();
		for (PolicyMembers mem : members) {

			if (mem.getInsurancePolicyId() == id) {
				names.add(mem.getMemberName());
			}
		}
		return names;
	}

	public int getCustIdByUserId(int userId) {
		return insurenceDao.getCustIdByUserId(userId);
		
	}

}