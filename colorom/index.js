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
}

module.exports = chalk
