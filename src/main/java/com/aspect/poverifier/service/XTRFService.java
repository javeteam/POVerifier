package com.aspect.poverifier.service;

import com.aspect.poverifier.dao.XTRFDao;
import com.aspect.poverifier.entity.Customer;
import com.aspect.poverifier.entity.ProjectNameDelimiter;
import com.aspect.poverifier.entity.Task;
import com.aspect.poverifier.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class XTRFService {
    private final XTRFDao xtrfDao;
    private final Logger logger;

    public XTRFService(XTRFDao xtrfDao) {
        this.xtrfDao = xtrfDao;
        this.logger = LoggerFactory.getLogger(XTRFService.class);
    }


    public List<Task> getXTRFTasks(String dateFrom, String dateTo, int customerId, String delimiter) throws BadRequestException {
        try{
            return this.xtrfDao.getXTRFTasks(LocalDate.parse(dateFrom), LocalDate.parse(dateTo), customerId, ProjectNameDelimiter.valueOf(delimiter));
        } catch (DateTimeParseException | IllegalArgumentException ex) {
            logger.error("Error while parsing input values" + ex);
            throw new BadRequestException("Provided values are invalid " + ex.getMessage());
        }
    }

    public List<Customer> getCustomers(){
        return this.xtrfDao.getCustomers();
    }


}
