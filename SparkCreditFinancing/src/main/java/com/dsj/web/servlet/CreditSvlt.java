package com.dsj.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dsj.web.entity.Result1;
import com.dsj.web.jdbc.JDBCHelper;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class CreditSvlt
 */
public class CreditSvlt extends HttpServlet {
    private static final long serialVersionUID = 1L;
    JDBCHelper jdbcHelper = JDBCHelper.getInstance();
    ResultSet rs = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreditSvlt() {
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
        Map<String, Object> r1map = getResult1();
        Map<String, Object> r2map = getResult2();
        Map<String, Object> r3map = getResult3();
        Map<String, Object> r4map = getResult4();
        Map<String, Object> r5map = getResult5();
        Map<String, Object> r6map = getResult6();
        Map<String, Object> r7map = getResult7();
        Map<String, Object> r8map = getResult8();
        Map<String, Object> r9map = getResult9();


        Map<String, Object> map = new HashMap<String, Object>();
        map.put("periodnums", r1map.get("periodnums"));
        map.put("periods", r1map.get("periods"));

        map.put("sexnums", r2map.get("sexnums"));
        map.put("sexs", r2map.get("sexs"));


        map.put("provincenums", r3map.get("provincenums"));
        map.put("provincenames", r3map.get("provincenames"));

        map.put("sumsalarys", r4map.get("sumsalarys"));
        map.put("salarylevels", r4map.get("salarylevels"));

        map.put("banknums", r5map.get("banknums"));
        map.put("cardnums", r5map.get("cardnums"));

        map.put("banknames", r6map.get("banknames"));
        map.put("usernums", r6map.get("usernums"));

        map.put("consumeproportions", r7map.get("consumeproportions"));
        map.put("consumenums", r7map.get("consumenums"));

        map.put("billlevels", r8map.get("billlevels"));
        map.put("billnums", r8map.get("billnums"));

        map.put("creditlevels", r9map.get("creditlevels"));
        map.put("creditnums", r9map.get("creditnums"));

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.write(JSONObject.fromObject(map).toString());
        pw.flush();
        pw.close();

    }

    public Map<String, Object> getResult9() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] creditlevels = new String[4];
        int[] creditnums = new int[4];
        String sql = "select amountlevel as creditlevel,num as creditnum from result9";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String creditlevel = rs.getString("creditlevel");
                creditlevels[i] = creditlevel;
                int creditnum = rs.getInt("creditnum");
                creditnums[i] = creditnum;
                ++i;
            }

            map.put("creditlevels", creditlevels);
            map.put("creditnums", creditnums);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Object> getResult8() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] billlevels = new String[6];
        int[] billnums = new int[6];
        String sql = "select amountlevel as billlevel,num as billnum from result8";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String billlevel = rs.getString("billlevel");
                billlevels[i] = billlevel;
                int billnum = rs.getInt("billnum");
                billnums[i] = billnum;
                ++i;
            }

            map.put("billlevels", billlevels);
            map.put("billnums", billnums);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Object> getResult7() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] consumeproportions = new String[5];
        int[] consumenums = new int[5];
        String sql = "select consumeproportion,num as consumenum from result7";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String consumeproportion = rs.getString("consumeproportion");
                consumeproportions[i] = consumeproportion;
                int consumenum = rs.getInt("consumenum");
                consumenums[i] = consumenum;
                ++i;
            }

            map.put("consumeproportions", consumeproportions);
            map.put("consumenums", consumenums);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Object> getResult6() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] banknames = new String[16];
        int[] usernums = new int[16];
        String sql = "select b.bankname,r.usernum from result6 r join bank b on r.bankid=b.bankid where r.bankid<=16";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String bankname = rs.getString("bankname");
                banknames[i] = bankname;
                int usernum = rs.getInt("usernum");
                usernums[i] = usernum;
                ++i;
            }

            map.put("banknames", banknames);
            map.put("usernums", usernums);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Object> getResult5() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] banknums = new String[5];
        int[] cardnums = new int[5];
        String sql = "select banknum,cardnum from result5";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String banknum = rs.getString("banknum");
                banknums[i] = banknum;
                int cardnum = rs.getInt("cardnum");
                cardnums[i] = cardnum;
                ++i;
            }

            map.put("banknums", banknums);
            map.put("cardnums", cardnums);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Object> getResult4() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] salarylevels = new String[6];
        int[] sumsalarys = new int[6];
        String sql = "select salarylevel,num as sumsalary from result4";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String salarylevel = rs.getString("salarylevel");
                salarylevels[i] = salarylevel;
                int sumsalary = rs.getInt("sumsalary");
                sumsalarys[i] = sumsalary;
                ++i;
            }

            map.put("sumsalarys", sumsalarys);
            map.put("salarylevels", salarylevels);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Object> getResult3() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] provincenames = new String[27];
        int[] provincenums = new int[27];
        String sql = "select p.name as provincename,r.num as provincenum from result3  r join province p on r.province=p.pid";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String provincename = rs.getString("provincename");
                provincenames[i] = provincename;
                int provincenum = rs.getInt("provincenum");
                provincenums[i] = provincenum;
                ++i;
            }
            map.put("provincenums", provincenums);
            map.put("provincenames", provincenames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Object> getResult2() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] sexs = new String[3];
        int[] sexnums = new int[3];
        String sql = "select sex,num from result2";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String sex = rs.getString("sex");
                if (sex.equals("0")) {
                    sex = "未知";
                } else if (sex.equals("1")) {
                    sex = "男性";
                } else {
                    sex = "女性";
                }
                sexs[i] = sex;
                int num = rs.getInt("num");
                sexnums[i] = num;
                ++i;
            }
            map.put("sexnums", sexnums);
            map.put("sexs", sexs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    public Map<String, Object> getResult1() {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] periods = new String[5];
        int[] periodnums = new int[5];
        String sql = "select period,num as periodnum from result1";
        try {
            ResultSet rs = getResultSet(sql);
            int i = 0;
            while (rs.next()) {
                String period = rs.getString("period");
                periods[i] = period;
                int periodnum = rs.getInt("periodnum");
                periodnums[i] = periodnum;
                ++i;
            }
            map.put("periodnums", periodnums);
            map.put("periods", periods);
            System.out.println(periods);
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
