<!DOCTYPE html>
<meta charset="utf-8">

<link href="css/d3.css" rel="stylesheet" type="text/css">

<style>
#test1 {
  margin: 0;
  padding: 0;
  overflow: none;
}
</style>


<body>

<div id="test1">
  <svg></svg>
</div>

<script src="js/d3.js" charset="utf-8"></script>
<script src="js/jquery-3.0.0.js" charset="utf-8"></script>
<script src="js/nvtooltip.js" charset="utf-8"></script>
<script src="js/d3legend.js" charset="utf-8"></script> 
<script src="js/d3line.js" charset="utf-8"></script> 
<script src="js/d3linewithlegend.js" charset="utf-8"></script> 
<script>

function log(text) {
  if (console && console.log) console.log(text);
  return text;
}


$(document).ready(function() {
  var margin = {top: 30, right: 10, bottom: 50, left: 60},
      chart = d3LineWithLegend()
                .xAxis.label('Tweet ID')
                .width(width(margin))
                .height(height(margin))
                .yAxis.label('Sentiment Score');


  var svg = d3.select('#test1 svg')
      .datum(generateData())

  svg.transition().duration(500)
      .attr('width', width(margin))
      .attr('height', height(margin))
      .call(chart);


  chart.dispatch.on('showTooltip', function(e) {
  var offset = $('#test1').offset(), // { left: 0, top: 0 }
        left = e.pos[0] + offset.left,
        top = e.pos[1] + offset.top,
        formatter = d3.format(".04f");

    var content = '<h3>' + e.series.label + '</h3>' +
                  '<p>' +
                  '<span class="value">[' + e.point[1] + ', ' + e.point[2] + ']</span>' +
                  '</p>';

    nvtooltip.show([left, top], content);
  });

  chart.dispatch.on('hideTooltip', function(e) {
    nvtooltip.cleanup();
  });




  $(window).resize(function() {
    var margin = chart.margin();

    chart
      .width(width(margin))
      .height(height(margin));

    d3.select('#test1 svg')
      .attr('width', width(margin))
      .attr('height', height(margin))
      .call(chart);

    });




  function width(margin) {
    var w = $(window).width() - 20;

    return ( (w - margin.left - margin.right - 20) < 0 ) ? margin.left + margin.right + 2 : w;
  }

  function height(margin) {
    var h = $(window).height() - 20;

    return ( h - margin.top - margin.bottom - 20 < 0 ) ? 
              margin.top + margin.bottom + 2 : h;
  }


  //data
  function generateData() {
    var sin = [];
    var json = <%=request.getAttribute("data")%>;
    var size = <%=request.getAttribute("size")%>;
    console.log(json.tweetObj[0]);
    for (var i = 0; i < size; i++) {
      sin.push([ i, json.tweetObj[i].sentimentScore, json.tweetObj[i].entities]);
    }

    return [
      {
        data: sin,
        label: "Sentiment Score"
      }
    ];
  }

});


</script>