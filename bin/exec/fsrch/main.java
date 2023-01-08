import java.io.*;
import java.util.*;
import java.lang.*;
import java.nio.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;

import java.awt.*;
import java.awt.event.*;
import static java.awt.image.ImageObserver.WIDTH;
import java.util.logging.*;

import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public class main {
	public static Date then = new Date();

	public static void printHelp(){
		System.out.println("\033[32mFSRCH: File Search");
		System.out.println("\033[37mA file searching CLI program with Java");
		System.out.println("\n\n\033[31mArguments:\033[39m");
		System.out.println("_________________________________________________");
		System.out.println("|\033[33mName      \033[34mArgument       \033[35mType     \033[30mParameters   \033[39m|");
		System.out.println("|===============================================|");
		System.out.println("|\033[33mPath      \033[34m--path,-p      \033[35mString   \033[30m1            \033[39m|");
		System.out.println("|\033[33mFind      \033[34m--find,-f      \033[35mString   \033[30m1            \033[39m|");
		System.out.println("|\033[33mContent   \033[34m--content,-c   \033[35mBoolean  \033[30m0            \033[39m|");
		System.out.println("|\033[33mVerbose   \033[34m--verbose,-v   \033[35mBoolean  \033[30m0            \033[39m|");
		System.out.println("|_______________________________________________|");
	}

	public static void openApp(){
		SrchApp app = new SrchApp();
		Printer.setMode("app");
		Printer.setApp(app);
	}

	public static void main(String[] args) {
		Argument argument = Argument.from(args);

		if (argument.isEmpty()) {
			System.out.println("\033[31m[ ! ] no parameter is passed\033[00m");
			System.out.println("\033[32m[ + ] \033[00mExiting program");
			// openApp();
			System.exit(0);
		} else {
			ArgumentBuilder builder = argument.build();
			if (builder.isEmpty()){
				System.out.println("\033[31m[ ! ] no parameter or is passed\033[00m");
				return;
			}
			if (builder.length() == 1){
				if(builder.parse("--help", "-h")){
					printHelp();
					return;
				}
				else{
					System.out.println("\033[31m[ ! ] unknown command \033[00m");
					System.exit(1);
				}
			}
			if(builder.parse("-rnmext")){
				Param rnmfiles = builder.parseParam("-rnmext");
				String from = rnmfiles.getParamAt(0);
				String to = rnmfiles.getParamAt(1);
				boolean verbose = builder.parse("-v", "--verbose");
				boolean ignore = builder.parse("-i", "--ignore");
				ArrayList<String> results = FileSearch.search("./", ignore, (FileSearch.Argument arg)->{
					if(verbose == true) Printer.print("Renaming: "+lastPath(arg.name)); 
					if(arg.name.endsWith(from)){
						File oldFile = new File(arg.name);
						String newname = arg.name.replace(from, to);
						File newFile = new File(newname);
						if(newFile.exists()){
							Printer.print("Error: "+arg.name+" Cannot be re-named, it already exists");
						} else {
							oldFile.renameTo(newFile);
							if(verbose == true) Printer.print("Renamed: "+lastPath(arg.name)+" to "+lastPath(newname)); 
						}
					}
					return new TextMatcher(arg.name).match(from) > 0;
				});
				return;
			}

			boolean verbose = builder.parse("-v", "--verbose");
			Param path = builder.parseParam("-p");
			boolean content = builder.parse("-c", "--content");
			Param find = builder.parseParam("-f");
			boolean isapp = builder.parse("--app", "-a");
			boolean ignore = builder.parse("-i", "--ignore");

			if(isapp) openApp();

			ArrayList<String> results = new ArrayList<>();

			if(content){
				results = FileSearch.searchByContent(path.getParamAt(0), find.getParamAt(0), verbose, ignore);
			} else {
				results = FileSearch.searchByName(path.getParamAt(0), find.getParamAt(0), verbose, ignore);
			}

			System.out.println("listing "+Integer.toString(results.size())+" entries.");
			for(String result : results){
				Printer.print(result);
			}

			Date now = new Date();
			int time = getTime((int)((now.getTime() - then.getTime())));

			System.out.println("Found: "+Integer.toString(results.size())+" entries in "+Integer.toString(time) + " seconds.");

		}
	}

	public static int getTime(int ms){
		return ms/1000;
	}

	public static String lastPath(String pth){
		String[] sth = pth.split("/");
		return sth[sth.length-1];
	}

}

class FileSearch {

