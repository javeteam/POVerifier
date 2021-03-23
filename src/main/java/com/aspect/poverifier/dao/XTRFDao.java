package com.aspect.poverifier.dao;

import com.aspect.poverifier.entity.Customer;
import com.aspect.poverifier.entity.ProjectNameDelimiter;
import com.aspect.poverifier.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class XTRFDao extends JdbcDaoSupport {
    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter sqlDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public XTRFDao(DataSource dataSource){
        this.setDataSource(dataSource);
        this.jdbcTemplate = this.getJdbcTemplate();
    }

    private final String tasksRequest = "SELECT t.task_id, CONCAT(u.first_name, ' ', u.last_name) AS manager_name, p.customer_project_number, p.id_number, tf.total_agreed FROM project p " +
            "LEFT JOIN task t ON p.project_id = t.project_id " +
            "LEFT JOIN task_finance tf ON t.task_id = tf.task_id " +
            "LEFT JOIN xtrf_user u ON u.xtrf_user_id =  p.project_manager_id " +
            "WHERE p.customer_id = ? AND p.status != 'CANCELLED' AND (p.deadline BETWEEN TO_TIMESTAMP(?,'YYYY-MM-DD') AND TO_TIMESTAMP(?,'YYYY-MM-DD'))";

    private final String customersRequest = "SELECT customer_id, name, name_normalized FROM customer " +
            "WHERE status = 'ACTIVE' and number_of_projects > 0 ORDER BY number_of_projects DESC";

    public List<Task> getXTRFTasks(LocalDate dateFrom, LocalDate dateTo, int customerId, ProjectNameDelimiter delimiter){
        try{
            return this.jdbcTemplate.query(tasksRequest, new TaskRowMapper(delimiter), customerId, dateFrom.format(sqlDateFormatter), dateTo.format(sqlDateFormatter));
        } catch (EmptyResultDataAccessException ignored){
            return new ArrayList<>();
        }
    }

    public List<Customer> getCustomers(){
        try{
           return this.jdbcTemplate.query(customersRequest, new CustomerRowMapper());
        } catch (EmptyResultDataAccessException ignored){
            return new ArrayList<>();
        }
    }

    private static class CustomerRowMapper implements RowMapper<Customer>{
        @Override
        public Customer mapRow(ResultSet rs, int i) throws SQLException {
            Customer customer = new Customer();
            customer.setId(rs.getLong("customer_id"));
            customer.setName(rs.getString("name"));

            return customer;
        }
    }

    private static class TaskRowMapper implements RowMapper<Task>{
        private final ProjectNameDelimiter delimiter;

        public TaskRowMapper(ProjectNameDelimiter delimiter){
            this.delimiter = delimiter;
        }

        @Override
        public Task mapRow(ResultSet rs, int i) throws SQLException {
            Task task = new Task();
            task.setId(rs.getLong("task_id"));
            task.setProjectIdNumber(rs.getString("id_number"));
            task.setProjectManager(rs.getString("manager_name"));
            task.setClientProjectNames(splitNames(rs.getString("customer_project_number"), delimiter));
            task.setTotalAgreed(new BigDecimal(rs.getString("total_agreed")));

            return task;
        }

        private Set<String> splitNames(String names, ProjectNameDelimiter delimiter){
            Set<String> result = new HashSet<>();
            switch (delimiter){
                case COMA:
                case SPACE:
                    if(names == null || names.trim().isEmpty()) return result;
                    String str = names.trim();

                    int index = str.indexOf(delimiter.getSymbol());
                    if(index == -1){
                        result.add(str);
                    } else if(index == 0 && str.length() > 1){
                        result.addAll(splitNames(str.substring(1), delimiter));
                    } else if(index + 1 < str.length()) {
                        result.add(str.substring(0, index));
                        result.addAll(splitNames(str.substring(index+1), delimiter));
                    }
                    return result;

                case BOTH:
                    Set<String> tmpResult;
                    tmpResult = splitNames(names, ProjectNameDelimiter.COMA);
                    for(String intermediateNames : tmpResult){
                        result.addAll(splitNames(intermediateNames, ProjectNameDelimiter.SPACE));
                    }
                    return result;

                default:
                    return new HashSet<>();
            }
        }
    }


}
