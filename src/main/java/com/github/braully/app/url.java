package com.github.braully.app;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author braully
 */
public class url {

    public static Map<String, String> redirect = new TreeMap<>();

    static {
        redirect.put("/", "/jsf/index.xhtml");
        redirect.put("/home", "/jsf/index.xhtml");
        redirect.put("/index", "/jsf/index.xhtml");
    }

    public static Map<String, String> rewrite_forward = new TreeMap<>();

    static {
        rewrite_forward.put("/enter", "/html/login.html");
        rewrite_forward.put("/index", "/jsf/index.xhtml");
        //        rewrite_forward.put("/enter", "/jsp/login.jsp");
        //        rewrite_forward.put("/enter", "/web/jsp/login.jsp"); 
        /*
        (1, 10, 'Colaborator', 'colaborator', '/administrative/colaborator', 'ico-colab'),
        (1, 11, 'Payroll', 'payroll', '/administrative/payroll', 'ico-payroll'),
        (1, 12, 'Task', 'task', '/administrative/task', 'ico-task'),
         */
        rewrite_forward.put("/administrative/dashboard/", "/jsf/app/dashboard.xhtml");
        rewrite_forward.put("/administrative/colaborator/", "/jsf/app/colaborator.xhtml");
        rewrite_forward.put("/administrative/payroll/", "/jsf/app/em-construcao.xhtml");

        rewrite_forward.put("/administrative/task/", "/autogen/task.xhtml");
        rewrite_forward.put("/administrative/task/{op}/{id}", "/autogen/task/{op}.xhtml");

        rewrite_forward.put("/administrative/partner/", "/jsf/app/partners.xhtml");
        rewrite_forward.put("/administrative/partner/{op}/{id}", "/autogen/partner/{op}.xhtml");

        /*
        (2, 20, 'Cashier', 'cashier', '/financial/cashier', 'ico-cashier'),
        (2, 21, 'Receivable', 'receivable', '/financial/receivable', 'ico-receivable'),
        (2, 22, 'Payable', 'payable', '/financial/payable', 'ico-payable'),
        (2, 23, 'Stock', 'stock', '/financial/stock', 'ico-stock'),
        (2, 24, 'Sale', 'sale', '/financial/sale', 'ico-sale'),
         */

        rewrite_forward.put("/financial/cashier", "/jsf/app/cashier.xhtml");
        rewrite_forward.put("/financial/receivable", "/jsf/app/receivable.xhtml");
        rewrite_forward.put("/financial/payable", "/jsf/app/payable.xhtml");
        rewrite_forward.put("/financial/stock", "/jsf/app/stock.xhtml");
        rewrite_forward.put("/financial/sale", "/jsf/app/sale.xhtml");

        /*         
        (3, 30, 'Report', 'report', '/system/report', 'ico-report'),
        (3, 31, 'Settings', 'settings', '/system/settings', 'ico-settings'),
        (3, 32, 'Import', 'import', '/system/import', 'ico-import');
         */
        rewrite_forward.put("/system/report", "/jsf/app/report.xhtml");
        rewrite_forward.put("/system/settings", "/jsf/app/settings.xhtml");
        rewrite_forward.put("/system/import", "/mb/import");

        rewrite_forward.put("/entity-scratch", "/jsf/entity-crud-scratch.xhtml");
    }
}