	public static class Argument {
		public String name;
		public int index;
		public int total;
		public Argument(String a, Integer b, Integer c){
			this.name = a;
			this.index = b;
			this.total = c;
		}
	};
	public static ArrayList<String> searchByName(String path, String query, boolean verbose, boolean ignore){
		FileSearchG g = new FileSearchG();
		var results = g.searchFiles(path, ignore, (Argument args) -> {
			if(verbose == true) Printer.print("Searching: "+args.name);
			return new TextMatcher(args.name).match(query) > 0;
		});
		return results;
	}
	public static ArrayList<String> searchByContent(String path, String query, boolean verbose, boolean ignore){
		FileSearchG g = new FileSearchG();
		var results = g.searchFiles(path, ignore, (Argument args) -> {
			if(verbose == true) Printer.print("Searching: "+args.name);
			return new TextMatcher(FileUtil.readFile(args.name)).match(query) > 0;
		});
		return results;
	}
	public static ArrayList<String> search(String path, boolean ignore, FileSearchG.Callback<Argument, Boolean> cb){
		FileSearchG g = new FileSearchG();
		var results = g.searchFiles(path, ignore, cb);
		return results;
	}
}

class FileSearchG {

	@FunctionalInterface
	public interface Callback<P, R> {
		public R call(P param);
	}

	public String ext(String file){
		String exte = file;
		if(file.indexOf(".") > -1){
			exte = file.substring(file.lastIndexOf(".") + 1);
		}
		return exte;
	}

	public ArrayList<String> searchFiles(String path, boolean ignore, Callback<FileSearch.Argument, Boolean> fn){
		ArrayList<String> searched = new ArrayList<String>();
		int index = 0;
		int totalFiles = 0;
		int foundFiles = 0;
		File file = new File(path);
		listFileAndSearch(file.listFiles(), index, searched, totalFiles, foundFiles, fn, ignore);
		return searched;
	}

	private void listFileAndSearch(File [] nodes, int index, ArrayList<String> searched, int totalFiles, int foundFiles, Callback<FileSearch.Argument, Boolean> fn, boolean ignore){
		if(index > nodes.length)
			return;

		for(File child : nodes){
			if(ignore == true && child.getName().startsWith("."))
				continue;
			if(child.isFile()){
				if(fn.call(new FileSearch.Argument(child.getAbsolutePath(), index, nodes.length))){
					searched.add(child.getAbsolutePath());
					foundFiles++;
				}
			}
			if(child.isDirectory()){
				listFileAndSearch(Objects.requireNonNull(child.listFiles()), Objects.requireNonNull(child.listFiles()).length, searched, totalFiles, foundFiles, fn, ignore);
			}
		}

		totalFiles++;
		index += 1;
	}
}

class TextFilter {

	private final String data; // base data

	public TextFilter(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public int length() {
		return data.length();
	}

	/*
	 * returns the amount of char in data
	 * m = size of data
	 * uses O(m) for searching
	 */
	public int amountOf(char a) {
		int amount = 0;
		for (char c : data.toCharArray()) {
			if (c == a)
				amount++;
		}

		return amount;
	}

	/*
	 * checks amount of c in data
	 * base algorithm to search
	 */
	public int amountOf(String c){
		if(c == null || c.equals(""))
			return 0;

		if(c.equals(data))
			return 1;

		int s = 0;
		for(int i = 0; i <= data.length() - c.length(); i++){
			String window = data.substring(i, i + c.length());
			if(c.equals(window)){
				s += 1;
				i += (c.length()-1);
			}
		}

		return s;
	}

	/*
	public int amountOf(String c) {
		if(c==null || c.equals(""))
			return 0;

		int q = data.length() - (data.length() % c.length());
		q = (q % 2 == 1) ? q : q + 1;
		int FileCon = 0;
		for (int i = 0; i < q - c.length(); i++) {
			String window = data.substring(i, i + c.length());
			if (c.equals(window)) {
				FileCon++;
			}
		}
		return FileCon;
	}
	 */
	public float getPercent(char c) {
		return ((float) amountOf(c) / (float) data.length()) * 100f;
	}

	public float getPercent(String c) { return (((float) amountOf(c) * c.length()) / (float) length() ) * 100f; }

	@Override
	public String toString() {
		return "TextFilter{" +
				"data='" + data + '\'' +
				'}';
	}

}

class TextMatcher {

	private final String data;

	public TextMatcher(String data){
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TextMatcher that = (TextMatcher) o;
		return Objects.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		return 0;
	}

	public float match(String search){
		if(equals((Object)search))
			return 100f;

		return new TextFilter(data).getPercent(search);

	}

}

class FileUtil{

