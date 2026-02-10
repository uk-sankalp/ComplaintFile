package com.sankalp.ImageToDB.controller;
import com.sankalp.ImageToDB.dto.ProductResponse;
import com.sankalp.ImageToDB.entity.Product;
import com.sankalp.ImageToDB.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> addProduct(
           @RequestParam String name,
           @RequestParam double price,
           @RequestParam MultipartFile image
    ) throws IOException {

        // 1. Save image to disk
        Path uploadDir= Paths.get("uploads");
        Files.createDirectories(uploadDir);
        String imageName= UUID.randomUUID()+"_"+image.getOriginalFilename();
        Files.write(uploadDir.resolve(imageName),image.getBytes());

        // 2. Save product to DB
        Product product=new Product();
        product.setName(name);
        product.setPrice(price);
        product.setImageName(imageName);
        Product saved=productRepository.save(product);

        // 3. Build response
        String imageUrl="http://localhost:8080/api/products/images/"+imageName;
        ProductResponse productResponse=new ProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getPrice(),
                imageUrl
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id){
    Product product=productRepository.findById(id).orElseThrow(()-> new RuntimeException("No Product Found"));
    String imageUrl="http://localhost:8080/api/products/images/"+product.getImageName();
    ProductResponse pr=new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            imageUrl
    );
    return ResponseEntity.ok().body(pr);
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws Exception {
        Path path = Paths.get("uploads").resolve(imageName);
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream"; // fallback
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
