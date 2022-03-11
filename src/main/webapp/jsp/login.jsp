<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.github.braully.web.jsf.AppMB" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Login page</title>
    <link rel="stylesheet" href="/pkg/bootstrap/dist/css/bootstrap.min.css" />
    <link rel="stylesheet" href="/pkg/font-awesome/css/font-awesome.min.css" type="text/css" />
</head>


<body>
<div class="container">
    <div class="row">
        <div class="col-lg-4 mx-md-auto">
            <div class="login-panel card">
                <div class="card-header">
                    <h3 class="card-title">Please Sign In</h3>
                </div>
                <div class="card-body">
                    <form role="form" method="post" action="/login">
                    <fieldset>
                            <div class="form-group">
                                <input class="form-control" placeholder="username" id="username" name="username"
                                       autofocus>
                            </div>
                            <div class="form-group">
                                <input class="form-control" placeholder="Password" name="password" type="password"
                                       value="">
                            </div>
                            <div class="form-check">
                                <label class="form-check-label">
                                    <input class="form-check-input" type="checkbox" name="remember" value="Remember Me">
                                    Remember Me</label>
                            </div>
                        
                            <c:if test="${not empty param.error}">

                            </c:if>
                            <c:if test="${not empty param.error}">
                                <label class="lbl" style="color: red; font-size: normal; font-weight: bold;">Usuário ou senha incorreto!</label>
                            </c:if>
                            <input type="submit" class="btn btn-lg btn-success btn-block">Login</input>
                    </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
    </body>
</html>
