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
        <script>

	      	window.onload = function() {
    	    	/*
                var
                data = [
                  ['', 'Kia', 'Nissan', 'Toyota', 'Honda'],
                  ['2008', 10, 11, 12, 13],
                  ['2009', 20, 11, 14, 13],
                  ['2009', 30, 15, 12, 13]
                ],
                container = document.getElementById('hot')
                ;
  	      	var hot = new Handsontable(container, {
                data: data,
                startRows: 5,
                startCols: 5,
                colHeaders: true,
                comments: true,
                minSpareRows: 1,
                beforeChange: function (changes, source) {
                	console.log(changes.length);
                	console.log(changes[0]);
                	console.log(source);
      	      		var json = JSON.stringify({data: hot.getData()});
      	      		console.log(json);

      	      		var commentsPlugin = this.getPlugin('comments');
      	      		commentsPlugin.setCommentAtCell(1, 1, "WASS");
                }
  	      	  //data: data(),
    	      	//  minSpareCols: 1,
    	      	  //minSpareRows: 1,
    	      	  //rowHeaders: true,
    	      	  //colHeaders: true,
    	      	  //contextMenu: true
    	      	});
  	      	
  	      		var json = JSON.stringify({data: hot.getData()});
  	      		console.log(json);
  	      		  	      	*/	
            } 
        </script>
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
	            	console.log(jsonText.length);
                    if (jsonText.length > 3) {
                    	//console.log(jsonText);
                    	var json = JSON.parse(jsonText);
                    	var origdata = json.origdata;
                    	var errors = json.errors;
                    	//console.log(errors);
                    	//hot.loadData(data.data);
                    	
        	//var errdata = data.data;

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
                    	for (var i=0; i<change.length; i++) {
                    		var c = change[0];
                            var rowNumber = c[0];
                            var columnname = c[1]; // prop
                            var oldValue = c[2];
                            var newValue = c[3];
                            console.log(JSON.stringify({data: hot.getSourceData()[rowNumber]}));
                    	}                        
                        //console.log(source);
                    }
                }
  	      	})
  	      	
		    var commentsPlugin = hot.getPlugin('comments');
  	      	var errs = errors.data;
  	      	for (var i = 0; i < errs.length; i++) {
  	      		var status = errs[i][0];
  	      		//console.log(errs[i]);
  	        	var row = errs[i][1] - 1;
  	        	var cols = errs[i][2];
  	        	if (row != null && cols != null) {
  	  	        	var errnum = errs[i][3];
  	  	        	var comment = errs[i][4];
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
  	      	


  	      	/*
                        addText(file.previewTemplate, jsonText);
                        //file.previewTemplate.appendChild(document.createTextNode(jsonText));
                        file.previewElement.classList.add("dz-error");
                        //file.previewElement.querySelector("[data-dz-errormessage]").textContent = jsonText;
                        //file.previewElement.querySelector("[data-dz-errormessage]").innerHTML = jsonText;
                        file.previewElement.querySelector("[data-dz-errormessage]").innerHTML = "";//getHTMLText(jsonText);
                        */
                    }  
	            	/*
                    file.previewTemplate.appendChild(document.createTextNode(jsonText));    
                    var str = jsonText.valueOf();
                    file.previewTemplate.appendChild(document.createTextNode("<br>"+str));   
                    if (0 == str.length) file.previewTemplate.appendChild(document.createTextNode("ss"));   
                    */
                    /*
	            	var str = jsonText.valueOf();
                    if (!jsonText || 0 === str.length) {
                        file.previewTemplate.appendChild(document.createTextNode(jsonText));                        
                    }
                    else {
                        file.previewTemplate.appendChild(document.createTextNode(jsonText));                        
                        file.previewElement.classList.add("dz-error");
                        file.previewElement.querySelector("[data-dz-errormessage]").textContent = jsonText;
                    }
                    */
	            });
	              /*
	            this.on("addedfile", function(file) {
	                // Create the remove button
	                var removeButton = Dropzone.createElement("<button>Remove</button>");
	                // Capture the Dropzone instance as closure.
	                var _this = this;
	                // Listen to the click event
	                removeButton.addEventListener("click", function(e) {
	                  // Make sure the button click doesn't submit the form:
	                  e.preventDefault();
	                  e.stopPropagation();
	                  // Remove the file preview.
	                  _this.removeFile(file);
	                  // If you want to the delete the file on the server as well,
	                  // you can do the AJAX request here.
	                });
	                // Add the button to the file preview element.
                    file.previewElement.appendChild(document.createElement('BR'));
                    file.previewElement.appendChild(document.createElement('BR'));
                    file.previewElement.appendChild(removeButton);
                    file.previewElement.appendChild(document.createElement('BR'));
	              });
	              */
	            //this.on("complete", function(file) {
	            //    this.removeFile(file);
	            //});
	          }
	        };
	        
	        function cellRenderer(instance, td, row, col, prop, value, cellProperties) {
	            Handsontable.renderers.TextRenderer.apply(this, arguments);
	            var meta = instance.getCellMeta(row, col);
	      		if (instance.getCellMeta(row, col).status) {
	      			//console.log(instance.getCellMeta(row, col).status);
	      		//if (meta.prop("status").equals("1")) {
		            td.style.fontWeight = 'bold';
		            //td.style.color = 'red';
		            if (instance.getCellMeta(row, col).status == 1) td.style.background = 'yellow';
		            else td.style.background = 'red';
	      		}
	          }
	        
	        function addText(node,text){     
	        	//console.log(text);
	            var t=text.split("\n"),
	                i;
	            node.appendChild(document.createElement('BR'));
	            node.appendChild(document.createElement('BR'));
                table = document.createElement('table');
                table.setAttribute("border", "1");
                table.setAttribute("cellpadding", "5");
                var tindex = 0;

	            if (t[0].length > 0) {
	            	r = table.insertRow(tindex); c = r.insertCell(0); c.innerHTML = t[0].substring(1,t[0].length-1); tindex++;
	              //node.appendChild(document.createTextNode(t[0].substring(1,t[0].length-1)));
	            }
	            for (i=1;i<t.length;i++){
	               //node.appendChild(document.createElement('BR'));
	               if (t[i].length > 0){
	                   r = table.insertRow(tindex); c = r.insertCell(0); c.innerHTML = t[i].substring(1,t[i].length-1); tindex++;
	                 //node.appendChild(document.createTextNode(t[i].substring(1,t[i].length-1)));
	               }
	            } 
	            node.appendChild(table);
	        } 	        
	        function getHTMLText(text){     
	        	//console.log(text);
	            var t=text.split("\n"),
	                i;
                table = "<table cellpadding='5' border='1' width='100%'>";
                var tindex = 0;

	            if (t[0].length > 0) {
	            	table = table + "<tr><td>" + t[0].substring(1,t[0].length-1) + "</td></tr>"; tindex++;
	            }
	            for (i=1;i<t.length;i++){
	               //node.appendChild(document.createElement('BR'));
	               if (t[i].length > 0){
		            	table = table + "<tr><td>" + t[tindex].substring(1,t[tindex].length-1) + "</td></tr>"; tindex++;
	               }
	            } 
	            table = table + "</table>";
	            //console.log(table);
	            return table;
	        } 	    
	        
	        
		</script>
    
    
        <section>
            <div id="dropzone">
                <form method="post" action="result" enctype="multipart/form-data" class="dropzone needsclick" id="my-dropzone">

	            <div class="dz-message needsclick">
	                Wähle deinen Einsendebogen oder ziehe ihn hierauf<br />
	            </div>

                </form>
            </div>
        </section>
        		
		<div id="hot"></div>
    </body>
</html>