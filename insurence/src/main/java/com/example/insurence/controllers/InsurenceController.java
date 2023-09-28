package com.example.insurence.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.insurence.models.Claim;
import com.example.insurence.models.ClaimApplication;
import com.example.insurence.models.CustomerData;
import com.example.insurence.models.UserData;
import com.example.insurence.repositories.InsurenceRepository;

import jakarta.servlet.http.HttpSession;

@RestController
public class InsurenceController {

	private final String uploadDir = "insurence/src/main/resources/static/file"; // Replace with your actual upload
																					// directory

	InsurenceRepository insurenceRepository;
	private HttpSession session;

	List<UserData> UserDataList;

	@Autowired
	public InsurenceController(InsurenceRepository insurenceRepository, HttpSession httpSession) {

		this.insurenceRepository = insurenceRepository;
		this.session = httpSession;
	}

	@PostMapping("/saveUserData")
	@ResponseBody
	public Long saveUserData(@RequestParam("username") String userName, @RequestParam("password") String password) {

		// System.out.println("Received user data: " + userData);

		return insurenceRepository.saveUserData(userName, password);

	}

	@PostMapping("/saveCustomerData")
	@ResponseBody
	public String saveCustomerData(@RequestBody CustomerData customerData,Model model) {
		customerData.setCust_user_id(2);
		// replace withint userId= (int)sessionGetAttribute("userId");
		System.out.println("Received customer_userId: " + customerData.getCust_user_id());
		
		int userId= (int)session.getAttribute("userId");
		int customerId=insurenceRepository.getCustIdByUserId(userId);
		
		model.addAttribute("customerId",customerId);
		model.addAttribute("userId",userId);

		customerData.setCust_status("Active");
		customerData.setCust_luudate(new Date());
		customerData.setCust_luuser(1);

		// Set a default value for cust_cdate (assuming it's a Date field)
		customerData.setCust_cdate(new Date()); // You can set it to the current date

		System.out.println("Received customer data: " + customerData);
		insurenceRepository.saveCustomerData(customerData);

		return "Customer data saved successfully";
	}

	@RequestMapping(value = "/Customers", method = RequestMethod.GET)

	public List<CustomerData> getAllCustomers() {

		System.out.println("customers");

		List<CustomerData> list = insurenceRepository.getAllCustomers();
		return list;
	}

	@RequestMapping(value = "/UpdateCustomers", method = RequestMethod.POST)

