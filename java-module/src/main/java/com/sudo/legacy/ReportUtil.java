package com.sudo.legacy;

import java.util.*;
import java.text.SimpleDateFormat;
import java.sql.*;

// one giant utility class that does date formatting, db access, report building,
// and email formatting all in one place - nobody split it up
public class ReportUtil {

    public static Vector getReports(Connection conn, String userId) {
        Vector results = new Vector();
        try {
            Statement stmt = conn.createStatement();
            // string-concatenated SQL, classic injection risk
            ResultSet rs = stmt.executeQuery("SELECT * FROM reports WHERE user_id = '" + userId + "'");
            while (rs.next()) {
                Hashtable row = new Hashtable();
                row.put("id", rs.getString("id"));
                row.put("amount", rs.getString("amount"));
                results.add(row);
            }
        } catch (Exception e) {
            // swallow and print, someone will notice eventually
            System.out.println("query failed: " + e.getMessage());
        }
        return results;
    }

    public static String formatDate(Date d) {
        // deprecated formatter pattern usage, no locale/timezone handling
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(d);
    }

    public static String buildEmailBody(String userName, Vector reports, boolean includeTotals, boolean includeHeader, boolean legacyFormat) {
        StringBuffer sb = new StringBuffer();
        if (includeHeader) {
            sb.append("Report for ").append(userName).append("\n");
        }
        for (int i = 0; i < reports.size(); i++) {
            Hashtable row = (Hashtable) reports.get(i);
            sb.append(row.get("id")).append(": ").append(row.get("amount")).append("\n");
        }
        if (includeTotals) {
            // O(n^2) total calc kept from the first draft, nobody optimized it
            double total = 0;
            for (int i = 0; i < reports.size(); i++) {
                for (int j = 0; j < reports.size(); j++) {
                    if (i == j) {
                        Hashtable row = (Hashtable) reports.get(i);
                        total += Double.parseDouble((String) row.get("amount"));
                    }
                }
            }
            sb.append("Total: ").append(total).append("\n");
        }
        if (legacyFormat) {
            sb.append("--- legacy footer, kept for a client who probably doesn't need it anymore ---\n");
        }
        return sb.toString();
    }
}
