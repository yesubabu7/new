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

@WebServlet("/Products")
public class Products extends HttpServlet {

	String ProductName;
	String ProductType;
	String ImageUrl;
	Double ProductPrice;

	public Products() {
	}

	public Products(String productName, String productType, String imageUrl, Double productPrice) {
		super();
		ProductName = productName;
		ProductType = productType;
		ImageUrl = imageUrl;
		ProductPrice = productPrice;
	}

	public String getProductName() {
		return ProductName;
	}

	public void setProductName(String productName) {
		ProductName = productName;
	}

	public String getProductType() {
		return ProductType;
	}

	public void setProductType(String productType) {
		ProductType = productType;
	}

	public String getImageUrl() {
		return ImageUrl;
	}

	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}

	public Double getProductPrice() {
		return ProductPrice;
	}

	public void setProductPrice(Double productPrice) {
		ProductPrice = productPrice;
	}

	ArrayList<Products> p = new ArrayList<>();

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		PrintWriter out = res.getWriter();

		String Image = req.getParameter("Image");
		String productTypee = req.getParameter("product_type");
		String proName = req.getParameter("product_name");
		Double proPrice = Double.parseDouble(req.getParameter("product_price"));

		p.add(new Products(proName, productTypee, Image, proPrice));

		HttpSession hs = req.getSession();
		hs.setAttribute("productsCart", p);
		int sessionTimeoutInSeconds = 30 * 60;
		hs.setMaxInactiveInterval(sessionTimeoutInSeconds);

		res.sendRedirect("DisplayProducts");

	}
}
