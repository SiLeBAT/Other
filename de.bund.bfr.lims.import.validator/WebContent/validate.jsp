<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
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
<link rel="stylesheet" type="text/css" href="css/tooltipster.bundle.min.css" />
<link rel="stylesheet" type="text/css" href="css/my.tooltipster.css" />
<link rel="stylesheet" type="text/css" href="css/my.HOT.css" />

<script type="text/javascript" src="js/pikaday/pikaday.js"></script>
<script type="text/javascript" src="js/moment/moment.js"></script>
<script type="text/javascript" src="js/zeroclipboard/ZeroClipboard.js"></script>
<script type="text/javascript" src="js/numbro/numbro.js"></script>
<script type="text/javascript" src="js/numbro/languages.js"></script>
<script type="text/javascript" src="js/handsontable.js"></script>

<script type="text/javascript" src="js/jquery-3.1.1.min.js"></script>

<script type="text/javascript" src="js/tooltipster.bundle.min.js"></script>

</head>
<body>

	<script>
		// myDropzone is the configuration for the element that has an id attribute
		// with the value my-dropzone (or myDropzone)
		Dropzone.options.myDropzone = {
			createImageThumbnails : false,
			acceptedFiles : ".xls,.xlsx",
			uploadMultiple : true,
			init : function() {
				this.on("thumbnail", function(file, dataUrl) {
					//file.previewElement.classList.get('dz-image').last().find('img').attr({width: '100%', height: '100%'});
				}), this.on("success", function(file, jsonText) {
					var mydiv = document.getElementById("dropzone");
					mydiv.style.visibility = "";
					mydiv.style.display = "none";
					//file.previewElement.classList.get('dz-image').css({"width":"100%", "height":"auto"});
					//console.log(file);
					console.log(jsonText);
					if (jsonText && jsonText.length > 5) {
						var jsonO = getJson(jsonText);
						if (jsonO) {
							fillHOT(jsonO);						
						}
						else {
							var errMsg = "<br>" + jsonText;
							document.getElementById('errmsg').innerHTML = errMsg;
						}
					}
					else {
						var errMsg = "<br>Die Exceldatei hat offenbar kein korrektes Format.<br>Eine mögliche Ursache: der Name des Tabellenblattes ist nicht 'Einsendeformular'!";
						document.getElementById('errmsg').innerHTML = errMsg;
					}
				});
				this.on("addedfile", function(file) {
					console
							.log("addedfile: "
									+ ("" + (new Date().getTime() / 1000))
											.substring(6));
				});
			}
		};

		function fillHOT(json) {
			console.log("fhot_start: "
					+ ("" + (new Date().getTime() / 1000)).substring(6));
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
			} else {
				var selected;
				if (hot) {
					selected = hot.getSelected();
					hot.destroy();
				}
				var colH = origdata.colHeaders;
				var data = origdata.data; //sanitizeData(origdata.data);
				var cols = origdata.columns; //sanitizeColumns(origdata.columns);
				hot = new Handsontable(container, {
					data : data,
					columns : cols,
					colHeaders : colH,
					rowHeaders: true,
					stretchH : 'all',
					colWidths : [ 40 ],
					autoWrapRow : true,
					comments : true,
					debug : true,
					manualColumnResize : true,
					manualRowResize : true,
					renderAllRows : data.length < 200,
					//minSpareRows: 1,	  	                
					cells : function(row, col, prop) {
						var cellProperties = {};
						cellProperties.renderer = cellRenderer;
						return cellProperties;
					},
					afterChange : function(change, source) {
						if (source === 'loadData') { // "alter", "edit", "populateFromArray", "loadData", "autofill", "paste".
							return; //don't save this change
						}

						if (change != null) {
							document.body.style.cursor = 'progress';
				            console.log("fhot_change: "
				                    + ("" + (new Date().getTime() / 1000)).substring(6));
				            var sData = hot.getSourceData();
				            undoSuggestions(sData, errs, change[0][0], change[0][1]);
				            console.log("fhot_change_undo_end: "
				                    + ("" + (new Date().getTime() / 1000)).substring(6));
							var data = JSON.stringify({
								data : sData
							}); // [rowNumber]
							//data = unsanitizeData(data, colH);

							xhr = new XMLHttpRequest();
							xhr.open("POST", "result", true);
							xhr.setRequestHeader("Content-type",
									"application/json; charset=utf-8");//"application/x-www-form-urlencoded" multipart/form-data
							xhr.onreadystatechange = function() {
								if (xhr.readyState == XMLHttpRequest.DONE
										&& xhr.status == 200) {
									//console.log(xhr.responseText);
									fillHOT(JSON.parse(xhr.responseText));
								}
								document.body.style.cursor = 'default';
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

				console.log("fhot_comment: "
						+ ("" + (new Date().getTime() / 1000)).substring(6));
				for (var i = 0; i < errs.length; i++) {
					// Status, Zeile, Spalte(n), Fehler-Nr, Kommentar
					var status = errs[i]["Status"];
					//console.log(errs[i]);
					var row = errs[i]["Zeile"];
					if (row !== null) {
						row = row - 1;
	                    var cols = errs[i]["Spalte"];
	                    if (cols !== null) {
	                        var errnum = errs[i]["Fehler-Nr"];
	                        var comment = errs[i]["Kommentar"];
	                        cols += "";
	                        var colarr = cols.split(";");
	                        for (var j = 0; j < colarr.length; j++) {
	                            var col = colarr[j] - 1;
	                            //console.log(row + " - " + col);
	                            if (status == 1) {
	                                if (!hot.getCellMeta(row, col).warningMessage)
	                                    hot.getCellMeta(row, col).warningMessage = "<li>"
	                                            + comment + "</li>";
	                                else
	                                    hot.getCellMeta(row, col).warningMessage += "<li>"
	                                            + comment + "</li>";
                                } else if (status == 2) {
                                    if (!hot.getCellMeta(row, col).errorMessage)
                                        hot.getCellMeta(row, col).errorMessage = "<li>"
                                                + comment + "</li>";
                                    else
                                        hot.getCellMeta(row, col).errorMessage += "<li>"
                                                + comment + "</li>";
                                } else if (status == 4) {
                                    if (!hot.getCellMeta(row, col).infoMessage)
                                        hot.getCellMeta(row, col).infoMessage = "<li>"
                                                + comment + "</li>";
                                    else
                                        hot.getCellMeta(row, col).infoMessage += "<li>"
                                                + comment + "</li>";
	                            }
	                        }
	                    }
					}
				}
				hot.render();
				hot.updateSettings({
					rowHeaders: true
				});
				console.log("fhot_end: "
						+ ("" + (new Date().getTime() / 1000)).substring(6));
			}
		}
		
		function getJson(str) {
		    try {
		        return JSON.parse(str);
		    } catch (e) {
		        return null;
		    }
		}
		
		function undoSuggestions(sData, errs, omitRow, omitProp) {
			var omitCol = hot.propToCol(omitProp);
			for (var i = 0; i < errs.length; i++) {
				// Status, Zeile, Spalte(n), Fehler-Nr, Kommentar
				var status = errs[i]["Status"];
				if (status == 4) {
					var row = errs[i]["Zeile"];
					if (row !== null) {
						row = row - 1;
	                    var cols = errs[i]["Spalte"];
	                    if (cols !== null) {
	                        cols += "";
	                        var colarr = cols.split(";");
	                        for (var j = 0; j < colarr.length; j++) {
	                            var col = colarr[j] - 1;
	                        	if (row != omitRow || col != omitCol) {
	                        		if (errs[i]["Original"]) sData[row][hot.colToProp(col)] = errs[i]["Original"];
	            				}
	                        }
	                    }
					}
				}
			}
		}

		function cellRenderer(instance, td, row, col, prop, value,
				cellProperties) {
			Handsontable.renderers.TextRenderer.apply(this, arguments);

			var meta = instance.getCellMeta(row, col);

			if (meta.errorMessage || meta.warningMessage || meta.infoMessage) {
				//console.log(row + " - " + col + " - " + td.className);
				//if ($(td).tooltipster) {
				//$(td).tooltipster('destroy');
				//}
				td.style.fontWeight = 'bold';
				if (meta.errorMessage) {
					td.style.background = '#ffc1c1'; // red
					if (!td.tipster) {
						$(td).tooltipster(
								{
									/*
									functionBefore: function(instance, helper) {
										console.log(row + " - " + col + " - ");
									},
									 */
									repositionOnScroll : true,
									animation : 'grow', // fade
									delay : 0,
									theme : [ 'tooltipster-error' ],
									touchDevices : false,
									trigger : 'hover',
									contentAsHTML : true,
									content : "<ul type='disc'>"
											+ meta.errorMessage + "</ul>", // row+"-"+col+":<br>"+ 
									side : 'top',
									arrowColor : '#ffc1c1'
								});
					}
				}
				else if (meta.infoMessage) {
                    var multi = true;
                    if (!meta.errorMessage && !meta.warningMessage) {
                        td.style.background = '#F0F8FF'; //green     
                        multi = false;
                    }
                    if (!td.tipster) {
                        $(td).tooltipster(
                                {
                                    repositionOnScroll : true,
                                    animation : 'grow',
                                    delay : 0,
                                    theme : [ 'tooltipster-info' ],
                                    touchDevices : false,
                                    trigger : 'hover',
                                    contentAsHTML : true,
                                    // don't forget to provide content here as the first tooltip will have deleted the original title attribute of the element
                                    content : "<ul type='disc'>"
                                            + meta.infoMessage + "</ul>", // row+"-"+col+":<br>"+ 
                                    side : 'top',
                                    arrowColor : '#F0F8FF',
                                    multiple : multi
                                });
                    }
                }

				if (meta.warningMessage) {
					var multi = true;
					if (!meta.errorMessage) {
						td.style.background = '#fffacd'; //yellow	  
						multi = false;
					}
					if (!td.tipster) {
						$(td).tooltipster(
								{
									repositionOnScroll : true,
									animation : 'grow',
									delay : 0,
									theme : [ 'tooltipster-warning' ],
									touchDevices : false,
									trigger : 'hover',
									contentAsHTML : true,
									// don't forget to provide content here as the first tooltip will have deleted the original title attribute of the element
									content : "<ul type='disc'>"
											+ meta.warningMessage + "</ul>", // row+"-"+col+":<br>"+ 
									side : 'bottom',
									arrowColor : '#fffacd',
									multiple : multi
								});
					}
				}

				td.tipster = true;
				//$(td).tooltipster('destroy');
			} else {
				//$(td).tooltipster('destroy');
			}

			/*
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
			 */
			/*
			if (meta.status) {
			//console.log(meta.status);
			td.style.fontWeight = 'bold';
			//td.style.color = 'red';
			if (meta.status == 1) td.style.background = '#fffacd'; //yellow
			else td.style.background = '#ffc1c1'; // red
			}
			 */
		}
			 
			 function $_GET(param) {
					var vars = {};
					window.location.href.replace( location.hash, '' ).replace( 
						/[?&]+([^=&]+)=?([^&]*)?/gi, // regexp
						function( m, key, value ) { // callback
							vars[key] = value !== undefined ? value : '';
						}
					);

					if ( param ) {
						return vars[param] ? vars[param] : null;	
					}
					return vars;
				}
			 
	</script>


	<section>
	<div id="dropzone">
		<form method="post" action="result" enctype="multipart/form-data"
			class="dropzone needsclick" id="my-dropzone" style="height: 800px;">
			
			<input type="text" name="workflowname" id="wfn" style="width: 400px;"
				value="testing/Alex_testing/Proben-Einsendung_Web21" /> <!-- testing/Hartung_Weba -->
				
			<script>
			  if ($_GET('wfo') == 1) {
				  document.getElementById('wfn').type = 'text';
			  }
			  else {
				  document.getElementById('wfn').type = 'hidden';
			  }
			</script>
			  
			<div class="dz-message needsclick">
				Wähle deinen Einsendebogen oder ziehe ihn hierauf<br />
			</div>

		</form>
	</div>
	</section>

	<section>
	<div id="errmsg"></div>
	<div id="hot"></div>
	</section>

	<script>
		var container = document.getElementById('hot');
		var hot;
	</script>
</body>
</html>