import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class RoomPanel extends JPanel{
	private Image bg;
	public RoomPanel(Image bg){
		this.bg=bg;
		setFocusable(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
	}
}