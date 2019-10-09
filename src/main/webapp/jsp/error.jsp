<%@page isErrorPage="true" import="java.io.*" contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import = "java.util.Map" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />

        <link href="/pkg/font-awesome/css/font-awesome.css" rel="stylesheet" />
        <link href="/tmp/css/ionicons.css" rel="stylesheet" />
        <link href="/pkg/perfect-scrollbar/css/perfect-scrollbar.min.css" rel="stylesheet" />

        <link rel="stylesheet" href="/tmp/css/slim.css" />

        <title>Error</title>
    </head>
    <body>
        <p>Error</p>

        <div class="page-error-wrapper">
            <div>
                <h1 class="error-title">500</h1>
                <h5 class="tx-sm-24 tx-normal">Ocorreu um erro.</h5>
                <p class="mg-b-50"> Mensagem: <%=exception%>

        StackTrace:
        <%

        %>
        <br />
        <%
                Map<String, String[]> parameters = request.getParameterMap();
                for(String parameter : parameters.keySet()) {
                    if(parameter.toLowerCase().startsWith("question")) {
                        String[] values = parameters.get(parameter);
                        //your code here
                    }
                }

        %>


</p>
                <p class="mg-b-50"><a href="/index" class="btn btn-error">Voltar para pagina</a></p>
                <p class="error-footer"></p>
            </div>

        </div><!-- page-error-wrapper -->

        

       

        <script src="/pkg/jquery/dist/jquery.min.js"></script>
        <script src="/pkg/popper.js/dist/umd/popper.min.js"></script>
        <script src="/pkg/bootstrap/dist/js/bootstrap.js"></script>
        <script src="/tmp/js/slim.js"></script>
    </body>
</html>
