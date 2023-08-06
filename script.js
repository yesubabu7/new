const productsPerPage = 4; // Number of products per page
let currentPage = 1;
let currentRatingFilter = 0; // Default: All Ratings
let productsData = [];

$(document).ready(function() {
  fetch("products.json")
    .then(function(response) {
      return response.json();
    })
    .then(function(products) {
      productsData = products;
      displayProducts(currentPage);
    });

  $("#prevPage").on("click", function() {
    if (currentPage > 1) {
      currentPage--;
      displayProducts(currentPage);
    }
  });

  $("#nextPage").on("click", function() {
    if (currentPage < Math.ceil(productsData.length / productsPerPage)) {
      currentPage++;
      displayProducts(currentPage);
    }
  });

  $("#filterButton").on("click", function() {
    currentRatingFilter = parseInt($("#ratingFilter").val());
    displayProducts(currentPage);
  });

  $("#view-cart").on("click", function(e) {
    e.preventDefault();
    $("#cart-content").toggle();
    displayCartItems();
  });
});

function displayProducts(page) {
  let startIndex = (page - 1) * productsPerPage;
  let endIndex = startIndex + productsPerPage;
  let filteredProducts = productsData;

  if (currentRatingFilter !== 0) {
    filteredProducts = productsData.filter(product => product.ratings >= currentRatingFilter);
  }

  let placeholder = $("#data-output");
  let out = "";
  for (let i = startIndex; i < endIndex && i < filteredProducts.length; i++) {
    let product = filteredProducts[i];
    out += `
      <div class="col-md-3 mb-4">
        <div class="card">
          <img src="${product.image}" class="card-img-top product-image" alt="${product.name}">
          <div class="card-body">
            <h5 class="card-title">${product.name}</h5>
            <p class="card-text">Actual Price: ${product.actual_price}</p>
            <p class="card-text">Discount Price: ${product.discount_price}</p>
            <p class="card-text">Rating: ${product.ratings}</p>
            <button class="btn btn-primary add-to-cart" data-id="${i}">Add to Cart</button>
          </div>
        </div>
      </div>
    `;
  }

  placeholder.html(out);

  $(".add-to-cart").on("click", function() {
    let productId = parseInt($(this).attr("data-id"));
    addToCart(productId);
  });
}

function addToCart(productId) {
  let cartItems = localStorage.getItem("cartItems");
  if (!cartItems) {
    cartItems = [];
  } else {
    cartItems = JSON.parse(cartItems);
  }
  cartItems.push(productsData[productId]);
  localStorage.setItem("cartItems", JSON.stringify(cartItems));
}

function displayCartItems() {
  let cartItemsContainer = $("#cart-items");
  cartItemsContainer.html("");

  let cartItems = JSON.parse(localStorage.getItem("cartItems")) || [];

  cartItems.forEach((item, index) => {
    let cartItemDiv = $("<div class='cart-item'></div>");
    cartItemDiv.html(`
      <span>${item.name} - ${item.discount_price}</span>
      <button class="btn btn-danger remove-btn" data-index="${index}">Remove</button>
    `);
    cartItemsContainer.append(cartItemDiv);
  });

  $(".remove-btn").on("click", function() {
    let index = parseInt($(this).attr("data-index"));
    cartItems.splice(index, 1);
    localStorage.setItem("cartItems", JSON.stringify(cartItems));
    displayCartItems();
  });
}

