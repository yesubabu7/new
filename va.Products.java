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















<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Online Store</title>
    <style>
        .product-card {
            border: 1px solid #ccc;
            padding: 10px;
            margin: 10px;
            width: 200px;
            display: inline-block;
        }

        .product-image {
            width: 100%;
            max-height: 150px;
        }

        .product-name {
            font-weight: bold;
            margin-top: 5px;
        }

        .product-price {
            color: #007bff;
        }
    </style>
</head>
<body>
    <h1>Online Store</h1>
    <div id="productList"></div>
    <div id="cart">
        <h2>Cart</h2>
        <ul id="cartItems"></ul>
     </div>
        
        <button id="viewCart">View Cart</button>
    </div>
	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	
    <script>
        const products = [
            // Your product data JSON here
            {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg",
    "product_type": "Gold",
    "product_name": "Alloy-Gold plated Gold",
    "price": 99
  },
  {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg ",
    "product_type": "Silver",
    "product_name": "Sterling Silver Necklace",
    "price": 99
  },
  {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg",
    "product_type": "Electronics",
    "product_name": "Smartphone X",
    "price": 999
  },
  {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg",
    "product_type": "Clothing",
    "product_name": "Men's T-shirt",
    "price": 99
  },
  {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg ",
    "product_type": "Books",
    "product_name": "The Great Gatsby",
    "price": 12.95
  },
  {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg",
    "product_type": "Food",
    "product_name": "Organic Apples",
    "price": 4.50
  },
  {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg ",
    "product_type": "Beauty",
    "product_name": "Anti-aging Serum",
    "price": 99
  },
  {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg",
    "product_type": "Sports",
    "product_name": "Yoga Mat",
    "price": 24.95
  },
  {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg",
    "product_type": "Furniture",
    "product_name": "Modern Coffee Table",
    "price": 99.50
  },
  {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg",
    "product_type": "Toys",
    "product_name": "Remote Control Car",
    "price": 99
  },
  {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg",
    "product_type": "Accessories",
    "product_name": "Classic Watch",
    "price": 149
  },
  {
    "ImageUrl": "https://cdn1.jewelxy.com/live/img/business_product/7OOQveQu2n_20220323155709.jpg",
    "product_type": "Electronics",
    "product_name": "Laptop XYZ",
    "price": 99
  }
        ];

        const productList = document.getElementById("productList");
        const cartItems = document.getElementById("cartItems");
        const viewCartButton = document.getElementById("viewCart");

        function renderProducts() {
            productList.innerHTML = "";
            products.forEach(product => {
                const productCard = document.createElement("div");
                productCard.classList.add("product-card");

                const productImage = document.createElement("img");
                productImage.src = product.ImageUrl;
                productImage.classList.add("product-image");
                productCard.appendChild(productImage);

                const productName = document.createElement("div");
                productName.textContent = product.product_name;
                productName.classList.add("product-name");
                productCard.appendChild(productName);

                const productPrice = document.createElement("div");
                productPrice.textContent = `$${product.price.toFixed(2)}`;
                productPrice.classList.add("product-price");
                productCard.appendChild(productPrice);

                const addToCartButton = document.createElement("button");
                addToCartButton.textContent = "Add to Cart";
                addToCartButton.addEventListener("click", () => addToCart(product));
                productCard.appendChild(addToCartButton);

                productList.appendChild(productCard);
            });
        }

        const cart = [];

        function addToCart(product) {
            cart.push(product);
            updateCartView();
        }

        function updateCartView() {
            cartItems.innerHTML = "";
            cart.forEach(item => {
                const cartItem = document.createElement("li");
                cartItem.textContent = item.product_name;

                const removeButton = document.createElement("button");
                removeButton.textContent = "Remove";
                removeButton.addEventListener("click", () => removeFromCart(item));
                cartItem.appendChild(removeButton);

                cartItems.appendChild(cartItem);
            });
        }

        function removeFromCart(itemToRemove) {
            const index = cart.findIndex(item => item.product_name === itemToRemove.product_name);
            if (index !== -1) {
                cart.splice(index, 1);
                updateCartView();
            }
        }

        viewCartButton.addEventListener("click", () => {
            // Send an AJAX request to the CartServlet to remove items from the cart
            $.ajax({
                url: "CartServlet?action=remove",
                method: "GET",
                success: function () {
                    cart.length = 0;
                    updateCartView();
                    alert("Items removed from cart.");
                },
                error: function () {
                    alert("Error removing items from cart.");
                }
            });
        });

        renderProducts();

    </script>
</body>
</html>

