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
        redirect.put("/", "/index");
        redirect.put("/home", "/index");
        redirect.put("/index", "/autogen/entityDummy.xhtml");
    }

    public static Map<String, String> rewrite_forward = new TreeMap<>();

    static {
        rewrite_forward.put("/enter", "/jsp/login.jsp");
        rewrite_forward.put("/index", "/jsf/index.xhtml");

        /*
            (1, 10, 'Colaborator', 'colaborator', '/administrative/colaborator', 'ico-colab'),
            (1, 11, 'Payroll', 'payroll', '/administrative/payroll', 'ico-payroll'),
            (1, 12, 'Task', 'task', '/administrative/task', 'ico-task'),
         */
        rewrite_forward.put("/administrative/colaborator", "/autogen/colaborator.xhtml");
        rewrite_forward.put("/administrative/payroll", "/jsf/app/payroll.html");
        rewrite_forward.put("/administrative/tarefa", "/autogen/task.xhtml");
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
