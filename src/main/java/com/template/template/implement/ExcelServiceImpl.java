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
import java.util.concurrent.ExecutionException;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private FirebaseStorageService storageService;

    @Override
    public void processExcel(MultipartFile fileName) throws IOException {
        try (InputStream is = fileName.getInputStream()) {
            EasyExcel.read(is, Product.class, new ReadListener<ProductRow>() {
                @Override
                public void invoke(ProductRow product, AnalysisContext analysisContext) {
                    Product p = new Product();
                    p.setId(product.getId());
                    p.setName(product.getName());
                    p.setDescription(product.getDescription());
                    p.setPrice(product.getPrice());
                    try {
                        storageService.saveProduct(p);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).sheet().doRead();

        }

    }
}
