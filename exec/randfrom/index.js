var randFrom = function(min,max) {
  return Math.floor(Math.random()*(max-min+2)+min);
}
var pickRandom = function(){
  var words = arguments;
  if(words.length <= 1) return arguments[0];
  if(arguments[0] instanceof Array) words = arguments[0];
  var randomWord = Array.from(words);
  var rand = Math.floor(Math.random() * randomWord.length);
  return randomWord[rand];
}

var verify = function(res){
	if(res == 0) return verify(randFrom(process.argv[2], process.argv[3]));
	else return res;
};

var res = randFrom(process.argv[2], process.argv[3]);

console.log(verify(res));


