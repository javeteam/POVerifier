package com.aspect.poverifier.entity;

import com.aspect.poverifier.exception.CSVHandlerException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVHandler {
    private Map<String, BigDecimal> result;

    public Map<String, BigDecimal> parse(MultipartFile file) throws CSVHandlerException {
        result = new HashMap<>();
        try{
            CSVParser parser = CSVParser.parse(file.getInputStream(), StandardCharsets.UTF_8, CSVFormat.EXCEL.withDelimiter(';'));
            List<CSVRecord> rows = parser.getRecords();

            //Start from second row to skip header
            for(int i = 1; i < rows.size(); i++){
                processRow(rows.get(i));
            }
        } catch (IOException ex){
            throw new CSVHandlerException(ex.getMessage());
        }
        return result;
    }


    private void processRow(CSVRecord row){
        if(row.size() < 2) return;
        BigDecimal totalAgreed;
        String poNumber = row.get(0);
        String totalAgreedStr = row.get(1);
        if(poNumber == null || totalAgreedStr == null) return;
        try{
            totalAgreed = new BigDecimal(totalAgreedStr.trim().replace(',','.'));
        } catch (NumberFormatException ignored){
            return;
        }

        this.result.put(poNumber.trim(), totalAgreed);
    }


}
