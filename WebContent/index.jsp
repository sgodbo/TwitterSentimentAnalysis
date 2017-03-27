<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -->

<html>
	<head>
		<title>Interactive Line Graph</title>
		<script src="http://d3js.org/d3.v2.js"></script>
		<!-- 
			using JQuery for element dimensions
			This is a small aspect of this example so it can be removed fairly easily if needed.
		-->
		<script src="http://code.jquery.com/jquery-1.7.2.min.js"></script>
		<script src="sample_data.js"></script>
		<script src="line-graph.js"></script>
		<link rel="stylesheet" href="style.css" type="text/css">
		<style>
			body {
				font-family: "Helvetica Neue", Helvetica;
			}
					
			p {
				clear:both;
				top: 20px;
			}		
					
			div.aGraph {
				margin-bottom: 30px;
			}
		</style>
	</head>
	<body>
	<div id="graph1" class="aGraph" style="position:relative;width:100%;height:400px"></div>
	
	<div id="graph2" class="aGraph" style="float:left;position:relative;width:49%;height:200px"></div>
	<div id="graph3" class="aGraph" style="float:left;position:relative;width:49%;height:200px"></div>

	<script>
		/* 
		 * If running inside bl.ocks.org we want to resize the iframe to fit both graphs
		 */
		 if(parent.document.getElementsByTagName("iframe")[0]) {
			 parent.document.getElementsByTagName("iframe")[0].setAttribute('style', 'height: 650px !important');
		 }
	
		 /*
		 * Note how the 'data' object is added to here before rendering to provide decoration information.
		 * <p>
		 * This is purposefully done here instead of in data.js as an example of how data would come from a server
		 * and then have presentation information injected into it (rather than as separate arguments in another object)
		 * and passed into LineGraph.
		 *
		 * Also, CSS can be used to style colors etc, but this is also doable via the 'data' object so that the styling
		 * of different data points can be done in code which is often more natural for display names, legends, line colors etc
		 */
		 // add presentation logic for 'data' object using optional data arguments
		 data["displayNames"] = ["2xx","3xx","4xx","5xx"];
		 data["colors"] = ["green","orange","red","darkred"];
		 data["scale"] = "pow";
		 
		 // add presentation logic for 'data' object using optional data arguments
		 data2["displayNames"] = ["2xx","3xx","4xx","5xx"];
		 data2["colors"] = ["green","orange","red","darkred"];
		 data2["scale"] = "linear";
		 
		 // add presentation logic for 'data' object using optional data arguments
		 data3["displayNames"] = ["Data1", "Data2"];
		 data3["axis"] = ["left", "right"];
		 data3["colors"] = ["#2863bc","#c8801c"];
		 data3["rounding"] = [2, 0];

		 // create graph now that we've added presentation config
		var l1 = new LineGraph({containerId: 'graph1', data: data});
		var l2 = new LineGraph({containerId: 'graph2', data: data2});
		var l3 = new LineGraph({containerId: 'graph3', data: data3});
		
	setInterval(function() {
		/*
		* The following will simulate live updating of the data (see dataA, dataB, dataC etc in data.js which are real examples)
		* This is being simulated so this example functions standalone without a backend server which generates data such as data.js contains.
		*/
		// for each data series ...
		var newData = [];
		data.values.forEach(function(dataSeries, index) {
			// take the first value and move it to the end
			// and capture the value we're moving so we can send it to the graph as an update
			var v = dataSeries.shift();
			dataSeries.push(v);
			// put this value in newData as an array with 1 value
			newData[index] = [v];
		})
		
		// we will reuse dataA each time
		dataA.values = newData;
		// increment time 1 step
		dataA.start = dataA.start + dataA.step;
		dataA.end = dataA.end + dataA.step; 
					
		l1.slideData(dataA);
	}, 2000);

		
	</script>



	</body>
</html>