		private static void createNewFile(String str) {
				int lastIndexOf = str.lastIndexOf(File.separator);
				if (lastIndexOf > 0) {
						makeDir(str.substring(0, lastIndexOf));
				}
				File file = new File(str);
				try {
						if (!file.exists()) {
								file.createNewFile();
						}
				} catch (IOException e) {
						e.printStackTrace();
				}
		}

		public static String readFile(String str) {
				FileReader fileReader;
				IOException e;
				IOException e2;
				Throwable th;
				createNewFile(str);
				StringBuilder stringBuilder = new StringBuilder();
				try {
						fileReader = new FileReader(new File(str));
						try {
								char[] cArr = new char[1024];
								while (true) {
										int read = fileReader.read(cArr);
										if (read <= 0) {
												break;
										}
										stringBuilder.append(new String(cArr, 0, read));
								}
								if (fileReader != null) {
										try {
												fileReader.close();
										} catch (Exception e2_) {
												e2_.printStackTrace();
										}
								}
						} catch (IOException e3) {
								e = e3;
						}
				} catch (IOException e4) {
						e = e4;
						fileReader = null;
						try {
								e.printStackTrace();
								if (fileReader != null) {
										try {
												fileReader.close();
										} catch (Exception e22) {
												e22.printStackTrace();
										}
								}
								return stringBuilder.toString();
						} catch (Throwable th2) {
								th = th2;
								if (fileReader != null) {
										try {
												fileReader.close();
										} catch (Exception e5) {
												e5.printStackTrace();
										}
								}
								
						}
				} catch (Throwable th3) {
						th = th3;
						fileReader = null;
						if (fileReader != null) {
								try {
										fileReader.close();
								} catch (Exception e5) {
										e5.printStackTrace();
								}
						}
						
				}
				return stringBuilder.toString();
		}

		public static void writeFile(String str, String str2) {
				FileWriter fileWriter;
				IOException e;
				IOException e2;
				IOException e22;
				Throwable th;
				createNewFile(str);
				FileWriter fileWriter2 = null;
				try {
						fileWriter = new FileWriter(new File(str), false);
						try {
								fileWriter.write(str2);
								fileWriter.flush();
								if (fileWriter != null) {
										try {
												fileWriter.close();
										} catch (IOException e2_) {
												e2_.printStackTrace();
										}
								}
						} catch (IOException e3) {
								e2 = e3;
								try {
										e2.printStackTrace();
										if (fileWriter != null) {
												try {
														fileWriter.close();
												} catch (IOException e22_) {
														e22_.printStackTrace();
												}
										}
								} catch (Throwable th2) {
										th = th2;
										fileWriter2 = fileWriter;
										if (fileWriter2 != null) {
												try {
														fileWriter2.close();
												} catch (IOException e4) {
														e4.printStackTrace();
												}
										}
										
								}
						}
				} catch (IOException e5) {
						e22 = e5;
						fileWriter = null;
						e22.printStackTrace();
						if (fileWriter != null) {
								try {
										fileWriter.close();
								} catch (Exception _ee5) {
										_ee5.printStackTrace();
								}
						}
				} catch (Throwable th3) {
						th = th3;
						if (fileWriter2 != null) {
								try {
										fileWriter2.close();
								} catch (Exception e5) {
										e5.printStackTrace();
								}
						}
						
				}
		}

