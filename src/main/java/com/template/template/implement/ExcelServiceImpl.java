package com.template.template.implement;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.template.template.model.Product;
import com.template.template.service.ExcelService;
import com.template.template.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private FirebaseStorageService storageService;

    @Override
    public void processExcel(MultipartFile fileName) throws IOException {
        try (InputStream is = fileName.getInputStream()) {
            EasyExcel.read(is, Product.class, new ReadListener<Product>() {
                @Override
                public void invoke(Product product, AnalysisContext analysisContext) {
                    Product p = new Product();
                    p.setId(product.getId());
                    p.setName(product.getName());
                    p.setDescription(product.getDescription());
                    p.setPrice(product.getPrice());

                    storageService.saveProduct(p);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).sheet().doRead();

        }

    }
}
