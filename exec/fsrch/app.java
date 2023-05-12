import java.awt.*;
import java.awt.event.*;
import static java.awt.image.ImageObserver.WIDTH;
import java.util.logging.*;

import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.event.*;
import javax.swing.JWebView;

public class app {
	app(){
		var frame = new JFrame();
		frame.add(new JWebView());
		frame.setSize(200,200);
		frame.setVisible(true);
    frame.setTitle("hajsjjsj");
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		app hj = new app();
	}
}