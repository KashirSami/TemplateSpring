package com.template.template.service;

import com.template.template.model.Product;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

@Service
public class ExcelService {

    @Autowired
    private FirebaseStorageService storageService;

    public void processExcel(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Saltar cabecera
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row row = rows.next();
                Product product = new Product();

                product.setNombre(row.getCell(0).getStringCellValue());
                product.setDescripcion(row.getCell(1).getStringCellValue());
                product.setValor(row.getCell(2).getNumericCellValue());
                product.setCategoria(row.getCell(3).getStringCellValue());
                product.setStock((int) row.getCell(4).getNumericCellValue());

                storageService.saveProduct(product);
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}