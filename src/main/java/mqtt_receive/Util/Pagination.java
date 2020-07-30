package mqtt_receive.Util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Pagination {

    //public static final int numPerPage = 5;
    private int totalPages; // 总页数
    private int page;   // 当前页码
    private List resultList;    // 结果集存放List

    public List Pagination(Integer countSQL, String sql,int currentPage, int numPerPage, JdbcTemplate jTemplate) {
        if (jTemplate == null) {
            throw new IllegalArgumentException(
                    "com.starhub.sms.util.Pagination.jTemplate is null,please initial it first. ");
        } else if (sql == null || sql.equals("")) {
            throw new IllegalArgumentException(
                    "com.starhub.sms.util.Pagination.sql is empty,please initial it first. ");
        }

        //String countSQL = getSQLCount(sql1);
        setPage(currentPage);
        setTotalPages(numPerPage,countSQL);
        int startIndex = (currentPage - 1) * numPerPage;    //数据读取起始index

        StringBuffer paginationSQL = new StringBuffer(" ");
        paginationSQL.append(sql);
        paginationSQL.append(" limit "+ startIndex+","+numPerPage);
        setResultList(jTemplate.queryForList(paginationSQL.toString()));
        return resultList;
    }

    public String getSQLCount(String sql){
        String sqlBak = sql.toLowerCase();
        String searchValue = " from ";
        String sqlCount = "select count(*) from "+ sql.substring(sqlBak.indexOf(searchValue)+searchValue.length(), sqlBak.length());
        return sqlCount;
    }

    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public List getResultList() {
        return resultList;
    }
    public void setResultList(List resultList) {
        this.resultList = resultList;
    }
    // 计算总页数
    public void setTotalPages(int numPerPage,int totalRows) {
        if (totalRows % numPerPage == 0) {
            this.totalPages = totalRows / numPerPage;
        } else {
            this.totalPages = (totalRows / numPerPage) + 1;
        }
    }

}
