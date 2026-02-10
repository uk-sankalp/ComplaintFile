package com.sankalp.ImageToDB.service;

import com.sankalp.ImageToDB.dto.ComplaintRequest;
import com.sankalp.ImageToDB.dto.ComplaintResponse;
import com.sankalp.ImageToDB.dto.UpdateStatus;
import com.sankalp.ImageToDB.entity.Complaint;
import com.sankalp.ImageToDB.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ComplaintService {

    private final Path uploadDir= Paths.get("complaint_file");

    @Autowired
    private ComplaintRepository complaintRepository;

    // Create Complaint
    public ComplaintResponse addComplaint(String title, String description, String location, MultipartFile image) throws IOException {
        Files.createDirectories(uploadDir);
        String imageName= UUID.randomUUID()+"_"+image.getOriginalFilename();
        Files.write(uploadDir.resolve(imageName),image.getBytes());

        Complaint complaint=new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setLocation(location);
        complaint.setImageName(imageName);
        complaint.setStatus("OPEN");
        complaint.setCreatedAt(LocalDate.now());
        Complaint saved=complaintRepository.save(complaint);
        return toResponse(saved);
    }

    //Read One at a time
    public ComplaintResponse getComplaint(Long id){
        Complaint complaint=complaintRepository.findById(id).orElseThrow(()-> new RuntimeException("No Complaint Found With This Id, Please Check Once Again"));
        return toResponse(complaint);
    }

    //Read All
    public List<ComplaintResponse> getAllComplaint(){
        List<Complaint> list= complaintRepository.findAll();
        List<ComplaintResponse> ansList=new ArrayList<>();
        for(Complaint c:list){
            ansList.add(toResponse(c));
        }
        return ansList;
    }

    public ComplaintResponse updateComplaint(Long id,ComplaintRequest request){
    Complaint complaint=complaintRepository.findById(id).orElseThrow(()-> new RuntimeException("No Complaint Found,Please Add One"));
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            complaint.setTitle(request.getTitle());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            complaint.setDescription(request.getDescription());
        }

        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            complaint.setLocation(request.getLocation());
        }
    complaintRepository.save(complaint);
    return toResponse(complaint);
    }

    public ComplaintResponse updateStatus(Long id,String status){
        Complaint complaint=complaintRepository.findById(id).orElseThrow(()->new RuntimeException("Complaint Not Found"));
        complaint.setStatus(status.toUpperCase());
        Complaint updated=complaintRepository.save(complaint);
        return toResponse(updated);
    }

    public void deleteComplaint(Long id) throws IOException {
        Complaint complaint=complaintRepository.findById(id).orElseThrow(()->new RuntimeException("Complaint Not Found"));
        Path imagePath=uploadDir.resolve(complaint.getImageName());
        Files.deleteIfExists(imagePath);
        complaintRepository.deleteById(id);
    }

    public Path imagePath(String imageName){
        return uploadDir.resolve(imageName);
    }

    public ComplaintResponse updateComplaintImage(Long id,MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Please provide an image");
        }

        Complaint complaint=complaintRepository.findById(id).orElseThrow(()->new RuntimeException("No Complaint Found"));
        if(complaint.getImageName()!=null){
            Path oldpath=uploadDir.resolve(complaint.getImageName());
            Files.deleteIfExists(oldpath);
        }
        String newImage=UUID.randomUUID()+"_"+image.getOriginalFilename();
        Files.createDirectories(uploadDir);
        Files.write(uploadDir.resolve(newImage),image.getBytes());
        complaint.setImageName(newImage);
        Complaint update=complaintRepository.save(complaint);
        return toResponse(update);
    }

    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws IOException {
    Path path=uploadDir.resolve(imageName);
    Resource resource=new UrlResource(path.toUri());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
    String contentType=Files.probeContentType(path);
    if(contentType==null){
        contentType = "application/octet-stream";
    }
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
    }

    // like toString
    private ComplaintResponse toResponse(Complaint c) {
        String imageUrl = "http://localhost:8080/api/complaints/images/" + c.getImageName();
        return new ComplaintResponse(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getLocation(),
                c.getStatus(),
                c.getCreatedAt(),
                imageUrl
        );
    }
}
