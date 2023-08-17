package demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject; // Import the JSONObject class

@WebServlet("/JdbcServlet")
public class JdbcServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String jdbcUrl = "jdbc:postgresql://192.168.110.48:5432/plf_training";
		String username = "plf_training_admin";
		String password = "pff123";
		response.setContentType("application/json"); // Set the content type to JSON
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String action = request.getParameter("action");
		System.out.println(action);
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

			HttpSession session = request.getSession(true);
			ResultSet resultSet = (ResultSet) session.getAttribute("resultSet");

			Integer currentPointer = (Integer) session.getAttribute("currentPointer");

			if (currentPointer == null) {
				currentPointer = 0; // Set a default value
			}

			if (resultSet == null) {
				resultSet = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)
						.executeQuery("SELECT * FROM emp");
				session.setAttribute("resultSet", resultSet);
			}

			JSONObject jsonObject = new JSONObject(); // Create JSON object to store response data

			if ("first".equals(action)) {
				if (resultSet.first()) {
					currentPointer = 0;
				}
			} else if ("previous".equals(action)) {
				if (resultSet.previous()) {
					currentPointer--;
				}
			} else if ("next".equals(action)) {
				if (resultSet.next()) {
					currentPointer++;
				}
			} else if ("last".equals(action)) {
				if (resultSet.last()) {
					currentPointer = resultSet.getRow() - 1;
				}
			}

			// Retrieve data from the result set based on the currentPointer
			int empId = resultSet.getInt("emp_no");
			String empName = resultSet.getString("ename");

			// Populate the JSON object with empId and empName
			jsonObject.put("empId", empId);
			jsonObject.put("empName", empName);

			// Send the JSON response
			out.print(jsonObject.toString());

			session.setAttribute("currentPointer", currentPointer); // Update currentPointer

			connection.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			JSONObject errorObject = new JSONObject();
			errorObject.put("error", e.getMessage());
			out.println(errorObject.toString());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
