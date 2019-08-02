package com.dsj.web.servlet;

import com.dsj.web.jdbc.JDBCHelper;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet implementation class AppSvlt
 */
public class AppSvlt extends HttpServlet {
    private static final long serialVersionUID = 1L;
    JDBCHelper jdbcHelper = JDBCHelper.getInstance();
    ResultSet rs = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AppSvlt() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> r1map = getAppCounts();
        int userCount = getUserCounts();

        map.put("appnames", r1map.get("appnames"));
        map.put("counts", r1map.get("counts"));

        map.put("userCount", userCount);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.write(JSONObject.fromObject(map).toString());
        pw.flush();
        pw.close();
    }

    /**
     * 统计app页面访问排名
     *
     * @return
     */
    public Map<String, Object> getAppCounts() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] appnames = new String[5];
        int[] counts = new int[5];
        String sql = "select appname,count from appCounts order by count desc limit 5";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String appname = rs.getString("appname");
                appnames[i] = appname;
                int count = rs.getInt("count");
                counts[i] = count;
                ++i;
            }
            map.put("appnames", appnames);
            map.put("counts", counts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 统计用户uv
     *
     * @return
     */
    public int getUserCounts() {
        String sql = "select count(1)   from userCounts";
        try {
            ResultSet rs = getResultSet(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 返回ResultSet对象
     */

    public ResultSet getResultSet(String sql) throws Exception {
        jdbcHelper.executeQuery(sql, null, new JDBCHelper.QueryCallback() {

            @Override
            public void process(ResultSet rs1) throws Exception {
                rs = rs1;
            }
        });
        return rs;
    }

}
