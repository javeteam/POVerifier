package com.aspect.poverifier.entity;

import com.aspect.poverifier.exception.CSVHandlerException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVHandler {
    private Map<String, ClientPO> result;

    public Map<String, ClientPO> parse(MultipartFile file) throws CSVHandlerException {
        result = new HashMap<>();
        try{
            byte[] fileBytes = file.getBytes();
            if(fileBytes.length < 2) return result;

            Charset charset;
            // BOM is FE FF or FF FE
            if(fileBytes[0] == (byte) 0xFE && fileBytes[1] == (byte) 0xFF || fileBytes[0] == (byte) 0xFF && fileBytes[1] == (byte) 0xFE) charset = StandardCharsets.UTF_16;
            else charset = StandardCharsets.UTF_8;

            CSVParser parser = CSVParser.parse(new ByteArrayInputStream(fileBytes), charset, CSVFormat.EXCEL.withDelimiter(';'));
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
        ClientPO clientPO = new ClientPO();
        clientPO.setNumber(poNumber.trim());
        clientPO.setTotalAgreed(totalAgreed);
        this.result.put(clientPO.getNumber(), clientPO);
    }


}
