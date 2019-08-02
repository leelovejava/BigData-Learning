package com.dsj.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dsj.web.jdbc.JDBCHelper;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class NewsSvlt
 */
public class NewsSvlt extends HttpServlet {
    private static final long serialVersionUID = 1L;
    JDBCHelper jdbcHelper = JDBCHelper.getInstance();
    ResultSet rs = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewsSvlt() {
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
        // 总新闻话题数
        int newsCount = getNewsCount();
        Map<String, Object> newsRank = getNewsRank();
        Map<String, Object> periodRank = getPeriodRank();
        Map<String, Object> map = new HashMap<String, Object>(5);
        // 新闻浏览量排行
        map.put("name", newsRank.get("name"));
        map.put("newscount", newsRank.get("count"));

        // 新闻时段浏览量排行
        map.put("logtime", periodRank.get("logtime"));
        map.put("periodcount", periodRank.get("count"));

        // 新闻话题总量
        map.put("newssum", newsCount);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.write(JSONObject.fromObject(map).toString());
        pw.flush();
        pw.close();
    }


    /**
     * 新闻曝光总量
     */
    public int getNewsCount() {
        String sql = "select count(1) from newscount";
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
     * 查询每条新闻浏览量排行榜
     */
    public Map<String, Object> getNewsRank() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] names = new String[10];
        String[] counts = new String[10];
        String sql = "select name ,count from newscount order by count desc limit 10";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String name = rs.getString("name");
                String count = rs.getString("count");
                names[i] = name;
                counts[i] = count;
                ++i;
            }
            map.put("name", names);
            map.put("count", counts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 查询时段新闻浏览量排行榜
     *
     * @return
     */
    public Map<String, Object> getPeriodRank() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] logtimes = new String[10];
        String[] counts = new String[10];
        String sql = "select logtime,count from periodcount order by count desc limit 10";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String logtime = rs.getString("logtime");
                String count = rs.getString("count");
                logtimes[i] = logtime;
                counts[i] = count;
                ++i;
            }
            map.put("logtime", logtimes);
            map.put("count", counts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
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
