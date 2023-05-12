const chalk = require('colorom');
const fs = require('fs');
const path = require('path');
const logger = require('morgan');
const express = require('express');
const shell = require('child_process');
const cors = require('cors');
const readline = require("readline");

let args = Array.from(process.argv);
args.shift();
args.shift();

var port = args[0] || 8080;

if(port.toString().match("help")){
	console.log(chalk.yellow("nserver: Native Server"));
	console.log(chalk.white("To start server:")+"\n"+
    chalk.red("nserver ")+ chalk.blue.bold("[Int: port] ")
  );
  console.log(chalk.white("To start server(with xampp):")+"\n"+
    chalk.red("nserver ")+ chalk.yellow("xas")
  );
  console.log(chalk.white("To restart server(with xampp):")+"\n"+
    chalk.red("nserver ")+ chalk.yellow("xar")
  );
  console.log(chalk.white("To stop server(with xampp):")+"\n"+
    chalk.red("nserver ")+ chalk.yellow("xast")
  );
	return;
}

if(port == "xas"){
	port = 8080;
	console.log(chalk.green.bold("Starting localhost with xampp"));
	console.log(shell.execSync("sudo /opt/lampp/lampp start").toString());
	return;
}

if(port == "xar"){
	port = 8080;
	console.log(chalk.yellow.bold("Restarting localhost with xampp"));
	console.log(shell.execSync("sudo /opt/lampp/lampp restart").toString());
	return;
}

if(port == "xast"){
	port = 8080;
	console.log(chalk.red.bold("Stopping localhost with xampp"));
	console.log(shell.execSync("sudo /opt/lampp/lampp stop").toString());
	return;
}

var app = express();

app.use(cors({
	origin: "*"
}));

var manp = path.resolve(args[1] || "./");

console.log(chalk.yellow("Server will start in dir: "+ manp));

app.use(express.static(manp));

app.use(logger("dev"));

app.listen(parseInt(port), () => {console.log(chalk.green.bold("Server started: http://localhost:"+ port +"/"))});
