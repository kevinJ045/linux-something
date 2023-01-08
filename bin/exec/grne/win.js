const chalk = {
	red: (str) => {
		return "\033[31m"+str+"\033[37m";
	},
	green: (str) => {
		return "\033[32m"+str+"\033[37m";
	},
	yellow: (str) => {
		return "\033[33m"+str+"\033[37m";
	},
	blue: (str) => {
		return "\033[34m"+str+"\033[37m";
	},
	purple: (str) => {
		return "\033[35m"+str+"\033[37m";
	},
	black: (str) => {
		return "\033[30m"+str+"\033[37m";
	},
	white: (str) => {
		return "\033[37m"+str+"\033[37m";
	}
};

for(var i in chalk){
	chalk[i].bold = function(str){
		return "\033[1m" + chalk[i](str) + "\033[0m";	
	}
};
const fs = require('fs');
const path = require('path');
const readline = require("readline");

var toArray = function(list) {
  return Array.prototype.slice.call(list || [], 0);
},pickRandom = function(){
  var words = arguments;
  if(words.length <= 1) return arguments[0];
  if(arguments[0] instanceof Array) words = arguments[0];
  var randomWord = toArray(words);
  var rand = Math.floor(Math.random() * randomWord.length);
  return randomWord[rand];
},randFrom = function(min,max) {
  return Math.floor(Math.random()*(max-min+1)+min);
};

const txt = path.join(__dirname,"grne.txt");

function rand(count, spacing, nonum, onlynum){
  if(typeof count != "number" || count <= 0) count = 48;
  var time = new Date().getTime().toString();
  var rnd = new String();
  for (var i = 0; i < count; i++) {
    var args = [];
    if(!nonum) args.push(randFrom(0,9));
    if(!onlynum) args.push(A_Z[randFrom(0,A_Z.length-1)]);
    if(!nonum) args.push(randFrom(i,randFrom(i*2,i*4)));
    if(!nonum) args.push(time[randFrom(0,time.length-1)]);
    if(!onlynum) args.push(a_z[randFrom(0,a_z.length-1)]);
    if(onlynum && nonum) args.push(randFrom(randFrom(randFrom(0,9),randFrom(0,9)),randFrom(randFrom(0,9),randFrom(0,9))));
    var r = pickRandom.apply(null, args);
    rnd += (spacing ? " " : "") + r;
  }
  return rnd;
}

function tobool(str){
  if(str.match("true")) return true;
  else return false;
}

let args = Array.from(process.argv);
args.shift();
args.shift();

if(args[0] && args[0].match("help")){
  console.log(chalk.yellow("GRNE: Generate Random (Native|Numeric) Entry"));

  console.log(chalk.white("To generate a random word type:")+"\n"+
    chalk.red("grne ")+ chalk.blue.bold("[Int: number of characters] ")+
    chalk.blue.bold("[Int: number of characters repeated] ")
    + chalk.yellow.bold("[Bool: no numbers?] ")+ chalk.yellow.bold("[Bool: only numbers?] ")
    + chalk.yellow.bold("[Bool: spacing?] ")
    + chalk.yellow.bold("[Bool: save?]")
  );

  console.log(chalk.white("To get all generated random words type:")+"\n"+
    chalk.red("grne ")+chalk.yellow("list"));

  console.log(chalk.white("To delete all generated random words type:")+"\n"+
    chalk.red("grne ")+chalk.yellow("list-remove"));

  console.log(chalk.white("To delete 1 generated random word type:")+"\n"+
    chalk.red("grne ")+chalk.yellow("list-remove ")+
    chalk.blue.bold("[Int: index]"));

  return;
}

if(args[0] && args[0].match("list-remove")){
  if(args[1] && !isNaN(parseInt(args[1]))){
    var grne_list = fs.readFileSync(txt).toString().split("\n");
    var s = grne_list[parseInt(args[1])-1];
    if(s != null){
      grne_list = grne_list.filter((item, index) => {
        return index != (parseInt(args[1])-1);
      });
      console.log(chalk.red("Deleted "+s+"!"));
    }
    fs.writeFileSync(txt,grne_list.join("\n"));
  } else {
    var r1 = readline.createInterface({
      input: process.stdin,
      output: process.stdout
    });
    r1.question("Are you sure you want to delete all random entries?[(y)es/(n)o]",function(s){
      if(s.toLowerCase() == "yes" || s.toLowerCase() == "y"){
        var grne_list = fs.readFileSync(txt).toString().split("\n");
        fs.writeFileSync(txt,"");
        console.log(chalk.red("Deleted "+grne_list.length+" entries!!"));
      }
      r1.close();
    });
  }

  return;
}

if(args[0] && args[0].match("list")){
  var index = 0;
  var grne_list = fs.readFileSync(txt).toString();
  grne_list = grne_list.replace(/\(.+\)/gi,(string) => {
    index++;
    return chalk.white(index+" ("+chalk.blue(string.split("(")[1].split(")")[0])+")");
  });
  grne_list = grne_list.replace(/\: .+/gi,(string) => ":"+chalk.yellow(string.replace(":",'')));
  console.log(grne_list);

  return;
}

let times = isNaN(args[0]) ? 100 : parseInt(args[0]);
let rnds = isNaN(args[1]) ? 20 : parseInt(args[1]);
let nonum = args[2] ? tobool(args[2]) : false;
let onlynum = args[3] ? tobool(args[3]) : false;
let spacing = args[4] ? tobool(args[4]) : false;
let save = args[5] ? tobool(args[5]) : true;

var a_z = ["a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"];
var A_Z = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];

var strrnd = "";

while(times--){
	strrnd += rand(rnds, spacing, nonum, onlynum);
}

if(save){
  var time = new Date().toLocaleString();
  fs.writeFileSync(txt,(fs.readFileSync(txt).toString().trim()+"\n("+time+"): "+strrnd).trim());
}

strrnd = strrnd.replace(/([0-9])/ig, (a, b) => {
	return chalk.blue.bold(b);
});

strrnd = strrnd.replace(/([A-Za-z])/ig, (a, b) => {
	if(b == "m") return b;
	return chalk.yellow.bold(b);
});

console.log(strrnd);