		public static void copyFile(String str, String str2) {
				FileInputStream fileInputStream;
				FileOutputStream fileOutputStream;
				IOException e;
				IOException e22;
				IOException e2222;
				Throwable th;
				FileInputStream fileInputStream2 = null;
				if (isExistFile(str)) {
						createNewFile(str2);
						try {
								fileInputStream = new FileInputStream(str);
								try {
										fileOutputStream = new FileOutputStream(str2, false);
										try {
												byte[] bArr = new byte[1024];
												while (true) {
														int read = fileInputStream.read(bArr);
														if (read <= 0) {
																break;
														}
														fileOutputStream.write(bArr, 0, read);
												}
												if (fileInputStream != null) {
														try {
																fileInputStream.close();
														} catch (IOException e2) {
																e2.printStackTrace();
														}
												}
												if (fileOutputStream != null) {
														try {
																fileOutputStream.close();
														} catch (IOException e22_) {
																e22_.printStackTrace();
														}
												}
										} catch (IOException e3) {
												e22 = e3;
												fileInputStream2 = fileInputStream;
										} catch (Throwable th2) {
												th = th2;
										}
								} catch (IOException e4) {
										e22 = e4;
										fileOutputStream = null;
										fileInputStream2 = fileInputStream;
										try {
												e22.printStackTrace();
												if (fileInputStream2 != null) {
														try {
																fileInputStream2.close();
														} catch (IOException e222) {
																e222.printStackTrace();
														}
												}
												if (fileOutputStream != null) {
														try {
																fileOutputStream.close();
														} catch (IOException e2222_) {
																e2222_.printStackTrace();
														}
												}
										} catch (Throwable th3) {
												th = th3;
												fileInputStream = fileInputStream2;
												if (fileInputStream != null) {
														try {
																fileInputStream.close();
														} catch (IOException e5) {
																e5.printStackTrace();
														}
												}
												if (fileOutputStream != null) {
														try {
																fileOutputStream.close();
														} catch (IOException e6) {
																e6.printStackTrace();
														}
												}
												
										}
								} catch (Throwable th4) {
										th = th4;
										fileOutputStream = null;
										if (fileInputStream != null) {
												fileInputStream.close();
										}
										if (fileOutputStream != null) {
												fileOutputStream.close();
										}
										
								}
						} catch (IOException e7) {
								e2222 = e7;
								fileOutputStream = null;
								e2222.printStackTrace();
								if (fileInputStream2 != null) {
										try {
												fileInputStream2.close();
										} catch (Exception _ee5) {
												_ee5.printStackTrace();
										}
								}
								if (fileOutputStream != null) {
										try {
												fileOutputStream.close();
										} catch (Exception _ee5) {
												_ee5.printStackTrace();
										}
								}
						} catch (Throwable th5) {
								th = th5;
								fileOutputStream = null;
								fileInputStream = null;
								if (fileInputStream != null) {
										try {
												fileInputStream.close();
										} catch (Exception _ee5) {
												_ee5.printStackTrace();
										}
								}
								if (fileOutputStream != null) {
										try {
												fileOutputStream.close();
										} catch (Exception _ee5) {
												_ee5.printStackTrace();
										}
								}
								
						}
				}
		}

		public static void moveFile(String str, String str2) {
				copyFile(str, str2);
				deleteFile(str);
		}

		public static void deleteFile(String str) {
				File file = new File(str);
				if (!file.exists()) {
						return;
				}
				if (file.isFile()) {
						file.delete();
						return;
				}
				File[] listFiles = file.listFiles();
				if (listFiles != null) {
						for (File file2 : listFiles) {
								if (file2.isDirectory()) {
										deleteFile(file2.getAbsolutePath());
								}
								if (file2.isFile()) {
										file2.delete();
								}
						}
				}
				file.delete();
		}

		public static boolean isExistFile(String str) {
				return new File(str).exists();
		}

		public static void makeDir(String str) {
				if (!isExistFile(str)) {
						new File(str).mkdirs();
				}
		}

		public static void listDir(String str, ArrayList<String> arrayList) {
				File file = new File(str);
				if (file.exists() && !file.isFile()) {
						File[] listFiles = file.listFiles();
						if (listFiles != null && listFiles.length > 0 && arrayList != null) {
								arrayList.clear();
								for (File absolutePath : listFiles) {
										arrayList.add(absolutePath.getAbsolutePath());
								}
						}
				}
		}

		public static boolean isDirectory(String str) {
				if (isExistFile(str)) {
						return new File(str).isDirectory();
				}
				return false;
		}

		public static boolean isFile(String str) {
				if (isExistFile(str)) {
						return new File(str).isFile();
				}
				return false;
		}

		public static long getFileLength(String str) {
				if (isExistFile(str)) {
						return new File(str).length();
				}
				return 0;
		}
}

class Argument {

	private final String [] args;

	private Argument(String [] args){
		this.args = args;
	}


	public static Argument from(String [] args){
		if(args == null)
			throw new NullPointerException();

		return new Argument(args);
	}


	// checks if args is 0
	public boolean isEmpty(){return args.length == 0;}

	// returns the length of args
	public int length(){
		return args.length;
	}

	public int argumentLength(){
		int count = 0;
		for (String arg : args) {
			if (isArgument(arg))
				count++;
		}

		return count;
	}

	public int parameterLength(){
		int count = 0;
		for (String arg : args) {
			if (!isArgument(arg))
				count++;
		}

		return count;
	}
	

	public ArgumentBuilder build(){
		return new ArgumentBuilder(args);
	}


	private static boolean isArgument(String s){
		return s.startsWith("--") || s.startsWith("-");
	}


	@Override
	public String toString() {
		return "Argument{" +
				"args=" + Arrays.toString(args) +
				'}';
	}
}






class ArgumentBuilder {

