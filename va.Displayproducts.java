package demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/DisplayProducts")
public class DisplayProducts extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DisplayProducts() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		HttpSession hs = request.getSession();
		ArrayList<Products> myProducts = (ArrayList<Products>) hs.getAttribute("productsCart");

		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Products Display</title>");
		out.println(
				"<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\">");
		out.println("</head>");
		out.println("<body>");

		out.println("<div class=\"container\">");

		if (myProducts != null) {
			for (Products item : myProducts) {
				out.println("<div class=\"card\" style=\"width: 18rem;\">");
				out.println("<img src=\"" + item.getImageUrl() + "\" class=\"card-img-top\" alt=\""
						+ item.getProductName() + "\">");
				out.println("<div class=\"card-body\">");
				out.println("<h5 class=\"card-title\">" + item.getProductName() + "</h5>");
				out.println("<p class=\"card-text\">Type: " + item.getProductType() + "</p>");
				out.println("<p class=\"card-text\">Price: $" + item.getProductPrice() + "</p>");
				out.println("</div>");
				out.println("</div>");
			}
		} else {
			out.println("<p>No Products to display</p>");
		}

		out.println("</div>");

		out.println("</body>");
		out.println("</html>");
	}
}
