$('#svgbasics').svg('get').clear();
console.clear();

function calcYShift(x1, y1, x2, y2, offset) {
	var X = x2-x1;
	var Y = y2-y1;
}

var svg = $('#svgbasics').svg('get')
var ecx = 150, ecy = 150, erx = 145, ery = 50;
svg.ellipse(ecx, ecy, erx, ery, {fill: 'none', stroke: 'red', 'stroke-width': 3});
var g = svg.group({stroke: 'black', 'stroke-width': 2});
svg.line(g, ecx-erx, ecy, ecx+erx, ecy);
svg.line(g, ecx, ecy-ery, ecx, ecy+ery);

function calcYShift(x1, y1, x2, y2, offset) {
	var X = x2-x1;
	var Y = y2-y1;
	var f = (Y*Y)-(offset*offset);
	var M = (X/Y) * Math.sqrt(f);
	return M;
}


/*

M = (X/Y) * SQRT((Y*Y) - (Z*Z))


 */
