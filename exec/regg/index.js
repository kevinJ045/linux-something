const axios = require("axios");
const chalk = require('colorom');
const fs = require("fs");
const path = require("path");

let args = Array.from(process.argv);
args.shift();
args.shift();

var cmd = args[0];
var str = args[1];

function exit(code, text){
	if(text) console.log(text);
	process.exit(code);
}

if(!cmd || cmd == 'help'){
	console.log('refer to the code instead pls');
	exit(0);
}

var jpath = path.join(__dirname, 'reg.json');

var RegexSet = JSON.parse(fs.readFileSync(jpath));

var mkRegEx = function(str,flags){
  var that = this;
  if(!flags) flags = 'mg';
  return new RegExp(str.replace(/\%([A-Za-z0-9_]+)/g,function(a,b){
	if(!RegexSet[b]) return exit(1, chalk.red('variable not found!!'));
    return RegexSet[b];
  }),flags);
};

if(cmd == 'list'){
	if(str == 'all'){
		for(var i in RegexSet) console.log(chalk.green(i+':'),RegexSet[i]);
		exit(0);
	}
	console.log(Object.keys(RegexSet).join('\n'));
	exit(0);
}

if(args.length > 1){
	if(cmd == 'add'){
		var val = str;
		var v = args[2];
		if(!v || !val) exit(1, 'needed [regex] [varname]');
		RegexSet[v] = mkRegEx(val);
		console.log("Saving",chalk.green(mkRegEx(cmd)), "as", v);
		fs.writeFileSync(jpath, JSON.stringify(RegexSet));
	}
	if(cmd == 'test'){
		var val = str;
		var v = args[2];
		if(!v || !val) exit(1, 'needed [string-to-test] [regex]');
		var reg = mkRegEx(v);
		console.log(chalk.green('Testing '+ str+ ' with '+ reg),":", reg.test(str));
	}
} else {
	console.log(chalk.green(mkRegEx(cmd)));	
}
