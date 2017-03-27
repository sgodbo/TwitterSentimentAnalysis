<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript" src="js/jquery-3.0.0.js"></script>
<script type="text/javascript">

$(document).ready(function(){

    var counter = 2;
		
    $("#addButton").click(function () {
				
	if(counter>10){
            alert("Only 10 textboxes allow");
            return false;
	}   
		
	var newTextBoxDiv = $(document.createElement('div'))
	     .attr("id", 'TextBoxDiv' + counter);
                
	newTextBoxDiv.after().html('<label>Query #'+ counter + ' : </label>' +
	      '<input type="text" name="query' + counter + 
	      '" id="query' + counter + '" value="" >');
            
	newTextBoxDiv.appendTo("#TextBoxesGroup");

				
	counter++;
     });

     $("#removeButton").click(function () {
	if(counter==1){
          alert("No more textbox to remove");
          return false;
       }   
        
	counter--;
			
        $("#TextBoxDiv" + counter).remove();
			
     });
		
     
  });
</script>
</head>
<body>
	<form action="welcome" method="GET">
		<div id='TextBoxesGroup'>
			<div id="TextBoxDiv1">
				<label>Query #1 : </label><input type='text' name = 'query1' id='query1' value=''>
			</div>
		</div>
		<input type='button' value='Add Query' id='addButton'>
		<input type='button' value='Remove Query' id='removeButton'>
		<label>Exclude Retweets : <input type="checkbox" value='' id='excludert' name='excludert'></label>
		<input type="submit" value="Submit">
	</form>
</body>
</html>