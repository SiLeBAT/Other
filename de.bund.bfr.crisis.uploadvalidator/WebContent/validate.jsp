<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Template Validator</title>
<link href="footer.css" type="text/css" rel="stylesheet"></head>
</head>
<body>
	    <form method="post" action="result"
	        enctype="multipart/form-data">
	        Select template to validate<BR><BR>
	        <input type="file" name="file" size="60" />
	        <br />
	        <br />
	        <BR>
	        <input type="submit" value="Validate" />
	    </form>
    <div id="footer">Your trust is important to us!<BR>Uploaded files will be deleted directly after validation. We do not keep any personal information.</div>
</body>
</html>