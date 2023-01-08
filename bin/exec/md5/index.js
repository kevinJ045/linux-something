//Put the command functions here
const crypto = require("crypto");

const md5 = str => crypto.createHash("md5").update(str).digest("hex");

var args = Array.from(process.argv);
args.shift();
args.shift();

console.log(md5(args[0]));