	public String UpdateCustomers(@RequestBody List<CustomerData> updatedCustomerData) {

		for (CustomerData customerData : updatedCustomerData) {
			customerData.setCust_status("Active");
			customerData.setCust_luudate(new Date());
			customerData.setCust_luuser(1);

			// Set a default value for cust_cdate (assuming it's a Date field)
			customerData.setCust_cdate(new Date());
		}

		String check = insurenceRepository.updateCustomersData(updatedCustomerData);

		return check;
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public List<UserData> getAllUsers() {

		System.out.println("users");

		UserDataList = insurenceRepository.getAllUsers();

		return UserDataList;
	}

	@RequestMapping(value = "/UserLogin", method = RequestMethod.POST)

	public String userCredinitial(@RequestParam("username") String userName,
			@RequestParam("password") String password) {

		UserDataList = insurenceRepository.getAllUsers();
		boolean b = insurenceRepository.userChecking(userName, password, UserDataList);
		if (b) {
			return "login successful";
		}

		System.out.println("customers");

		return "login Failed";
	}

	@GetMapping("/email")
	@ResponseBody
	public String email(@RequestParam("to") String to_mail) {
		String email = to_mail;
		session.setAttribute("email", email);
		// storing generated otp
		int OTP = insurenceRepository.sendmail(to_mail);
		System.out.println(to_mail + "email here");
		System.out.println(OTP + "otp here");

		LocalTime currentTime = LocalTime.now();
		session.setAttribute("time", currentTime.plusMinutes(5));
		session.setAttribute("OTP", OTP);

		return "Email Sent Successfully";

	}

	@PostMapping(value = "/validateOTP")
	public String validateOTP(@RequestParam("otp") String otp, Model model) {
		model.addAttribute("to", "");
		int OTP = Integer.parseInt(otp);

		int originalOtp = (Integer) session.getAttribute("OTP");
		String email = (String) session.getAttribute("email");

		LocalTime time = (LocalTime) session.getAttribute("time");
		int comp = time.compareTo(LocalTime.now());
		// checking the otp sent by the user if true returning reset page else need to stay in the same page with error
		// msg
		if (originalOtp == OTP && comp > 0) {

			return "otp Match";
		}
		if (comp < 0)
			return "OTP expired, please try again..";
		else
			return "Invalid OTP, please try again..";

	}

	@PostMapping("/reset")
	public String reset(Model model, @RequestParam("email") String email, @RequestParam("pwd") String pwd,
			@RequestParam("cnfpwd") String cnfpwd) {
		System.out.println(email + " " + pwd + " " + cnfpwd);
		int x = insurenceRepository.resetpwd(email, pwd, cnfpwd);
		if (x > 0)
			return "password Changed";
		else
			return "pasword not match";

	}

	@RequestMapping(value = "/claimbills", method = RequestMethod.POST)
	public String claimData(@RequestParam("file[]") MultipartFile[] files, @RequestParam("claimAmount[]") int[] amount,
			Claim claim, ClaimApplication application, Model model) {

		System.out.println(claim.toString() + "amountRequested");
		System.out.println(application.toString() + "adresss");

		insurenceRepository.addClaimApplication(application);
		System.out.println(claim.getClamIplcId() + "brooo");
		insurenceRepository.addClaim(claim.getClamIplcId(), application.getClaimAmountRequested());
		Claim clm_id = insurenceRepository.getClaimByid(claim.getClamIplcId());
		int cid = clm_id.getClamId();
		String uploadDir = "src/main/resources/static/file";
		int i = 0;
		try {
			// Create the target directory if it doesn't exist
			Files.createDirectories(Paths.get(uploadDir));

			for (MultipartFile file : files) {
				// Get the original file name
				String fileName = StringUtils.cleanPath(file.getOriginalFilename());

				// Create the target file path within the directory
				Path targetLocation = Paths.get(uploadDir).resolve(fileName);

				// Copy the file to the target location
				Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

				String fn = "file/" + file.getOriginalFilename();

				System.out.println("original file name..." + file.getOriginalFilename());
				System.out.println("file path..." + fn);

				insurenceRepository.addClaimBills(file.getOriginalFilename(), fn, cid, amount[i]);

				i++;

			}

			// After successfully storing all files, you can redirect to a success page or return a response accordingly
			return "SETCLAIMS";
		} catch (IOException ex) {
			ex.printStackTrace();

		}

		return "SETCLAIMS";
	}

	@GetMapping(value = "/getFamilyMembers")
	public List<String> getFamily(@RequestParam("policy") int id, Model model) {
		List<String> members = insurenceRepository.getFamilyByPolicy(id);
		return members;
	}

	@GetMapping(value = "/getAllClaims")
	public String getAllClaims(Model model) {
		System.out.println("madh");
		ArrayList<Claim> li = (ArrayList<Claim>) insurenceRepository.getAllClaims();
		System.out.println(li.size());
		model.addAttribute("claims", li);
		return "Claims";
	}

	@PostMapping(value = "/viewClaim")
	public String getClaimById(Model model, @RequestParam("clamId") int clamId) {
		Claim cl = insurenceRepository.getClaimById(clamId);
		model.addAttribute("claim", cl);
		return "viewclaim";
	}

	@GetMapping(value = "/getFilteredClaims")
	public String getFilteredClaims(Model model, @RequestParam("status") String status) {
		System.out.println("madh");
		ArrayList<Claim> li = (ArrayList<Claim>) insurenceRepository.getFilteredClaims(status);
		System.out.println(li.size());
		model.addAttribute("claims", li);
		return "Claims";
	}

}