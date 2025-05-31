package com.template.template.implement;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.template.template.model.Product;
import com.template.template.model.ProductRow;
import com.template.template.service.ExcelService;
import com.template.template.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ExcelServiceImpl implements ExcelService {

    
    private final FirebaseStorageService storageService;

    @Autowired
    public ExcelServiceImpl(FirebaseStorageService storageService) {
        this.storageService = storageService;
    }


    @Override
    public void processExcel(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            EasyExcel.read(is, ProductRow.class, new ReadListener<ProductRow>() {
                @Override
                public void invoke(ProductRow row, AnalysisContext analysisContext) {
                    Product p = new Product();
                    p.setId(row.getId());
                    p.setNombre(row.getNombre());
                    p.setDescripcion(row.getDescripcion());
                    p.setCategoria(row.getCategoria());
                    p.setPrecio(row.getPrecio());
                    p.setStock(row.getStock());
                    try {
                        storageService.saveProduct(p);
                    } catch (Exception e) {
                        throw new RuntimeException("Error guardando producto " + p.getNombre(), e);
                    }
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext ctx) { }
            }).sheet().doRead();
        }
    }
}