	private String [] args; // base property of the argument

	/**
	 * @param args
	 * Param builder is an object which is used to build a parameter
	 */
	public ArgumentBuilder(String [] args){
		this.args = args;
	}

	/* param builder */
	public Param parseParam(String... p){

		if (!checkParam(p))
			return null;

		String selected = getParam(p);
		int start = indexOf(selected);
		int capacity = countToNextArg(start);

		if(capacity == 0)
			return new Param(null);

		String[] params = new String[ countToNextArg(start) ]; // len of all the params to next parameters
		/* loop over index of args and add to param */
		for(int i=start+1, index =0; i<start+params.length+1; i++, index++)
			params[index] =  args[i];

		return new Param(params);
	}

	/**
	 *
	 * @param param
	 * @return String []
	 * returns String [] by building from Param class
	 */
	public String [] rawArgs(){return args;}

	/**
	 *  @param p
	 * @return boolean
	 * returns if the param (p) is in the argument list
	 */
	public boolean parse(String... choice){
		for(String s: args){
			for (String c: choice){
				if(c.equals(s))
					return true;
			}
		}

		return false;
	}


	public boolean isEmpty(){return  args.length == 0;}

	// returns the length of arguments
	public int length (){ return args.length;}


	/*
		@return boolean
		check if param is in argument
	 */
	public boolean checkParam(String... param){
		for(String s : args){
			for(String p: param){
				if(p.equals(s))
					return true;
			}
		}
		return false;
	}

	/*
	returns selected param form the args
	 */
	private String getParam(String... params){
		String param = null;
		for(String arg: args){
			for (String search: params){
				if(arg.equals(search))
					param = search;
			}
		}
		return param;
	}

	/*
	 * @param search
	 * @return int
	 * returns the index of search from a given arg list
	 */
	public int indexOf(String p){
		int index = 0;
		for(String s: args){
			if(s.equals(p))
				break;

			index++;
		}
		return index;
	}

	/*
	 * @param start
	 * @return int
	 * counts from a given index to next arg
	 * while counting arg check null or (-)
	 */
	public int countToNextArg(int start){
		// check for the next item if it is the last one or if the selected is the last one
		if(start+1 >= args.length)
			return 0;

		int count = 0;
		// count until item from the start to the item which not contain (-)
		for(int i = start+1; i< args.length; i++){
			if(args[i].startsWith("-"))
				break;

			count++;
		}

		return count;
	}


	/**
	 returns argument class as a string
	 */
	@Override
	public String toString() {
		return "ArgumentBuilder{" +
				"args=" + Arrays.toString(args) + // array is used to speed up the process to build the method
				'}';
	}
}

class Param {

	private final String [] params;

	public Param(String [] params){
		this.params = params;
	}

	public String [] toArray(){
		return params;
	}

	public String getParamAt(int index){
		if(index >= params.length) // check for the index
			throw new IndexOutOfBoundsException();

		return params[index];
	}

	public int length(){  return  params.length; } // returns the length of the param

	public boolean isEmpty(){
		return params.length == 0;
	} // check if the param is null

	@Override
	public String toString() {
		return "Param{" +
				"params=" + Arrays.toString(params) +
				'}';
	}

}

class SrchApp {
	public JFrame frame = new JFrame();
	// public JScrollPane pane = new JScrollPane();
	public JPanel panel = new JPanel();

	SrchApp(){

		JInternalFrame iff = new JInternalFrame("hey", true, true, true, true);
		JFrame jff = new JFrame();

		// String str = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title></title></head><body>"+
		// "<button>Hello</button>"+
		// "<script>Hello</script>"+
		// "</body></html>";


		// var html = new BasicHTML();
		// System.out.println(html.isHTMLString(str));
		// var panvi = html.createHTMLView(panel, str);

		// pane.add(panel);
		// frame.add(panel);
		// panel.add(new JButton("hello world"));
		// frame.add(pane);

		frame.add(iff);
		// frame.add(jff);

		frame.setSize(500, 600);
		frame.setVisible(true);
    frame.setTitle("FSrch");
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE); 
	}

	public void addTxt(String t){
		JLabel txt = new JLabel(t);
		panel.add(txt);
	}
}

class Printer {
	public static SrchApp app = null;
	public static String md = "not_app";
	public static void setMode(String mode){
		md = mode;
	}
	public static void setApp(SrchApp apop){
		app = apop;
	}
	public static void print(String str){
		if(md.equals("app")){
			app.addTxt(str);
			System.out.println(str);
		} else {
			System.out.println(str);
		}
	}
}
