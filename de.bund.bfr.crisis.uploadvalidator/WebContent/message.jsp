<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Validation Result</title>
<link href="footer.css" type="text/css" rel="stylesheet"></head>


<body>

    <h2>Validation Result</h2>
    ${requestScope.message}

<c:set var="b" value="${requestScope.message}" scope="page"/>
    <iframe id="FileFrame" src="about:blank" width="100%" height="75%"></iframe>
<script type="text/javascript">
var doc = document.getElementById('FileFrame').contentWindow.document;
doc.open();
var aquaStr = '<html><head><title></title></head><body>hallo</body></html>';
doc.write(aquaStr);
doc.close();
</script>

    <div id="footer">Your trust is important to us!<BR>Uploaded files will be deleted directly after validation.<BR>We do not keep any personal information.</div>
</body>
</html>
