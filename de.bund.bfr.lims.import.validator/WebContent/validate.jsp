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
        <link rel="stylesheet" href="css/bootstrap.min.css">
        <link rel="stylesheet" href="css/bootstrap-theme.min.css">

        <script type="text/javascript" src="js/pikaday/pikaday.js"></script>
        <script type="text/javascript" src="js/moment/moment.js"></script>
        <script type="text/javascript" src="js/zeroclipboard/ZeroClipboard.js"></script>
        <script type="text/javascript" src="js/numbro/numbro.js"></script>
        <script type="text/javascript" src="js/numbro/languages.js"></script>
        <script type="text/javascript" src="js/handsontable.js"></script>

        <script type="text/javascript" src="js/jquery-3.1.1.min.js"></script>
        <script type="text/javascript" src="js/bootstrap.min.js"></script>

       <style>
            body { font-family:'lucida grande', tahoma, verdana, arial, sans-serif; font-size:11px; }
            h1 { font-size: 15px; }
            a { color: #548dc4; text-decoration: none; }
            a:hover { text-decoration: underline; }
            .handsontable th {white-space: normal!important;}
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
                    	//console.log(jsonText);
	            		fillHOT(JSON.parse(jsonText));
	            });
	            this.on("addedfile", function(file) {
	            	console.log("addedfile: " + (""+(new Date().getTime()/1000)).substring(6));
	            });
	          }
	        };
	        
	        function fillHOT(json) {
	        	console.log("fhot_start: " + (""+(new Date().getTime()/1000)).substring(6));
            	var origdata = json.origdata;
            	var errors = json.errors;
            	//console.log(errors);
            	//console.log(origdata);
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
	  	        	var selected;
	  	        	if (hot) {
	  	        		selected = hot.getSelected();
	  	        		hot.destroy();
	  	        	}
	  	        	var colH = origdata.colHeaders;
	  	            var data = origdata.data;
	  	            var cols = origdata.columns;
                    $(td).tooltip('destroy');
	  	  	      	hot = new Handsontable(container, {
	  	                data: data,
	  	                columns: cols,
	  	                colHeaders : colH,
	  	                stretchH: 'all',
	  	                colWidths: [40],
	  	                autoWrapRow: true,
	  	                comments: true,
	  	                debug: true,
	  	              	manualColumnResize: true,
	  	            	manualRowResize: true,
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
	  	                    	var data = JSON.stringify({data: hot.getSourceData()}); // [rowNumber]

	  	                    	xhr = new XMLHttpRequest();
	  	                    	xhr.open("POST", "result", true);
	  	                    	xhr.setRequestHeader("Content-type", "application/json");//"application/x-www-form-urlencoded" multipart/form-data
	  	                    	xhr.onreadystatechange = function () { 
	  	                    	    if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
	  	                    	      fillHOT(JSON.parse(xhr.responseText));
	  	                    	    }
	  	                    	}	  	                    	
  	                            //console.log(data);
	  	                    	xhr.send(data);
	  	                    }
	  	                }
	  	  	      	});
	  	  	      	if (selected) {
	  	  	      		//console.log(selected);
	  	  	      		hot.selectCell(selected[0], selected[1]);
	  	  	      	}
	      	
		        	console.log("fhot_comment: " + (""+(new Date().getTime()/1000)).substring(6));
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
	  	  	  	  	      		if (!hot.getCellMeta(row, col).status || status > hot.getCellMeta(row, col).status) {
	  	  	  	  	  	      		hot.setCellMeta(row, col, "status", ""+status);
	  	  	  	  	      		}

	  	  	  	  	    		if (!hot.getCellMeta(row,col).errorMessage) hot.getCellMeta(row,col).errorMessage = "-> " + comment;
	  	  	  	  	    		else hot.getCellMeta(row,col).errorMessage += "<br>-> " + comment;
/*
	  			  			    var commentsPlugin = hot.getPlugin('comments');
 		  	  	        		var gcc = commentsPlugin.getCommentAtCell(row, col);
  		  	  	  	        	if (!gcc) commentsPlugin.setCommentAtCell(row, col, comment);
  		  	  	  	        	else commentsPlugin.setCommentAtCell(row, col, gcc + "<br>" + comment);
	  		  	  	  	        	*/
	  	  	  	        	}
	  	  	        	}
	  	  	      	}
	  	  	      	hot.render();
		        	console.log("fhot_end: " + (""+(new Date().getTime()/1000)).substring(6));
      			}
	        }
	        
	        function cellRenderer(instance, td, row, col, prop, value, cellProperties) {
	            Handsontable.renderers.TextRenderer.apply(this, arguments);
	            
	            var meta = instance.getCellMeta(row, col);

	            if (meta.errorMessage) {
	            	$(td).tooltip({
	                    trigger: 'hover active',
	                    title: '<p align="left">' + meta.errorMessage + '</p>',
	                    placement: 'auto',
	                    container: 'body',
	                    html: true,
	                    template: '<div class="tooltip" role="tooltip"><div class="tooltip-arrow"></div><div class="tooltip-inner"></div></div>'
	                  });
	                }
	            else {
	                	$(td).tooltip('destroy');
	                }
	            	            
	      		if (meta.status) {
	      			//console.log(meta.status);
		            td.style.fontWeight = 'bold';
		            //td.style.color = 'red';
		            if (meta.status == 1) td.style.background = '#fffacd'; //yellow
		            else td.style.background = '#ffc1c1'; // red
	      		}
	            
	          }
	        
		</script>
    
    
        <section>
            <div id="dropzone">
                <form method="post" action="result" enctype="multipart/form-data" class="dropzone needsclick" id="my-dropzone">
            	<input type="text" name="workflowname" style="width: 400px;" value="testing/Alex_testing/Proben-Einsendung_Web7baw" />

	            <div class="dz-message needsclick">
	                Wähle deinen Einsendebogen oder ziehe ihn hierauf<br />
	            </div>

                </form>
            </div>
        </section>
        		
		<div id="errmsg"></div>
		<div id="hot"></div>
		
		<script>
	        var container = document.getElementById('hot');
	        var hot;
		</script>
    </body>
</html>