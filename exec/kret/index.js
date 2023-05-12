var shell = require('child_process');
var chalk = require('colorom');

var args = Array.from(process.argv);
args.shift();
args.shift();
var name = args[0];

if(name == "help"){
	console.log(chalk.green('keeps executing commands until they work... usually used for apt install so if it fails it retries until it works'));
	return;
}

function exec(str){
	console.log(chalk.green('Executing: '+str));
	shell.exec(str, (err, stdout,  stderr) => {
		if(err || stderr){
			console.log(chalk.red("Error: "+stderr));
			console.log(chalk.green("Retrying..."));
			exec(str);
		} else {
			console.log(stdout);
		}
	});
}

exec(args.join(" "));
