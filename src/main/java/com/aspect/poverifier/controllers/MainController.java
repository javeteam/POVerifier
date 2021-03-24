package com.aspect.poverifier.controllers;

import com.aspect.poverifier.entity.CSVHandler;
import com.aspect.poverifier.entity.POComparator;
import com.aspect.poverifier.entity.Task;
import com.aspect.poverifier.exception.AppException;
import com.aspect.poverifier.exception.BadRequestException;
import com.aspect.poverifier.exception.CSVHandlerException;
import com.aspect.poverifier.service.XTRFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    private final XTRFService xtrfService;

    @Autowired
    MainController(XTRFService xtrfService){
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
            Map<String, BigDecimal> csvPOMap = csvHandler.parse(file);
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
            List<Task> xtrfTasks = xtrfService.getXTRFTasks(dateFrom, dateTo, Integer.parseInt(customerId), delimiter, uninvoicedOnly);
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
        Map<String, BigDecimal> csvPOMap = (Map<String, BigDecimal>) request.getSession().getAttribute("csvPOMap");

        POComparator comparator = new POComparator();
        comparator.compare(tasks, csvPOMap);

        model.addAttribute("exclusiveClientPONumbers", comparator.getExclusiveClientPONumbers());
        model.addAttribute("exclusiveXtrfPONumbers", comparator.getExclusiveXtrfPONumbers());
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

}
