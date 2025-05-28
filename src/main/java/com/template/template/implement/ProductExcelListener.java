package com.template.template.implement;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.template.template.model.Product;
import com.template.template.service.FirebaseStorageService;
import com.template.template.model.ProductRow;

public class ProductExcelListener extends AnalysisEventListener<ProductRow> {

    private final FirebaseStorageService storageService;

    public ProductExcelListener(FirebaseStorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void invoke(ProductRow row, AnalysisContext analysisContext) {
        Product p = new Product();
        p.setId(row.getId());
        p.setName(row.getName());
        p.setDescription(row.getDescription());
        p.setPrice(row.getPrice());
        storageService.uploadProduct(p);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
