$('#svgbasics').svg('get').clear();
console.clear();
var svg = $('#svgbasics').svg('get')
var ecx = 150, ecy = 150, erx = 145, ery = 50;
svg.ellipse(ecx, ecy, erx, ery, {fill: 'none', stroke: 'red', 'stroke-width': 3});
var g = svg.group({stroke: 'black', 'stroke-width': 2});
svg.line(g, ecx-erx, ecy, ecx+erx, ecy);
svg.line(g, ecx, ecy-ery, ecx, ecy+ery); 
