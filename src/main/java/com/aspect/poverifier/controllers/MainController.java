package com.aspect.poverifier.controllers;

import com.aspect.poverifier.entity.*;
import com.aspect.poverifier.exception.AppException;
import com.aspect.poverifier.service.XTRFService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Controller
public class MainController {
    private final String auth_token;
    private final XTRFService xtrfService;

    @Autowired
    MainController(XTRFService xtrfService, @Value("${app.auth_token}") String auth_token){
        this.auth_token = auth_token;
        this.xtrfService = xtrfService;
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String getLoginPage(){
        return "login";
    }

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String getMainPage(Model model){
        model.addAttribute("customers", this.xtrfService.getCustomers());
        return "mainPage";
    }

    @RequestMapping(value = {"/addCSV"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String addCSVFile(@RequestParam("csvFile") MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        try{
            CSVHandler csvHandler = new CSVHandler();
            Map<String, ClientPO> csvPOMap = csvHandler.parse(file);
            request.getSession().setAttribute("csvPOMap", csvPOMap);
            return "{\"status\":\"Success\", \"rowsCount\":\"" + csvPOMap.size() + " \"}";
        } catch (AppException ex){
            response.setStatus(422);
            return "{\"error\":\"Error\", \"message\":\"" + ex.getMessage() + "\"}";
        }
    }

    @RequestMapping(value = {"/addXTRFProperties"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String addXtrfProperties(HttpServletRequest request, HttpServletResponse response, String dateFrom, String dateTo, String delimiter, String customerId, Boolean uninvoicedOnly){
        try{
            List<Task> xtrfTasks = xtrfService.getTasks(dateFrom, dateTo, Integer.parseInt(customerId), delimiter, uninvoicedOnly);
            request.getSession().setAttribute("xtrfTasks", xtrfTasks);
            return "{\"status\":\"Success\", \"rowsCount\":\"" + xtrfTasks.size() + " \"}";
        } catch (AppException ex){
            response.setStatus(400);
            return "{\"error\":\"Error\", \"message\":\"" + ex.getMessage() + "\"}";
        }
    }

    @RequestMapping(value = {"/compare"}, method = RequestMethod.POST)
    public String compare(HttpServletRequest request, Model model){
        List<Task> tasks = (List<Task>) request.getSession().getAttribute("xtrfTasks");
        Map<String, ClientPO> csvPOMap = (Map<String, ClientPO>) request.getSession().getAttribute("csvPOMap");

        POComparator comparator = new POComparator();
        comparator.compare(tasks, csvPOMap);

        model.addAttribute("exclusiveClientPOs", comparator.getExclusiveClientPOs());
        model.addAttribute("exclusiveXtrfPOs", comparator.getExclusiveXtrfPOs());
        model.addAttribute("POsWithProblems", comparator.getPOsWithProblems());

        return "comparingResults";
    }

    @RequestMapping(value = {"/getCSVTemplate"}, method = RequestMethod.GET)
    public void getAccountantInvoice(HttpServletResponse response){
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment;filename=template.csv");

        try (OutputStream out = response.getOutputStream()) {
            out.write("po_number;total_amount\n".getBytes());
            response.flushBuffer();
        } catch (IOException ex){
            ex.getMessage();
        }
    }

    @RequestMapping(value = {"/api/CSoftDataProcess/{auth_token}/"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String processCSoftData(@RequestBody List<ClientPO> clientPOList, @PathVariable String auth_token, HttpServletResponse response) throws JsonProcessingException {
        if(!this.auth_token.equals(auth_token)){
            response.setStatus(401);
            return "{\"status\":\"Error\", \"message\": \"" + (auth_token == null ? "Authentication token required" : "Token is invalid") + "\"}";
        }
        POComparator comparator = new POComparator();
        Map<String, ClientPO> itemsToProceed = new HashMap<>();
        List<Task> xtrfTasks = this.xtrfService.getUninvoicedTasks( 1636, ProjectNameDelimiter.COMA);

        for(ClientPO po : clientPOList){
            switch (po.getStatus().toLowerCase()){
                case "to accept":
                case "to invoice":
                case "accepted":
                    itemsToProceed.put(po.getNumber(), po);
                    break;
            }
        }

        comparator.compare(xtrfTasks, itemsToProceed);
        List<ClientPO> exclusiveXtrfPOs = comparator.getExclusiveXtrfPOs();
        List<ClientPO> exclusiveClientPOs = comparator.getExclusiveClientPOs();

        final ObjectMapper mapper = new ObjectMapper();

        return "{\"status\":\"Success\", " +
                "\"allItems\":" + mapper.writeValueAsString(new ArrayList<>(itemsToProceed.values())) + ", " +
                "\"exclusiveClientPOs\":" + mapper.writeValueAsString(exclusiveClientPOs) + ", " +
                "\"exclusiveXtrfPOs\":" + mapper.writeValueAsString(exclusiveXtrfPOs) + "}";
    }


}
