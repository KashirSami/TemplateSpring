package com.template.template.controller;

import com.template.template.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/excel")
public class ExcelController {
    @Autowired
    private ExcelService excelService;

    @PostMapping("/uploadexcel")
    public String uploadExcel(@RequestParam("file")MultipartFile file) throws IOException {
        excelService.processExcel(file);
        return "redirect:/excel/success";
    }
}
