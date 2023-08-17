package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/EmployeeServlet")
public class JdbcServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String jdbcUrl = "jdbc:postgresql://192.168.110.48:5432/plf_training";
		String username = "plf_training_admin";
		String password = "pff123";
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

			HttpSession session = request.getSession(true);
			ResultSet resultSet = (ResultSet) session.getAttribute("resultSet");

			Integer currentPointer = (Integer) session.getAttribute("currentPointer");

			String action = request.getParameter("action");

			if (resultSet == null) {
				resultSet = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)
						.executeQuery("SELECT * FROM emp");
				session.setAttribute("resultSet", resultSet);
			}

			if (currentPointer == null) {
				currentPointer = 0; // Set a default value
			}
			session.setAttribute("currentPointer", currentPointer);

			if ("first".equals(action)) {
				currentPointer = 0;
			} else if ("previous".equals(action)) {
				if (currentPointer > 0) {
					currentPointer--;
				}
			} else if ("next".equals(action)) {
				if (resultSet.next()) {
					currentPointer++;
				}
			} else if ("last".equals(action)) {
				resultSet.last();
				currentPointer = resultSet.getRow() - 1;
			} else if ("edit".equals(action)) {
				resultSet.absolute(currentPointer + 1);
				String editedName = resultSet.getString("ename");
				int editedEmpId = resultSet.getInt("emp_no");
				session.setAttribute("editedName", editedName);
				session.setAttribute("editedEmpId", editedEmpId);
			}

			else if ("save".equals(action)) {
				String editedName = request.getParameter("editedName");
				String editedEmpIdString = request.getParameter("editedEmpId");
				int editedEmpId = 0; // Default value

				if (!editedEmpIdString.isEmpty()) {
					try {
						editedEmpId = Integer.parseInt(editedEmpIdString);
					} catch (NumberFormatException e) {
						e.printStackTrace();
						// Handle the exception if parsing fails
						// You might want to set an appropriate error message here
					}
				}

				resultSet.absolute(currentPointer + 1);
				int empId = resultSet.getInt("emp_no");
				String updateQuery = "UPDATE emp SET ename = ?, emp_no = ? WHERE emp_no = ?";
				PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
				preparedStatement.setString(1, editedName);
				preparedStatement.setInt(2, editedEmpId);
				preparedStatement.setInt(3, empId);
				preparedStatement.executeUpdate();
				session.removeAttribute("editedName");
				session.removeAttribute("editedEmpId");
			}

			else if ("delete".equals(action)) {
				resultSet.absolute(currentPointer + 1);
				int empId = resultSet.getInt("emp_no");
				String deleteQuery = "DELETE FROM emp WHERE emp_no = ?";
				PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
				preparedStatement.setInt(1, empId);
				preparedStatement.executeUpdate();
				resultSet = connection.createStatement().executeQuery("SELECT * FROM emp");
				session.setAttribute("resultSet", resultSet);
			}

			session.setAttribute("currentPointer", currentPointer);

			out.println("<html><body>");
			if (resultSet.absolute(currentPointer + 1)) {
				String name = resultSet.getString("ename");
				int empId = resultSet.getInt("emp_no");
				out.println("<h1>Employee Details:</h1>");
				out.println("<p><strong>ID:</strong> " + empId + "</p>");
				out.println("<p><strong>Name:</strong> " + name + "</p>");
			} else {
				out.println("<h1>No more employee records.</h1>");
			}
			out.println("<hr>");
			out.println("<form action=\"EmployeeServlet\" method=\"get\">");
			out.println("<input type=\"hidden\" name=\"action\" value=\"first\">");
			out.println("<input type=\"submit\" value=\"First\">");
			out.println("<input type=\"submit\" name=\"action\" value=\"previous\">");
			out.println("<input type=\"submit\" name=\"action\" value=\"next\">");
			out.println("<input type=\"submit\" name=\"action\" value=\"last\">");
			out.println("<input type=\"submit\" name=\"action\" value=\"edit\">");
			out.println("<input type=\"submit\" name=\"action\" value=\"delete\">");
			out.println("<input type=\"submit\" name=\"action\" value=\"save\">");
			out.println("</form>");
			if ("edit".equals(action)) {
				String editedName = (String) session.getAttribute("editedName");
				int editedEmpId = (int) session.getAttribute("editedEmpId");
				out.println("<form action=\"EmployeeServlet\" method=\"post\">");
				out.println("<input type=\"hidden\" name=\"action\" value=\"save\">");
				out.println("<input type=\"hidden\" name=\"editedEmpId\" value=\"" + editedEmpId + "\">");
				out.println("Name: <input type=\"text\" name=\"editedName\" value=\"" + editedName + "\"><br>");
				out.println("Employee ID: " + editedEmpId + "<br>");
				out.println("<input type=\"submit\" value=\"Save\">");
				out.println("</form>");
			}
			out.println("</body></html>");

			connection.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			out.println("An error occurred: " + e.getMessage());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}















import com.google.gson.JsonObject; // Import the Gson library for JSON handling

@WebServlet("/EmployeeServlet")
public class JdbcServlet extends HttpServlet {
    // ... other code ...

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ... existing code ...

        if ("first".equals(action) || "previous".equals(action) || "next".equals(action) || "last".equals(action)) {
            JsonObject jsonObject = new JsonObject();
            if (resultSet.absolute(currentPointer + 1)) {
                int empId = resultSet.getInt("emp_no");
                String empName = resultSet.getString("ename");
                jsonObject.addProperty("empId", empId);
                jsonObject.addProperty("empName", empName);
            }
            response.setContentType("application/json");
            response.getWriter().write(jsonObject.toString());
            return;
        }

        // ... remaining code ...
    }

    // ... other methods ...
}

