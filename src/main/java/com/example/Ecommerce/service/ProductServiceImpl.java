package com.example.Ecommerce.service;

import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.model.Cart;
import com.example.Ecommerce.model.Category;
import com.example.Ecommerce.model.Product;
import com.example.Ecommerce.payload.CartDTO;
import com.example.Ecommerce.payload.ProductDTO;
import com.example.Ecommerce.payload.ProductResponse;
import com.example.Ecommerce.repository.CartRepository;
import com.example.Ecommerce.repository.CategoryRepo;
import com.example.Ecommerce.repository.ProductRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class ProductServiceImpl implements ProductService{


    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    String path;

    @Override
    public ProductDTO addProduct(Integer categoryId, ProductDTO productDTO) {
        Category category=categoryRepo.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("category","categoryid",categoryId));

        Product product=modelMapper.map(productDTO,Product.class);

        Product isProductPresent = productRepo.findByProductName(product.getProductName());
        if(isProductPresent!=null)
            throw new APIException("Product with name: "+product.getProductName()+" is already exists");

        product.setCategory(category);
        product.setImage("default.png");
        Double specialPrice=product.getPrice()-(product.getDiscount()*0.01* product.getPrice());
        product.setSpecialPrice(specialPrice);

        Product savedProduct=productRepo.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(String sortBy, String sortOrder,Integer pageNumber, Integer pageSize) {
        Sort sort=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable=PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> productPage=productRepo.findAll(pageable);
        List<Product> products=productPage.getContent();
        if(products.isEmpty())
            throw new APIException("products not found");
        List<ProductDTO> productDTOS=products.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Integer categoryId,String sortBy, String sortOrder,Integer pageNumber, Integer pageSize) {
        Category category=categoryRepo.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("category","categoryid",categoryId));
        Sort sort=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable=PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> productPage=productRepo.findByCategory(category,pageable);
        List<Product> products=productPage.getContent();
        if(products.isEmpty())
            throw new APIException("No Product Found with the mentioned category");
        List<ProductDTO> productDTOS=products.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByKeyword(String keyword,String sortBy, String sortOrder,Integer pageNumber, Integer pageSize) {
        Sort sort=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable=PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> productPage=productRepo.findByProductNameLikeIgnoreCase('%'+keyword+'%',pageable);
        List<Product> products=productPage.getContent();
        if(products.isEmpty())
            throw new APIException("No Product Found");
        List<ProductDTO> productDTOS=products.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Integer productId) {
        Product productFromDB=productRepo.findById(productId).orElseThrow(()-> new ResourceNotFoundException("product","productId",productId));
        productFromDB.setProductName(productDTO.getProductName());
        productFromDB.setPrice(productDTO.getPrice());
        productFromDB.setDiscount(productDTO.getDiscount());
        productFromDB.setSpecialPrice(productDTO.getSpecialPrice());
        productFromDB.setQuantity(productDTO.getQuantity());

       Product savedProduct=productRepo.save(productFromDB);

       List<Cart> carts=cartRepository.findCartsByProductId(productId);

       List<CartDTO> cartDTOs=carts.stream().map(cart -> {
           CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
           List<ProductDTO> products=cart.getCartItems().stream().map(p->modelMapper.map(p.getProduct(),ProductDTO.class)).toList();
           cartDTO.setProducts(products);
           return cartDTO;
       }).toList();

       cartDTOs.forEach(cart->cartService.updateProductInCarts(cart.getCartId(),productId));

        return modelMapper.map(savedProduct,ProductDTO.class);

    }

    @Override
    public void deleteProduct(Integer productId) {
        Product productFromDB=productRepo.findById(productId).orElseThrow(()-> new ResourceNotFoundException("product","productId",productId));
        List<Cart> carts=cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));
        productRepo.delete(productFromDB);
    }

    @Override
    public ProductDTO updateProductImage(Integer productId, MultipartFile file) throws IOException {
        Product productFromDB=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));

        String fileName=fileService.uploadImage(path,file);

        productFromDB.setImage(fileName);

        Product savedProduct=productRepo.save(productFromDB);

        return modelMapper.map(savedProduct, ProductDTO.class);

    }
}
