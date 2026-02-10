package com.sankalp.ImageToDB.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintRequest {
    private String title;
    private String description;
    private String location;
}
