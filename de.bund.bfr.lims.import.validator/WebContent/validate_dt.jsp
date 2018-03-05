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

<script type="text/javascript" src="js/jsonpath.js"></script>

<script lang="javascript" src="js/xlsx.full.min.js"></script>

</head>
<body>

	<script>
	
		function getStuff(index, col) {
		  return jsondata[index].errors[col];		 
		}
	
		function cellRenderer(instance, td, row, col, prop, value,
				cellProperties) {
			Handsontable.renderers.TextRenderer.apply(this, arguments);

			var err = getStuff(row,prop);
			if (err) {
				
				var errs, warns, infos;
				for (var i = 0; i < err.length; i++) {
					var msg = err[i].message; 					
					var level = err[i].level; 
					//console.log(prop + ' -> ' + level + ' -> ' + msg);
                    if (level == 1) {
                        if (!warns) warns = "<li>" + msg + "</li>";
                        else warns += "<li>" + msg + "</li>";
                    } else if (level == 2) {
                        if (!errs) errs = "<li>" + msg + "</li>";
                        else errs += "<li>" + msg + "</li>";
                    } else if (level == 4) {
                        if (!infos) infos = "<li>" + msg + "</li>";
                        else infos += "<li>" + msg + "</li>";
                    }
				}
				
				td.style.fontWeight = 'bold';
						
				if (errs) { // error
					td.style.background = '#ffc1c1'; // red
					//instance.getPlugin('comments').setCommentAtCell(row, col, "some text comment");
					//cellProperties.comment = errs;
					
					if (!td.tipster) {
						$(td).tooltipster(
								{
									repositionOnScroll : true,
									animation : 'grow', // fade
									delay : 0,
									theme : [ 'tooltipster-error' ],
									touchDevices : false,
									trigger : 'hover',
									contentAsHTML : true,
									content : "<ul type='disc'>"
											+ errs + "</ul>", // row+"-"+col+":<br>"+ 
									side : 'top',
									arrowColor : '#ffc1c1'
								});
					}
					
				}
				else if (infos) { // info
                    var multi = true;
                    if (!errs && !warns) {
                        td.style.background = '#F0F8FF'; //blue     
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
                                            + infos + "</ul>", // row+"-"+col+":<br>"+ 
                                    side : 'top',
                                    arrowColor : '#F0F8FF',
                                    multiple : multi
                                });
                    }
				}
						
				if (warns) { // warning
					//td.style.background = '#fffacd'; //yellow	  
					//cellProperties.commment = errs;
				
					var multi = true;
					if (!errs) {
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
											+ warns + "</ul>", // row+"-"+col+":<br>"+ 
									side : 'bottom',
									arrowColor : '#fffacd',
									multiple : multi
								});
					}
					
				}

				// all
				td.tipster = true;									
			}
			else {
				td.style = null;
			}
		}		
	
		function fillHOT() {
			console.log("fhot_start: "
					+ ("" + (new Date().getTime() / 1000)).substring(6));
		      //console.log(jsondata);
			//var data = jsondata.data;
			//var errors = jsondata.errors;
			var data = jsonPath(jsondata, "$..data");

				hot = new Handsontable(container, {
					data : data,
					colHeaders: ["Ihre Probenummer", "Probenummer nach<br>AVVData", "Erreger<br>(Text aus ADV-Kat-Nr.16)", "Erreger<br>(Textfeld/ Ergänzung)", "Datum der Probenahme", "Datum der Isolierung", "Ort der Probenahme<br>(Code aus ADV-Kat-Nr.9)", "Ort der Probenahme (PLZ)", "Ort der Probenahme (Text)", "Oberbegriff (Kodiersystem) der Matrizes (Code aus ADV-Kat-Nr.2)", "Matrix Code (Code aus ADV-Kat-Nr.3)", "Matrix (Textfeld/ Ergänzung)", "Verarbeitungszustand (Code aus ADV-Kat-Nr.12)", "Grund der Probenahme (Code aus ADV-Kat-Nr.4)", "Grund der Probenahme (Textfeld/ Ergänzung)", "Betriebsart (Code aus ADV-Kat-Nr.8)", "Betriebsart (Textfeld/ Ergänzung)", "VVVO-Nr / Herde", "Bemerkung (u.a. Untersuchungsprogramm)"],
					stretchH : 'all',
					colWidths : [ 40 ],
					autoWrapRow : true,
					comments : true,
					debug : true,
					manualColumnResize : true,
					manualRowResize : true,
					renderAllRows : true,	  
					rowHeaders: true,
					cells : function(row, col, prop) {
						var cellProperties = {};
						cellProperties.renderer = cellRenderer;
						return cellProperties;
					}
				});


				hot.render();
				/*
				hot.updateSettings({
					rowHeaders: true
				});
				*/
				console.log("fhot_end: "
						+ ("" + (new Date().getTime() / 1000)).substring(6));
			
		}
		function getJson(str) {
			//console.log(str);
		    try {
		        return JSON.parse(str);
		    } catch (e) {
		        return null;
		    }
		}
		
			 var oFileIn;

			 $(function() {
			     oFileIn = document.getElementById('my_file_input');
			     if(oFileIn.addEventListener) {
			         oFileIn.addEventListener('change', filePicked, false);
			     }
			 });


			 function filePicked(oEvent) {
			     // Get The File From The Input
			     var oFile = oEvent.target.files[0];
			     var sFilename = oFile.name;
			     // Create A File Reader HTML5
			     var reader = new FileReader();
			     
			     // Ready The Event For When A File Gets Selected
			     reader.onload = function(e) {
			         var data = e.target.result;
				       var workbook = XLSX.read(data, {
					         type: 'binary',cellDates:true, cellText:false
					       });	
				       var sheet = workbook.Sheets['Einsendeformular'];
				       var json_object = XLSX.utils.sheet_to_json(sheet, {dateNF:'dd"."mm"."yyyy', blankrows:false, range:41, defval:"", header:["sample_id","sample_id_avv","pathogen_adv","pathogen_text","sampling_date","isolation_date","sampling_location_adv","sampling_location_zip","sampling_location_text","matrix_adv","topic_adv","matrix_text","process_state","sampling_reason_adv","sampling_reason_text","operations_mode_adv","operations_mode_text","vvvo","comment"]});
				       for (var i=0;i<json_object.length;i++) {
				    	   var j = 0;
				    	   var len = Object.keys(json_object[i]).length;
				    	   	for (var key in json_object[i]) {
				    	   		if (json_object[i][key]) break;
				    	   		j++;
				    	   	}
				    	   	//console.log(j);
				    	   	if (j == len) {
				    	   		//console.log(i);
				    	   		json_object.splice(i,1)
				    	   		i--;
				    	   	}
				       }
				       //console.log(json_object);
				       //console.log(JSON.stringify(json_object))
				       var data = JSON.stringify(json_object);
				    // Sending and receiving data in JSON format using POST method
				       //
				       var xhr = new XMLHttpRequest();
				       var url = "https://epilab.bfr.berlin/api/v1/upload";
				       xhr.open("POST", url, true);
				       xhr.setRequestHeader("Content-type", "application/json");
				       xhr.onreadystatechange = function () {
				           if (xhr.readyState === 4 && xhr.status === 200) {
				               jsondata = getJson(xhr.responseText);
				               //console.log(jsondata);
									if (jsondata) {
										fillHOT();						
									}	
									else {
										alert('W');
									}
				           }
				       };
				       xhr.send(data);				       
			     };
			     

			     reader.onerror = function(ex) {
			       console.log(ex);
			     };

			     reader.readAsBinaryString(oFile);
			 }
			 </script>



	<section>
				<input type="file" id="my_file_input" />
		<div id="errmsg"></div>
		<div id="hot"></div>
	</section>

	<script>
		var container = document.getElementById('hot');
		var hot;
		var jsondata;
	</script>
</body>
</html>