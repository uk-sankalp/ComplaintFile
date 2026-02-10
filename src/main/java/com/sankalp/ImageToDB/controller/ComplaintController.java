package com.sankalp.ImageToDB.controller;

import com.sankalp.ImageToDB.dto.ComplaintRequest;
import com.sankalp.ImageToDB.dto.ComplaintResponse;
import com.sankalp.ImageToDB.dto.UpdateStatus;
import com.sankalp.ImageToDB.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {
    @Autowired
    private ComplaintService complaintService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComplaintResponse> addComplaint(@RequestParam String title,@RequestParam String description,@RequestParam String location,@RequestParam MultipartFile image) throws IOException {
    ComplaintResponse response=complaintService.addComplaint(title,description,location,image);
    return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponse> getComplaint(@PathVariable Long id){
        ComplaintResponse response=complaintService.getComplaint(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> getAllComplaint(){
        return ResponseEntity.ok(complaintService.getAllComplaint());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ComplaintResponse> updateComplaint(@PathVariable Long id,@RequestBody ComplaintRequest complaintRequest){
    return ResponseEntity.ok(complaintService.updateComplaint(id,complaintRequest));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ComplaintResponse> updateStatus(@PathVariable Long id,@RequestBody UpdateStatus status){
        return ResponseEntity.ok(complaintService.updateStatus(id,status.getStatus()));
    }

    @PatchMapping("/{id}/updateImage")
    public ResponseEntity<ComplaintResponse> updateImage(@PathVariable Long id,@RequestParam MultipartFile image) throws IOException {
     return ResponseEntity.ok(complaintService.updateComplaintImage(id,image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComplaint(@PathVariable Long id) throws IOException {
        complaintService.deleteComplaint(id);
        return ResponseEntity.ok("Complaint Deleted Successfully");
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws IOException {
        return complaintService.getImage(imageName);
    }
}
