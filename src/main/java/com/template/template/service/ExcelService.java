package com.template.template.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface ExcelService {


    void processExcel(MultipartFile file) throws IOException;
}
