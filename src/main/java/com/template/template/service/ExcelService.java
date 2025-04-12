package com.template.template.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ExcelService {
    public void processExcel(MultipartFile file) throws IOException {
        //Workbook workbook = WorkbookFactory.create(file.getInputStream());
        //Sheet sheet = workbook.getSheetAt(0);
        // Lógica para leer celdas...
    }
}
