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
	<center>
	    <form method="post" action="result"
	        enctype="multipart/form-data">
	        Select template to validate: <input type="file" name="file" size="60" /><br />
	        <br /> <input type="submit" value="Validate" />
	    </form>
	</center>
    <div id="footer"><b>NOTE: your file upload will NOT be saved on this server!</b><BR>Uploaded file will be deleted directly after validation! We do <b>NOT</b> keep any probably personal information!</div>
</body>
</html>