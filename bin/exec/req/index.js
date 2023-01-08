const axios = require("axios");
const chalk = require('colorom');
const fs = require("fs");

let args = Array.from(process.argv);
args.shift();
args.shift();

var opt = args[0];
var url = args[1];
var data = args[2];

function errorHandler(error){
	console.log(chalk.red.bold(error));
}

var getData = str => {
	var sets = str.match("&") ? str.split("&") : [str];
	var obj = {};
	sets.forEach((set)=>{
		var key = set.split("=")[0];
		var val = set.split("=")[1];
		obj[key] = val;
	});
	return obj;
}

var post_req = (...args)=>{
	axios.post(...args)
	.then((result) => {
		console.log(result.data);
	}).catch(errorHandler);
};

if(opt == "-g" || opt == "--get"){
	axios.get(url)
	.then((result) => {
		console.log(result.data);
	}).catch(errorHandler);
} else if(opt == "-p" || opt == "--post"){
	if(data.match(/^\@FILE\:/)){
		var _data = fs.createReadStream(data.replace("@FILE:",""));
		_data.on("end", function(){
			var fdata = new FormData();
			fdata.append("file", _data);
			post_req({
				url: url,
				headers: {
					"Content-Type": "multipart/form-data"
				},
				data: _data
			});
		});
		return;
	}
	post_req(url, getData(data));
} else {
	console.log(chalk.yellow("req: Request to Server"));

  console.log(chalk.white("To send an http Request to a server with POST:")+"\n"+
    chalk.red("req ")+ 
    chalk.blue.bold("-p ")+
    chalk.yellow.bold("[String: url] ")+
    chalk.yellow.bold("[String: data]")
  );

  console.log(chalk.white("To send an http Request to a server with GET:")+"\n"+
    chalk.red("req ")+ 
    chalk.blue.bold("-g ")+
    chalk.yellow.bold("[String: url] ")
  );

  console.log(chalk.white("To send an http Request to a server with POST with a file as data:")+"\n"+
    chalk.red("req ")+ 
    chalk.blue.bold("-p ")+
    chalk.yellow.bold("[String: url](@FILE:filename.ext) ")
  );

  console.log(chalk.green("Arguments: "));
  console.log(chalk.yellow("Name/Method | Location | Argument"));
  console.log(chalk.white("Get:   ") + chalk.red.bold("@req.get   ")+ chalk.blue.bold("--get, -g"));
  console.log(chalk.white("Post:   ") + chalk.red.bold("@req.post   ")+ chalk.blue.bold("--post, -p"));
}
