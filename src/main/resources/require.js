var require = function(r) { 
	out.println("Requires [" + r + "]");
	return {post : function(x){
		// a fake post function so the transmission script does not fail
	}};   
}
