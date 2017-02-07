<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Einsendebogen Portal</title>
        <script src="js/dropzone.js"></script>
        <link rel="stylesheet" href="css/mydropzone.css">
        <link rel="stylesheet" href="css/handsontable.css">
        <link rel="stylesheet" href="js/pikaday/pikaday.css">

        <script type="text/javascript" src="js/pikaday/pikaday.js"></script>
        <script type="text/javascript" src="js/moment/moment.js"></script>
        <script type="text/javascript" src="js/zeroclipboard/ZeroClipboard.js"></script>
        <script type="text/javascript" src="js/numbro/numbro.js"></script>
        <script type="text/javascript" src="js/numbro/languages.js"></script>
        <script type="text/javascript" src="js/handsontable.js"></script>

        <style>
            body { font-family:'lucida grande', tahoma, verdana, arial, sans-serif; font-size:11px; }
            h1 { font-size: 15px; }
            a { color: #548dc4; text-decoration: none; }
            a:hover { text-decoration: underline; }
            table.testgrid { border-collapse: collapse; border: 1px solid #CCB; width: 800px; }
            table.testgrid td, table.testgrid th { padding: 5px; border: 1px solid #E0E0E0; }
            table.testgrid th { background: #E5E5E5; text-align: left; }
            input.invalid { background: red; color: #FDFDFD; }
        </style>
    </head>
    <body>
    
        <script>	      	
	        // myDropzone is the configuration for the element that has an id attribute
	        // with the value my-dropzone (or myDropzone)
	        Dropzone.options.myDropzone = {
	        	createImageThumbnails: false,
	        	acceptedFiles: ".xls,.xlsx",
       			uploadMultiple: true,
	          init: function() {
	              this.on("thumbnail", function(file, dataUrl) {
	            	  //file.previewElement.classList.get('dz-image').last().find('img').attr({width: '100%', height: '100%'});
	              }),
	            	this.on("success", function(file, jsonText) {
	            	//file.previewElement.classList.get('dz-image').css({"width":"100%", "height":"auto"});
                    	//console.log(file);
	            		fillHOT(JSON.parse(jsonText));
	            });
	          }
	        };
	        
	        function fillHOT(json) {
            	var origdata = json.origdata;
            	var errors = json.errors;
            	console.log(errors);
            	console.log(origdata);
            	//hot.loadData(data.data);
            	
	            var showTable = true;
	  	      	var errs = errors.data;
	  	      	var errMsg = "";
	  	      	for (var i = 0; i < errs.length; i++) {
	  	        	var row = errs[i]["Zeile"];
	  	      		if (row == null) {
	  	      			var comment = errs[i]["Kommentar"];
	  	      			errMsg += "<br>" + comment;
	  	      			showTable = false;
	  	      		}
	  	      	}
		  	    if (!showTable) {
		  	    	  document.getElementById('errmsg').innerHTML = errMsg;
		  	      }
	  	        else {
	  	        	var colH = origdata.colHeaders;
	  	            var data = origdata.data;
	  	            var cols = origdata.columns;
	  	            var container = document.getElementById('hot');
	  	  	      	var hot = new Handsontable(container, {
	  	                data: data,
	  	                columns: cols,
	  	                colHeaders : colH,
	  	                stretchH: 'all',
	  	                autoWrapRow: true,
	  	                comments: true,
	  	                debug: true,
	  	                //minSpareRows: 1,
	  	                cells: function (row, col, prop) {
	  	                    var cellProperties = {};
	
	  	                    cellProperties.renderer = cellRenderer; 
	
	  	                    return cellProperties;
	  	                },
	  	                afterChange: function (change, source) {
	  	                    if (source === 'loadData') { // "alter", "edit", "populateFromArray", "loadData", "autofill", "paste".
	  	                        return; //don't save this change
	  	                    }
	
	  	                    if (change != null) {
	  	                        //console.log(change);
	  	                        /*
	  	                    	for (var i=0; i<change.length; i++) {
	  	                    		var c = change[0];
	  	                            var rowNumber = c[0];
	  	                            var columnname = c[1]; // prop
	  	                            var oldValue = c[2];
	  	                            var newValue = c[3];
	  	                    	}             
	  	                        */
	  	                        //console.log(source);
	  	                        
	  	                   // Sending and receiving data in JSON format using POST mothod
	  	                    	//
	  	                    	var data = JSON.stringify({data: hot.getSourceData()}); // [rowNumber]

	  	                    	xhr = new XMLHttpRequest();
	  	                    	xhr.open("POST", "result", true);
	  	                    	xhr.setRequestHeader("Content-type", "application/json");//"application/x-www-form-urlencoded" multipart/form-data
	  	                    	xhr.onreadystatechange = function () { 
	  	                    	    if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
	  	                    	      fillHOT(JSON.parse(xhr.responseText));
	  	                    	    }
	  	                    	}	  	                    	
  	                            console.log(data);
	  	                    	xhr.send(data);
	  	                    }
	  	                }
	  	  	      	})
	      	
	  			    var commentsPlugin = hot.getPlugin('comments');
	  	  	      	//var errs = errors.data;
	  	  	      	for (var i = 0; i < errs.length; i++) {
	  	  	      		// Status, Zeile, Spalte(n), Fehler-Nr, Kommentar
	  	  	      		var status = errs[i]["Status"];
	  	  	      		//console.log(errs[i]);
	  	  	        	var row = errs[i]["Zeile"] - 1;
	  	  	        	var cols = errs[i]["Spalte"];	
	  	  	        	if (row != null && cols != null) {
	  	  	  	        	var errnum = errs[i]["Fehler-Nr"];
	  	  	  	        	var comment = errs[i]["Kommentar"];
	  	  	  	        	cols += "";
	  	  	  	        	var colarr = cols.split(";");
	  	  	  	        	for (var j = 0; j < colarr.length; j++) {
	  	  	  	        		var col = colarr[j] - 1;
	  	  	  	        		//console.log(row + " - " + col);
	  	  	  	  	        	if (commentsPlugin.getCommentAtCell(row, col) == null) commentsPlugin.setCommentAtCell(row, col, comment);
	  	  	  	  	        	else commentsPlugin.setCommentAtCell(row, col, commentsPlugin.getCommentAtCell(row, col) + "<br>" + comment);
	  	  	  	  	      		if (!hot.getCellMeta(row, col).status || status > hot.getCellMeta(row, col).status) {
	  	  	  	  	  	      		hot.setCellMeta(row, col, "status", ""+status);
	  	  	  	  	      		}
	  	  	  	        	}
	  	  	        	}
	  	  	      	}
      			}
	        }
	        
	        function cellRenderer(instance, td, row, col, prop, value, cellProperties) {
	            Handsontable.renderers.TextRenderer.apply(this, arguments);
	            var meta = instance.getCellMeta(row, col);
	      		if (instance.getCellMeta(row, col).status) {
		            td.style.fontWeight = 'bold';
		            //td.style.color = 'red';
		            if (instance.getCellMeta(row, col).status == 1) td.style.background = 'yellow';
		            else td.style.background = 'red';
	      		}
	          }
	        
		</script>
    
    
        <section>
            <div id="dropzone">
                <form method="post" action="result" enctype="multipart/form-data" class="dropzone needsclick" id="my-dropzone">
            	<input type="text" name="workflowname" style="width: 400px;" value="testing/Alex_testing/Proben-Einsendung_Web6aaw" />

	            <div class="dz-message needsclick">
	                Wähle deinen Einsendebogen oder ziehe ihn hierauf<br />
	            </div>

                </form>
            </div>
        </section>
        		
		<div id="errmsg"></div>
		<div id="hot"></div>
    </body>
</html>